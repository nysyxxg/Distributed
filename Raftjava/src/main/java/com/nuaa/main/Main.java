package com.nuaa.main;

import com.nuaa.nodes.Node;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wangjiuyong on 2017/7/24.
 */
public class Main {
    public static void main(String[] args) {

        Set<Node> nodeSets = new HashSet<Node>();

        Node node1=new Node();
        node1.setNodeId("1");
        node1.setNodeName("node1");
        Thread t1=new Thread(node1);
        t1.setName("node1");

        Node node2=new Node();
        node2.setNodeId("2");
        node2.setNodeName("node2");
        Thread t2=new Thread(node2);
        t2.setName("node2");

        Node node3=new Node();
        node3.setNodeId("3");
        node3.setNodeName("node3");
        Thread t3=new Thread(node3);
        t3.setName("node2");

        nodeSets.add(node1);
        nodeSets.add(node2);
        node1.addNode(node2);
        ExecutorService es = Executors.newCachedThreadPool();
        t1.start();
        t2.start();
/*        es.submit(t1);
        es.submit(t2);*/
        /*node1.addNode(node3);
        node2.addNode(node3);*/




/*

        HeartBeatRunnable r = new HeartBeatRunnable();
        ExecutorService es = Executors.newCachedThreadPool();
        Thread t = new Thread(r);
        es.submit(t);

        try {
            Thread.sleep(100);
            r.receiveHeartBeat();
            Thread.sleep(100);
            r.receiveHeartBeat();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        es.shutdown();*/
    }
}
