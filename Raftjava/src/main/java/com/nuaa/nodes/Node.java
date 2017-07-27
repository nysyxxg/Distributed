package com.nuaa.nodes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.nuaa.protocol.raft.*;
import com.nuaa.protocol.raft.util.LogUtil;
import com.nuaa.protocol.raft.util.NodeState;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wangjiuyong on 2017/7/24.
 */
public class Node implements Runnable {
    //节点状态机的现态，初始现态都是FOLLOWER
    private NodeStateRole nodeRole = NodeStateRole.FOLLOWER;
    //节点状态机的动作
    private NodeStateAction nodeStateAction;
    //节点名称
    private String nodeName;
    //节点ID号
    private int nodeId;
    //所有的节点集合
    public Set<Node> nodeSets = new HashSet<>();
    //定时器
    private TimerMachine timerMachine;
    private Thread timerMachineThread;
    //投票数目
    private int voteCount = 0;
    private ExecutorService es = Executors.newCachedThreadPool();
    private RPCSendRequest rpcSendRequest;
    private RPCReceiveRequest rpcReceiveRequest;
    protected NodeState nodeState;
    private VoteStatic voteStatic = new VoteStatic();



    public Node() {
        ExecutorService es = Executors.newCachedThreadPool();
        rpcSendRequest = new RPCSendRequest(this);
        rpcReceiveRequest = new RPCReceiveRequest(this);
        es.submit(rpcSendRequest);
        es.submit(rpcReceiveRequest);
        nodeStateAction = NodeStateAction.START_FOLLOWER;
        nodeState = new NodeState();
    }

    public void addNode(Node node) {
        this.nodeSets.add(node);
        node.getNodeSets().add(this);
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
        return nodeName+" ";
    }



    public void handleEvent(NodeStateEvent event) {
        LogUtil.log(this +" handleEvent "+event);
        if (event == NodeStateEvent.TIMEOUT) {
            if (nodeRole == NodeStateRole.CANDIDATE) {
                //选举超时事件，发送选举事件
                VoteRequest requestMessage = buildVoteRequest();
                rpcSendRequest.addMessage(requestMessage);
            }
        } else if (event == NodeStateEvent.BROADCASTLEADER) {
            //节点已经成为leader节点的事件
            if (nodeRole == NodeStateRole.LEADER) {
                LogUtil.log(this +" become the leader");
                nodeRole = NodeStateRole.LEADER;
                LogUtil.log(this +" buildHeartRequest ");
                AppendEntriesRequest requestMessage = buildHeartRequest();
                rpcSendRequest.addMessage(requestMessage);
            }
        }else if (event == NodeStateEvent.BROADCASTHEART) {
            //节点已经成为leader节点的事件
            LogUtil.log(this +" BROADCASTHEART ");
            AppendEntriesRequest requestMessage = buildHeartRequest();
            rpcSendRequest.addMessage(requestMessage);
        }
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
        LogUtil.log(this);
        if ((nodeRole == NodeStateRole.FOLLOWER)) {
            nodeRole = NodeStateRole.CANDIDATE;
            if (nodeSets.size() > 0) {
                //开始选举过程
                timerMachine = new TimerMachine(this);
                timerMachineThread = new Thread(timerMachine);
                timerMachineThread.setName(Thread.currentThread().getName() + "TimerRunnable");
                timerMachineThread.start();
            }
        }
    }



    public NodeStateAction getNodeStateAction() {
        return nodeStateAction;
    }

    public void setNodeStateAction(NodeStateAction nodeStateAction) {
        this.nodeStateAction = nodeStateAction;
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

    public void setNodeSets(Set<Node> nodeSets) {
        this.nodeSets = nodeSets;
    }

    public NodeStateRole getNodeRole() {
        return nodeRole;
    }

    public void setNodeRole(NodeStateRole nodeRole) {
        this.nodeRole = nodeRole;
    }

    public TimerMachine getTimerMachine() {
        return timerMachine;
    }

    public void setTimerMachine(TimerMachine timerMachine) {
        this.timerMachine = timerMachine;
    }

    public Thread getTimerMachineThread() {
        return timerMachineThread;
    }

    public void setTimerMachineThread(Thread timerMachineThread) {
        this.timerMachineThread = timerMachineThread;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public ExecutorService getEs() {
        return es;
    }

    public void setEs(ExecutorService es) {
        this.es = es;
    }

    public RPCSendRequest getRpcSendRequest() {
        return rpcSendRequest;
    }

    public void setRpcSendRequest(RPCSendRequest rpcSendRequest) {
        this.rpcSendRequest = rpcSendRequest;
    }

    public NodeState getNodeState() {
        return nodeState;
    }

    public void setNodeState(NodeState nodeState) {
        this.nodeState = nodeState;
    }

    public RPCReceiveRequest getRpcReceiveRequest() {
        return rpcReceiveRequest;
    }

    public void setRpcReceiveRequest(RPCReceiveRequest rpcReceiveRequest) {
        this.rpcReceiveRequest = rpcReceiveRequest;
    }

    public VoteStatic getVoteStatic() {
        return voteStatic;
    }

    public void setVoteStatic(VoteStatic voteStatic) {
        this.voteStatic = voteStatic;
    }

    public String info(){
        return nodeName+JSON.toJSONString(nodeRole)+JSON.toJSONString(nodeStateAction)+JSON.toJSONString(nodeState)+JSON.toJSONString(voteStatic);
    }
}
