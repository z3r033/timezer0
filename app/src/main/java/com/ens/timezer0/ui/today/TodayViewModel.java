package com.ens.timezer0.ui.today;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ens.timezer0.UrlsGlobal;
import com.ens.timezer0.models.modelutilisateur;
import com.ens.timezer0.utils.SharedPref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TodayViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<modelutilisateur> reclamationList;
    public TodayViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Bonjour dans notre application");
    }
    LiveData<modelutilisateur> getUser(String userId,String token) {

        if (reclamationList == null) {

            reclamationList = new MutableLiveData<>();
            taskAsync = new GetUtilisateur(userId,token);
            taskAsync.execute();
        }
        return reclamationList;
    }
    private GetUtilisateur taskAsync;
    private String te;
    public LiveData<String> getText() {
        return mText;
    }



    private class GetUtilisateur extends AsyncTask<Void,Void,String> {
        String userId;
        String token;
        public GetUtilisateur(String userId,String token) {
            this.userId=userId;
            this.token=token;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new Parser(s).execute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.send();
        }

        private String send() {
            Object mconnect = connect.connect(UrlsGlobal.getuserbyid+ userId,token);
            if (mconnect.toString().startsWith("Error")) {
                return mconnect.toString();
            }
            try {
                HttpURLConnection connection = (HttpURLConnection) mconnect;

                int responsecode = connection.getResponseCode();
                if (responsecode == connection.HTTP_OK) {

                    InputStream is = new BufferedInputStream(connection.getInputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while ((line = br.readLine()) != null) {
                        response.append(line + "\n");
                    }
                    br.close();
                    is.close();

                    te = response.toString();


                    return response.toString();


                } else {
                    return "erreurs" + String.valueOf(responsecode);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            //Get response


            return "Check your connection !!! ";
        }


    }
    static class connect {

        public static Object connect(String url,String token) {

            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(25000);
                //connection.setDoInput(true);
                //  connection.setDoOutput(true);
                connection.setRequestProperty("Accept", "application/json");
                connection.setUseCaches(true);
                connection.setDefaultUseCaches(true);
                connection.addRequestProperty("Authorization", "Bearer " +token );
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
    private class Parser extends AsyncTask<Void, Void, Boolean> {



        String jdata;


        modelutilisateur modelutilisateur = new modelutilisateur();

        ProgressDialog pd;

        public Parser(String jdata) {

            this.jdata = jdata;



        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            return this.parseReclamation();

        }


        private Boolean parseReclamation() {
            try {

                JSONObject jo = new JSONObject(jdata);


                    String image_profile = jo.getString("image_profile");
                    String email = jo.getString("email");
                    String userId = jo.getString("userId");
                    String firstName = jo.getString("firstName");
                    String lastName=  jo.getString("lastName");
                    String telephone = jo.getString("telephone");


                    modelutilisateur.setUser_id(userId);
                    modelutilisateur.setUsername(firstName+" "+lastName);
                    modelutilisateur.setFirstName(firstName);
                    modelutilisateur.setLastName(lastName);
                    modelutilisateur.setTelephone(telephone);
                    modelutilisateur.setEmail(email);
                    modelutilisateur.setTelephone(telephone);
                    modelutilisateur.setImageurl(image_profile);



                } catch (JSONException ex) {
                ex.printStackTrace();
            }

            return true;

        }

        @Override
        protected void onPostExecute(Boolean isParsed) {
            super.onPostExecute(isParsed);



            if (isParsed) {

                reclamationList.setValue(modelutilisateur);
            } else {
                reclamationList.setValue(null);
            }


        }

    }
}