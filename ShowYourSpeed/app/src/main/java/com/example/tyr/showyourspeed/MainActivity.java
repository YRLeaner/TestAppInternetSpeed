package com.example.tyr.showyourspeed;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tyr.showyourspeed.Float.FxService;
import com.example.tyr.showyourspeed.list.ShowMessage;
import com.example.tyr.showyourspeed.list.SpeedMsgAdapter;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Button showButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        showButton.setOnClickListener(new ButtonListener());

    }



    private void initView() {
        showButton = (Button)findViewById(R.id.show);
    }

    private class ButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            if (Build.VERSION.SDK_INT>=23){
                if (Settings.canDrawOverlays(getBaseContext())){
                    Intent intent = new Intent(MainActivity.this,FxService.class);
                    startService(intent);
                    Log.d("fas", "fasfasfafa");
                    finish();
                }else {
                    Intent intent2 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    startActivity(intent2);
                    Intent intent = new Intent(MainActivity.this,FxService.class);
                    startService(intent);
                    finish();
                }
            }else {
                Intent intent = new Intent(MainActivity.this,FxService.class);
                startService(intent);
                Log.d("fas", "fasfasfafa");
                finish();
            }

        }
    }


}
