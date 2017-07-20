package com.nuaa;

import com.alibaba.fastjson.JSON;
import com.nuaa.bean.ProPosalStatus;
import com.nuaa.bean.Promise;
import com.nuaa.bean.Proposal;

import java.util.*;

/**
 * Created by wangjiuyong on 2017/7/20.
 */
public class Proposer {
    private Proposal proposal;
    private static int round = 0;
    private Proposal acceptedProposal;

    public Proposal getProposal() {
        return proposal;
    }

    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }

    public void vote(Proposal proposal, Collection<Acceptor> acceptors) {
        int halfCount = ((int) acceptors.size() / 2) + 1;
        while (true) {
            System.out.println("round   " + (round++) + "   " + JSON.toJSONString(proposal));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<Acceptor> onPrepareSuccess = new ArrayList<Acceptor>();
            HashMap<Proposal, Integer> ProposalCount = new HashMap<>();
            for (Acceptor acceptor : acceptors) {
                Promise prepareResult = acceptor.onPrepare(proposal);
             /*   Promise hasBeenprepareResult = acceptor.onPrepare(proposal);*/
                if (prepareResult != null) {
                    if (prepareResult.isAcctped()) {
                        //决策者已经接受该提议
                        onPrepareSuccess.add(acceptor);
                    } else {
                        //决策者拒绝了该提议，
                        if (prepareResult.getStatus() == ProPosalStatus.Accespted) {
                            //表示该节点已经确认了某一个提案，将其保存下来
                            Proposal acceptedProposal = prepareResult.getProposal();
                            if (null != acceptedProposal
                                && acceptedProposal.getSerialId() < acceptedProposal
                                .getSerialId()) {
                                //表明当前已经知道的提案比已经确认的提案要小
                                acceptedProposal = acceptedProposal;
                            }
                            int count = 1;
                            if (ProposalCount.containsKey(acceptedProposal)) {
                                count = ProposalCount.get(acceptedProposal) + 1;
                            }
                            ProposalCount.put(acceptedProposal, count);
                        }
                        if (prepareResult.getProposal().getSerialId() > proposal.getSerialId()) {
                            proposal.setSerialId(prepareResult.getProposal().getSerialId() + 1);
                            break;
                        }
                    }
                }
            }
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
            }else{
                //在prePare阶段有超过一半的投票
                continuePrePare=false;
            }
            if (existVote) {
                //已经找到当前达成一致的提案
                break;
            } else if (continuePrePare) {
                //继续投票
                continue;
            }

            List<Acceptor> onAcceptSuccess = new ArrayList<Acceptor>();

            for (Acceptor acceptor : acceptors) {
                Promise acceptorResult = acceptor.onAccept(proposal);
                if (null != acceptorResult) {
                    if (acceptor.onAccept(proposal).isAcctped()) {
                        onAcceptSuccess.add(acceptor);
                    }
                }
            }
            if (onPrepareSuccess.size() < ((int) acceptors.size() / 2) + 1) {
                proposal = Util.nextProposal(proposal);
                continue;
            } else {
                break;
            }
        }

        System.out.println(proposal.getSubject() + "has accepted");

    }
}
