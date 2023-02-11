package com.ens.timezer0;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ens.timezer0.utils.SharedPref;
import com.ens.timezer0.utils.VolleyMultipartRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUpActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private Bitmap bitmap;
    ImageView imageView;
    TextView textView ;
    MaterialButton btnajouteretudiant ;
    TextInputEditText firstNameedt,lastNameedt,phoneedt, passwordedt, emailedt,passwordedt2 ;
    Button btngall;
    Button btnupload;
    String urlAddress;


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
   // urlAddress = AllUrls.ajouternvutilisateur;//"http://10.0.0.1/chatbackend/ajouternvutilisateur.php";

        btngall  =(Button) findViewById(R.id.btnCamera);
        btnupload = (Button)findViewById(R.id.btnupload);
        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");

                startActivityForResult(intent, 1);
            }
        });
        btngall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });



        imageView = findViewById(R.id.photo);


        textView = findViewById(R.id.text_gallery);


    btnajouteretudiant = (MaterialButton) findViewById(R.id.ajouterutilisateur);
   firstNameedt = (TextInputEditText) findViewById(R.id.firstname_edit_text);
        lastNameedt = (TextInputEditText) findViewById(R.id.lastname_edit_text);
    passwordedt = (TextInputEditText) findViewById(R.id.password_edit_text);
        phoneedt = (TextInputEditText) findViewById(R.id.telephone_edit_text);
    emailedt = (TextInputEditText) findViewById(R.id.email_edit_text);
    passwordedt2 =(TextInputEditText)findViewById(R.id.password_edit_text2);
    imageView = (ImageView) findViewById(R.id.fourimage);
    textView = (TextView) findViewById(R.id.fourtext);
    textView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
         Intent intent = new Intent(SignUpActivity.this,dbtest.class);
         startActivity(intent);

        }
    });
    imageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        }
    });


        btnajouteretudiant.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean noErrors = true;
            String firstnameedtst = firstNameedt.getText().toString();
            String lastnameedtst = lastNameedt.getText().toString();
            String emailedtst = emailedt.getText().toString() ;
            String phoneedtst = phoneedt.getText().toString() ;
            String passwordedtst = passwordedt.getText().toString();
            String passwrodedtst2 = passwordedt2.getText().toString();

            String profile_url = "http://10.0.0.1/chatbackend/images/1.png";
            if (firstnameedtst.isEmpty()) {
                firstNameedt.setError("remplire ce champ obligatoire");
                noErrors = false;
            } else {
                firstNameedt.setError(null);
            }
            if (lastnameedtst.isEmpty()) {
                lastNameedt.setError("remplire ce champ obligatoire");
                noErrors = false;
            } else {
                lastNameedt.setError(null);
            }
            if (emailedtst.isEmpty()) {
                emailedt.setError("remplire ce champ obligatoire");
                noErrors = false;
            } else {
                emailedt.setError(null);
            }
            if (phoneedtst.isEmpty()) {
                phoneedt.setError("remplire ce champ obligatoire");
                noErrors = false;
            } else {
                phoneedt.setError(null);
            }
            if (passwordedtst.isEmpty()) {
                passwordedt.setError("remplire ce champ obligatoire");
                noErrors = false;
            } else {
                passwordedt.setError(null);
            }
            if (!passwordedtst.equals(passwrodedtst2)) {
                passwordedt.setError("les deux mot de pass pas les memes");
                passwordedt2.setError("les deux mot de pass pas les memes");
                noErrors = false;
            } else {
                passwordedt.setError(null);
                passwordedt2.setError(null);
            }
            if(!emailValidator(emailedtst.toString())){
                emailedt.setError("email non valid");
                noErrors=false;
            }

            if( phoneedtst.length() < 11 || phoneedtst.length() > 13){
                phoneedt.setError("phone number not valid");
                noErrors=false;

            }
            if (noErrors) {
                if(bitmap!=null ) {
                    try {
                        uploadVersLeSite(bitmap,firstnameedtst,lastnameedtst,emailedtst,phoneedtst,passwordedtst);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    new AlertDialog.Builder(SignUpActivity.this)
                            .setTitle("tu na pas uploader aucun image")
                            .setMessage("tu na pas uploader aucun image")


                            .setNegativeButton(android.R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
             //   new ajouterutilisateur(SignUpActivity.this, usernameedtst, emailedtst, passwordedtst, profile_url).execute();
//startActivity(new Intent(SignUpActivity.this,dbtest.class));

        /*        ContentValues contentValues = new ContentValues();
                contentValues.put(Test2Entry.COLUMN_USERNAME,"jjllll");
                contentValues.put(Test2Entry.COLUMN_EMAIL,"lll");
                contentValues.put(Test2Entry.COLUMN_PASSWORD,"jjj");
                Uri uri = CONTENT_URI ;
               Uri urirowinsrted  = getContentResolver().insert(uri , contentValues);
                Log.i("TAG1","item inserted at  "+urirowinsrted);
             //  long rowId =  databse.insert(Test2Entry.TABLE_NAME,null ,contentValues);
             //   Log.i("TAG1","iTEM INSERTED IN TABLE WITH ROW ID "+rowId);



                /*


               String [] projection= {
                        Test2Entry._ID,
                        Test2Entry.COLUMN_EMAIL,
                        Test2Entry.COLUMN_USERNAME,
                        Test2Entry.COLUMN_PASSWORD
                };
                String selection = null ;
                String [] selectionArgs = null ;

                String sortOrder = null ;

                Cursor cursor = databse.query(Test2Entry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                if(cursor != null ){
                    String str="";
                    while(cursor.moveToNext()){
                        String [] columns = cursor.getColumnNames();
                        for (String column : columns){
                            str+="\t" +cursor.getString(cursor.getColumnIndex(column));

                        }
                        str+= "\n";
                    }
                    cursor.close();
                    Log.i("TAG2",str);
                }


*/



           }

        }
    });


}

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    //  super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1888 && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            // Bitmap imageBitmap = (Bitmap) extras.get("data");
            bitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            path = MediaStore.Images.Media.insertImage(SignUpActivity.this.getContentResolver(), bitmap, "Title", null);



      /* Thread sender = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        GMailSender sender = new GMailSender("chaimae@gmail.com", "chaima123");
                        sender.sendMail("This is Subject",
                                "This is Body",
                                "chaima@gmail.com",
                                "chaima@gmail.com");

                    } catch (Exception e) {
                        Log.e("mylog", "Error: " + e.getMessage());
                    }
                }
            });
            sender.start();
*/
            String charset = "UTF-8";

            String requestURL = UrlsGlobal.signUrl;


            imageView.setImageBitmap(bitmap);
            textView.setText(path);

        } else if (requestCode == 1 && resultCode == Activity.RESULT_OK && null != data) {
            //Bundle extras = data.getExtras();

            Uri selectedImage = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(SignUpActivity.this.getContentResolver(), selectedImage);
                imageView.setImageBitmap(bitmap);
                path = MediaStore.Images.Media.insertImage(SignUpActivity.this.getContentResolver(), bitmap, "Title", null);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }







    private void uploadVersLeSite(final Bitmap imageBitmap,final String firstnameedtst,final String lastNamedts, final String emailedtst, final String phonetst ,final String passwordedtst) throws JSONException {

        String requestURL = UrlsGlobal.signUrl;



        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,requestURL ,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));

                            new AlertDialog.Builder(SignUpActivity.this)
                                    .setTitle("uploader avec succes")
                                    .setMessage("uploader avec succes" )


                                    .setNegativeButton(android.R.string.ok, null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();

                            Toast.makeText(getApplicationContext(),new String(response.data), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            JSONObject obj = new JSONObject(new String(error.networkResponse.data));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new AlertDialog.Builder(SignUpActivity.this)
                                .setTitle("email already exist")
                                .setMessage("Email already exist" )


                                .setNegativeButton(android.R.string.ok, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                            Toast.makeText(getApplicationContext(),new String(error.networkResponse.data), Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("Authorization: ", SharedPref.readSharedSetting(SignUpActivity.this,"token","lol"));
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("firstName", firstnameedtst);
                params.put("lastName", lastNamedts);
                params.put("email", emailedtst);
                params.put("password", passwordedtst);
                params.put("telephone",phonetst);


                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("file", new DataPart(imagename + ".png", getFileDataFromDrawable(imageBitmap)));
                return params;
            }





        };

        //adding the request to volley
    /*    JSONObject obj = new JSONObject();
        obj.put("firstName", firstnameedtst);
        obj.put("lastName", lastNamedts);
        obj.put("email", emailedtst);
        obj.put("password", passwordedtst);
        obj.put("telephone",phonetst);


        RequestQueue queue = volleyMultipartRequest.getRequestQueue();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,SPHERE_URL,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        hideProgressDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressDialog();
                    }
                });
        queue.add(jsObjRequest);
        volleyMultipartRequest.setRequestQueue(queue); */
        Volley.newRequestQueue(SignUpActivity.this).add(volleyMultipartRequest);

    }


    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = SignUpActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    Uri photoURI;
    String path;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(SignUpActivity.this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(getApplicationContext(),
                        "com.ens.timezer0.fileprovider",
                        photoFile);
                //    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1888);
            }
        }



    }








private class ajouterutilisateur extends AsyncTask<Void,Void,String> {
    String username , password , email , profile_url;
    Context ct;
    public ajouterutilisateur(Context ctx, String usernameedtst, String emailedtst, String passwordedtst, String profile_url) {
        this.username =usernameedtst;
        this.ct = ctx;
        this.password= passwordedtst;
        this.email = emailedtst;
        this.profile_url = profile_url;
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
        Object mconnect = connect.connect(urlAddress);
        if (mconnect.toString().startsWith("Error")) {
            return mconnect.toString();
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) mconnect;

            OutputStream os = new BufferedOutputStream(connection.getOutputStream());
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
            String dataurl = "username=" + username + "&email=" + email + "&password=" + password+ "&profile_url"+profile_url;

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
static class connect {

    public static Object connect(String url) {

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


    public boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }




}
