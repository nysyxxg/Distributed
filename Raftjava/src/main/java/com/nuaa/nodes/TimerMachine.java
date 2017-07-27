package com.nuaa.nodes;

import com.nuaa.protocol.raft.VoteStatic;
import com.nuaa.protocol.raft.util.LogUtil;
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

    TimerMachine(Node node) {
        this.node = node;
        heartBeatCondition = lock.newCondition();
        //启动时将节点状态修改为Candinate,并且申请成为leader
        node.setNodeStateAction(NodeStateAction.CANDINATE_LEADER);
        node.setNodeRole(NodeStateRole.CANDIDATE);
        LogUtil.log(this.node + " modify the status to NodeStatus.CANDIDATE");
        LogUtil.log(this.node + " modify the action to NodeStateAction.CANDINATE_LEADER");
    }

    @Override public void run() {
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    lock.lock();
                    while (true) {
                        int time = timeHeartbeat;
                        if (node.getNodeRole() == NodeStateRole.CANDIDATE) {
                            //处于Leader选举状态
                            time = RandomUtils.nextInt(1, 100000) % 10 + 3;
                        } else if (node.getNodeRole() == NodeStateRole.LEADER) {
                            time = timeHeartbeat - 1;
                        }
                        LogUtil.log(node + "   await  " + time);
                        boolean result = heartBeatCondition.await(time, TimeUnit.SECONDS);
                        if (result) {
                            //在事件范围内收到信息
                            if (node.getNodeStateAction() == NodeStateAction.START_FOLLOWER) {
                                //表示在使用过程中收到心跳消息
                                LogUtil.log(node + "   receive the heartbeat Signal");
                            } else if (node.getNodeStateAction()
                                == NodeStateAction.FOLLOWER_CANDINATE) {
                                //表示在使用过程中收到心跳消息
                                LogUtil.log(node + "   receive the heartbeat Signal");
                            }
                        } else {
                            //表示等待超时
                            if (node.getNodeRole() == NodeStateRole.LEADER) {
                                //表示leader节点的心跳事件到来，需要发送心跳消息
                                node.handleEvent(NodeStateEvent.BROADCASTHEART);
                            } else if (node.getNodeStateAction() == NodeStateAction.CANDINATE_LEADER
                                && node.getNodeRole() == NodeStateRole.CANDIDATE) {
                                //表示从candidate状态到leader状态迁移中等待超时
                                LogUtil.log(node + " do not receive the VoteRequest Signal");
                                //清除上一轮的统计信息
                                node.setVoteStatic(new VoteStatic());
                                node.handleEvent(NodeStateEvent.TIMEOUT);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println(node + "   receive the Interrupted Signal");
                } finally {
                    lock.unlock();
                }
                System.out.println(node + "   exit the followerHeartBeat cycle");
            }
        }).start();
    }


    public void signal() {
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    lock.lock();
                    heartBeatCondition.signal();
                } finally {
                    lock.unlock();
                }
            }
        }).start();
    }
}
