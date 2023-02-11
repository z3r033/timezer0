package com.ens.timezer0.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CompoundButton;

import com.ens.timezer0.R;
import com.ens.timezer0.utils.SharedPref;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class NotificationActivity extends AppCompatActivity {
    SwitchMaterial email ;
    SwitchMaterial sms ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        email = (SwitchMaterial) findViewById(R.id.nocemail);
        sms = (SwitchMaterial) findViewById(R.id.nocsms);
        final String emailswitch= SharedPref.readSharedSetting(getApplicationContext(), "emailon", "false");
        final String smsswitch= SharedPref.readSharedSetting(getApplicationContext(), "smson", "false");
        if(emailswitch.equals("false"))
        {
            email.setChecked(false);
        }else{
            email.setChecked(true);
        }
        if(smsswitch.equals("false"))
        {
            sms.setChecked(false);
        }else{
            sms.setChecked(true);
        }
        email.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SharedPref.saveSharedSetting(getApplicationContext(), "emailon", "true");
                }else{
                    SharedPref.saveSharedSetting(getApplicationContext(), "emailon", "false");
                }
            }
        });
        sms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SharedPref.saveSharedSetting(getApplicationContext(), "smson", "true");
                }else{
                    SharedPref.saveSharedSetting(getApplicationContext(), "smson", "false");
                }
            }
        });

        if (sms.isChecked()){

        }

    }
}