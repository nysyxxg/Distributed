package com.nuaa.nodes;

import org.apache.commons.lang3.RandomUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wangjiuyong on 2017/7/24.
 */
public class TimerRunnable implements Runnable {
    private final Condition heartBeatCondition;
    private Lock lock = new ReentrantLock();
    private Node node;
    private int timeHeartbeat = 10;

    TimerRunnable(Node node){
        this.node=node;
        heartBeatCondition = lock.newCondition();
    }

    @Override public void run() {
        lock.lock();
        boolean exit=false;
        try {
            while (!exit && !Thread.currentThread().isInterrupted()) {
                int time = timeHeartbeat;
                if(node.getNodeStatus()==NodeStatus.CANDIDATE) {
                    //处于Leader选举状态
                    time = RandomUtils.nextInt(1, 10);
                }
                System.out.println(Thread.currentThread().getName()+"   await  " + time);
                boolean result = heartBeatCondition.await(time, TimeUnit.SECONDS);
                if (result) {
                    if(node.getNodeStatus()==NodeStatus.CANDIDATE){
                        //在Leader选择状态中被中断，表示收到了其他的接地啊的Vote请求或者
                    }else{

                    }
                    System.out.println(Thread.currentThread().getName()+"   receive the heartbeat Signal,enter the next listener");
                } else {
                    //表示等待超时
                    System.out.println(Thread.currentThread().getName()+"   do not receive the heartbeat Signal. and modify the status to ");
                    if(node.getNodeStatus()==NodeStatus.CANDIDATE){
                        //在Leader选择状态中被中断，表示收到了其他的接地啊的Vote请求或者
                        node.handleCandidateTimeOut();
                    }
                }
            }
        } catch (InterruptedException e) {
            exit=true;
            System.out.println(Thread.currentThread().getName()+"   receive the Interrupted Signal");
        } finally {
            lock.unlock();
        }
        System.out.println(Thread.currentThread().getName()+"   exit the followerHeartBeat cycle");
    }

    public void receiveHeartBeat() {
        lock.lock();
        try {
            heartBeatCondition.signal();
        } finally {
            lock.unlock();
        }
    }
}
