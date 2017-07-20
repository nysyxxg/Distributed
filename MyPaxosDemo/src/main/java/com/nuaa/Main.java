package com.nuaa;

import com.nuaa.bean.Proposal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wangjiuyong on 2017/7/20.
 */
public class Main {

    private static final String[] PROPOSALS = {"ProjectA", "ProjectB", "ProjectC"};

    public static void main(String[] args){
        List<Acceptor> acceptors = new ArrayList<Acceptor>();
        Arrays.asList("A", "B", "C", "D", "E")
            .forEach(name -> acceptors.add(new Acceptor(name)));

        new Proposer().vote(new Proposal(1, "ProjectA"), acceptors);
    }

}
