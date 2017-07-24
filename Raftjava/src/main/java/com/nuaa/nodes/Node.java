package com.nuaa.nodes;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wangjiuyong on 2017/7/24.
 */
public class Node implements Runnable {
    private Lock lock = new ReentrantLock();
    private Condition newNode = lock.newCondition();
    private String nodeName;
    private String nodeId;
    public Set<Node> nodeSets = new HashSet<>();
    private NodeStatus nodeStatus = NodeStatus.FOLLOWER;
    private TimerRunnable timerRunnable;
    private Thread timerThread;
    private int voteCount = 0;
    private ExecutorService es = Executors.newCachedThreadPool();
    private Node LeaderNode;

    public NodeStatus getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(NodeStatus nodeStatus) {
        this.nodeStatus = nodeStatus;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Set<Node> getNodeSets() {
        return nodeSets;
    }


    public Node() {
        ExecutorService es = Executors.newCachedThreadPool();
    }

    public void addNode(Node node) {
        this.nodeSets.add(node);
        node.getNodeSets().add(this);
    }

    public Message handlerRPCMessage(Message message) {
        System.out.println(this + "   receive  " + message);
        if (message == Message.REQUEST_VOTE) {
            timerRunnable.receiveHeartBeat();
            return Message.RESPONCE_VOTE;
        } else if (message == Message.REQUEST_LEADER) {
            nodeStatus = NodeStatus.FOLLOWER;
            timerRunnable.receiveHeartBeat();
            return Message.RESPONCE_LEADER;
        } else if (message == Message.BECOME_LEADER) {
            timerThread.interrupt();
            return Message.HEARTBEAT;
        }
        return Message.HEARTBEAT;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Node) {
            Node temp = (Node) o;
            if (this.getNodeId().equals(temp.getNodeId()) && this.getNodeName()
                .equals(temp.getNodeName())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override public int hashCode() {
        return nodeName.hashCode() + nodeId.hashCode();
    }

    public String toString() {
        return nodeName + "   " + nodeId;
    }

    public void handleCandidateTimeOut() {
        System.out.println(this + "   handleTimeOut ");
        nodeStatus = NodeStatus.CANDIDATE;
        RPCRunnable voteRunnable = new RPCRunnable(this, Message.REQUEST_VOTE);
        Future<Integer> future = es.submit(voteRunnable);
        try {
            int voteCount = future.get();
            System.out.println(voteRunnable);
            if (voteCount + 1 > nodeSets.size()) {
                System.out.println(this + "   apply the leader");

                voteRunnable = new RPCRunnable(this, Message.REQUEST_LEADER);
                future = es.submit(voteRunnable);
                voteCount = future.get();
                System.out.println(voteRunnable);
                if (voteCount + 1 > nodeSets.size()) {
                    System.out.println(this + "   become the leader");
                    timerThread.interrupt();
                    voteRunnable = new RPCRunnable(this, Message.BECOME_LEADER);
                    es.submit(voteRunnable);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override public void run() {
        System.out.println(this);
        if ((nodeStatus == NodeStatus.FOLLOWER)) {
            nodeStatus = NodeStatus.CANDIDATE;
            if (nodeSets.size() > 0) {
                //开始选举过程
                timerRunnable = new TimerRunnable(this);
                timerThread = new Thread(timerRunnable);
                timerThread.setName(Thread.currentThread().getName() + "TimerRunnable");
                timerThread.start();
            }
        }
    }

    public Node getLeaderNode() {
        return LeaderNode;
    }

    public void setLeaderNode(Node leaderNode) {
        LeaderNode = leaderNode;
    }

    private void notifyLeaderNode(Node leaderNode) {
        LeaderNode = leaderNode;
    }
}
