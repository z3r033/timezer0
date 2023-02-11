package com.ens.timezer0.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ApiConnection {

    public static Object connect(String url) {

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod("POST");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(25000);
         //   connection.addRequestProperty("Content-type","application/json");
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(true);
            connection.setDefaultUseCaches(true);

            return connection;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "Error : url n'existe pas";

        } catch (IOException e) {
            e.printStackTrace();
            return "Error : erreur de connection !! ";
        }


    }


    //i use this for getting json String from the Rest api

    public static String getResponse(HttpURLConnection connection){

        try {
            InputStream is   = new BufferedInputStream(connection.getInputStream());

        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;

        StringBuffer response = new StringBuffer();
        while ((line = br.readLine()) != null) {
            response.append(line + "\n");
        }
        br.close();
        is.close();
        return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error : something wrong : ) ";
    }
    //connect with authorization header and Accept json representation , if i want xml json i need just to change Accept header
    //with a payload json

    public static Object connect3(String url) {

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYWFkNEBnbWFpbC5jb20iLCJleHAiOjE1OTA5NjA1MTl9.dIh4S_r5AMsg8yGMDBUD_9s0QdVl7xcc6fZ2v5c-xVe_S3HbT5Ud8RCTa3zdndKlYMwHX7b3VF8o4455S9ofBg";


            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(25000);
            connection.addRequestProperty("Authorization", "Bearer " +token);
     //       connection.addRequestProperty("Content-type","application/json ; utf-8");
            connection.addRequestProperty("Accept", "application/json");
          //  connection.setDoInput(true);
         //   connection.setDoOutput(true);
          //  connection.setUseCaches(true);
           // connection.setDefaultUseCaches(true);
            return connection;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "Error : url n'existe pas";

        } catch (IOException e) {
            e.printStackTrace();
            return "Error : erreur de connection !! ";
        }


    }


    public static Object connectGet(String url,String token) {

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(25000);
            connection.addRequestProperty("Authorization", "Bearer " +token);
            connection.addRequestProperty("Content-type","application/json");
            connection.addRequestProperty("Accept", "application/json");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(true);
            connection.setDefaultUseCaches(true);
            return connection;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "Error : url n'existe pas";

        } catch (IOException e) {
            e.printStackTrace();
            return "Error : erreur de connection !! ";
        }


    }
}
