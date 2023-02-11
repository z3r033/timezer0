package com.ens.timezer0.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.ens.timezer0.R;
import com.ens.timezer0.UrlsGlobal;
import com.ens.timezer0.models.modelproject;
import com.ens.timezer0.models.modeltache;
import com.ens.timezer0.ui.projects.ProjectsFragment;
import com.ens.timezer0.utils.SharedPref;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class TacheProjectActivity extends AppCompatActivity {
    private String jsonURL = UrlsGlobal.getTaches;
    private String jsonURL2 = UrlsGlobal.deletetache;
    ListView listView;
    Button btn;
    SwipeRefreshLayout refresh;
    View vieww;
    CustomAdapterProject adapter;
    String project_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tache_project);

        Intent intent = getIntent();
        project_id = intent.getStringExtra("project_id");

        FloatingActionButton fab = findViewById(R.id.fab3);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AjouterTacheActivity.class);
                intent.putExtra("project_id", project_id);
                startActivity(intent);
                //startActivity(new Intent(getApplicationContext(), AjouterTacheActivity.class));

                // i will add something here later
            }
        });
        TextInputEditText searchcontact = (TextInputEditText) findViewById(R.id.searchtachec);
        searchcontact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                (TacheProjectActivity.this).adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        listView = (ListView) findViewById(R.id.list_tache);
      // listView.setEmptyView(vieww);

        refresh = (SwipeRefreshLayout) findViewById(R.id.swiperefreshtache);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Dowloader(TacheProjectActivity.this, jsonURL, listView).execute();
                refresh.setRefreshing(false);
            }
        });




    }


    private class Dowloader extends AsyncTask<Void, Integer, String> {
        modelproject c;

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
            pd.setMessage("searching Taches :) !!! ");
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            return downloadProject();

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
    }







    private String downloadProject() {
        final String utilisateur_id = SharedPref.readSharedSetting(getApplicationContext(), "userid", "d");
        final String token = SharedPref.readSharedSetting(getApplicationContext(), "token", "ll");
        // String jurl= "http://10.0.0.1:8080/projects?creator_id="+utilisateur_id;
        String jurl= "http://10.0.0.1:8080/taches?project_id="+project_id;

        Object connection = connectGet.connect(jurl,token);
        HttpURLConnection connection1 = (HttpURLConnection) connection;

        try {
            /*    OutputStream os = new BufferedOutputStream(connection1.getOutputStream());
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

              String dataurl = "creator_id=" + utilisateur_id;

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






private class Parser extends AsyncTask<Void, Void, Boolean> {


    Context mContext;
    ListView mListView;
    String jdata;


    ArrayList<modeltache> tache = new ArrayList<>();

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

            tache.clear();
            modeltache con;

            for (int i = 0; i < ja.length(); i++) {
                jo = ja.getJSONObject(i);


                Long tache_id = jo.getLong("id");
                String creator_id = jo.getString("creatortache_id");
                String project_id = jo.getString("projecttache_id");
                String heading = jo.getString("heading");
                String message = jo.getString("message");
                String date = jo.getString("date");
                String time = jo.getString("time");
                String impo =jo.getString("impo");

              /// long project_id = Long.parseLong(project_id);

                con = new modeltache();
                con.setTache_id(tache_id);
                con.setTime(time);
                con.setMessage(message);
                con.setProjecttache_id(project_id);
                con.setCreatortache_id(creator_id);
                con.setDate(date);
                con.setHeading(heading);
                con.setImpo(impo);




                tache.add(con);
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
        adapter = new CustomAdapterProject(mContext, tache);

        if (isParsed) {

            mListView.setAdapter(adapter);

        } else {
            Toast.makeText(mContext, jdata, Toast.LENGTH_SHORT).show();
        }


    }

}





static class connectGet {

    public static Object connect(String url,String token) {

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(25000);
            //connection.setDoInput(true);
            //  connection.setDoOutput(true);
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
static class connectPost {

    public static Object connect(String url, String token) {

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(25000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(true);
            connection.setDefaultUseCaches(true);
            connection.addRequestProperty("Authorization", "Bearer " + token);
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

static class connectDelete {

    public static Object connect(String url,String token) {

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("DELETE");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(25000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
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

private class CustomAdapterProject extends BaseAdapter implements Filterable {

    private Context mContext;
    private ArrayList<modeltache> arraytache;
    private CustomAdapterProject.ValueFilter valueFilter;
    private ArrayList<modeltache> mStringFilterList;

    public CustomAdapterProject(Context context, ArrayList<modeltache> arraytache) {
        mContext = context;
        this.arraytache = arraytache;
        mStringFilterList = arraytache;
    }

    @Override
    public int getCount() {
        return arraytache.size();
    }

    @Override
    public Object getItem(int position) {
        return arraytache.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.rowtache, parent, false);
        }


        ImageView priorityimage = (ImageView) convertView.findViewById(R.id.prioritytache);
        TextView txtheading = (TextView) convertView.findViewById(R.id.headingtache);
        final TextView messagetxt = (TextView) convertView.findViewById(R.id.messagetache);
        CircleImageView trash = (CircleImageView) convertView.findViewById(R.id.trashtache);
        //  MaterialButton btnadduser =(MaterialButton) convertView.findViewById(R.id.addusersproject2);
        // MaterialButton btndeleteuser=  convertView.findViewById(R.id.projectuserin);
        TextView txtdate = (TextView) convertView.findViewById(R.id.datetache);
        TextView txttime = (TextView) convertView.findViewById(R.id.timetache);


        final String utilisateur_id = SharedPref.readSharedSetting(mContext, "userid", "0");
        final modeltache tache = (modeltache) this.getItem(position);
        final Long project_id = Long.parseLong(tache.getProjecttache_id());
        final String tacheheading = tache.getHeading();

        final String creator_id = tache.getCreatortache_id();
        final String impo = tache.getImpo();
        final Long tache_id=tache.getTache_id();
        final String message = tache.getMessage();
        final String date = tache.getDate();
        final String time = tache.getTime();
        //     final String profile_url = contact.getImageurl();
        //     final int contact_id = contact.getUtilisateur_id();

        txtdate.setText(date);
        txttime.setText(time);


        txtheading.setText(tacheheading);
        messagetxt.setText(message);
     /*   if (impo.equals("impo_and_urgent")) {
         //  priorityimage.setBackground(R.drawable.urgent_important);
            priorityimage.setBackground(ContextCompat.getDrawable(TacheProjectActivity.this, R.drawable.urgent_important));

        } else if (impo.equals("urgent_and_pas_important")) {
            priorityimage.setBackground(ContextCompat.getDrawable(TacheProjectActivity.this, R.drawable.urgent_pas_important));
        } else if (impo.equals("pas_urgent_and_important")){
            priorityimage.setBackground(ContextCompat.getDrawable(TacheProjectActivity.this, R.drawable.importantpasurgent));
         }else if (impo.equals("pas_urgent_and_pas_important")){
            priorityimage.setBackground(ContextCompat.getDrawable(TacheProjectActivity.this, R.drawable.pasugentpasimport));
        }
*/



        //here do it latter
        //    Glide.with(mContext).load(/*"https://chatappmek.000webhostapp.com/"+*/profile_url).into(imageprofile);
        if(utilisateur_id.equals(creator_id)) {
            trash.setVisibility(View.VISIBLE);
        }
        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TacheProjectActivity.this);
                builder.setMessage("tu est sure que vous voulez supprimer ce contact ?")
                        .setCancelable(false)
                        .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new deleteProject(TacheProjectActivity.this.getApplicationContext(),project_id,tache_id).execute();
                                new Dowloader(TacheProjectActivity.this, jsonURL, listView).execute();
                              //  new Dowloader(getApplicationContext(), jsonURL, listView).execute();
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
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

          //      openTaches(message, project_id+" ",  creator_id);

            }
        });
        return convertView;

    }


    private void openTaches(String... details) {
        Intent intent = new Intent(mContext, AjouterTacheActivity.class);
        intent.putExtra("project_name", details[0]);
        intent.putExtra("project_id", details[1]);
        intent.putExtra("creator_id", details[2]);

        mContext.startActivity(intent);
    }
    private class deleteProject extends AsyncTask<Void,Void,String> {
        Long project_id;
        Long tache_id;
        Context ct;
        public deleteProject(Context ctx, Long project_id,Long tache_id) {
            this.project_id = project_id;
            this.tache_id=tache_id;
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
            Toast.makeText(getApplicationContext().getApplicationContext(),"response : "+s,Toast.LENGTH_LONG);
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.send();
        }

        private String send() {
            final String token = SharedPref.readSharedSetting(mContext, "token", "0");
            Object mconnect = connect.connect("http://10.0.0.1:8080/project_users/deletetache",token);
            if (mconnect.toString().startsWith("Error")) {
                return mconnect.toString();
            }
            try {
                HttpURLConnection connection = (HttpURLConnection) mconnect;

                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                String dataurl = "project_id=" + project_id + "&tache_id=" + tache_id;

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



            return "go go go !!! ";
        }


    }

    @Override
    public Filter getFilter() {

        if (valueFilter == null) {

            valueFilter = new CustomAdapterProject.ValueFilter();
        }

        return valueFilter;

    }


    private class ValueFilter extends Filter {


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<modeltache> filterList = new ArrayList<modeltache>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).getHeading().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {
                        modeltache taches = new modeltache();
                        taches.setHeading(mStringFilterList.get(i).getHeading());
                        taches.setTache_id(mStringFilterList.get(i).getTache_id());
                        filterList.add(taches);
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


        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            arraytache = (ArrayList<modeltache>) results.values;
            notifyDataSetChanged();
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

                connection.addRequestProperty("Authorization", "Bearer " + token);
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


}