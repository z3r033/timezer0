package com.ens.timezer0.chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.ens.timezer0.R;
import com.ens.timezer0.UrlsGlobal;
import com.ens.timezer0.models.modelutilisateur;
import com.ens.timezer0.utils.SharedPref;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

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

public class add_contact extends AppCompatActivity {
  private String jsonURL = UrlsGlobal.getnotinyourlistcontact;//"http://10.0.0.1/chatbackend/getnotinyourlist.php";
  private String jsonURL2 = UrlsGlobal.ajoutercontact; ; //"http://10.0.0.1/chatbackend/ajouternvcontact.php";

    ListView mListView;
    Button btn;
    SwipeRefreshLayout refresh;

    CustomAdaptercontact adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);


 TextInputEditText searchcontact = (TextInputEditText) findViewById(R.id.search_utilisateur);
     searchcontact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                (add_contact.this).adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mListView = (ListView) findViewById(R.id.list_utilisateurs);

        refresh = (SwipeRefreshLayout) findViewById(R.id.swiperefreshutilisateurs);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Dowloader(add_contact.this, jsonURL, mListView).execute();
                refresh.setRefreshing(false);
            }
        });
        new Dowloader(add_contact.this, jsonURL, mListView).execute();


    }





       private class Dowloader extends AsyncTask<Void, Integer, String> {
        modelutilisateur c;

        Context mContext;
        String jurl;
        ListView mListView;
        ProgressDialog pd;

        public Dowloader(Context c, String jurl, ListView listView) {
            this.mContext = c;
            this.jurl = jurl;
            this.mListView = listView;

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(mContext);
            pd.setTitle("Attendez s'il veut plait !");
            pd.setMessage("searching contacts :) !!! ");
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            return downloadContact();

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            pd.dismiss();

            if (s.startsWith("Error")) {
                Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();

            } else { // Toast.makeText(mContext,s,Toast.LENGTH_SHORT).show();

                Parser p = new Parser(mContext, s, mListView);
                p.execute();

            }
        }

        private String downloadContact() {
            final String utilisateur_id = SharedPref.readSharedSetting(mContext, "userid", "0");
            final String token=SharedPref.readSharedSetting(mContext, "token", "df");
     String url ="http://10.0.0.1:8080/contacts/notin?utilisateur_id="+utilisateur_id;
    //  String url=      "http://10.0.0.1:8080/contacts/notin?utilisateur_id=OCojej2qg4TDLiMgbhwsqOWmt39Ojw";
            //Object connection = connectGet.connect(jurl+"?utilisateur_id="+utilisateur_id,token);
            Object connection = connectGet.connect(url,token);
            HttpURLConnection connection1 = (HttpURLConnection) connection;
            try {
            /*    OutputStream os = new BufferedOutputStream(connection1.getOutputStream());
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                String dataurl = "utilisateur_id=" + utilisateur_id;

                bw.write(dataurl);
                bw.flush();
                bw.close();
                os.close();*/
                if (connection1.getResponseCode() == connection1.HTTP_OK) {

                    InputStream is = new BufferedInputStream(connection1.getInputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));

                    String line;
                    StringBuffer jsonData = new StringBuffer();
                    while ((line = br.readLine()) != null) {
                        jsonData.append(line + "\n");
                    }
                    br.close();
                    is.close();
                    return jsonData.toString();
                } else {
                    return "Error " + connection1.getResponseMessage();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error" + e.getMessage();
            }
        }
    }

    private class Parser extends AsyncTask<Void, Void, Boolean> {


        Context mContext;
        ListView mListView;
        String jdata;
        int jj;

        ArrayList<modelutilisateur> contact = new ArrayList<>();

        ProgressDialog pd;

        public Parser(Context context, String jdata, ListView listView) {
            this.mContext = context;
            this.jdata = jdata;
            this.mListView = listView;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(mContext);
            pd.setTitle("attend ");
            pd.setMessage("atteint un moment !!! ");
            pd.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            return this.parsecontact();

        }


        private Boolean parsecontact() {
            try {

                JSONArray ja = new JSONArray(jdata);
                JSONObject jo;

                contact.clear();
                modelutilisateur con;



                for (int i = 0; i < ja.length(); i++) {
                    jo = ja.getJSONObject(i);


                    String userId = jo.getString("userId") ;
                    String firstName = jo.getString("firstName");
                    String lastName = jo.getString("lastName");
                    String email = jo.getString("email");
                    String profile_image =jo.getString("image_profile");
                    String telephone = jo.getString("telephone");

                    con= new modelutilisateur();
                    con.setUser_id(userId);
                    con.setImageurl("");
                    con.setUsername(firstName+" "+lastName);
                    con.setEmail(email);
                    con.setImageurl(profile_image);
                    con.setTelephone(telephone);


                contact.add(con);

                }



                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean isParsed) {
            super.onPostExecute(isParsed);

            pd.dismiss();
         adapter = new CustomAdaptercontact(mContext, contact);

            if (isParsed) {
//Toast.makeText(Contact.this,contact.get(0).getEmail(),Toast.LENGTH_LONG).show();
                mListView.setAdapter(adapter);

            } else {
                Toast.makeText(mContext, jdata, Toast.LENGTH_SHORT).show();
            }


        }

    }

    static class connect {

        public static Object connect(String url,String token) {

            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(25000);
                connection.addRequestProperty("Authorization", "Bearer " +token );
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
    static class connectGet {

        public static Object connect(String url, String token) {

            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(25000);
                connection.addRequestProperty("Authorization", "Bearer " + token);
              /*  connection.setDoInput(true);
                connection.setDoOutput(true);*/
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

    private class CustomAdaptercontact extends BaseAdapter implements Filterable {

        private Context mContext;
        private ArrayList<modelutilisateur> arraycontact;
        private ValueFilter valueFilter;
        private ArrayList<modelutilisateur> mStringFilterList;

        public CustomAdaptercontact(Context context, ArrayList<modelutilisateur> arraycontact) {
            mContext = context;
            this.arraycontact = arraycontact;
            mStringFilterList = arraycontact;
        }

        @Override
        public int getCount() {
            return arraycontact.size();
        }

        @Override
        public Object getItem(int position) {
            return arraycontact.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.add_contact_row, parent, false);
            }
            ImageView imageprofile = (ImageView) convertView.findViewById(R.id.profile_url);
            TextView txtusername = (TextView) convertView.findViewById(R.id.username);
            TextView txtemail =(TextView) convertView.findViewById(R.id.emailaddcontact);
            MaterialButton btnajouter = (MaterialButton) convertView.findViewById(R.id.ajoutercontact);
            final modelutilisateur contact = (modelutilisateur) this.getItem(position);

            final String username = contact.getUsername();
            final String email = contact.getEmail();
            final String profile_url = contact.getImageurl();
            final String contact_id = contact.getUser_id();
            final String utilisateur_id = SharedPref.readSharedSetting(mContext, "userid", "0");
if(utilisateur_id.equals(contact_id)){
    txtemail.setVisibility(View.INVISIBLE);
    txtusername.setText(username);
    txtusername.setVisibility(View.INVISIBLE);
    imageprofile.setVisibility(View.GONE);
    Glide.with(mContext).load(/*"https://chatappmek.000webhostapp.com/"+*/profile_url).into(imageprofile);
    txtemail.setText(email);
    btnajouter.setVisibility(View.INVISIBLE);
}else {
    txtusername.setText(username);
    Glide.with(mContext).load(/*"https://chatappmek.000webhostapp.com/"+*/profile_url).into(imageprofile);
    txtemail.setText(email);
    btnajouter.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(add_contact.this);
            builder.setMessage("tu est sure que vous voulez ajouter ce contact?")
                    .setCancelable(false)
                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new ajoutercontact(getApplicationContext(), utilisateur_id, contact_id).execute();
                            new Dowloader(add_contact.this, jsonURL, mListView).execute();
                        }
                    })
                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();


        }
    });
}
            return convertView;

        }



    private class ajoutercontact extends AsyncTask<Void, Void, String> {
        String utilisateur_id, utilisateur_contact_id;
        Context ct;
        public ajoutercontact(Context ctx, String utilisateur_id, String utilisateur_contact_id) {
            this.utilisateur_id=utilisateur_id;
            this.utilisateur_contact_id= utilisateur_contact_id;
            this.ct = ctx;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
              Toast.makeText(ct, "atteint !!! ", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(),"response : "+s, Toast.LENGTH_LONG);
        }

        @Override
        protected String doInBackground(Void... voids) {
               return this.send();
        }

        private String send() {
            final String utilisateur_id = SharedPref.readSharedSetting(mContext, "userid", "0");
            final String token=SharedPref.readSharedSetting(mContext, "token", "df");
              Object mconnect = connect.connect(jsonURL2,token);
            if (mconnect.toString().startsWith("Error")) {
                return mconnect.toString();
            }
            try {
                HttpURLConnection connection = (HttpURLConnection) mconnect;

                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                String dataurl = "utilisateur_id=" + utilisateur_id + "&utilisateur_contact_id=" + utilisateur_contact_id;

                bw.write(dataurl);
                bw.flush();
                bw.close();
                os.close();
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
        @Override
        public Filter getFilter() {

            if (valueFilter == null) {

                valueFilter = new ValueFilter();
            }

            return valueFilter;

        }


        private class ValueFilter extends Filter {

            //Invoked in a worker thread to filter the data according to the constraint.
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null && constraint.length() > 0) {
                    ArrayList<modelutilisateur> filterList = new ArrayList<modelutilisateur>();
                    for (int i = 0; i < mStringFilterList.size(); i++) {
                        if ((mStringFilterList.get(i).getUsername().toUpperCase())
                                .contains(constraint.toString().toUpperCase())) {
                            modelutilisateur contacts = new modelutilisateur();
                            contacts.setUsername(mStringFilterList.get(i).getUsername());
                            contacts.setUtilisateur_id(mStringFilterList.get(i).getUtilisateur_id());
                            filterList.add(contacts);
                        }
                    }
                    results.count = filterList.size();
                    results.values = filterList;
                } else {
                    results.count = mStringFilterList.size();
                    results.values = mStringFilterList;
                }
                return results;
            }

            //Invoked in the UI thread to publish the filtering results in the user interface.
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                arraycontact = (ArrayList<modelutilisateur>) results.values;
                notifyDataSetChanged();
            }

        }
    }
}
