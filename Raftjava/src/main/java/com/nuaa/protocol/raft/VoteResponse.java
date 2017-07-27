package com.nuaa.protocol.raft;

import com.alibaba.fastjson.JSON;

import java.io.DataInput;
import java.io.DataOutput;

/**
 * @author Bela Ban
 * @since  0.1
 */
public class VoteResponse extends RaftHeader {
    protected boolean result;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public VoteResponse() {}

    public VoteResponse(int term, boolean result) {super(term); this.result=result;}

    public String toString() {
        return JSON.toJSONString(this);
    }
}
