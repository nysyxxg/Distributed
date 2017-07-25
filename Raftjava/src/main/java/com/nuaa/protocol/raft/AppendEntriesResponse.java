package com.nuaa.protocol.raft;

/**
 * @author Bela Ban
 * @since  0.1
 */
public class AppendEntriesResponse extends RaftHeader {
    protected AppendResult result;

    public AppendEntriesResponse() {}
    public AppendEntriesResponse(int term, AppendResult result) {super(term); this.result=result;}


    @Override
    public String toString() {
        return super.toString() + ", result: " + result;
    }
}
