package com.daqin.medicinegod.entity;

import java.util.Arrays;

public class Msg {
    public int msgId;
    public String msgTo;
    public String msgToLname;
    public byte[] msgToImg;
    public String msgTime;
    public String msgLast;//最后一条消息

    public Msg() {
    }

    public Msg(int msgId, String msgTo, String msgToLname, byte[] msgToImg, String msgTime, String msgLast) {
        this.msgId = msgId;
        this.msgTo = msgTo;
        this.msgToLname = msgToLname;
        this.msgToImg = msgToImg;
        this.msgTime = msgTime;
        this.msgLast = msgLast;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "msgId=" + msgId +
                ", msgTo='" + msgTo + '\'' +
                ", msgToLname='" + msgToLname + '\'' +
                ", msgToImg=" + Arrays.toString(msgToImg) +
                ", msgTime='" + msgTime + '\'' +
                ", msgLast='" + msgLast + '\'' +
                '}';
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getMsgTo() {
        return msgTo;
    }

    public void setMsgTo(String msgTo) {
        this.msgTo = msgTo;
    }

    public String getMsgToLname() {
        return msgToLname;
    }

    public void setMsgToLname(String msgToLname) {
        this.msgToLname = msgToLname;
    }

    public byte[] getMsgToImg() {
        return msgToImg;
    }

    public void setMsgToImg(byte[] msgToImg) {
        this.msgToImg = msgToImg;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public String getMsgLast() {
        return msgLast;
    }

    public void setMsgLast(String msgLast) {
        this.msgLast = msgLast;
    }
}
