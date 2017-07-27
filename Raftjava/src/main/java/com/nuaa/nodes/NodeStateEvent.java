package com.nuaa.nodes;

/**
 * Created by wangjiuyong on 2017/7/24.
 */
public enum NodeStateEvent {
    //超时事件
    TIMEOUT,
    //本节点成为Leader节点
    BOCOMELEADER,
    //广播选举出新Leader节点事件
    BROADCASTLEADER,
    //广播心跳事件
    BROADCASTHEART;
}
