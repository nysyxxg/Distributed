package com.nuaa.protocol.raft.util;

import com.nuaa.nodes.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wangjiuyong on 2017/7/25.
 */
public class NodeState {
    //当前节点所处在的term
    private int currentTerm;
    //本节点的最高index,只在节点类型为Leader时生效
    private int index;
    //投票给节点的ID
    private int voteFor;
    //所有已经执行操作的entries
    private List<LogEntry> logEntries;
    //在所有的状态机中已知Commit的最大的commitIndex
    private int commitLogIndex;
    //在所有的状态机中已知Commit的对应的Term
    private int commitLogTerm;
    //在所有已知的状态机中，在本节点上已经执行的commitIndex
    private int lastApplied;
    //剩余Follower节点上的Entry信息
    private HashMap<Node,NodeAppliedIndexInfo>  nodeSendEntry;
    //leader节点
    private Node LeaderNode;

    public NodeState(){
        logEntries=new ArrayList<>();
        nodeSendEntry=new HashMap<>();
    }

    public NodeState(List<LogEntry> logEntries){
        if(null!=logEntries&&logEntries.size()>0){
            //获取本地已经执行commit的最高index
            LogEntry logEntry =logEntries.get(logEntries.size()-1);
            lastApplied=logEntry.getIndex();
        }
    }

    public LogEntry getlastLogEntry(){
        if(null!=logEntries&&logEntries.size()>0){
            return logEntries.get(logEntries.size()-1);
        }else {
            return new LogEntry();
        }
    }


    public void addLogEntry(LogEntry LogEntry){
        logEntries.add(LogEntry);
    }
    public Node getLeaderNode() {
        return LeaderNode;
    }

    public void setLeaderNode(Node leaderNode) {
        LeaderNode = leaderNode;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getVoteFor() {
        return voteFor;
    }

    public void setVoteFor(int voteFor) {
        this.voteFor = voteFor;
    }

    public List<LogEntry> getLogEntries() {
        return logEntries;
    }

    public void setLogEntries(List<LogEntry> logEntries) {
        this.logEntries = logEntries;
    }

    public int getLastApplied() {
        return lastApplied;
    }

    public void setLastApplied(int lastApplied) {
        this.lastApplied = lastApplied;
    }

    public HashMap<Node, NodeAppliedIndexInfo> getNodeSendEntry() {
        return nodeSendEntry;
    }

    public void setNodeSendEntry(HashMap<Node, NodeAppliedIndexInfo> nodeSendEntry) {
        this.nodeSendEntry = nodeSendEntry;
    }

    public int getCurrentTerm() {
        return currentTerm;
    }

    public void setCurrentTerm(int currentTerm) {
        this.currentTerm = currentTerm;
    }

    public int getCommitLogIndex() {
        return commitLogIndex;
    }

    public void setCommitLogIndex(int commitLogIndex) {
        this.commitLogIndex = commitLogIndex;
    }

    public int getCommitLogTerm() {
        return commitLogTerm;
    }

    public void setCommitLogTerm(int commitLogTerm) {
        this.commitLogTerm = commitLogTerm;
    }



    public static class NodeAppliedIndexInfo {
        private int nextIndex;
        private int matchedIdex;

        public int getNextIndex() {
            return nextIndex;
        }

        public void setNextIndex(int nextIndex) {
            this.nextIndex = nextIndex;
        }

        public int getMatchedIdex() {
            return matchedIdex;
        }

        public void setMatchedIdex(int matchedIdex) {
            this.matchedIdex = matchedIdex;
        }
    }

    public static class LogEntry {
        private int term;
        private int index;
        private String command;

        public int getTerm() {
            return term;
        }

        public void setTerm(int term) {
            this.term = term;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }
    }
}
