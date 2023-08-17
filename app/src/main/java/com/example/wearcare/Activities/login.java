package com.example.wearcare.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wearcare.R;

public class login extends AppCompatActivity {
    private  AppCompatActivity mContext;
    private Button signUp,login;
    private EditText userName,passWord;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String myPrefUsername,myPrefPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        mContext=this;
        userName=findViewById(R.id.username);
        passWord=findViewById(R.id.password);
        login=findViewById(R.id.login);
        signUp=findViewById(R.id.signup);

        sharedPreferences=getSharedPreferences("myPref",MODE_PRIVATE);
        editor=sharedPreferences.edit();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,sign_up.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(mContext, "Kindly Enter your Username", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (passWord.getText().toString().trim().isEmpty()) {
                    Toast.makeText(mContext, "Kindly Enter your Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                myPrefUsername=sharedPreferences.getString("Username","Incorrect name");
                myPrefPassword=sharedPreferences.getString("Password","Incorrect Password");

                if(userName.getText().toString().trim().equalsIgnoreCase(myPrefUsername) &&
                        passWord.getText().toString().trim().equalsIgnoreCase(myPrefPassword)){

                    editor.putBoolean("userLogged",true);
                    editor.apply();

                    Intent intent=new Intent(mContext, dashbord_activity.class);
                    startActivity(intent);
                    finish();

                }
                else {
                    passWord.setError("Incorrect Password");
                    Toast.makeText(mContext, "Something went wrong...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}