package com.nuaa.protocol.raft;

import com.alibaba.fastjson.JSON;

/**
 * Created by wangjiuyong on 2017/7/25.
 */
public class VoteStatic {
    private int maxTerm;
    private int successCount=0;

    public int getMaxTerm() {
        return maxTerm;
    }

    public void setMaxTerm(int maxTerm) {
        this.maxTerm = maxTerm;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    @Override
    public String toString(){
        return JSON.toJSONString(this);
    }
}
