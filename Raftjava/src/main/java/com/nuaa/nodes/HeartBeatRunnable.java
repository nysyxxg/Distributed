package com.nuaa.nodes;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wangjiuyong on 2017/7/24.
 */
public class HeartBeatRunnable implements Runnable{

    private Lock lock = new ReentrantLock();
    private final Condition heartBeatCondition;
    private Node node;
    private Message message;
    public HeartBeatRunnable(Node node,Message message){
        this.node=node;
        this.message=message;
        heartBeatCondition=lock.newCondition();

    }

    @Override public void run() {
        try {
            lock.lock();
            while (true) {
                Set<Node> nodeSet = node.getNodeSets();
                heartBeatCondition.await(10, TimeUnit.SECONDS);
                for (Node nodeChild : nodeSet) {
                    System.out.println("nodeChild   " + nodeChild);
                    if (node != nodeChild) {
                        nodeChild.handlerRPCMessage(message);
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
