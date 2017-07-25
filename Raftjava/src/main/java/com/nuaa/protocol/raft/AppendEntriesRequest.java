package com.nuaa.protocol.raft;

import com.alibaba.fastjson.JSON;
import com.nuaa.protocol.raft.util.NodeState;

import java.util.List;


public class AppendEntriesRequest extends RaftHeader {
    protected int prev_log_index;
    protected int prev_log_term;
    protected List<NodeState.LogEntry> entryTerms;
    protected int leader_commit;

    public AppendEntriesRequest() {
    }

    public int getPrev_log_index() {
        return prev_log_index;
    }

    public void setPrev_log_index(int prev_log_index) {
        this.prev_log_index = prev_log_index;
    }

    public int getPrev_log_term() {
        return prev_log_term;
    }

    public void setPrev_log_term(int prev_log_term) {
        this.prev_log_term = prev_log_term;
    }



    public int getLeader_commit() {
        return leader_commit;
    }

    public void setLeader_commit(int leader_commit) {
        this.leader_commit = leader_commit;
    }

    public List<NodeState.LogEntry> getEntryTerms() {
        return entryTerms;
    }

    public void setEntryTerms(List<NodeState.LogEntry> entryTerms) {
        this.entryTerms = entryTerms;
    }

    @Override public String toString() {
        return JSON.toJSONString(this);
    }
}
