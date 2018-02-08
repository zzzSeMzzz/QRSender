package ru.sem.qrsender.mvp.model;

import java.util.ArrayDeque;

/**
 * Created by Admin on 27.01.2018.
 */

public class QRContainer {

    private String hash;

    private ArrayDeque<String> qrs;

    private int sendCount;

    public int getSendCount() {
        return sendCount;
    }

    public void setSendCount(int sendCount) {
        this.sendCount = sendCount;
    }

    public int incSendCount(){
        sendCount++;
        return sendCount;
    }

    public QRContainer(String hash) {
        this.hash = hash;
        qrs = new ArrayDeque<>();
    }

    public void addFirst(String qr){
        qrs.push(qr);
    }

    public int getCountQue(){
        return qrs.size();
    }

    public String getFirst(){
        return qrs.getFirst();
    }

    public String getLast(){
        return qrs.getLast();
    }

    public ArrayDeque<String> getQrs() {
        return qrs;
    }
}
