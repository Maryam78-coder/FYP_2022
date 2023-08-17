package com.example.wearcare.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.wearcare.R;

public class infoDesk extends AppCompatActivity {
private Button picBtn,reBtn;
private AppCompatActivity mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_info_desk);

        mContext = this;

        picBtn = findViewById(R.id.button);
        reBtn = findViewById(R.id.record);

        picBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(infoDesk.this,homeIden.class);
                startActivity(intent);
            }
        });

        reBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(infoDesk.this,record.class);
                startActivity(intent);
            }
        });
    }
}