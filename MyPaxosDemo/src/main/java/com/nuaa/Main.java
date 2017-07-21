package com.nuaa;

import com.nuaa.bean.Proposal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wangjiuyong on 2017/7/20.
 */
public class Main {

    private static final String[] PROPOSALS = {"ProjectA", "ProjectB", "ProjectC"};

    public static void main(String[] args) {



        ExecutorService es = Executors.newFixedThreadPool(10);
        int round = 0;
        while (round++ < 100) {
            CountDownLatch latch = new CountDownLatch(3);//两个工人的协作
            System.out.println("    round   " + round);
            List<Acceptor> acceptors = initial();
            //initialCon(acceptors);
            int i = 1;
            for (String subject : PROPOSALS) {
                Proposer proposer = new Proposer(latch);
                String name = "proposer" + (i++);
                proposer.setName(name);
                proposer.setAcceptors(acceptors);
                proposer.setProposal(new Proposal(1, subject, name));
                es.submit(proposer);
            }
            try {
                //等待所有工人完成工作
                latch.await();
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        es.shutdown();
    }

    private static List<Acceptor> initial() {
        List<Acceptor> acceptors = new ArrayList<Acceptor>();
        Arrays.asList("A", "B", "C", "D", "E").forEach(name -> acceptors.add(new Acceptor(name)));
        return acceptors;
    }

    private static void initialCon(List<Acceptor> acceptors) {
        //预置条件，5个决策者中分别有两个人同时确认不同的提案
        Proposal proposal1 = new Proposal(3, "ProjectA", "proposer1");
        Proposal proposal2 = new Proposal(4, "ProjectB", "proposer2");
        Proposal proposal3 = new Proposal(5, "ProjectC", "proposer3");

        Acceptor AcceptorA = acceptors.get(0);
        AcceptorA.setAccepted(true);
        AcceptorA.setAcceptedProposal(proposal3);
        AcceptorA.setLastPrePare(proposal1);

        Acceptor AcceptorB = acceptors.get(1);
        AcceptorB.setAccepted(true);
        AcceptorB.setAcceptedProposal(proposal2);
        AcceptorB.setLastPrePare(proposal2);

        Acceptor AcceptorC = acceptors.get(2);
        AcceptorC.setAccepted(true);
        AcceptorC.setAcceptedProposal(proposal3);
        AcceptorC.setLastPrePare(proposal2);

        Acceptor AcceptorD = acceptors.get(3);
        AcceptorD.setAccepted(true);
        AcceptorD.setAcceptedProposal(proposal2);
        AcceptorD.setLastPrePare(proposal2);
    }

}
