package com.ens.timezer0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ens.timezer0.utils.AccessToken;
import com.ens.timezer0.utils.LoginService;
import com.ens.timezer0.utils.ServiceGenerator;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

import retrofit2.Call;

public class LoginActivity extends AppCompatActivity {
    GoogleSignInClient mGoogleSignInClient;


    private final String clientId = "847557355350-t2fs7edjul4tnntppdr5h1i8f1h5o513.apps.googleusercontent.com";
    private final String clientSecret = "T45n8XrHNA3snXey6xFjlQxU";
    private final String redirectUri = "http://10.0.0.1:3000/oauth2/redirect";



    MaterialButton btnlogin,btnsign,btnlogingoogle;
    TextView oublier;
    TextInputEditText usernametxt, passwordtxt;
    MaterialCheckBox mCheckBox;
    final static String url =  UrlsGlobal.login;
 //   final static String url =  UrlsGlobal.login; // "http://10.0.0.1/chatbackend/login.php";
   // final static String urlrec = UrlsGlobal.recuperermotdepass ; //"http://10.0.0.1/chatbackend/recuperermotdepass.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
// Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("847557355350-t2fs7edjul4tnntppdr5h1i8f1h5o513.apps.googleusercontent.com")
                .build();



        SignInButton signInButton = findViewById(R.id.sign_button_google);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.silentSignIn()
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<GoogleSignInAccount>() {
                            @Override
                            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                                handleSignInResult(task);
                            }
                        });
        usernametxt = (TextInputEditText) findViewById(R.id.edtemail2);
        passwordtxt = (TextInputEditText) findViewById(R.id.edtpassword2);
        btnlogin = (MaterialButton) findViewById(R.id.btnlogin2);
        btnsign = (MaterialButton) findViewById(R.id.sign_button);
        mCheckBox = (MaterialCheckBox) findViewById(R.id.checkboxadmin2);

        btnlogingoogle = (MaterialButton) findViewById(R.id.btnlogingoogle);


        btnlogingoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"fuck  login", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(ServiceGenerator.API_BASE_URL + "/login" + "?client_id=" + clientId + "&redirect_uri=" + redirectUri));
                startActivity(intent);


            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, 3301);

            }
        });

        mCheckBox.setChecked(true);
        oublier = (TextView) findViewById(R.id.oubliermotpas);
        oublier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean noErrors = true;
                final String s = usernametxt.getText().toString();

                if (s.isEmpty()) {
                    usernametxt.setError("remplire l'email pour recuperer password");
                    noErrors = false;
                } else {
                    usernametxt.setError(null);
                }

                if (noErrors) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("tu est sure que vous voulez recuperer cet mot de pass?")
                            .setCancelable(false)
                            .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
Toast.makeText(getApplicationContext(),"lol",Toast.LENGTH_LONG).show();
                                  /*  AndroidNetworking.post(urlrec)
                                            .setPriority(Priority.HIGH)
                                            .addBodyParameter("email", s)
                                            .build().getAsString(new StringRequestListener() {
                                        @Override
                                        public void onResponse(String response) {
                                            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onError(ANError anError) {
                                            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                                        }
                                    });

                                    */
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


            }
        });

        btnsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = usernametxt.getText().toString();
                String password = passwordtxt.getText().toString();

                if ((username.length() <= 0 || username == null) || (password.length() <= 0 || password == null)) {
                    usernametxt.setError("S'il vous plait remplire tous les champs");
                } else {
                 new LoginAuth(LoginActivity.this, url, usernametxt, passwordtxt,mCheckBox).execute();
          //          Toast.makeText(getApplicationContext(),"That's look goods :)",Toast.LENGTH_LONG).show();
                //    startActivity(new Intent(LoginActivity.this,NavigActivity.class));

                }

            }
        });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            String idToken = account.getIdToken();

            Toast.makeText(getApplicationContext(),account.getEmail(),Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(),account.getIdToken(),Toast.LENGTH_LONG).show();
            // Signed in successfully, show authenticated UI.
          //  updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("lll", "signInResult:failed code=" + e.getStatusCode());
          //  updateUI(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // the intent filter defined in AndroidManifest will handle the return from ACTION_VIEW intent
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(redirectUri)) {
            // use the parameter your API exposes for the code (mostly it's "code")
            String code = uri.getQueryParameter("code");
            if (code != null) {
                // get access token
                LoginService loginService =
                        ServiceGenerator.createService(LoginService.class, clientId, clientSecret);
                Call<AccessToken> call = loginService.getAccessToken(code, "authorization_code");
                try {
                    AccessToken accessToken = call.execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (uri.getQueryParameter("error") != null) {
                // show an error message here
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 3301) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    }

