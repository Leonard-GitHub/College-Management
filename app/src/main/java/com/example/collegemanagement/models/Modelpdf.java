package com.example.collegemanagement.models;

public class Modelpdf {
    String uid, id, title,description, subjectId, url;
    long timeStamp;

    public Modelpdf() {

    }

    public Modelpdf(String uid, String id, String title, String description, String subjectId, String url, long timeStamp) {
        this.uid = uid;
        this.id = id;
        this.title = title;
        this.description = description;
        this.subjectId = subjectId;
        this.url = url;
        this.timeStamp = timeStamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}


