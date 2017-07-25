package com.nuaa.nodes;

import com.alibaba.fastjson.JSON;
import com.nuaa.protocol.raft.*;
import com.nuaa.protocol.raft.util.NodeState;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by wangjiuyong on 2017/7/24.
 */
public class Node implements Runnable {
    //节点状态
    private Phase phase;
    //节点名称
    private String nodeName;
    //节点ID号
    private int nodeId;
    //所有的节点集合
    public Set<Node> nodeSets = new HashSet<>();
    //节点角色
    private NodeRole nodeStatus = NodeRole.FOLLOWER;
    //定时器
    private TimerMachine timerMachine;
    private Thread timerMachineThread;
    //投票数目
    private int voteCount = 0;
    private ExecutorService es = Executors.newCachedThreadPool();


    protected NodeState nodeState;


    public NodeState getNodeState() {
        return nodeState;
    }

    public void setNodeState(NodeState nodeState) {
        this.nodeState = nodeState;
    }

    public NodeRole getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(NodeRole nodeStatus) {
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

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public Set<Node> getNodeSets() {
        return nodeSets;
    }


    public Node() {
        ExecutorService es = Executors.newCachedThreadPool();
        phase = Phase.INITIAL;
        nodeState = new NodeState();
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public void addNode(Node node) {
        this.nodeSets.add(node);
        node.getNodeSets().add(this);
    }

    public RaftHeader handlerRPCMessage(RaftHeader message) {
        System.out.println(this + "   receive  " + message);
        timerMachine.receiveHeartBeat(message);
        if (message instanceof VoteRequest) {
            VoteRequest VoteRequest = (VoteRequest) message;
            NodeState.LogEntry localLogEntry = nodeState.getlastLogEntry();
            if (VoteRequest.getCandidateId() == nodeId) {
                //如果是相同的节点，则表示成功
                return new VoteResponse(nodeState.getCurrentTerm(), true);
            } else {
                if (localLogEntry.getTerm() > VoteRequest.getLast_log_term()) {
                    //如果本节点的term大于消息中包含的term,则返回false
                    return new VoteResponse(nodeState.getCurrentTerm(), false);
                } else if (localLogEntry.getTerm() == VoteRequest.getLast_log_term()
                    && localLogEntry.getIndex() > VoteRequest.getLast_log_index()) {
                    //如果term相同，本节点的index大于消息中的inex,返回false
                    return new VoteResponse(nodeState.getCurrentTerm(), false);
                } else {
                    //如果本节点的term和index都不大于消息中包含的信息，那么就返回true
                    nodeState.setVoteFor(message.getCandidateId());
                    return new VoteResponse(nodeState.getCurrentTerm(), true);
                }
            }
        }
        return null;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Node) {
            Node temp = (Node) o;
            if (this.getNodeId() == (temp.getNodeId()) && this.getNodeName()
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
        return nodeName.hashCode() + nodeId;
    }

    public String toString() {
        return nodeName + "   " + nodeId;
    }

    public void handleCandidateTimeOut() {
        System.out.println(this + "   handleTimeOut ");
        nodeStatus = NodeRole.CANDIDATE;
        VoteRequest VoteRequest = buildVoteRequest();
        RPCRunnable voteRunnable = new RPCRunnable(this, VoteRequest);
        Future<VoteStatic> future = es.submit(voteRunnable);
        try {
            VoteStatic voteStatic= future.get();
            System.out.println(voteRunnable);
            if (voteStatic.getSuccessCount()+1> nodeSets.size()) {
                nodeState.setCurrentTerm(voteStatic.getMaxTerm()+1);
                System.out.println(this + "   become the leader "+ JSON.toJSONString(nodeState));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendAppendEntriesRequest() {
        System.out.println(this + "   handleTimeOut ");
        nodeStatus = NodeRole.CANDIDATE;
        AppendEntriesRequest appendEntriesRequest = buildHeartRequest();
        RPCRunnable voteRunnable = new RPCRunnable(this, appendEntriesRequest);
    }

    private VoteRequest buildVoteRequest() {
        VoteRequest voteRequest = new VoteRequest();
        NodeState.LogEntry lastLogEntry = nodeState.getlastLogEntry();
        voteRequest.setLast_log_index(lastLogEntry.getIndex());
        voteRequest.setLast_log_term(lastLogEntry.getTerm());
        voteRequest.setCandidateId(nodeId);
        voteRequest.setTerm(lastLogEntry.getTerm());
        return voteRequest;
    }

    private AppendEntriesRequest buildHeartRequest() {
        AppendEntriesRequest appendEntriesRequest = new AppendEntriesRequest();
        NodeState.LogEntry lastLogEntry = nodeState.getlastLogEntry();
        //设置本次的term
        appendEntriesRequest.setTerm(nodeState.getCurrentTerm());
        //设置节点的ID
        appendEntriesRequest.setCandidateId(nodeId);
        //设置上次commit的Index
        appendEntriesRequest.setPrev_log_index(nodeState.getCommitLogIndex());
        appendEntriesRequest.setPrev_log_term(nodeState.getCommitLogTerm());
        appendEntriesRequest.setLeader_commit(nodeState.getCommitLogIndex());
        return appendEntriesRequest;
    }

    @Override public void run() {
        System.out.println(this);
        if ((nodeStatus == NodeRole.FOLLOWER)) {
            nodeStatus = NodeRole.CANDIDATE;
            if (nodeSets.size() > 0) {
                //开始选举过程
                timerMachine = new TimerMachine(this);
                timerMachineThread = new Thread(timerMachine);
                timerMachineThread.setName(Thread.currentThread().getName() + "TimerRunnable");
                timerMachineThread.start();
            }
        }
    }
}
