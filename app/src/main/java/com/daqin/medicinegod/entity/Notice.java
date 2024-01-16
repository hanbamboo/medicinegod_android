package com.daqin.medicinegod.entity;

public class Notice {
    private int notice_id;
    private String notice_title;
    private String notice_context;
    private String notice_from;
    private String notice_to;
    private String notice_time;
    private String notice_check;
    private String notice_lname;

    public Notice() {

    }

    public Notice(int notice_id,
                  String notice_title,
                  String notice_context,
                  String notice_from,
                  String notice_to,
                  String notice_time,
                  String notice_check,
                  String notice_lname) {
        this.notice_id = notice_id;
        this.notice_title = notice_title;
        this.notice_context = notice_context;
        this.notice_from = notice_from;
        this.notice_to = notice_to;
        this.notice_time = notice_time;
        this.notice_check = notice_check;
        this.notice_lname = notice_lname;
    }

    @Override
    public String toString() {
        return "Notice{" +
                "notice_id=" + notice_id +
                ", notice_title='" + notice_title + '\'' +
                ", notice_context='" + notice_context + '\'' +
                ", notice_from='" + notice_from + '\'' +
                ", notice_to='" + notice_to + '\'' +
                ", notice_time='" + notice_time + '\'' +
                ", notice_check='" + notice_check + '\'' +
                ", notice_lname='" + notice_lname + '\'' +
                '}';
    }

    public int getNotice_id() {
        return notice_id;
    }

    public void setNotice_id(int notice_id) {
        this.notice_id = notice_id;
    }

    public String getNotice_title() {
        return notice_title;
    }

    public void setNotice_title(String notice_title) {
        this.notice_title = notice_title;
    }

    public String getNotice_context() {
        return notice_context;
    }

    public void setNotice_context(String notice_context) {
        this.notice_context = notice_context;
    }

    public String getNotice_from() {
        return notice_from;
    }

    public void setNotice_from(String notice_from) {
        this.notice_from = notice_from;
    }

    public String getNotice_to() {
        return notice_to;
    }

    public void setNotice_to(String notice_to) {
        this.notice_to = notice_to;
    }

    public String getNotice_time() {
        return notice_time;
    }

    public void setNotice_time(String notice_time) {
        this.notice_time = notice_time;
    }

    public String getNotice_check() {
        return notice_check;
    }

    public void setNotice_check(String notice_check) {
        this.notice_check = notice_check;
    }

    public String getNotice_lname() {
        return notice_lname;
    }

    public void setNotice_lname(String notice_lname) {
        this.notice_lname = notice_lname;
    }
}
