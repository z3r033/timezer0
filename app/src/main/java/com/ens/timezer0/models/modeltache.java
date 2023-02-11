package com.ens.timezer0.models;

public class modeltache {
    private Long tache_id ; //id
    private String creatortache_id ;
    private String projecttache_id;
    private String heading ;
    private  String message;
    private String date;
    private String time ;
    private String impo ;

    public String getProjecttache_id() {
        return projecttache_id;
    }

    public void setProjecttache_id(String projecttache_id) {
        this.projecttache_id = projecttache_id;
    }

    public Long getTache_id() {
        return tache_id;
    }

    public void setTache_id(Long tache_id) {
        this.tache_id = tache_id;
    }

    public String getCreatortache_id() {
        return creatortache_id;
    }

    public void setCreatortache_id(String creatortache_id) {
        this.creatortache_id = creatortache_id;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImpo() {
        return impo;
    }

    public void setImpo(String impo) {
        this.impo = impo;
    }
}
