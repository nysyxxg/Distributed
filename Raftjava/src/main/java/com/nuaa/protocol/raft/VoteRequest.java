package com.nuaa.protocol.raft;

import com.alibaba.fastjson.JSON;

/**
 * @author Bela Ban
 * @since 0.1
 */
public class VoteRequest extends RaftHeader {
    protected int last_log_term;
    protected int last_log_index;


    public VoteRequest() {
    }


    public int getLast_log_term() {
        return last_log_term;
    }

    public void setLast_log_term(int last_log_term) {
        this.last_log_term = last_log_term;
    }

    public int getLast_log_index() {
        return last_log_index;
    }

    public void setLast_log_index(int last_log_index) {
        this.last_log_index = last_log_index;
    }

    public String toString() {
        return JSON.toJSONString(this);
    }
}
