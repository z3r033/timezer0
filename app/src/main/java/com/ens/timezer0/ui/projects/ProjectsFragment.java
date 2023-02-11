package com.ens.timezer0.ui.projects;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ens.timezer0.R;
import com.ens.timezer0.UrlsGlobal;
import com.ens.timezer0.models.modelproject;
import com.ens.timezer0.ui.AddUserProjectActivity;
import com.ens.timezer0.ui.AjouterProject;
import com.ens.timezer0.ui.AjouterTacheActivity;
import com.ens.timezer0.ui.TacheProjectActivity;
import com.ens.timezer0.ui.deleteProjectUserActivity;
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

public class ProjectsFragment extends Fragment {

    private ProjectsViewModel projectsViewModel;
    private String jsonURL = UrlsGlobal.getProjects;
    private String jsonURL2 = UrlsGlobal.deleteproject;
    View vieww;
    ListView listView;
    ListView mListView;
    Button btn;
    SwipeRefreshLayout refresh;
    String client_id;
    CustomAdapterProject adapter;
    MaterialButton delete_past_btn,btn_today,btn_all;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        projectsViewModel =
                ViewModelProviders.of(this).get(ProjectsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_projects, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);

        FloatingActionButton fab = root.findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AjouterProject.class));
            }
        });
        TextInputEditText searchcontact = (TextInputEditText) root.findViewById(R.id.searchproject);
        searchcontact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                (ProjectsFragment.this).adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        listView = (ListView) root.findViewById(R.id.list_project);
        listView.setEmptyView(vieww);

        refresh = (SwipeRefreshLayout) root.findViewById(R.id.swiperefresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Dowloader(getActivity(), jsonURL, listView).execute();
                refresh.setRefreshing(false);
            }
        });

        projectsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
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
            pd.setMessage("searching projects :) !!! ");
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

        private String downloadProject() {
            final String utilisateur_id = SharedPref.readSharedSetting(mContext, "userid", "d");
            final String token = SharedPref.readSharedSetting(mContext, "token", "ll");
           // String jurl= "http://10.0.0.1:8080/projects?creator_id="+utilisateur_id;
            String jurl= "http://10.0.0.1:8080/project_users?user_id="+utilisateur_id;

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
    }

    private class Parser extends AsyncTask<Void, Void, Boolean> {


        Context mContext;
        ListView mListView;
        String jdata;


        ArrayList<modelproject> project = new ArrayList<>();

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

                project.clear();
                modelproject con;

                for (int i = 0; i < ja.length(); i++) {
                    jo = ja.getJSONObject(i);


                    Long project_id = jo.getLong("id");
                    String project_name = jo.getString("project_name");
                    String creator_id = jo.getString("creator_id");



                    con = new modelproject();

                    con.setProject_id(project_id);
                    con.setProject_name(project_name);
                    con.setCreator_name(creator_id);



                    project.add(con);
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
            adapter = new CustomAdapterProject(mContext, project);

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

        public static Object connect(String url,String token) {

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
        private ArrayList<modelproject> arrayproject;
        private ValueFilter valueFilter;
        private ArrayList<modelproject> mStringFilterList;

        public CustomAdapterProject(Context context, ArrayList<modelproject> arrayproject) {
            mContext = context;
            this.arrayproject = arrayproject;
            mStringFilterList = arrayproject;
        }

        @Override
        public int getCount() {
            return arrayproject.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayproject.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.rowproject, parent, false);
            }
            ImageView imageprofile = (ImageView) convertView.findViewById(R.id.profile_url);
            TextView txtproject = (TextView) convertView.findViewById(R.id.projectname);
            final TextView creator = (TextView) convertView.findViewById(R.id.usercreator);
            CircleImageView trash = (CircleImageView) convertView.findViewById(R.id.trashcontact);
            MaterialButton btnadduser =(MaterialButton) convertView.findViewById(R.id.addusersproject2);
                    MaterialButton btndeleteuser=  convertView.findViewById(R.id.projectuserin);

            final String utilisateur_id = SharedPref.readSharedSetting(mContext, "userid", "0");
            final modelproject project = (modelproject) this.getItem(position);
            final Long project_id = project.getProject_id();
            final String project_name = project.getProject_name();
            final String creator_id = project.getCreator_name();
       //     final String profile_url = contact.getImageurl();
       //     final int contact_id = contact.getUtilisateur_id();

btnadduser.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(mContext, AddUserProjectActivity.class);
        intent.putExtra("project_name", project_name);
        intent.putExtra("project_id", project_id);
        intent.putExtra("creator_id",creator_id);

        mContext.startActivity(intent);
    }
});
            btndeleteuser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, deleteProjectUserActivity.class);
                    intent.putExtra("project_name", project_name);
                    intent.putExtra("project_id", project_id);
                    intent.putExtra("creator_id",creator_id);

                    mContext.startActivity(intent);
                }
            });
            txtproject.setText(project_name);
            creator.setText(creator_id);
        //    Glide.with(mContext).load(/*"https://chatappmek.000webhostapp.com/"+*/profile_url).into(imageprofile);
            if(utilisateur_id.equals(creator_id)) {
                trash.setVisibility(View.VISIBLE);
            }
            trash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("tu est sure que vous voulez supprimer ce contact ?")
                            .setCancelable(false)
                            .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    new deleteProject(getActivity().getApplicationContext(),project_id).execute();
                                    new Dowloader(getActivity(), jsonURL, listView).execute();
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

                    openTaches(project_name, project_id+" ",  creator_id);

                }
            });
            return convertView;

        }


        private void openTaches(String... details) {
            Intent intent = new Intent(mContext, TacheProjectActivity.class);
            intent.putExtra("project_name", details[0]);
            intent.putExtra("project_id", details[1]);
            intent.putExtra("creator_id", details[2]);

            mContext.startActivity(intent);
        }
        private class deleteProject extends AsyncTask<Void,Void,String> {
            Long project_id;
            Context ct;
            public deleteProject(Context ctx, Long project_id) {
               this.project_id = project_id;
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
                Toast.makeText(getActivity().getApplicationContext(),"response : "+s,Toast.LENGTH_LONG);
            }

            @Override
            protected String doInBackground(Void... voids) {
                return this.send();
            }

            private String send() {
                final String token = SharedPref.readSharedSetting(mContext, "token", "ll");
                Object mconnect = connectDelete.connect(jsonURL2+project_id,token);
                if (mconnect.toString().startsWith("Error")) {
                    return mconnect.toString();
                }
                try {
                    HttpURLConnection connection = (HttpURLConnection) mconnect;

              //      OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                //    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                 //   String dataurl = "project_id=" + project_id ;

                //    bw.write(dataurl);
               //     bw.flush();
                 //   bw.close();
                   // os.close();
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

                valueFilter = new ValueFilter();
            }

            return valueFilter;

        }


        private class ValueFilter extends Filter {


            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null && constraint.length() > 0) {
                    ArrayList<modelproject> filterList = new ArrayList<modelproject>();
                    for (int i = 0; i < mStringFilterList.size(); i++) {
                        if ((mStringFilterList.get(i).getProject_name().toUpperCase())
                                .contains(constraint.toString().toUpperCase())) {
                            modelproject projects = new modelproject();
                            projects.setProject_name(mStringFilterList.get(i).getProject_name());
                            projects.setProject_id(mStringFilterList.get(i).getProject_id());
                            filterList.add(projects);
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
                arrayproject = (ArrayList<modelproject>) results.values;
                notifyDataSetChanged();
            }

        }
    }
}
