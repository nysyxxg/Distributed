package com.nuaa.nodes;

/**
 * Created by wangjiuyong on 2017/7/27.
 */
public enum NodeStateAction {
    //节点刚刚启动是成为follower
    START_FOLLOWER,
    //从follower申请成为CANDINATE
    FOLLOWER_CANDINATE,
    //从CANDINATE申请成为Leader
    CANDINATE_LEADER,
    //从CANDINATE下降为FOLLOW
    LEADER_FOLLOW;
}
