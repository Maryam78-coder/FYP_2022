package com.example.wearcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wearcare.Activities.dashbord_activity;
import com.example.wearcare.Activities.login;
import com.example.wearcare.Activities.sign_up;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Animation topAnim,bottomAnim;
    private AppCompatActivity mContext;
    private ImageView imageView;
    private TextView textView1,textView2;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mContext=this;
        topAnim= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        imageView=findViewById(R.id.image_View);
        textView1=findViewById(R.id.text1);
        textView2=findViewById(R.id.text2);

        imageView.setAnimation(topAnim);
        textView1.setAnimation(bottomAnim);
        textView2.setAnimation(bottomAnim);

        sharedPreferences = getSharedPreferences("myPerf", MODE_PRIVATE);

        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (sharedPreferences.contains("userLogged")) {
                    boolean userlogged = sharedPreferences.getBoolean("userLogged", false);
                    Intent intent;
                    if (userlogged) {
                        intent = new Intent(mContext, dashbord_activity.class);
                    } else {
                        intent = new Intent(mContext, sign_up.class);
                    }
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, login.class);
                    startActivity(intent);
                }
                finish();
            }

        },3000);


    }
}