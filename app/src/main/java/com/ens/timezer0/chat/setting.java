package com.ens.timezer0.chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.ens.timezer0.R;
import com.ens.timezer0.SignUpActivity;
import com.ens.timezer0.UrlsGlobal;
import com.ens.timezer0.utils.SharedPref;
import com.ens.timezer0.utils.VolleyMultipartRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.http.Url;

public class setting extends AppCompatActivity {

    TextView txtusername,txtemail ,txtlastName, txttelephone;
MaterialButton btnmodifierimage,btnchangermotpass;
TextInputEditText recmot,nvmot1,nvmot2;
CircleImageView mImageView;


private Bitmap mBitmap;

private final int img_request =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

txtusername = (TextView) findViewById(R.id.usernamemodifier) ;
txtemail = (TextView)findViewById(R.id.emailmodifier);
btnmodifierimage  = (MaterialButton) findViewById(R.id.modifierimage);
mImageView = (CircleImageView) findViewById(R.id.imageupload);
btnchangermotpass =(MaterialButton) findViewById(R.id.btnchangermotdepass);
txttelephone = (TextView)findViewById(R.id.telephonemodifier);
txtlastName = (TextView)findViewById(R.id.lastNamemodifier);
recmot = (TextInputEditText) findViewById(R.id.recmotpass);
nvmot1 = (TextInputEditText)findViewById(R.id.nvmotpass);
nvmot2 = (TextInputEditText) findViewById(R.id.nvmotpass2);

btnchangermotpass.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
             boolean noErrors = true;
                    String recpassword = recmot.getText().toString();
                    String nvpassword = nvmot1.getText().toString() ;
                    String nvpassword2 = nvmot2.getText().toString();

