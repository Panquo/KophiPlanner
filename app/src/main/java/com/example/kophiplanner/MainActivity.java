package com.example.kophiplanner;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.preference.PreferenceManager;

import java.util.List;


public class MainActivity extends AppCompatActivity {



    public final static String MAINCODE = "code";
    ImageView cimg = null;
    ProgressBar pb = null;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        cimg = (ImageView) findViewById(R.id.coverimg);
        pb = (ProgressBar) findViewById(R.id.pgrssBar);
        ImageButton stat_but = (ImageButton) findViewById(R.id.stats_but);
        final ImageButton cam_but = (ImageButton) findViewById(R.id.settings_but);
        ImageButton list_But = (ImageButton) findViewById(R.id.kList_but);
        ImageButton K_But = (ImageButton) findViewById(R.id.takeK_but);
        final TextView txt = (TextView) findViewById(R.id.text);
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        //db = new CoffeeDB(this);
        //app.setDB(db);




        list_But.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                cimg.setVisibility(View.VISIBLE);
                presentActivity(v,2);


            }
        });


        K_But.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sp.getBoolean("camera", false)) {
                    pb.setVisibility(View.VISIBLE);
                    cimg.setVisibility(View.VISIBLE);

                }
                presentActivity(v,1);
            }
        });

        /*Stock_But.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sp.getBoolean(CAMERA_PREF,false)){
                    presentActivity(v,0);
                }else {
                    pb.setVisibility(View.VISIBLE);
                    cimg.setVisibility(View.VISIBLE);
                    presentActivity(v,0);
                }
            }
        });*/

        cam_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Log.d("CAMERA STATUS","cam status is "+ sp.getBoolean(CAMERA_PREF,false));
                if(sp.getBoolean(CAMERA_PREF,false)){
                    cam_but.setImageResource(R.drawable.no_cam_icon);
                    sp.edit().putBoolean(CAMERA_PREF,false).apply();
                    app.setCAMERA_STATUS(sp.getBoolean(CAMERA_PREF,false));
                }else {
                    cam_but.setImageResource(R.drawable.cam_icon);
                    sp.edit().putBoolean(CAMERA_PREF,true).apply();
                    app.setCAMERA_STATUS(sp.getBoolean(CAMERA_PREF,false));
                }*/
                presentActivity(v,6);


            }
        });

        stat_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Soon !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        pb.setVisibility(View.INVISIBLE);
        cimg.setVisibility(View.INVISIBLE);
    }


    public void presentActivity(View view,int code) {
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);
        Intent intent;
        if (sp.getBoolean("camera", false)&&code==1) {
            intent = new Intent(MainActivity.this, ScanActivity.class);
        }else if(code==6){
            intent = new Intent(MainActivity.this, SettingsActivity.class);
        }else{
            intent = new Intent(MainActivity.this, ListActivity.class);
        }
        intent.putExtra(MAINCODE, code);

        //overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        intent.putExtra(ListActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(ListActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }


}
