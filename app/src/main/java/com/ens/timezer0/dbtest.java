package com.ens.timezer0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ens.timezer0.basedonnes.BaseHelper;

public class dbtest extends AppCompatActivity {
    private EditText edt1;
    private EditText edt2;
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbtest);

        edt1 = (EditText) findViewById(R.id.nom);
        edt2 = (EditText) findViewById(R.id.prenom);
        btn = (Button) findViewById(R.id.btninsert);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseHelper nomHelper = new BaseHelper(getApplicationContext());
                SQLiteDatabase DB = nomHelper.getWritableDatabase();

                String query = "select * from TachDooo  where project_id =;";
                Cursor result = DB.rawQuery(query,null);
                if(result.moveToFirst()){
                    Toast.makeText(getApplicationContext(),result.getString(1),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
