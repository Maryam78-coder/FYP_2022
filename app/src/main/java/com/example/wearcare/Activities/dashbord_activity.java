package com.example.wearcare.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.wearcare.MainActivity;
import com.example.wearcare.R;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.Timer;

public class dashbord_activity extends AppCompatActivity implements View.OnClickListener {
    private CardView cardView1,cardView2,cardView3,cardView4;
    private AppCompatActivity mContext;
    private FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashbord);

        mContext=this;

        floatingActionButton = findViewById(R.id.fab_button);
        cardView1 = (CardView) findViewById(R.id.cardPic);
        cardView2 = (CardView) findViewById(R.id.cardData);
        cardView3 = (CardView) findViewById(R.id.cardDetail);
        cardView4 = (CardView) findViewById(R.id.cardAbout);

        cardView1.setOnClickListener(this);
        cardView2.setOnClickListener(this);
        cardView3.setOnClickListener(this);
        cardView4.setOnClickListener(this);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(dashbord_activity.this,login.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.cardPic:
                i=new Intent(this,homeIden.class);
                startActivity(i);
                break;

            case R.id.cardData:
                i=new Intent(this, infoDesk.class);
                startActivity(i);
                break;

            case R.id.cardDetail:
                i=new Intent(dashbord_activity.this, record.class);
                startActivity(i);
                break;

            case R.id.cardAbout:
                i=new Intent(dashbord_activity.this, aboutUs.class);
                startActivity(i);
                break;
            default:
                break;

        }


    }
}