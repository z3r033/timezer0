package com.ens.timezer0;

public class UrlsGlobal {

    // mqtt url :
    public static String urlmqtt = "tcp://10.0.0.1:1883";
    //url for project
    public static String ajouterProject= "http://10.0.0.1:8080/projects/";
    public static String getProjects = "http://10.0.0.1:8080/projects";
    public static String deleteproject="http://10.0.0.1:8080/projects/";
    public static String getnotinyourlistuserproject = "http://10.0.0.1:8080/projectusers/in";
    public static String ajouternvuserproject ="http://10.0.0.1:8080/projectusers/notin";
    public static String getuserbyid ="http://10.0.0.1:8080/users/";
    public static String signUrl = "http://10.0.0.1:8080/users/sign";
    public static String getnotinyourlistcontact="http://10.0.0.1:8080/contacts/notin";
    public static String ajoutercontact="http://10.0.0.1:8080/contacts/";
    public static String getcontact22="http://10.0.0.1:8080/contacts";
    public static String deletecontact2="http://10.0.0.1:8080/contacts/delete";
    public static String ajoutermessage2="http://10.0.0.1:8080/messages";
    public static String getmessages2="http://10.0.0.1:8080/messages";
    public static String updateimage="http://10.0.0.1:8080/users/updateimage";
    public static String updateuser="http://10.0.0.1:8080/users/updateuser";
    public static String updatepassword="http://10.0.0.1:8080/users/updatepassword";
    public static String login="http://10.0.0.1:8080/users/login";
    public static String deletemessage;
    public static String getTaches;
    public static String deletetache="http://10.0.0.1:8080/project_users/deletetache";
}
