package com.nuaa.nodes;

import com.alibaba.fastjson.JSON;
import com.nuaa.protocol.raft.AppendEntriesRequest;
import com.nuaa.protocol.raft.VoteRequest;
import com.nuaa.protocol.raft.VoteResponse;
import com.nuaa.protocol.raft.util.LogUtil;

import java.util.concurrent.*;

/**
 * Created by wangjiuyong on 2017/7/27.
 */
public class RPCReceiveRequest implements Runnable {
    private ExecutorService es = Executors.newCachedThreadPool();
    private Node node;
    public  LinkedBlockingQueue<Object> messageQueue = new LinkedBlockingQueue<>();

    public RPCReceiveRequest(Node node) {
        this.node = node;
    }

    public  void addMessage(Object message) {
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override public void run() {
        while (true) {
            Object message = null;
            try {
                message = messageQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (message instanceof VoteRequest) {
                LogUtil.log(node + "receive the VoteRequest Message    " + message);
                VoteRequest voteRequest=(VoteRequest) message;
                VoteRequestHandler voteRequesthandler = new VoteRequestHandler();
                voteRequesthandler.setMessage(voteRequest);
                voteRequesthandler.setNode(node);
                FutureTask<VoteResponse> future = new FutureTask<>(voteRequesthandler);
                es.submit(future);
                try {
                    VoteResponse voteResponse = future.get();
                    LogUtil.log(node.toString()+voteResponse.toString());
                    node.getRpcSendRequest().setRemoteNode(getNodeById(voteRequest.getCandidateId()));
                    node.getRpcSendRequest().addMessage(voteResponse);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } else if (message instanceof VoteResponse) {
                LogUtil.log(node+"receive the VoteResponse Message   "+message);
                if (node.getNodeRole() == NodeStateRole.CANDIDATE) {
                    int halfSize = node.getNodeSets().size() / 2 + 1;
                    VoteResponse voteResponse = (VoteResponse) message;
                    int maxTerm = node.getVoteStatic().getMaxTerm();
                    int voteCount = node.getVoteStatic().getSuccessCount();
                    LogUtil.log(
                        info() + " receive  voteResponse " + JSON.toJSONString(voteResponse));
                    if (maxTerm < voteResponse.getTerm()) {
                        maxTerm = voteResponse.getTerm();
                    }
                    if (voteResponse.isResult()) {
                        LogUtil.log(
                            info() + "VoteRequest accepted   " + JSON.toJSONString(voteResponse));
                        voteCount++;
                    } else {
                        LogUtil.log(
                            info() + "VoteRequest refused   " + JSON.toJSONString(voteResponse));
                    }
                    node.getVoteStatic().setSuccessCount(voteCount);
                    node.getVoteStatic().setMaxTerm(maxTerm);
                    LogUtil.log(node+" voteStatic   "+node.getVoteStatic());
                    if (node.getVoteStatic().getSuccessCount() + 1> halfSize) {
                        //已经满足了大于50%的要求
                        LogUtil.log(node+" become Event.BOCOMELEADER   ");
                        if(node.getNodeRole()==NodeStateRole.CANDIDATE&&node.getNodeStateAction()==NodeStateAction.CANDINATE_LEADER) {
                            //节点处于从candidate向leader申请的过程中，
                            node.setNodeRole(NodeStateRole.LEADER);
                            node.setNodeStateAction(null);
                            node.handleEvent(NodeStateEvent.BROADCASTLEADER);
                        }
                    }
                }
            } else if (message instanceof NodeStateEvent) {
                LogUtil.log(node+"receive the Event Message   "+message);
                NodeStateEvent event = (NodeStateEvent) message;
                if (event == NodeStateEvent.BOCOMELEADER) {
                    node.setNodeRole(NodeStateRole.LEADER);
                    node.handleEvent(NodeStateEvent.BROADCASTLEADER);
                }
            }else if (message instanceof AppendEntriesRequest) {
                //表示收到leader几点的心跳信息
                node.setNodeRole(NodeStateRole.FOLLOWER);
                node.setNodeStateAction(null);
                LogUtil.log(node+"receive the heart Message   ");
            }else{
                LogUtil.log(node+"receive unknown message   ");
            }
            LogUtil.log(node+" enter next wait round ");
            node.getTimerMachine().signal();
            LogUtil.log(node.info()+" enter next wait round ");
        }
    }

    public String info() {
        return node.toString();
    }

    private Node getNodeById(int nodeId){
        for(Node node:node.getNodeSets()){
            if(node.getNodeId()==nodeId){
                return node;
            }
        }
        return node.getNodeState().getLeaderNode();
    }
}
