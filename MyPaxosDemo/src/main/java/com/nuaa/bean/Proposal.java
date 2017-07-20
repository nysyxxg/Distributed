package com.nuaa.bean;

/**
 * Created by wangjiuyong on 2017/7/20.
 */
public class Proposal {

    private String subject;

    private int serialId;

    public  Proposal(int serialId, String subject) {
        this.serialId = serialId;
        this.subject = subject;
    }


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getSerialId() {
        return serialId;
    }

    public void setSerialId(int serialId) {
        this.serialId = serialId;
    }


    @Override
    public boolean equals(Object o){
        if(this==o){
            return true;
        }
        if(o instanceof Proposal){
            Proposal obj =(Proposal)o;
            if(this.subject.equals(obj.getSubject())&&this.getSerialId()==obj.getSerialId()){
                return true;
            }
            else{
                return false;
            }
        }
        return false;
    }

    @Override
    public int hashCode(){
        return subject.hashCode()+serialId;
    }
}
