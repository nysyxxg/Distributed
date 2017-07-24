package com.nuaa.nodes;

import java.util.Set;

/**
 * Created by wangjiuyong on 2017/7/24.
 */
public class HeartBeatRunnable implements Runnable{

    private Node node;
    private Message message;
    public HeartBeatRunnable(Node node,Message message){
        this.node=node;
        this.message=message;
    }

    @Override public void run() {
        while(true) {
            Set<Node> nodeSet = node.getNodeSets();
            for (Node nodeChild : nodeSet) {
                System.out.println("nodeChild   " + nodeChild);
                if (node != nodeChild) {
                    nodeChild.handlerRPCMessage(message);
                }
            }
        }
    }
}
