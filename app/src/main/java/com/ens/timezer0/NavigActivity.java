package com.ens.timezer0;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ens.timezer0.chat.Contact;
import com.ens.timezer0.chat.NotificationActivity;
import com.ens.timezer0.chat.add_contact;
import com.ens.timezer0.chat.setting;
import com.ens.timezer0.ui.AjouterTacheActivity;
import com.ens.timezer0.utils.SharedPref;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class NavigActivity extends AppCompatActivity    {


    private AppBarConfiguration mAppBarConfiguration;
      String clientId;
    MqttAndroidClient client;
   String utilisateur_id;
    IMqttToken token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navig);

        utilisateur_id = SharedPref.readSharedSetting(NavigActivity.this, "user_id", "0");
        clientId = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(NavigActivity.this, UrlsGlobal.urlmqtt,
                        clientId);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);






    //    navigationView.setNavigationItemSelectedListener(this);


      /*  FloatingActionButton fab = findViewById(R.id.fab);
     fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent(NavigActivity.this, AjouterTacheActivity.class));

           /*     Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
    //        }
    //  });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View h = navigationView.getHeaderView(0);
       final String prof = SharedPref.readSharedSetting(NavigActivity.this, "image_profile", "0");
        ImageView img = h.findViewById(R.id.imageViewpro);
        TextView us = h.findViewById(R.id.nomnav);
        TextView emailtxt = h.findViewById(R.id.emailnav);
        Glide.with(getApplicationContext()).load(prof)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(img);
        final String email = SharedPref.readSharedSetting(NavigActivity.this, "email", "saad@gmail.com");
        final String username = SharedPref.readSharedSetting(NavigActivity.this, "username", "application");

        us.setText(username);
        emailtxt.setText(email);


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_today, R.id.nav_all, R.id.nav_projects)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        MenuItem logOutItem = navigationView.getMenu().findItem(R.id.nav_login);
        logOutItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                SharedPref.saveSharedSetting(NavigActivity.this,"userconnect","False");
                Intent logout = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(logout);
                finish();

                return true;
            }
        });
        MenuItem nocItem = navigationView.getMenu().findItem(R.id.nav_start_noc);
        nocItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

            startActivity(new Intent(NavigActivity.this, NotificationActivity.class));

                return true;
            }
        });
        MenuItem settingItem = navigationView.getMenu().findItem(R.id.nav_setting);
        settingItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                startActivity(new Intent(NavigActivity.this, setting.class));

                return true;
            }
        });

        if(token!=null){

        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //  super.onBackPressed();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("est ce que tu est sure que vous voullez quitez lapp?")
                    .setCancelable(false)
                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        getMenuInflater().inflate(R.menu.settingmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            SharedPref.saveSharedSetting(NavigActivity.this,"userconnect","False");
            Intent logout = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(logout);
            finish();
            return true;


        }
        else if (id == R.id.action_settings){
            startActivity(new Intent(getApplicationContext(), setting.class));
        }    else if (id == R.id.add_contact){
            startActivity(new Intent(getApplicationContext(), add_contact.class));
        }else if(id==R.id.nav_setting){
            startActivity(new Intent(getApplicationContext(), setting.class));
        }
        else if(id==R.id.nav_connect_nv){
            Intent intent = new Intent(getApplicationContext(),Contact.class);
            intent.putExtra("client_id",clientId);
            startActivity(intent);

        }
        else if(id==R.id.nav_start_noc){
          start_notification();
        }


        return super.onOptionsItemSelected(item);
    /*    switch (item.getItemId()) {
            case R.id.action_logout:
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }*/
    }

    private void start_notification() {
        clientId = MqttClient.generateClientId();
        final MqttAndroidClient client =
                new MqttAndroidClient(getApplicationContext(), UrlsGlobal.urlmqtt,
                        clientId);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    //    Toast.makeText(getApplicationContext(),"success connection",Toast.LENGTH_LONG).show();
                    String topic = "noc/"+utilisateur_id;
                    int qos = 1;
                    try {
                        IMqttToken subToken = client.subscribe(topic, qos);
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // The message was published
                                //        Toast.makeText(getApplicationContext(),"success pub",Toast.LENGTH_LONG).show();
                                client.setCallback(new MqttCallback() {
                                    @Override
                                    public void connectionLost(Throwable throwable) {

                                    }

                                    @Override
                                    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                                        Toast.makeText(getApplicationContext(),new String(mqttMessage.getPayload()),Toast.LENGTH_LONG).show();
                                        //      generateNotification(getApplicationContext(), new String(mqttMessage.getPayload()));
                                        //  mHandler.post(deletemessageretained);

                                    }

                                    @Override
                                    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {



                                    }
                                });
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                                  Throwable exception) {
                                // The subscription could not be performed, maybe the user was not
                                // authorized to subscribe on the specified topic e.g. using wildcards

                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(getApplicationContext(),"failed connection",Toast.LENGTH_LONG).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }




    private void generateNotification(Context context, String message) {
        String [] messages = message.split("///");


        int icon = R.drawable.ic_sync_black_24dp;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, Contact.class);
       /*     intent.putExtra("username", messages[1]);
            intent.putExtra("profile_url", messages[2]);
            intent.putExtra("username", messages[3]);
            intent.putExtra("contact_id", messages[4]);*/
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(context);
        // builder.setDefaults( Notification.DEFAULT_VIBRATE);
        // builder.setDefaults( Notification.DEFAULT_SOUND);
        builder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.slow));
        long[] vibrate = { 0, 100, 200, 300 };
        builder.setVibrate(vibrate);
        builder.setAutoCancel(true);
        builder.setContentTitle("new message");
        builder.setContentText("vous avez un nouveau message"+message);
        builder.setSmallIcon(icon);
        builder.setContentIntent(pendingIntent);

        //    builder.setOnlyAlertOnce(true);
        builder.setOngoing(true);
        builder.setNumber(100);
        builder.build();

        Notification notification = builder.getNotification();
        notificationManager.notify(0, notification);

    }
}
