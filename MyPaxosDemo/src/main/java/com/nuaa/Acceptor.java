package com.nuaa;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.nuaa.bean.ProPosalStatus;
import com.nuaa.bean.Promise;
import com.nuaa.bean.Proposal;
import org.apache.commons.lang3.RandomUtils;

/**
 * Created by wangjiuyong on 2017/7/20.
 */
public class Acceptor {
    private Proposal lastPrePare = new Proposal(0, null, null);
    private boolean isAccepted;
    private Proposal acceptedProposal;
    private String name;

    public Proposal getLastPrePare() {
        return lastPrePare;
    }

    public void setLastPrePare(Proposal lastPrePare) {
        this.lastPrePare = lastPrePare;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public Proposal getAcceptedProposal() {
        return acceptedProposal;
    }

    public void setAcceptedProposal(Proposal acceptedProposal) {
        this.acceptedProposal = acceptedProposal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Acceptor(String name) {
        this.name = name;
    }

    public synchronized Promise onPrepare(Proposal proposal) {
        System.out.println(
            "Thread Name" + Thread.currentThread().getName() + " " + "ACCEPTER_" + name
                + "   Para proposal " + proposal + "     lastPrePare proposal " + lastPrePare);
        //假设这个过程有50%的几率失败
        if (Math.random() - 0.5 > 0) {
            Util.printInfo(
                "Thread Name" + Thread.currentThread().getName() + " " + "ACCEPTER_" + name,
                "PREPARE", "NO RESPONSE");
            return null;
        }
        if (proposal == null)
            throw new IllegalArgumentException("null proposal");

        sleepRandom();
        if (!isAccepted) {
            //当前没有确认
            if (proposal.getSerialId() > lastPrePare.getSerialId()) {
                Promise response = new Promise(true, ProPosalStatus.PREPARE, proposal);
                lastPrePare = proposal;
                Util.printInfo(
                    "Thread Name" + Thread.currentThread().getName() + " " + "ACCEPTER_" + name,
                    "PREPARE", "OK");
                System.out.println(
                    "Thread Name" + Thread.currentThread().getName() + " " + "ACCEPTER_" + name
                        + "     current proposal " + lastPrePare);
                return response;
            } else {
                Util.printInfo(
                    "Thread Name" + Thread.currentThread().getName() + " " + "ACCEPTER_" + name,
                    "PREPARE", "REJECTED");
                return new Promise(false, ProPosalStatus.PREPARE, lastPrePare);
            }
        } else {
            //已经确认某一个提议，
            if (acceptedProposal.getName().equals(proposal.getName())) {
                //表示来自同一个Proposer的相同的Proposal
                if (acceptedProposal.getSerialId() < proposal.getSerialId()) {
                    //表示已经确认的提案的提交人已经有的更新，那么就去除已经确认，重新设置状态为PrePare
                    isAccepted = false;
                    lastPrePare = proposal;
                    acceptedProposal = null;
                    return new Promise(true, ProPosalStatus.PREPARE, proposal);
                } else {
                    return new Promise(false, ProPosalStatus.ACCESPTED, acceptedProposal);
                }
            } else {
                //当前已经确认的提案与当前的提案不是来自同一个人
                //那么就将确认的提议返回，并且保存当前最大的proposal
                lastPrePare = proposal;
                return new Promise(false, ProPosalStatus.ACCESPTED, acceptedProposal);
            }
        }
    }

    public synchronized Promise onAccept(Proposal proposal) {
        sleepRandom();
        if (isAccepted) {
            //如果已经确认，那么就返回已经确认的结果
            return new Promise(false, ProPosalStatus.ACCESPTED, acceptedProposal);
        }
        if (lastPrePare == proposal) {
            //确认提议与当前保存的提议相同，那么就返回OK
            acceptedProposal = proposal;
            isAccepted = true;
            return new Promise(true, ProPosalStatus.ACCESPTED, acceptedProposal);
        } else {
            return null;
        }
    }

    @Override public String toString() {
        return JSON.toJSONString(this, SerializerFeature.DisableCircularReferenceDetect);
    }

    private void sleepRandom() {
        int randomTime = randomTime();
        try {
            Thread.sleep(randomTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int randomTime() {
        return RandomUtils.nextInt(1, 50);
    }
}
