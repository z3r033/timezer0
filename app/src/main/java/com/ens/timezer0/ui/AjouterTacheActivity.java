package com.ens.timezer0.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ens.timezer0.R;
import com.ens.timezer0.basedonnes.BaseContract;
import com.ens.timezer0.dbtest;
import com.ens.timezer0.ui.projects.ProjectsFragment;
import com.ens.timezer0.ui.today.TodayViewModel;
import com.ens.timezer0.utils.SharedPref;
import com.ens.timezer0.utils.TimePickerFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.sql.Types.NULL;

public class AjouterTacheActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener , LoaderManager.LoaderCallbacks<Cursor> {

    Long project_id;
    String project_idstr="0";
    String json_object;
    MaterialButton date;
    MaterialButton time;
    ProgressDialog progressDialog;
    String time_string ;
    String date_String;
    String heading_string;
    String message_string ;
    //ca utilise pour la prierorite
    String impo_string;
    TextInputEditText heading, message;
    MaterialCheckBox urgentcheckbox , importantcheckbox;

    Uri mCurrentTacheUri;
    int id_tache;

    int MONTH;
    int DAY;
    int YEAR;
    int HOUR;
    int MINUTE;

    int MONTH_CURRENT ;
    int DAY_CURRENT;
    int YEAR_CURRENT;
    int HOUR_CURRENT ;
    int MINUTE_CURRENT;

    private static final int TACHE_LOADER=  1;
    private boolean mTacheHasChanged = false;



