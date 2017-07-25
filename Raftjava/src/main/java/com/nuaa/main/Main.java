package com.nuaa.main;

import com.nuaa.nodes.Node;
import com.nuaa.protocol.raft.util.NodeState;

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

        NodeState.LogEntry logEntry1=new NodeState.LogEntry();
        logEntry1.setTerm(4);
        logEntry1.setIndex(21);

        NodeState.LogEntry logEntry2=new NodeState.LogEntry();
        logEntry2.setTerm(4);
        logEntry2.setIndex(21);

        NodeState.LogEntry logEntry3=new NodeState.LogEntry();
        logEntry3.setTerm(3);
        logEntry3.setIndex(2);

        Node node1=new Node();
        node1.setNodeId(1);
        node1.setNodeName("node1");
        Thread t1=new Thread(node1);
        t1.setName("node1");
        node1.getNodeState().setCurrentTerm(4);
        node1.getNodeState().addLogEntry(logEntry1);

        Node node2=new Node();
        node2.setNodeId(2);
        node2.setNodeName("node2");
        Thread t2=new Thread(node2);
        t2.setName("node2");
        node2.getNodeState().setCurrentTerm(4);
        node2.getNodeState().addLogEntry(logEntry2);

        Node node3=new Node();
        node3.setNodeId(3);
        node3.setNodeName("node3");
        Thread t3=new Thread(node3);
        t3.setName("node3");
        node3.getNodeState().setCurrentTerm(3);
        node3.getNodeState().addLogEntry(logEntry3);

        nodeSets.add(node1);
        nodeSets.add(node2);
        node1.addNode(node2);
        node1.addNode(node3);
        node2.addNode(node3);
        t1.start();
        t2.start();
        t3.start();

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
