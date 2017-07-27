package com.nuaa.protocol.raft.util;

import java.util.Date;

/**
 * Created by wangjiuyong on 2017/7/27.
 */
public class LogUtil {
    public static void log(Object content){
        System.out.println(Thread.currentThread().getName()+" "+ (new Date()).toString()+"    "+content);
    }
}
