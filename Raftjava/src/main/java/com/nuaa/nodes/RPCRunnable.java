package com.nuaa.nodes;

import com.alibaba.fastjson.JSON;
import com.nuaa.protocol.raft.RaftHeader;
import com.nuaa.protocol.raft.VoteRequest;
import com.nuaa.protocol.raft.VoteResponse;
import com.nuaa.protocol.raft.VoteStatic;

import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by wangjiuyong on 2017/7/24.
 */
public class RPCRunnable implements Callable<VoteStatic>{
    private Node node;
    private RaftHeader message;
    public RPCRunnable(Node node,RaftHeader message){
        this.node=node;
        this.message=message;
    }

    @Override public VoteStatic call() throws Exception {
        Set<Node> nodeSet = node.getNodeSets();
        int voteCount=0;
        int maxTerm=0;
        VoteStatic voteStatic=new VoteStatic();
        for(Node nodeChild:nodeSet){
            System.out.println("nodeChild   "+nodeChild);
            if(node!=nodeChild) {
                RaftHeader raftHeader =nodeChild.handlerRPCMessage(message);
                if(raftHeader instanceof  VoteResponse){
                    VoteResponse voteResponse=(VoteResponse)raftHeader;
                    System.out.println("   RPCRunnable voteResponse "+ JSON.toJSONString(voteResponse));
                    if(maxTerm<voteResponse.getTerm()){
                        maxTerm=voteResponse.getTerm();
                    }
                    if(voteResponse.isResult()){
                        System.out.println("VoteRequest accepted   "+ JSON.toJSONString(voteResponse));
                        voteCount++;
                    }
                    else{
                        System.out.println("VoteRequest refused   "+ JSON.toJSONString(voteResponse));
                    }
                }
            }
        }
        voteStatic.setMaxTerm(maxTerm);
        voteStatic.setSuccessCount(voteCount);
        return voteStatic;
    }
}
