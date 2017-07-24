package com.nuaa.nodes;

import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by wangjiuyong on 2017/7/24.
 */
public class RPCRunnable implements Callable<Integer>{
    private Node node;
    private Message message;
    public RPCRunnable(Node node,Message message){
        this.node=node;
        this.message=message;
    }

    @Override public Integer call() throws Exception {
        Set<Node> nodeSet = node.getNodeSets();
        int voteCount=0;
        for(Node nodeChild:nodeSet){
            System.out.println("nodeChild   "+nodeChild);
            if(node!=nodeChild) {
                if(message==Message.REQUEST_VOTE) {
                    if (nodeChild.handlerRPCMessage(message) == Message.RESPONCE_VOTE) {
                        voteCount++;
                    }
                }else if(message==Message.REQUEST_LEADER) {
                    if (nodeChild.handlerRPCMessage(message) == Message.RESPONCE_LEADER) {
                        voteCount++;
                    }
                }
                else if(message==Message.BECOME_LEADER) {
                    if (nodeChild.handlerRPCMessage(message) == Message.HEARTBEAT) {
                        voteCount++;
                    }
                }
            }
        }
        return voteCount;
    }
}
