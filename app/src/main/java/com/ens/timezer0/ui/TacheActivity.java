package com.ens.timezer0.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ens.timezer0.NavigActivity;
import com.ens.timezer0.R;
import com.ens.timezer0.Receiver.NotificationReceiver;
import com.ens.timezer0.basedonnes.BaseContract;
import com.ens.timezer0.utils.SharedPref;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TacheActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int TACHE_LOADER = 1;
    private Uri mCurrentTacheUri;
    SwitchMaterial nSwitch;
    String project_id="0";
    TextView heading;
    TextView message;
    TextView date;
    TextView time;
    TextView priorite;
    TextView NotificationText;
    MaterialButton btn;
    String heading_tache;
    String message_tache;
    String date_tache;
    String time_tache;
    String priorite_tache;
    int id_tache;
    int notification_todo;

    int MONTH;
    int DAY;
    int YEAR;
    int HOUR;
    int MINUTE;

    int MONTH_CURRENT;
    int DAY_CURRENT;
    int YEAR_CURRENT;
    int HOUR_CURRENT;
    int MINUTE_CURRENT;


    int isSwitchChecked;

    boolean firstTimeFlag;

    AlarmManager alarmManager;
    PendingIntent broadcast;
    Intent notificationIntent;

    private void updateIntoDatabaseNotification(int i) {
        ContentValues values = new ContentValues();
        values.put(BaseContract.InfoBase.COLUMN_NOTIFICATION, i);
        getContentResolver().update(mCurrentTacheUri, values, null, null);
    }


    private void Notification() {
        Log.i("NOTIFICATION", " CREATED");
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra("ID", id_tache);
        notificationIntent.putExtra("Title", heading_tache);
        notificationIntent.putExtra("Message", message_tache);
        notificationIntent.putExtra("impo",priorite_tache);
        notificationIntent.putExtra("SwitchChecked", isSwitchChecked);
        broadcast = PendingIntent.getBroadcast(this, id_tache, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, DAY);
        cal.set(Calendar.MONTH, MONTH - 1);
        cal.set(Calendar.YEAR, YEAR);
        cal.set(Calendar.HOUR_OF_DAY, HOUR);
        cal.set(Calendar.MINUTE, MINUTE);
        cal.set(Calendar.SECOND, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
    }

    public void back(View view) {
        startActivity(new Intent(TacheActivity.this, NavigActivity.class));//NavUtils.navigateUpFromSameTask(TacheActivity.this);
    }

    public void edit(View view) {
        Intent intent = new Intent(TacheActivity.this, AjouterTacheActivity.class);
        intent.setData(mCurrentTacheUri);
        cancelNotification();
        updateIntoDatabaseNotification(0);
        startActivity(intent);
        this.finish();
    }

    private void deleteToDo() {
        int rowsDeleted = getContentResolver().delete(mCurrentTacheUri, null, null);
        cancelNotification();
        if (rowsDeleted == 0) {
            Toast.makeText(this, "error dans la suppression",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "delete succss",
                    Toast.LENGTH_SHORT).show();
        }
        // Close the activity
        finish();

    }



    private void cancelNotification() {
        if (broadcast != null) {
         //   Log.i("NOTIFICATION", " CANCELLED");
            alarmManager.cancel(broadcast);
            broadcast = null;
        }

    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("tu es sure que vous voullez supprimer?");
        builder.setCancelable(false);
        builder.setTitle("DELETE");
        builder.setIcon(android.R.drawable.ic_menu_delete);
        builder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteToDo();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void delete(View view) {
        showDeleteConfirmationDialog();
    }

    public void updateIntoDatabase() {

        ContentValues values = new ContentValues();
        values.put(BaseContract.InfoBase.COLUMN_HEADING, heading_tache);
        values.put(BaseContract.InfoBase.COLUMN_MESSAGE, message_tache);
        values.put(BaseContract.InfoBase.COLUMN_DATE, date_tache);
        values.put(BaseContract.InfoBase.COLUMN_TIME, time_tache);
        values.put(BaseContract.InfoBase.COLUMN_IMPO, priorite_tache);
        values.put(BaseContract.InfoBase.COLUMN_NOTIFICATION, isSwitchChecked);

        Integer rowsAffected = getContentResolver().update(mCurrentTacheUri, values, null, null);
    }






    private class addTache extends AsyncTask<Void,Void,String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(TacheActivity.this, "atteint !!! ", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(TacheActivity.this ,"response : "+s,Toast.LENGTH_LONG);
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.send();
        }

        private String send() {
            final String utilisateur_id = SharedPref.readSharedSetting(TacheActivity.this, "userid", "d");
            final String token = SharedPref.readSharedSetting(TacheActivity.this, "token", "ll");
           // if(!project_idstr.equals("0")){

                Toast.makeText(TacheActivity.this,"it works",Toast.LENGTH_LONG).show();
                try {
                    RequestQueue requestQueue = Volley.newRequestQueue(TacheActivity.this);
                    String URL = "http://10.0.0.1:8080/taches";

                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("creatortache_id", utilisateur_id);

               /*     jsonBody.put("projecttache_id",project_idstr );
                    jsonBody.put("heading", heading_string);
                    jsonBody.put("message", message_string);
                    jsonBody.put("impo", impo_string);
                    jsonBody.put("date", date_String);
                    jsonBody.put("time", time_string);
*/
                    jsonBody.put("projecttache_id",1 );
                    jsonBody.put("heading", "kjklj");
                    jsonBody.put("message", "lkkm");
                    jsonBody.put("impo", "klmk");
                    jsonBody.put("date", "lk");
                    jsonBody.put("time", "lmkml");
                    final String mRequestBody = jsonBody.toString();

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("LOG_VOLLEY", response);
                            Toast.makeText(TacheActivity.this,"sucess", Toast.LENGTH_LONG).show();

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("LOG_VOLLEY", error.toString());
                            Toast.makeText(TacheActivity.this,"failer", Toast.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }

               /*     @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                            return null;
                        }
                    }*/

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();

                            params.put("Authorization: ", token);
                            return params;
                        }

                        @Override
                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
                            String responseString = "";
                            if (response != null) {

                                responseString = String.valueOf(response.statusCode);
                                Toast.makeText(TacheActivity.this,responseString,Toast.LENGTH_LONG).show();
                            }
                            return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                        }
                    };

                    requestQueue.add(stringRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
       //     }


            return "go go go !!! ";
        }


    }
    static class connect {

        public static Object connect(String url, String token) {

            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(25000);
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
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

    private class down extends AsyncTask <Void,Void,String> {
        String json; Long project_id;
        Context ct;
        public down(Context ctx ,String json) {
            this.json=json;
            this.project_id= project_id;
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
            Toast.makeText(getApplicationContext(),"response : "+s,Toast.LENGTH_LONG);
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.send();
        }

        private String send() {
            final String token = SharedPref.readSharedSetting(TacheActivity.this, "token", "0");
            Object mconnect = connect.connect("http://10.0.0.1:8080/taches",token);
            if (mconnect.toString().startsWith("Error")) {
                return mconnect.toString();
            }
            try {
                HttpURLConnection connection = (HttpURLConnection) mconnect;

                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
              //  String jsonInputString = "{\"email\": \"" +mUser.getEmail()+ "\", \"password\": \"" +mUser.getPassword()+ "\"}";
                String jsonInputString = json;//"{\"creatortache_id\":\"etwyvG2p5JzcxAsCO5lGjcDIBRD8T5\",\"projecttache_id\":7,\"heading\":\"kjklj\",\"message\":\"lkkm\",\"impo\":\"klmk\",\"date\":\"lk\",\"time\":\"lmkml\"}";
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


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tache);
     //   getSupportActionBar().hide();

        heading = (TextView) findViewById(R.id.heading_ToDo);
        message = (TextView) findViewById(R.id.message_ToDo);
        date = (TextView) findViewById(R.id.date_ToDo);
        time = (TextView) findViewById(R.id.time_ToDo);
        priorite  = (TextView) findViewById(R.id.priorite_Tache);
        NotificationText = (TextView) findViewById(R.id.notificationText);
        nSwitch = (SwitchMaterial) findViewById(R.id.notificationSwitch);


/*btn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {


        final String utilisateur_id = SharedPref.readSharedSetting(TacheActivity.this, "userid", "d");
        final String token = SharedPref.readSharedSetting(TacheActivity.this, "token", "ll");
        // if(!project_idstr.equals("0")){
        final String URL = "http://10.0.0.1:8080/taches";
        Intent intent2 = getIntent();

        String project_id = intent2.getStringExtra("project_id");
        String  json_object =intent2.getStringExtra("json_object");
        if(!project_id.equals("0")){
            new down(TacheActivity.this,json_object).execute();
        }



        //   new down(TacheActivity.this,7L,utilisateur_id).execute();
// add the request object to the queue to be executed
   //     ApplicationController.getInstance().addToRequestQueue(request_json);
    /*    Toast.makeText(TacheActivity.this,"it works",Toast.LENGTH_LONG).show();
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(TacheActivity.this);
            String URL = "http://10.0.0.1:8080/taches";

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("creatortache_id", utilisateur_id);

               /*     jsonBody.put("projecttache_id",project_idstr );
                    jsonBody.put("heading", heading_string);
                    jsonBody.put("message", message_string);
                    jsonBody.put("impo", impo_string);
                    jsonBody.put("date", date_String);
                    jsonBody.put("time", time_string);
*/
      /*      jsonBody.put("projecttache_id",7 );
            jsonBody.put("heading", "kjklj");
            jsonBody.put("message", "lkkm");
            jsonBody.put("impo", "klmk");
            jsonBody.put("date", "lk");
            jsonBody.put("time", "lmkml");
            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("LOG_VOLLEY", response);
                    Toast.makeText(TacheActivity.this,"sucess", Toast.LENGTH_LONG).show();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("LOG_VOLLEY", error.toString());
                    Toast.makeText(TacheActivity.this,new String(error.networkResponse.data), Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                            return null;
                        }
                    }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
String token2 = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ6ZXIwQGdtYWlsLmNvbSIsImV4cCI6MTU5OTk1MzA1M30.athYCBuCcFaLLDuSQCo5Uhg5tDR6aM0jTmlRYOJauRdoE9kNsKwM6T-Xt88dVgKvXJXdIQXHQyLaZP5y0xNy-g";
                    params.put("Authorization: ", token2);
                    return params;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {

                        responseString = String.valueOf(response.statusCode);
                        Toast.makeText(TacheActivity.this,responseString,Toast.LENGTH_LONG).show();
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        //     }


        Intent intent = getIntent();
        mCurrentTacheUri = intent.getData();
         project_id = intent.getStringExtra("project_id");
          String  json_object =intent.getStringExtra("json_object");
        if(project_id!=null){
            new down(TacheActivity.this,json_object).execute();
        }

        if (mCurrentTacheUri == null) {
            int position_ID = intent.getIntExtra("ID", 0);
            mCurrentTacheUri = ContentUris.withAppendedId(BaseContract.InfoBase.CONTENT_URI, position_ID);
        }

        getSupportLoaderManager().initLoader(TACHE_LOADER, null, this);
        firstTimeFlag = true;

    }

    @Override
    protected void onResume() {
        super.onResume();

        nSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!firstTimeFlag) {
                    if (isChecked) {
                        Log.i("IF: ", Boolean.toString(isChecked));
                        isSwitchChecked = 1;
                        updateIntoDatabase();
                        Notification();
                    } else {
                        Log.i("ELSE: ", Boolean.toString(isChecked));
                        isSwitchChecked = 0;
                        updateIntoDatabase();
                        cancelNotification();
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                BaseContract.InfoBase._ID,
                BaseContract.InfoBase.COLUMN_HEADING,
                BaseContract.InfoBase.COLUMN_MESSAGE,
                BaseContract.InfoBase.COLUMN_DATE,
                BaseContract.InfoBase.COLUMN_TIME,
                BaseContract.InfoBase.COLUMN_NOTIFICATION,
                BaseContract.InfoBase.COLUMN_IMPO};

        return new CursorLoader(this, mCurrentTacheUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }
        if (data.moveToFirst()) {
            int idColumnIndex = data.getColumnIndex(BaseContract.InfoBase._ID);
            int headingColumnIndex = data.getColumnIndex(BaseContract.InfoBase.COLUMN_HEADING);
            int messageColumnIndex = data.getColumnIndex(BaseContract.InfoBase.COLUMN_MESSAGE);
            int dateColumnIndex = data.getColumnIndex(BaseContract.InfoBase.COLUMN_DATE);
            int timeColumnIndex = data.getColumnIndex(BaseContract.InfoBase.COLUMN_TIME);
            int impoColumnIndex = data.getColumnIndex(BaseContract.InfoBase.COLUMN_IMPO);
            int notificationColumnIndex = data.getColumnIndex(BaseContract.InfoBase.COLUMN_NOTIFICATION);

            id_tache = data.getInt(idColumnIndex);
            heading_tache = data.getString(headingColumnIndex);
            message_tache = data.getString(messageColumnIndex);
            date_tache = data.getString(dateColumnIndex);
            time_tache = data.getString(4);
            priorite_tache= data.getString(impoColumnIndex);
            notification_todo = data.getInt(notificationColumnIndex);

            String day_string = Character.toString(date_tache.charAt(0)) + Character.toString(date_tache.charAt(1));
            String month_string = Character.toString(date_tache.charAt(5)) + Character.toString(date_tache.charAt(6));
            String year_string = Character.toString(date_tache.charAt(10)) + Character.toString(date_tache.charAt(11)) +
                    Character.toString(date_tache.charAt(12)) + Character.toString(date_tache.charAt(13));

            DAY = Integer.parseInt(day_string);
            MONTH = Integer.parseInt(month_string);
            YEAR = Integer.parseInt(year_string);

            String minute_string = Character.toString(time_tache.charAt(5)) + Character.toString(time_tache.charAt(6));
            MINUTE = Integer.parseInt(minute_string);

            String hour_string = Character.toString(time_tache.charAt(0)) + Character.toString(time_tache.charAt(1));
            int HOUR_TEMP = Integer.parseInt(hour_string);

            if (time_tache.length() == 10) {
                if (time_tache.charAt(8) == 'A' && time_tache.charAt(9) == 'M') {
                    if (HOUR_TEMP == 12) {
                        HOUR = 0;
                    } else {
                        HOUR = HOUR_TEMP;
                    }
                } else {
                    if (HOUR_TEMP == 12) {
                        HOUR = 12;
                    } else {
                        HOUR = HOUR_TEMP + 12;
                    }
                }
            } else {
                HOUR = HOUR_TEMP;
            }

            heading.setText(heading_tache);
            message.setText(message_tache);
            date.setText(date_tache);
            time.setText(time_tache);


            Log.i("NOTIFICATION ", Integer.toString(notification_todo));


            if (notification_todo != 2) {
                try {
                    Date dateObject = new Date();
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd / MM / YYYY");
                    SimpleDateFormat timeFormatter;
                    if (time.length() == 10) {
                        timeFormatter = new SimpleDateFormat("h : mm a");
                    } else {
                        timeFormatter = new SimpleDateFormat("HH : mm");
                    }

                    String current_date_string = dateFormatter.format(dateObject);
                    String current_time_string = timeFormatter.format(dateObject);

                    if (current_time_string.length() == 9) {
                        current_time_string = "0" + current_time_string;
                    }

                    String day_string1 = Character.toString(current_date_string.charAt(0)) + Character.toString(current_date_string.charAt(1));
                    String month_string1 = Character.toString(current_date_string.charAt(5)) + Character.toString(current_date_string.charAt(6));
                    String year_string1 = Character.toString(current_date_string.charAt(10)) + Character.toString(current_date_string.charAt(11)) +
                            Character.toString(current_date_string.charAt(12)) + Character.toString(current_date_string.charAt(13));

                    DAY_CURRENT = Integer.parseInt(day_string1);
                    MONTH_CURRENT = Integer.parseInt(month_string1);
                    YEAR_CURRENT = Integer.parseInt(year_string1);

                    String minute_string1 = Character.toString(current_time_string.charAt(5)) + Character.toString(current_time_string.charAt(6));
                    MINUTE_CURRENT = Integer.parseInt(minute_string1);

                    String HOUR_CURRENT_string = Character.toString(current_time_string.charAt(0)) + Character.toString(current_time_string.charAt(1));
                    int HOUR_CURRENT_TEMP = Integer.parseInt(HOUR_CURRENT_string);

                    if (current_time_string.length() == 10) {
                        if (current_time_string.charAt(8) == 'a' && current_time_string.charAt(9) == 'm') {
                            if (HOUR_CURRENT_TEMP == 12) {
                                HOUR_CURRENT = 0;
                            } else {
                                HOUR_CURRENT = HOUR_CURRENT_TEMP;
                            }
                        } else {
                            if (HOUR_CURRENT_TEMP == 12) {
                                HOUR_CURRENT = 12;
                            } else {
                                HOUR_CURRENT = HOUR_CURRENT_TEMP + 12;
                            }
                        }
                    } else {
                        HOUR_CURRENT = HOUR_CURRENT_TEMP;
                    }


                    if (YEAR < YEAR_CURRENT) {
                        updateIntoDatabaseNotification(2);
                        notification_todo = 2;
                    } else if (YEAR > YEAR_CURRENT) {
                    } else {
                        if (MONTH < MONTH_CURRENT) {
                            updateIntoDatabaseNotification(2);
                            notification_todo = 2;
                        } else if (MONTH > MONTH_CURRENT) {
                        } else {
                            if (DAY < DAY_CURRENT) {
                                updateIntoDatabaseNotification(2);
                                notification_todo = 2;
                            } else if (DAY > DAY_CURRENT) {
                            } else {
                                if (HOUR < HOUR_CURRENT) {
                                    updateIntoDatabaseNotification(2);
                                    notification_todo = 2;
                                } else if (HOUR > HOUR_CURRENT) {
                                } else {
                                    if(MINUTE <= MINUTE_CURRENT){
                                        updateIntoDatabaseNotification(2);
                                        notification_todo = 2;
                                    }
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (notification_todo == 2) {
                nSwitch.setEnabled(false);
                NotificationText.setText("cet tache est terminer");
            } else {
                if (notification_todo == 1) {
                    nSwitch.setChecked(true);
                    isSwitchChecked = 1;
                    NotificationText.setText("desactiver notifiation");
                    Log.i("DOWN", "IF");
                } else {
                    nSwitch.setChecked(false);
                    isSwitchChecked = 0;
                    NotificationText.setText("activer notification");
                    Log.i("DOWN", "ELSE");
                }
                Log.i("LOAD FINISHED", Integer.toString(isSwitchChecked));
            }

            firstTimeFlag = false;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
