package com.ens.timezer0;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class connection_backendjson {
    public static Object connect (String url)
    {

        HttpURLConnection connection = null;

        try {

            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(25000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(true);
            connection.setDefaultUseCaches(true);
            return connection;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "Error : cette url n'existe pas !!! ";

        } catch (IOException e) {
            e.printStackTrace();
            return "Error !!!";
        }


    }

}