    private boolean checkDateAndTime() {

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
            return true;
        } else if (YEAR > YEAR_CURRENT) {
            return false;
        } else {
            if (MONTH < MONTH_CURRENT) {
                return true;
            } else if (MONTH > MONTH_CURRENT) {
                return false;
            } else {
                if (DAY < DAY_CURRENT) {
                    return true;
                } else if (DAY > DAY_CURRENT) {
                    return false;
                } else {
                    if (HOUR < HOUR_CURRENT) {
                        return true;
                    } else if (HOUR > HOUR_CURRENT) {
                        return false;
                    } else {
                        if(MINUTE <= MINUTE_CURRENT){
                            return true;
                        }else{
                            return false;
                        }
                    }
                }
            }
        }

    }

    private void showUnsavedChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("tu es sure que vous voulez ecartez vous echange?");
        builder.setPositiveButton("ecarter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mCurrentTacheUri == null) {
                    NavUtils.navigateUpFromSameTask(AjouterTacheActivity.this);
                } else {
                   Intent intent = new Intent(AjouterTacheActivity.this, TacheActivity.class);
                    intent.setData(mCurrentTacheUri);
                    startActivity(intent);
                }
                finish();

            }
        });
        builder.setNegativeButton("KEEP EDITING", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mTacheHasChanged = true;
            return false;
        }
    };
    private void Keyboard_management() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }


    private void deleteTache() {
        int rowsDeleted = getContentResolver().delete(mCurrentTacheUri, null, null);
        Keyboard_management();
        if (rowsDeleted == 0) {
            Toast.makeText(this, "Error with deleting To Do",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Deletion of To Do Successfully",
                    Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(AjouterTacheActivity.this, ListActivity.class);
       startActivity(intent);
       this.finish();

    }


    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure, you want to delete it?");
        builder.setCancelable(false);
        builder.setTitle("DELETE");
        builder.setIcon(android.R.drawable.ic_menu_delete);
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteTache();
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
    private void setDialogOfDate(int year, int month, int day) {
        DatePickerDialog dialog = new DatePickerDialog(
                AjouterTacheActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener, year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    public void setDateDialog(View view) {

        if (YEAR == NULL && DAY == NULL && MONTH == NULL) {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            setDialogOfDate(year, month, day);
        } else {
            setDialogOfDate(YEAR, MONTH - 1, DAY);
        }
    }
    public void setTimeDialog(View view) {

        DialogFragment timePicker = new TimePickerFragment();
        if (HOUR != NULL && MINUTE != NULL) {
            Bundle bundle = new Bundle();
            bundle.putInt("hour", HOUR);
            bundle.putInt("minute", MINUTE);
            timePicker.setArguments(bundle);
        }
        timePicker.show(getSupportFragmentManager(), "time picker");
    }

    public int insertIntoDatabase() {

        date_String = date_String.trim();
        time_string = time_string.trim();

        ContentValues values = new ContentValues();
        values.put(BaseContract.InfoBase.COLUMN_HEADING, heading_string);
        values.put(BaseContract.InfoBase.COLUMN_MESSAGE, message_string);
        values.put(BaseContract.InfoBase.COLUMN_DATE, date_String);
        values.put(BaseContract.InfoBase.COLUMN_TIME, time_string);
        values.put(BaseContract.InfoBase.COLUMN_IMPO,impo_string);
        values.put(BaseContract.InfoBase.COLUMN_NOTIFICATION, 0);

        Uri uri = getContentResolver().insert(BaseContract.InfoBase.CONTENT_URI, values);

        long id = Long.parseLong(uri.getLastPathSegment());

        int ID = (int) id;

        if (uri == null) {
            Toast.makeText(this, "error",
                    Toast.LENGTH_SHORT).show();
        } else {
            long id_uri = ContentUris.parseId(uri);
            id_tache = (int) id_uri;
            Toast.makeText(this, "ajouter",
                    Toast.LENGTH_SHORT).show();
        }
        return ID;
    }
    public void updateIntoDatabase() {
        date_String = date_String.trim();
        time_string = time_string.trim();

        ContentValues values = new ContentValues();
        values.put(BaseContract.InfoBase.COLUMN_HEADING, heading_string);
        values.put(BaseContract.InfoBase.COLUMN_MESSAGE, message_string);
        values.put(BaseContract.InfoBase.COLUMN_DATE, date_String);
        values.put(BaseContract.InfoBase.COLUMN_TIME, time_string);
        values.put(BaseContract.InfoBase.COLUMN_IMPO,impo_string);

        values.put(BaseContract.InfoBase.COLUMN_NOTIFICATION, 0);

        Integer rowsAffected = getContentResolver().update(mCurrentTacheUri, values, null, null);
        if (rowsAffected == null) {
            Toast.makeText(this, "Erro",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "updated",
                    Toast.LENGTH_SHORT).show();
        }
    }




    private class addTache extends AsyncTask<Void,Void,String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(AjouterTacheActivity.this, "atteint !!! ", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(AjouterTacheActivity.this ,"response : "+s,Toast.LENGTH_LONG);
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.send();
        }

        private String send() {
            final String utilisateur_id = SharedPref.readSharedSetting(AjouterTacheActivity.this, "userid", "d");
            final String token = SharedPref.readSharedSetting(AjouterTacheActivity.this, "token", "ll");
            if(!project_idstr.equals("0")){

                Toast.makeText(AjouterTacheActivity.this,"it works",Toast.LENGTH_LONG).show();
                try {
                    RequestQueue requestQueue = Volley.newRequestQueue(AjouterTacheActivity.this);
                    String URL = "http://10.0.0.1:8080/taches";

                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("creatortache_id", utilisateur_id);

                    jsonBody.put("projecttache_id",project_idstr );
                    jsonBody.put("heading", heading_string);
                    jsonBody.put("message", message_string);
                    jsonBody.put("impo", impo_string);
                    jsonBody.put("date", date_String);
                    jsonBody.put("time", time_string);

                    final String mRequestBody = jsonBody.toString();

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("LOG_VOLLEY", response);
                            Toast.makeText(AjouterTacheActivity.this,"sucess", Toast.LENGTH_LONG).show();

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("LOG_VOLLEY", error.toString());
                            Toast.makeText(AjouterTacheActivity.this,"failer", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(AjouterTacheActivity.this,responseString,Toast.LENGTH_LONG).show();
                            }
                            return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                        }
                    };

                    requestQueue.add(stringRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            return "go go go !!! ";
        }


    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_tache);

        Intent intent2 = getIntent();
        project_idstr = intent2.getStringExtra("project_id");
     //    project_id = (Long)Long.parseLong(hi);
      //  project_id =(intent2.getStringExtra("project_id"));
        time = (MaterialButton) findViewById(R.id.timeButton);
        date = (MaterialButton) findViewById(R.id.dateButton);
        heading = (TextInputEditText) findViewById(R.id.heading);
        message = (TextInputEditText) findViewById(R.id.message);
        urgentcheckbox = (MaterialCheckBox) findViewById(R.id.urgentcheckbox);
        importantcheckbox = (MaterialCheckBox) findViewById(R.id.importantcheckbox);

        time.setOnTouchListener(mTouchListener);
        date.setOnTouchListener(mTouchListener);

        heading.setOnTouchListener(mTouchListener);
        message.setOnTouchListener(mTouchListener);

        Intent intent = getIntent();
        mCurrentTacheUri = intent.getData();


        if (mCurrentTacheUri == null) {
            getSupportActionBar().setTitle("ajouter tache");

        } else {
            getSupportActionBar().setTitle("editer la tache");
            getSupportLoaderManager().initLoader(TACHE_LOADER, null, this);
        }


        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;

                DAY = day;
                MONTH = month;
                YEAR = year;
                if (day < 10) {
                    if (month < 10) {
                        date_String = "0" + day + " / 0" + month + " / " + year;
                    } else {
                        date_String = "0" + day + " / " + month + " / " + year;
                    }
                } else {
                    if (month < 10) {
                        date_String = day + " / 0" + month + " / " + year;
                    } else {
                        date_String = day + " / " + month + " / " + year;
                    }
                }

                date.setText(date_String);
            }
        };

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem delete = menu.findItem(R.id.delete);
        delete.setIcon(android.R.drawable.ic_menu_delete);
        if (mCurrentTacheUri != null) {
            delete.setVisible(true);
        } else {
            delete.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save:
                message_string = message.getText().toString().trim();
                heading_string = heading.getText().toString().trim();
                if (urgentcheckbox.isChecked() && importantcheckbox.isChecked()) {
                    impo_string = "impo_and_urgent";

                } else if (!urgentcheckbox.isChecked() && importantcheckbox.isChecked()) {
                    impo_string = "pas_urgent_and_important";

                } else if (urgentcheckbox.isChecked() && !importantcheckbox.isChecked()) {
                    impo_string = "urgent_and_pas_important";

                } else if (!urgentcheckbox.isChecked() && !importantcheckbox.isChecked()) {
                    impo_string = "pas_urgent_and_pas_important";
                }
                if (TextUtils.isEmpty(heading_string) || TextUtils.isEmpty(message_string) || TextUtils.isEmpty(time_string) || TextUtils.isEmpty(date_String)) {
                    Toast.makeText(AjouterTacheActivity.this, "tu es oublier de remplire tout les champs!", Toast.LENGTH_LONG).show();
                } else if (checkDateAndTime()) {
                    Keyboard_management();
                    Toast.makeText(AjouterTacheActivity.this, "ce date est dans le passe!", Toast.LENGTH_LONG).show();
                } else {
                    Keyboard_management();
                //    new addTache().execute();
                    if (mCurrentTacheUri == null) {





                        int ID = insertIntoDatabase();
              //       if(!project_idstr.equals("0")){
                //            Toast.makeText(getApplicationContext(),"it works",Toast.LENGTH_LONG).show();
                  //      }
                        // startActivity(new Intent(AjouterTacheActivity.this,dbtest.class));


                         json_object= "{\"creatortache_id\":\""+SharedPref.readSharedSetting(AjouterTacheActivity.this,"userid","ll")+"\",\"projecttache_id\":"+project_idstr+",\"heading\":\""+heading_string+"\",\"message\":\""+message_string+"\",\"impo\":\""+impo_string+"\",\"date\":\""+date_String+"\",\"time\":\""+time_string+"\"}";;
                        Intent intent = new Intent(AjouterTacheActivity.this, TacheActivity.class);
                        Uri currentToDoUri = ContentUris.withAppendedId(BaseContract.InfoBase.CONTENT_URI, ID);
                        intent.setData(currentToDoUri);
                        intent.putExtra("project_id",project_idstr);
                        intent.putExtra("json_object",json_object);
                        startActivity(intent);
                    } else {

                        updateIntoDatabase();
                        //    startActivity(new Intent(AjouterTacheActivity.this, dbtest.class));
                        Intent intent = new Intent(AjouterTacheActivity.this, TacheActivity.class);
                        intent.putExtra("project_id",project_idstr);
                        intent.setData(mCurrentTacheUri);
                        startActivity(intent);
                    }
                    //exit activity
                    finish();
                }






                return true;

            case R.id.delete:
                Keyboard_management();
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                Keyboard_management();
                if (!mTacheHasChanged) {
                    if (mCurrentTacheUri == null) {
                        NavUtils.navigateUpFromSameTask(AjouterTacheActivity.this);
                    } else {
                        Intent intent = new Intent(AjouterTacheActivity.this, TacheActivity.class);
                        intent.setData(mCurrentTacheUri);
                        startActivity(intent);
                    }
                    finish();
                } else {
                    showUnsavedChangesDialog();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);

    }
    @Override
    public void onBackPressed() {
        if (!mTacheHasChanged) {
            if (mCurrentTacheUri == null) {
                super.onBackPressed();
                return;
            } else {
               Intent intent = new Intent(AjouterTacheActivity.this, TacheActivity.class);
                intent.setData(mCurrentTacheUri);
               startActivity(intent);
                finish();
            }

        } else {
            showUnsavedChangesDialog();
        }

    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (DateFormat.is24HourFormat(AjouterTacheActivity.this)) {
            if (hourOfDay < 10) {
                if (minute < 10) {
                    time_string = "0" + hourOfDay + " : 0" + minute;
                } else {
                    time_string = "0" + hourOfDay + " : " + minute;
                }
            } else {
                if (minute < 10) {
                    time_string = hourOfDay + " : 0" + minute;
                } else {
                    time_string = hourOfDay + " : " + minute;
                }
            }
        } else {
            if (hourOfDay == 0) {
                if (minute < 10) {
                    time_string = 12 + " : 0" + minute + " AM";
                } else {
                    time_string = 12 + " : " + minute + " AM";
                }
            } else if (hourOfDay < 10) {
                if (minute < 10) {
                    time_string = "0" + hourOfDay + " : 0" + minute + " AM";
                } else {
                    time_string = "0" + hourOfDay + " : " + minute + " AM";
                }
            } else if (hourOfDay < 12) {
                if (minute < 10) {
                    time_string = hourOfDay + " : 0" + minute + " AM";
                } else {
                    time_string = hourOfDay + " : " + minute + " AM";
                }
            } else if (hourOfDay == 12) {
                if (minute < 10) {
                    time_string = 12 + " : 0" + minute + " PM";
                } else {
                    time_string = 12 + " : " + minute + " PM";
                }
            } else {
                if (minute < 10) {
                    if (hourOfDay - 12 < 10) {
                        time_string = "0" + (hourOfDay - 12) + " : 0" + minute + " PM";
                    } else {
                        time_string = (hourOfDay - 12) + " : 0" + minute + " PM";
                    }
                } else {
                    if (hourOfDay - 12 < 10) {
                        time_string = "0" + (hourOfDay - 12) + " : " + minute + " PM";
                    } else {
                        time_string = (hourOfDay - 12) + " : " + minute + " PM";
                    }
                }
            }
        }
        time.setText(time_string);

        HOUR = hourOfDay;
        MINUTE = minute;

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

            id_tache = data.getInt(idColumnIndex);
            heading_string = data.getString(headingColumnIndex);
            message_string = data.getString(messageColumnIndex);
            date_String = data.getString(dateColumnIndex);
            time_string = data.getString(timeColumnIndex);
            impo_string = data.getString(impoColumnIndex);

            String day_string = Character.toString(date_String.charAt(0)) + Character.toString(date_String.charAt(1));
            String month_string = Character.toString(date_String.charAt(5)) + Character.toString(date_String.charAt(6));
            String year_string = Character.toString(date_String.charAt(10)) + Character.toString(date_String.charAt(11)) +
                    Character.toString(date_String.charAt(12)) + Character.toString(date_String.charAt(13));

            DAY = Integer.parseInt(day_string);
            MONTH = Integer.parseInt(month_string);
            YEAR = Integer.parseInt(year_string);

            String minute_string = Character.toString(time_string.charAt(5)) + Character.toString(time_string.charAt(6));
            MINUTE = Integer.parseInt(minute_string);

            String hour_string = Character.toString(time_string.charAt(0)) + Character.toString(time_string.charAt(1));
            int HOUR_TEMP = Integer.parseInt(hour_string);

            if (time_string.length() == 10) {
                if (time_string.charAt(8) == 'A' && time_string.charAt(9) == 'M') {
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


            heading.setText(heading_string);
            message.setText(message_string);
            date.setText(date_String);
            time.setText(time_string);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
