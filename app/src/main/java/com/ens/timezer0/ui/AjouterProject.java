package com.ens.timezer0.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ens.timezer0.NavigActivity;
import com.ens.timezer0.R;
import com.ens.timezer0.UrlsGlobal;
import com.ens.timezer0.utils.SharedPref;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

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

public class AjouterProject extends AppCompatActivity {
    MaterialButton btnajouterProject ;
    TextInputEditText project_nom_edt;
    String urlAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_project);
        urlAddress = UrlsGlobal.ajouterProject;


        btnajouterProject = (MaterialButton) findViewById(R.id.btnajouterproject);
        project_nom_edt = (TextInputEditText) findViewById(R.id.nom_project_edit_text);

        btnajouterProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String project_nom = project_nom_edt.getText().toString();
                boolean noErrors = true;
             String   token = SharedPref.readSharedSetting(AjouterProject.this, "token", "0");
               String user_id = SharedPref.readSharedSetting(AjouterProject.this, "userid", "0");
                if (project_nom.isEmpty() ){
                    project_nom_edt.setError("remplire ce champ obligatoire");
                    noErrors = false;
                } else {
                    project_nom_edt.setError(null);
                }
                if (noErrors) {
                    new ajouterProject(AjouterProject.this, project_nom, token, user_id).execute();
                }
            }
        });



    }


    private class ajouterProject extends AsyncTask<Void,Void,String> {
        String nomproject , token , userid ;
        Context ct;
        public ajouterProject(Context ctx, String nomproject, String token, String userid) {
            this.nomproject =nomproject;
            this.ct = ctx;
            this.token = token;
            this.userid = userid;
;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(ct, "Ajouter Bien FAit!!! ", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(),"response : "+s,Toast.LENGTH_LONG);
            startActivity(new Intent(AjouterProject.this, NavigActivity.class));
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.send();
        }

        private String send() {
            Object mconnect = connect.connect(urlAddress,token);
            if (mconnect.toString().startsWith("Error")) {
                return mconnect.toString();
            }
            try {
                HttpURLConnection connection = (HttpURLConnection) mconnect;

                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
             //   String dataurl = "project_name=" + nomproject + "&user_id=" + userid ;


                //   String loginData = new format_de_url(mUser).packLoginData();
                String jsonInputString = "{\"project_name\": \"" +nomproject+ "\", \"creator_id\": \"" +userid+ "\"}";
                try(OutputStream os2 = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os2.write(input, 0, input.length);
                }

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
                    //        edtmessage.setText("");
                    return response.toString();


                } else {
                    return "erreurs" + String.valueOf(responsecode);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            //Get response


            return "go go go !!! ";
        }


    }
    static class connect {

        public static Object connect(String url,String token) {

            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                connection.setReadTimeout(10000);
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setConnectTimeout(25000);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(true);
                connection.setDefaultUseCaches(true);
                connection.addRequestProperty("Authorization", "Bearer " +token );
         //       connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

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
}