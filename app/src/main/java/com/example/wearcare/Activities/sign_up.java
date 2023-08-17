package com.example.wearcare.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wearcare.R;
import com.scwang.wave.MultiWaveHeader;

public class sign_up extends AppCompatActivity {
    private AppCompatActivity mContext;
    private EditText userName,passWord;
    private Button signUp;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        mContext = this;
        userName = findViewById(R.id.userName);
        passWord = findViewById(R.id.passWord);
        signUp = findViewById(R.id.signUp);

        sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(mContext, "Kindly Enter Username", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (passWord.getText().toString().trim().isEmpty()) {
                    Toast.makeText(mContext, "Kindly Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                editor.putString("Username", userName.getText().toString().trim());
                editor.putString("Password", passWord.getText().toString().trim());
                editor.apply();

                Intent intent = new Intent(mContext, login.class);
                startActivity(intent);
            }
        });

    }
}