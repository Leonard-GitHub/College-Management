package com.example.collegemanagement.models;

public class Modelpdf {
    String uid, id, title,Description, SubjectId, Url;
    long TimeStamp;

    public Modelpdf() {
    }

    public Modelpdf(String uid, String id, String title, String description, String subjectId, String url, long timeStamp) {
        this.uid = uid;
        this.id = id;
        this.title = title;
        Description = description;
        SubjectId = subjectId;
        Url = url;
        TimeStamp = timeStamp;
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
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getSubjectId() {
        return SubjectId;
    }

    public void setSubjectId(String subjectId) {
        SubjectId = subjectId;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        TimeStamp = timeStamp;
    }
}


