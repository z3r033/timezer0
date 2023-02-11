package com.ens.timezer0.models;

public class modelproject {
    Long project_id;

    String project_name ;
    String creator_name;

    public Long getProject_id() {
        return project_id;
    }

    public void setProject_id(Long project_id) {
        this.project_id = project_id;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getCreator_name() {
        return creator_name;
    }

    public void setCreator_name(String creator_id) {
        this.creator_name = creator_id;
    }
}
