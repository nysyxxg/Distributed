package com.nuaa.nodes;

import com.nuaa.protocol.raft.RaftHeader;
import com.nuaa.protocol.raft.VoteResponse;
import com.nuaa.protocol.raft.util.NodeState;

import java.util.concurrent.Callable;

/**
 * Created by wangjiuyong on 2017/7/27.
 */
public class VoteRequestHandler implements Callable<VoteResponse> {

    private Node  node;
    private RaftHeader message;

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public RaftHeader getMessage() {
        return message;
    }

    public void setMessage(RaftHeader message) {
        this.message = message;
    }

    @Override public VoteResponse call() throws Exception {
        NodeState nodeState = node.getNodeState();
        com.nuaa.protocol.raft.VoteRequest voteRequest = (com.nuaa.protocol.raft.VoteRequest) message;
        NodeState.LogEntry localLogEntry = nodeState.getlastLogEntry();
        if (voteRequest.getCandidateId() == node.getNodeId()) {
            //如果是相同的节点，则表示成功
            return new VoteResponse(nodeState.getCurrentTerm(), true);
        } else {
            if (localLogEntry.getTerm() > voteRequest.getLast_log_term()) {
                //如果本节点的term大于消息中包含的term,则返回false
                return new VoteResponse(nodeState.getCurrentTerm(), false);
            } else if (localLogEntry.getTerm() == voteRequest.getLast_log_term()
                && localLogEntry.getIndex() > voteRequest.getLast_log_index()) {
                //如果term相同，本节点的index大于消息中的inex,返回false
                return new VoteResponse(nodeState.getCurrentTerm(), false);
            } else {
                //如果本节点的term和index都不大于消息中包含的信息，那么就返回true
                nodeState.setVoteFor(voteRequest.getCandidateId());
                return new VoteResponse(nodeState.getCurrentTerm(), true);
            }
        }
    }
}
