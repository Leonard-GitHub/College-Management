package com.example.collegemanagement.models;

public class ModelSubject {

    String id, Subject, imageurl, uid;
    long timestamp;

    public ModelSubject() {
    }

    public ModelSubject(String id, String subject, String imageurl, String uid, long timestamp) {
        this.id = id;
        Subject = subject;
        this.imageurl = imageurl;
        this.uid = uid;
        this.timestamp = timestamp;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
