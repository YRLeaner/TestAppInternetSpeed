package com.example.tyr.showyourspeed.Float;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.tyr.showyourspeed.R;
import com.example.tyr.showyourspeed.list.ShowMessage;
import com.example.tyr.showyourspeed.list.SpeedMsgAdapter;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tyr on 2016/5/26.
 */
public class FxService extends Service {

    LinearLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    WindowManager mWindowManager;

    private List<ShowMessage> mShowmsg,oldShowmsg;
    private int FIRST = 0;
    private Timer mTimer;

    private ListView mFloatView;
    private List<ShowMessage> mDatas;
    private SpeedMsgAdapter speedMsgAdapter;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            ShowMessage showMessage = (ShowMessage)msg.obj;
            mDatas.add(showMessage);
            speedMsgAdapter.notifyDataSetChanged();

        }
    };

    private Button showButton;

    private static final String TAG = "FxService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "oncreat");
        mShowmsg = new ArrayList<ShowMessage>();
        oldShowmsg = new ArrayList<ShowMessage>();
        createFloatView();

    }
    private void initDatas() {
        mDatas = new ArrayList<ShowMessage>();
        mDatas.add(new ShowMessage("title", 123));

        speedMsgAdapter = new SpeedMsgAdapter(this,mDatas);
        mFloatView.setAdapter(speedMsgAdapter);

    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createFloatView() {
        initwmParam();

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.listview_layout, null);
        mWindowManager.addView(mFloatLayout, wmParams);

        /*
        加入数据
         */
        mFloatView = (ListView) mFloatLayout.findViewById(R.id.list_layout_item);
        initDatas();
        getAppTrafficList();

        //定时刷新数据
        /* TimerTask task = new TimerTask() {
            @Override
            public void run() {
                mDatas.clear();
                getAppTrafficList();
            }
        };
        mTimer = new Timer();
        mTimer.schedule(task, 1000, 1000);*/
        Thread mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mDatas.clear();
                    getAppTrafficList();

                }
            }
        });

        mThread.start();


        //处理listView的移动
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth() / 2);

        mFloatView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getX() < v.getLeft() || event.getX() > v.getRight() || event.getY() < v.getTop() || event.getY() > v.getBottom()) {
                    mFloatView.setVisibility(View.GONE);
                    showButton.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        showButton = (Button)mFloatLayout.findViewById(R.id.show_btn);
        showButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                wmParams.x = (int) event.getRawX() - showButton.getMeasuredWidth() / 2;
                wmParams.y = (int) event.getRawY() - showButton.getMeasuredHeight() / 2 - 25;
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                return false;
            }
        });

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFloatView.getVisibility()==View.GONE){
                    mFloatView.setVisibility(View.VISIBLE);
                    showButton.setVisibility(View.GONE);
                }
            }
        });


    }

    //初始化wmParam
    private void initwmParam() {
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public void onDestroy(){
        super.onDestroy();
       // mTimer.cancel();
        if (mFloatView!=null){

            mWindowManager.removeView(mFloatView);
        }
    }


    /*
    * 获取应用程序网速*/
    private void getAppTrafficList() {
        oldShowmsg.clear();
        oldShowmsg.addAll(mShowmsg);
        PackageManager pm = getPackageManager();
        List<PackageInfo> pinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);
        //获取每个app的包信息
        mShowmsg.clear();
        for (PackageInfo info:pinfos){
            String[] permissions = info.requestedPermissions;
            if (permissions!=null&&permissions.length>0){
                //找到有上网权限的APP
                for (String permission:permissions){
                    if ("android.permission.INTERNET".equals(permission)){
                        int uId = info.applicationInfo.uid;
                        //获取到现在为止该APP使用的总流量
                        long rx = TrafficStats.getUidRxBytes(uId);
                        long tx = TrafficStats.getUidTxBytes(uId);
                        if (rx<0||tx<0){
                            continue;
                        }else {
                            //得到上网总流量
                            if ((/*(nrx+ntx)-*/(rx+tx))>0){
                                ShowMessage message = new ShowMessage(info.applicationInfo.loadLabel(pm)+" ",rx+tx);
                                /*Message m  = Message.obtain();
                                m.obj = message;
                                mHandler.sendMessage(m);*/
                                mShowmsg.add(message);
                               // Log.d("INTEERNET",info.applicationInfo.loadLabel(pm)+" "+android.text.format.Formatter.formatFileSize(this,((/*(nrx+ntx)-*/(rx+tx))))+"/s");

                            }
                        }
                    }
                }
            }
        }

        for (ShowMessage sm:mShowmsg){
            if (FIRST==0){
                Log.d("INTEERNET",sm.getTitle()+" 1"+(sm.getSpeed()));
                FIRST++;
            }else {
                for (ShowMessage olm:oldShowmsg){
                    //Log.d("INTEERNET",sm.getTitle()+" "+(sm.getSpeed()+(olm.getSpeed())));
                    if (olm.getTitle().equals(sm.getTitle())&&((sm.getSpeed()-olm.getSpeed())>0)){
                        Log.d("INTEERNET",sm.getTitle()+" "+(sm.getSpeed()-olm.getSpeed()));
                        ShowMessage postsm = new ShowMessage(sm.getTitle(),(sm.getSpeed()-olm.getSpeed()));
                        Message m  = Message.obtain();
                        m.obj = postsm;
                        mHandler.sendMessage(m);
                        break;
                    }

                }
            }

        }
    }


    public void logic() {

    }


}
