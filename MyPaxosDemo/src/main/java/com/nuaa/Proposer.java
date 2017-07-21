package com.nuaa;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.nuaa.bean.ProPosalStatus;
import com.nuaa.bean.Promise;
import com.nuaa.bean.Proposal;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Created by wangjiuyong on 2017/7/20.
 */
public class Proposer implements Runnable {
    private Proposal proposal;
    private int round = 0;
    private Proposal acceptedProposal;
    private String name;
    private List<Acceptor> acceptors;
    CountDownLatch latch;
    Proposer(CountDownLatch latch){
        this.latch=latch;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
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

    public Proposal getProposal() {
        return proposal;
    }

    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }

    private void info() {
        System.out.println(
            "Thread Name" + Thread.currentThread().getName() + " " + this);
    }

    public List<Acceptor> getAcceptors() {
        return acceptors;
    }

    public void setAcceptors(List<Acceptor> acceptors) {
        this.acceptors = acceptors;
    }

    @Override public void run() {
        int halfCount = ((int) acceptors.size() / 2) + 1;
        while (true) {
            round++;
            System.out.println(
                "Thread Name" + Thread.currentThread().getName() + " " + name + " round   "
                    + (round) + "   " + proposal);
            List<Acceptor> onPrepareSuccess = new ArrayList<Acceptor>();
            HashMap<Proposal, Integer> ProposalCount = new HashMap<>();
            for (Acceptor acceptor : acceptors) {
                Promise prepareResult = acceptor.onPrepare(proposal);
                if (prepareResult != null) {
                    if (prepareResult.isAcctped()) {
                        System.out.println(
                            "Thread Name" + Thread.currentThread().getName() + " " + prepareResult + "   accepted by " + acceptor);
                        //决策者已经接受该提议
                        onPrepareSuccess.add(acceptor);
                    } else {
                        System.out.println(
                            "Thread Name" + Thread.currentThread().getName() + " " + prepareResult + "   refused by " + acceptor);
                        //决策者拒绝了该提议，
                        if (prepareResult.getStatus() == ProPosalStatus.ACCESPTED) {
                            //表示该节点已经确认了某一个提案，将其保存下来
                            System.out.println(
                                "Thread Name" + Thread.currentThread().getName() + " find one accepted Proposal " + prepareResult+" and  save it ");
                            Proposal acceptedAcceptorProposal = prepareResult.getProposal();
/*                            if (proposal.getSerialId() <= acceptedAcceptorProposal.getSerialId()) {
                                System.out.println(
                                    "Thread Name" + Thread.currentThread().getName() + " current serial is less accepted proposal,update the serial ID ");
                                //表明当前正在提交的提案比已经确认的提案要小,那么将当前提案的序列增加1
                                proposal.setSerialId(acceptedAcceptorProposal.getSerialId() + 1);
                            }*/
                            int count = 1;
                            if (ProposalCount.containsKey(acceptedAcceptorProposal)) {
                                count = ProposalCount.get(acceptedAcceptorProposal) + 1;
                            }
                            ProposalCount.put(acceptedAcceptorProposal, count);
                        } else if (prepareResult.getProposal().getSerialId() >= proposal
                            .getSerialId()) {
                            //当前决策者的提案大于本client的提案
                            proposal.setSerialId(prepareResult.getProposal().getSerialId() + 1);
                            break;
                        }
                    }
                }
            }
            info();
            boolean existVote = false;
            boolean continuePrePare = true;
            if (onPrepareSuccess.size() < halfCount) {
                //在prePare阶段没有超过一半的投票
                proposal = Util.nextProposal(proposal);
                for (Map.Entry<Proposal, Integer> entry : ProposalCount.entrySet()) {
                    if (entry.getValue() >= halfCount) {
                        //表明该提案已经超过一般人同意
                        proposal = entry.getKey();
                        existVote = true;
                        break;
                    }
                }
            } else {
                //在prePare阶段有超过一半的投票
                continuePrePare = false;
            }
            if (existVote) {
                //已经找到当前达成一致的提案
                break;
            } else if (continuePrePare) {
                //继续投票
                continue;
            }

            List<Acceptor> onAcceptSuccess = new ArrayList<>();

            for (Acceptor acceptor : acceptors) {
                Promise acceptorResult = acceptor.onAccept(proposal);
                if (null != acceptorResult) {
                    if (acceptorResult.isAcctped()) {
                        System.out.println(
                            "Thread Name" + Thread.currentThread().getName() + " onAccept success" + proposal);
                        onAcceptSuccess.add(acceptor);
                    }
                }
            }
            System.out.println(
                "Thread Name" + Thread.currentThread().getName() + " Size " + onAcceptSuccess.size() + " onAcceptSuccess " + onAcceptSuccess);
            if (onAcceptSuccess.size() < halfCount) {
                proposal = Util.nextProposal(proposal);
                continue;
            } else {
                proposal = onAcceptSuccess.get(0).getAcceptedProposal();
                break;
            }
        }

        System.out.println(
            "Thread Name" + Thread.currentThread().getName() + " " + name + " " + proposal.getSubject() + " has accepted ");
        latch.countDown();
    }

    @Override
    public String toString(){
        return JSON.toJSONString(this, SerializerFeature.DisableCircularReferenceDetect);
    }
}
