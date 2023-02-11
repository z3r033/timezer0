package com.ens.timezer0.Receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.ens.timezer0.R;
import com.ens.timezer0.ui.AjouterTacheActivity;
import com.ens.timezer0.ui.TacheActivity;
import com.ens.timezer0.ui.TacheProjectActivity;
import com.ens.timezer0.utils.SharedPref;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "com.ens.timezer0.tachlist";

    @Override
    public void onReceive(final Context context, Intent intent) {

        int ID;
        int active;
        final String title;
        final String message;

        final String priorite;

        ID= intent.getIntExtra("ID",0);
        title = intent.getStringExtra("Title");
        message = intent.getStringExtra("Message");
        priorite = intent.getStringExtra("impo");
        active = intent.getIntExtra("SwitchChecked",2);


        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client =
                new MqttAndroidClient(context.getApplicationContext(), "tcp://10.0.0.1:1883",
                        clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    final String emailswitch= SharedPref.readSharedSetting(context.getApplicationContext(), "emailon", "false");
                    final String smsswitch= SharedPref.readSharedSetting(context.getApplicationContext(), "smson", "false");



                    String topic1 = "noc/email";
                    String topic2= "noc/sms";
                   String payload= "{\"creatortache_id\":\""+ SharedPref.readSharedSetting(context.getApplicationContext(),"userid","ll")+"\",\"projecttache_id\":"+1+",\"heading\":\""+title+"\",\"message\":\""+message+"\",\"impo\":\""+priorite+"\",\"date\":\""+3+"\",\"time\":\""+3+"\"}";;
                  //  String payload = "the payload";
                    byte[] encodedPayload = new byte[0];
                    try {
                        encodedPayload = payload.getBytes("UTF-8");
                        MqttMessage message = new MqttMessage(encodedPayload);
                        if(emailswitch.equals("false")&& smsswitch.equals("true"))
                        {
                            client.publish(topic2,message);
                        }else if (emailswitch.equals("true")&&smsswitch.equals("false")){
                            client.publish(topic1, message);
                        }
                        else if(emailswitch.equals("false")&&smsswitch.equals("false"))
                        {

                        }else if (emailswitch.equals("true")&&smsswitch.equals("true")){
                            client.publish(topic1, message);
                            client.publish(topic2,message);
                        }


                    } catch (UnsupportedEncodingException | MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("TAG", "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        if (ID != 0){
            Intent notificationIntent = new Intent(context, TacheActivity.class);
            notificationIntent.putExtra("ID",ID);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(TacheActivity.class);
            stackBuilder.addNextIntent(notificationIntent);


            PendingIntent pendingIntent = stackBuilder.getPendingIntent(ID, PendingIntent.FLAG_UPDATE_CURRENT);

            int icon = R.drawable.urgent_important;

            if (priorite.equals("impo_and_urgent")) {
                //  priorityimage.setBackground(R.drawable.urgent_important);
                icon = R.drawable.urgent_important;

            } else if (priorite.equals("urgent_and_pas_important")) {
                icon = R.drawable.urgent_pas_important;
            } else if (priorite.equals("pas_urgent_and_important")){
                icon = R.drawable.importantpasurgent;
            }else if (priorite.equals("pas_urgent_and_pas_important")){
                icon = R.drawable.pasugentpasimport;
            }
            Notification.Builder builder = new Notification.Builder(context);
            builder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.slow));
            long[] vibrate = { 0, 100, 200, 300 };
            builder.setVibrate(vibrate);
            builder.setSmallIcon(icon);
            Notification notification = builder.setContentTitle(title)
                    .setContentText(message)
                    .setTicker("Nouveau tache ! " + priorite)
                    .setAutoCancel(true)
                    .setSmallIcon(icon)
                    .setVibrate(new long[]{1000,500,1000,500,1000})
                    .setContentIntent(pendingIntent).build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId(CHANNEL_ID);
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "To Do",
                        NotificationManager.IMPORTANCE_HIGH
                );

                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(ID, notification);

        //    Log.i("NOTIFICATION RECIEVER  ", Integer.toString(active));

            if (active == 0 || active == 2) {
                notificationManager.cancel(ID);
            }

        }
    }
}
