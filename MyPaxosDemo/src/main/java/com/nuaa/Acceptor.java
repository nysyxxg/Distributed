package com.nuaa;

import com.alibaba.fastjson.JSON;
import com.nuaa.bean.ProPosalStatus;
import com.nuaa.bean.Promise;
import com.nuaa.bean.Proposal;

/**
 * Created by wangjiuyong on 2017/7/20.
 */
public class Acceptor {
    private Proposal lastPrePare = new Proposal(0, null,null);
    private boolean isAccepted;
    private Proposal acceptedProposal;
    private String name;

    public Acceptor(String name) {
        this.name = name;
    }

    public Promise onPrepare(Proposal proposal) {
        //假设这个过程有50%的几率失败
        if (Math.random() - 0.5 > 0) {
            Util.printInfo("ACCEPTER_" + name, "PREPARE", "NO RESPONSE");
            return null;
        }
        if (proposal == null)
            throw new IllegalArgumentException("null proposal");

        if (!isAccepted) {
            //当前没有确认
            System.out.println(
                "ACCEPTER_" + name + "   Para proposal " + JSON.toJSONString(proposal)
                    + "     lastPrePare proposal " + JSON.toJSONString(lastPrePare));

            if (proposal.getSerialId() > lastPrePare.getSerialId()) {
                Promise response = new Promise(true, ProPosalStatus.PrePare, proposal);
                lastPrePare = proposal;
                Util.printInfo("ACCEPTER_" + name, "PREPARE", "OK");
                System.out.println(
                    "ACCEPTER_" + name + "     current proposal " + JSON.toJSONString(lastPrePare));
                return response;
            } else {
                Util.printInfo("ACCEPTER_" + name, "PREPARE", "REJECTED");
                return new Promise(false, ProPosalStatus.PrePare, lastPrePare);
            }
        } else {
            //已经确认某一个提议，那么就将确认的提议返回，并且保存当前最大的proposal
            lastPrePare = proposal;
            return new Promise(false, ProPosalStatus.Accespted, acceptedProposal);
        }
    }

    public Promise onAccept(Proposal proposal) {
        //假设这个过程有50%的几率失败
        /*if (Math.random() - 0.5 > 0) {
            Util.printInfo("ACCEPTER_" + name, "PREPARE", "NO RESPONSE");
            return false;
        }*/
        if (isAccepted) {
            //如果已经确认，那么就返回已经确认的结果
            return new Promise(false, ProPosalStatus.Accespted, acceptedProposal);
        }
        if (lastPrePare.getSerialId() == proposal.getSerialId()) {
            //确认提议与当前保存的提议相同，那么就进行确认，
            acceptedProposal = proposal;
            isAccepted = true;
            return new Promise(true, ProPosalStatus.Accespted, acceptedProposal);
        } else {
            return null;
        }
    }
}
