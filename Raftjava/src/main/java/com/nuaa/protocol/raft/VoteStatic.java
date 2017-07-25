package com.nuaa.protocol.raft;

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
}
