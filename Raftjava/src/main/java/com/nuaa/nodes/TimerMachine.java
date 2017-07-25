package com.nuaa.nodes;

import com.nuaa.protocol.raft.RaftHeader;
import com.nuaa.protocol.raft.VoteRequest;
import org.apache.commons.lang3.RandomUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 定时器，用于处理超时机制
 * Created by wangjiuyong on 2017/7/24.
 */
public class TimerMachine implements Runnable {
    private final Condition heartBeatCondition;
    private Lock lock = new ReentrantLock();
    private Node node;
    private int timeHeartbeat = 10;
    private RaftHeader message;

    TimerMachine(Node node) {
        this.node = node;
        heartBeatCondition = lock.newCondition();
    }

    @Override public void run() {
        lock.lock();
        try {
            while (true) {
                int time = timeHeartbeat;
                if (node.getNodeStatus() == NodeRole.CANDIDATE) {
                    //处于Leader选举状态
                    time = RandomUtils.nextInt(1, 100000) % 10 + 3;
                }
                System.out.println(Thread.currentThread().getName() + "   await  " + time);
                boolean result = heartBeatCondition.await(time, TimeUnit.SECONDS);
                if (result) {
                    if (node.getPhase() != Phase.INITIAL) {
                        //表示在使用过程中收到心跳消息
                        System.out.println(
                            Thread.currentThread().getName() + "   receive the heartbeat Signal");
                    }
                } else {
                    //表示等待超时
                    if (node.getPhase() != Phase.INITIAL) {
                        //表示在使用过程中心跳超时,
                    } else {
                        //表示没有主节点，那么就是初始节点选取
                        System.out.println(Thread.currentThread().getName()
                            + "   do not receive the heartbeat Signal. and modify the status to NodeStatus.CANDIDATE");
                        node.setNodeStatus(NodeRole.CANDIDATE);
                        //在Leader选择状态中被中断，表示没有收到了其他的接地啊的Vote请求或者
                        node.handleCandidateTimeOut();
                    }
                }
            }
        } catch (InterruptedException e) {
            System.out
                .println(Thread.currentThread().getName() + "   receive the Interrupted Signal");
        } finally {
            lock.unlock();
        }
        System.out
            .println(Thread.currentThread().getName() + "   exit the followerHeartBeat cycle");
    }

    public void receiveHeartBeat(RaftHeader meesage) {
        lock.lock();
        try {
            this.message = meesage;
            handlerMessage(meesage);
        } finally {
            lock.unlock();
        }
    }

    private void handlerMessage(RaftHeader meesage) {
        if (node.getPhase() == Phase.INITIAL) {
            if (message instanceof VoteRequest) {
                //在Leader选择状态中被中断，表示收到了其他的接地啊的Vote请求或者,那么就重新计算时间
                heartBeatCondition.signal();
            }
        }
    }
}
