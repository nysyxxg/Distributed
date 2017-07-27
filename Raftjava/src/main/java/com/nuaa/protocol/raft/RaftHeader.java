package com.nuaa.protocol.raft;

/**
 * @author Bela Ban
 * @since  0.1
 */
public abstract class RaftHeader {
    protected int term;
    private int candidateId;

    public RaftHeader() {}
    public RaftHeader(int term) {this.term=term;}

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }
}
