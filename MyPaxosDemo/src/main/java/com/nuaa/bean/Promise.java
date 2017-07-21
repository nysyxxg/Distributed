package com.nuaa.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Created by wangjiuyong on 2017/7/20.
 */
public class Promise {

    private boolean isAcctped;
    private ProPosalStatus status;
    private Proposal proposal;

    public Promise(boolean isAcctped,ProPosalStatus status,Proposal proposal){
        this.isAcctped = isAcctped;
        this.status=status;
        this.proposal=proposal;
    }

    public ProPosalStatus getStatus() {
        return status;
    }

    public void setStatus(ProPosalStatus status) {
        this.status = status;
    }

    public Proposal getProposal() {
        return proposal;
    }

    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }

    public boolean isAcctped() {
        return isAcctped;
    }

    public void setAcctped(boolean acctped) {
        isAcctped = acctped;
    }

    @Override
    public String toString(){
        return JSON.toJSONString(this, SerializerFeature.DisableCircularReferenceDetect);
    }
}