                    if (recpassword.isEmpty()) {
                   recmot.setError("remplire ce champ obligatoire");
                   noErrors = false;
               } else {
                   recmot.setError(null);
               }
                if (nvpassword.isEmpty()) {
                    nvmot1.setError("remplire ce champ obligatoire");
                    noErrors = false;
                } else {
                    nvmot1.setError(null);
                }
                if (nvpassword2.isEmpty()) {
                    nvmot2.setError("remplire ce champ obligatoire");
                    noErrors = false;
                } else {
                    nvmot2.setError(null);
                }
                if(!nvpassword.equals(nvpassword2)){
                        nvmot2.setError("les deux mot deux pass pas le meme");
                         nvmot1.setError("les deux mot deux pass pas le meme");
                         noErrors= false;
                }else{
                             nvmot2.setError(null);
                         nvmot1.setError(null);
                }
                 if (noErrors) {
                           final String email = SharedPref.readSharedSetting(setting.this,"email","0");
               new changermotpass(setting.this, recpassword, nvpassword, email).execute();
           }
    }

});
 final String utilisateur_id = SharedPref.readSharedSetting(setting.this, "userid","0");
 final String username = SharedPref.readSharedSetting(setting.this,"username","0");
        final String email = SharedPref.readSharedSetting(setting.this,"email","0");
        final String profile_url = SharedPref.readSharedSetting(setting.this,"image_profile","0");
        final String firstName = SharedPref.readSharedSetting(setting.this,"firstName","0");
        final String lastName = SharedPref.readSharedSetting(setting.this,"lastName","ben");
        final String telephone = SharedPref.readSharedSetting(setting.this,"telephone","0");
 final String token =  SharedPref.readSharedSetting(setting.this, "token", "0");
           Glide.with(getApplicationContext()).load(/*"https://chatappmek.000webhostapp.com/"+*/profile_url)
                   .placeholder(R.drawable.ic_notifications_active_black_24dp)
                   .error(R.drawable.ic_person_add_black_24dp).into(mImageView);
           mImageView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   selectimage();
               }
           });

           btnmodifierimage.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                   try {
                       uploadimageVersLeSite(mBitmap,txtusername.getText().toString(),txtlastName.getText().toString(),txtemail.getText().toString(),txtemail.getText().toString());
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
              /*   AndroidNetworking.post(uploadurl)
                           .addHeaders("Authorization", "Bearer " +token)
                           .addBodyParameter("fil",utilisateur_id+".png")
                           .addBodyParameter("utilisateur_id",utilisateur_id+"")
                           .addBodyParameter("file",imagetostring(mBitmap))
                           .build().getAsString(new StringRequestListener() {
                       @Override
                       public void onResponse(String response) {
                           Toast.makeText(getApplicationContext(),response+" ", Toast.LENGTH_SHORT).show();
                            AndroidNetworking.post(updateprofile_url)
                                    .addBodyParameter("utilisateur_id",utilisateur_id+"")
                                    .build().getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {

                                        Toast.makeText(getApplicationContext(),response+" ", Toast.LENGTH_SHORT).show();
                                          SharedPref.saveSharedSetting(getApplicationContext(),"profile_url","http://10.0.0.1/chatbacked/images/"+utilisateur_id+".png");
                                }

                                @Override
                                public void onError(ANError anError) {
    Toast.makeText(getApplicationContext(),"error ", Toast.LENGTH_SHORT).show();
                                }
                            });
                           Glide.with(getApplicationContext()).load(/*"https://chatappmek.000webhostapp.com/"+*///profile_url)//.into(mImageView);
                 /*          SharedPref.saveSharedSetting(getApplicationContext(),"profile_url","http://10.0.0.1/chatbacked/images/"+utilisateur_id+".png");

                       }

                       @Override
                       public void onError(ANError anError) {

                       }
                   });*/
               }
           });

         txtusername.setText(firstName);
         txtusername.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 LayoutInflater li = LayoutInflater.from(getApplicationContext());
                 View promptsView = li.inflate(R.layout.promptview, null);
                 android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(setting.this);
                 alertDialogBuilder.setTitle("ecrire un autre nom");
           alertDialogBuilder.setView(promptsView);

                 final EditText userInput = (EditText) promptsView
                         .findViewById(R.id.edit_edit_text);
          alertDialogBuilder
                         .setCancelable(false)
                         .setPositiveButton("OK",
                                 new DialogInterface.OnClickListener() {
                                     public void onClick(DialogInterface dialog, int id) {

                                         txtusername.setText(userInput.getText());
                                         new editinfo(getApplicationContext(),UrlsGlobal.updateuser,userInput.getText().toString(),txtlastName.getText().toString(),txtemail.getText().toString(),txttelephone.getText().toString()).execute();
                                  //       new editinfousername(getApplicationContext(),urlupdateusername,userInput,utilisateur_id).execute();
                                         SharedPref.saveSharedSetting(getApplicationContext(), "firstName", userInput.getText().toString());
                                     }
                                 })
                         .setNegativeButton("Cancel",
                                 new DialogInterface.OnClickListener() {
                                     public void onClick(DialogInterface dialog, int id) {
                                         dialog.cancel();
                                     }
                                 });
              android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                 alertDialog.show();

             }
         });

                  txtemail.setText(email);
         txtemail.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 LayoutInflater li = LayoutInflater.from(getApplicationContext());
                 View promptsView = li.inflate(R.layout.promptview, null);
                 android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(setting.this);
                 alertDialogBuilder.setTitle("ecrire un autre email");
           alertDialogBuilder.setView(promptsView);

                 final EditText userInput = (EditText) promptsView
                         .findViewById(R.id.edit_edit_text);
          alertDialogBuilder
                         .setCancelable(false)
                         .setPositiveButton("OK",
                                 new DialogInterface.OnClickListener() {
                                     public void onClick(DialogInterface dialog, int id) {

                                         txtemail.setText(userInput.getText());

                SharedPref.saveSharedSetting(getApplicationContext(), "email", userInput.getText().toString());

                                         new editinfo(getApplicationContext(),UrlsGlobal.updateuser,txtusername.getText().toString(),txtlastName.getText().toString(),userInput.getText().toString(),txttelephone.getText().toString()).execute();

                                     }
                                 })
                         .setNegativeButton("Cancel",
                                 new DialogInterface.OnClickListener() {
                                     public void onClick(DialogInterface dialog, int id) {
                                         dialog.cancel();
                                     }
                                 });
              android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                 alertDialog.show();

             }
         });

        txtlastName.setText(lastName);
        txtlastName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(getApplicationContext());
                View promptsView = li.inflate(R.layout.promptview, null);
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(setting.this);
                alertDialogBuilder.setTitle("ecrire un autre nom");
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.edit_edit_text);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        txtlastName.setText(userInput.getText());

                                        SharedPref.saveSharedSetting(getApplicationContext(), "lastName", userInput.getText().toString());

                                        new editinfo(getApplicationContext(),UrlsGlobal.updateuser,txtusername.getText().toString(),userInput.getText().toString(),txtemail.getText().toString(),txttelephone.getText().toString()).execute();

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });
        txttelephone.setText(telephone);
        txttelephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(getApplicationContext());
                View promptsView = li.inflate(R.layout.promptview, null);
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(setting.this);
                alertDialogBuilder.setTitle("ecrire un autre numero telephone");
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.edit_edit_text);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        txttelephone.setText(userInput.getText());

                                        SharedPref.saveSharedSetting(getApplicationContext(), "telephone", userInput.getText().toString());
                                        String tele=userInput.getText().toString();

                                        new editinfo(getApplicationContext(),UrlsGlobal.updateuser,txtusername.getText().toString(),txtlastName.getText().toString(),txtemail.getText().toString(),tele).execute();

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

    }










    private void uploadimageVersLeSite(final Bitmap imageBitmap,final String firstnameedtst,final String lastNamedts, final String emailedtst, final String phonetst ) throws JSONException {

        String requestURL = UrlsGlobal.updateimage ;



        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,requestURL ,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));

                            new AlertDialog.Builder(setting.this)
                                    .setTitle("modification avec succes")
                                    .setMessage("modification avec succes" )


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
                        new AlertDialog.Builder(setting.this)
                                .setTitle("modification avec succes")
                                .setMessage("modification avec succes" )


                                .setNegativeButton(android.R.string.ok, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        Toast.makeText(getApplicationContext(),new String(error.networkResponse.data), Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("Authorization: ", SharedPref.readSharedSetting(setting.this,"token","lol"));
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("firstName", firstnameedtst);
                params.put("lastName", lastNamedts);
                params.put("email", emailedtst);

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
                params.put("file", new DataPart(imagename + ".png", getFileDataFromDrawable(mBitmap)));
                return params;
            }





        };
        Volley.newRequestQueue(setting.this).add(volleyMultipartRequest);

    }
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private class changermotpass extends AsyncTask<Void, Void, String> {
        String recpassword , nvpassword , email ;
        Context ct;
        public changermotpass(Context ctx, String recpassword, String nvpassword, String email) {
            this.recpassword =recpassword;
            this.ct = ctx;
            this.nvpassword= nvpassword;
            this.email = email;

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
        String token = SharedPref.readSharedSetting(setting.this,"token","0");
              Object mconnect = connect.connect(UrlsGlobal.updatepassword,token);
            if (mconnect.toString().startsWith("Error")) {
                return mconnect.toString();
            }
            try {
                HttpURLConnection connection = (HttpURLConnection) mconnect;

                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                String dataurl = "email=" + email + "&password=" + nvpassword ;

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

        public static Object connect(String url,String token) {

            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(25000);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.addRequestProperty("Authorization", "Bearer " +token );
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


    public class editinfo extends AsyncTask<Void, Void, String> {
        Context c;
        String urlAddress;
        EditText edttext;
        String firstNamee;
        String lastNamee;
        String emaile ;
        String telephonee ;


        ProgressDialog pd;

        public editinfo(Context c, String urlAddress,String firstName,String lastName,String email ,String telephone) {
            this.c = c;
            this.urlAddress = urlAddress;
            this.edttext = edttext;

            this.firstNamee = firstName;
            this.lastNamee = lastName;
            this.emaile = email ;
            this.telephonee = telephone;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(c, "atteint !!! ", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(c, s, Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.send();

        }

        private String send() {
            String token = SharedPref.readSharedSetting(setting.this,"token","0");
            Object mconnect = connect.connect(UrlsGlobal.updateuser,token);
            if (mconnect.toString().startsWith("Error")) {
                return mconnect.toString();
            }
            try {
                HttpURLConnection connection = (HttpURLConnection) mconnect;

                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                String dataurl = "firstName=" + firstNamee + "&lastName=" + lastNamee+ "&email="+emaile+"&telephone="+telephonee;

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


    public class editinfousername extends AsyncTask<Void, Void, String> {
        Context c;
        String urlAddress;
        EditText edttext;
        int utilisateur_id;


        ProgressDialog pd;

        public editinfousername(Context c, String urlAddress, EditText edttext, int utilisateur_id) {
            this.c = c;
            this.urlAddress = urlAddress;
            this.edttext = edttext;

            this.utilisateur_id = utilisateur_id;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(c, "atteint !!! ", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(c, s, Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.send();

        }

        private String send() {
            String token = SharedPref.readSharedSetting(setting.this,"token","0");
            Object mconnect = connect.connect(urlAddress,token);
            if (mconnect.toString().startsWith("Error")) {
                return mconnect.toString();
            }
            try {
                HttpURLConnection connection = (HttpURLConnection) mconnect;

                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                String dataurl = "utilisateur_id=" + utilisateur_id + "&username=" + edttext.getText().toString();

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
    public class editinfoemail extends AsyncTask<Void, Void, String> {
        Context c;
        String urlAddress;
        EditText edttext;
        int utilisateur_id;


        ProgressDialog pd;

        public editinfoemail(Context c, String urlAddress, EditText edttext, int utilisateur_id) {
            this.c = c;
            this.urlAddress = urlAddress;
            this.edttext = edttext;

            this.utilisateur_id = utilisateur_id;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(c, "atteint !!! ", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(c, s, Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.send();

        }

        private String send() {
            String token = SharedPref.readSharedSetting(setting.this,"token","0");
            Object mconnect = connect.connect(urlAddress,token);
            if (mconnect.toString().startsWith("Error")) {
                return mconnect.toString();
            }
            try {
                HttpURLConnection connection = (HttpURLConnection) mconnect;

                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                String dataurl = "utilisateur_id=" + utilisateur_id + "&email=" + edttext.getText().toString();

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

    private void selectimage(){
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,img_request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==img_request && resultCode==RESULT_OK && data!=null)
        {
            Uri path = data.getData();
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                mImageView.setImageBitmap(mBitmap);
                mImageView.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private String imagetostring(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);

    }

}

