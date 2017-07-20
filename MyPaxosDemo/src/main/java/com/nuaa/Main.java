package com.nuaa;

import com.nuaa.bean.Proposal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wangjiuyong on 2017/7/20.
 */
public class Main {

    private static final String[] PROPOSALS = {"ProjectA", "ProjectB", "ProjectC"};

    public static void main(String[] args){
        List<Acceptor> acceptors = new ArrayList<Acceptor>();
        Arrays.asList("A", "B", "C", "D", "E")
            .forEach(name -> acceptors.add(new Acceptor(name)));

        List<Proposer> proposers = new ArrayList<Proposer>();
        int i=1;
        ExecutorService es = Executors.newFixedThreadPool(10);
        for(String subject:PROPOSALS) {
            Proposer proposer=new Proposer();
            String name="proposer"+(i++);
            proposer.setName(name);
            proposer.setAcceptors(acceptors);
            proposer.setProposal(new Proposal(1, subject,name));
            es.submit(proposer);
        }
    }

}
