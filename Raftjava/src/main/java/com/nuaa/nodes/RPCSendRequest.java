package com.nuaa.nodes;

import com.nuaa.protocol.raft.*;
import com.nuaa.protocol.raft.util.LogUtil;

import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by wangjiuyong on 2017/7/24.
 */
public class RPCSendRequest implements Runnable {
    private Node node;
    public  LinkedBlockingQueue<RaftHeader> messageQueue=new LinkedBlockingQueue<>();
    private Node remoteNode;

    public Node getRemoteNode() {
        return remoteNode;
    }

    public void setRemoteNode(Node remoteNode) {
        this.remoteNode = remoteNode;
    }

    public RPCSendRequest(Node node) {
        this.node = node;
    }
    public void addMessage(RaftHeader message){
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override public void run() {
        Set<Node> nodeSet = node.getNodeSets();
        while(true) {
            RaftHeader message = null;
            try {
                message = messageQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(message instanceof VoteRequest || message instanceof AppendEntriesRequest){
                //是向其他节点发送请求
                LogUtil.log(node+" 向其他节点发送请求 ");
                for (Node nodeChild : nodeSet) {
                    if (node != nodeChild) {
                        LogUtil.log(info() + " send message to nodeChild   " + nodeChild);
                        nodeChild.getRpcReceiveRequest().addMessage(message);
                    }
                }
            }else if(message instanceof VoteResponse || message instanceof AppendEntriesResponse){
                //是回复节点的信息
                LogUtil.log(node+"  回复节点 "+remoteNode+" 的信息");
                remoteNode.getRpcReceiveRequest().addMessage(message);
            }
        }
    }


    public String info(){
        return node.toString();
    }
}
