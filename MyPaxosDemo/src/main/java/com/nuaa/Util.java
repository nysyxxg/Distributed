package com.nuaa;

import com.nuaa.bean.Proposal;

/**
 * Created by wangjiuyong on 2017/7/20.
 */
public class Util {
    public static void printInfo(String subject, String operation, String result) {
        System.out.println(subject + ":" + operation + "<" + result + ">");
    }

    public  static Proposal nextProposal(Proposal proposal){
        int serialId = proposal.getSerialId()+1;
        return new Proposal(serialId,proposal.getSubject(),proposal.getName());

    }
}
