package com.nuaa.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Created by wangjiuyong on 2017/7/20.
 */
public class Proposal implements Comparable<Proposal>{

    private String subject;

    private int serialId;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public  Proposal(int serialId, String subject,String name) {
        this.serialId = serialId;
        this.subject = subject;
        this.name=name;
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
            if(this.subject.equals(obj.getSubject())&&this.getSerialId()==obj.getSerialId()&&this.getName().equals(obj.getName())){
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
        return subject.hashCode()+name.hashCode()+serialId;
    }

    @Override
    public int compareTo(Proposal o) {
        return Long.compare(serialId, o.serialId);
    }

    @Override
    public String toString(){
        return JSON.toJSONString(this, SerializerFeature.DisableCircularReferenceDetect);
    }
}
