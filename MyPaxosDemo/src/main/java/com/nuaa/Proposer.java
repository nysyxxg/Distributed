package com.nuaa;

import com.alibaba.fastjson.JSON;
import com.nuaa.bean.ProPosalStatus;
import com.nuaa.bean.Promise;
import com.nuaa.bean.Proposal;

import java.util.*;

/**
 * Created by wangjiuyong on 2017/7/20.
 */
public class Proposer implements Runnable{
    private Proposal proposal;
    private int round = 0;
    private Proposal acceptedProposal;
    private String name;
    private List<Acceptor> acceptors;

    public  int getRound() {
        return round;
    }

    public  void setRound(int round) {
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

    private void info(){
        System.out.println( JSON.toJSONString(this));
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
            System.out.println(name + " round   " + (round) + "   " + JSON.toJSONString(proposal));
            List<Acceptor> onPrepareSuccess = new ArrayList<Acceptor>();
            HashMap<Proposal, Integer> ProposalCount = new HashMap<>();
            for (Acceptor acceptor : acceptors) {
                Promise prepareResult = acceptor.onPrepare(proposal);
                if (prepareResult != null) {
                    System.out.println(name + " round   " + (round) + "   " + JSON.toJSONString(prepareResult));
                    if (prepareResult.isAcctped()) {
                        //决策者已经接受该提议
                        onPrepareSuccess.add(acceptor);
                    } else {
                        //决策者拒绝了该提议，
                        if (prepareResult.getStatus() == ProPosalStatus.Accespted) {
                            //表示该节点已经确认了某一个提案，将其保存下来
                            Proposal acceptedAcceptorProposal = prepareResult.getProposal();
                            if (null != acceptedProposal
                                && acceptedProposal.getSerialId() < acceptedAcceptorProposal
                                .getSerialId()) {
                                //表明当前已经知道的提案比已经确认的提案要小
                                acceptedProposal = acceptedAcceptorProposal;
                            }
                            int count = 1;
                            if (ProposalCount.containsKey(acceptedAcceptorProposal)) {
                                count = ProposalCount.get(acceptedAcceptorProposal) + 1;
                            }
                            ProposalCount.put(acceptedAcceptorProposal, count);
                        }else if (prepareResult.getProposal().getSerialId() > proposal.getSerialId()) {
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

        System.out.println(name + " "+proposal.getSubject() + "has accepted");


    }
}
