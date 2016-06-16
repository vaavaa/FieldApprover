package com.asiawaters.fieldapprover;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;

import com.asiawaters.fieldapprover.classes.Model_NetState;
import com.asiawaters.fieldapprover.classes.NetListener;
import com.asiawaters.fieldapprover.classes.Network_Helper;

public class SplashActivity extends Activity {

    private int mSplashTime = 4500;
    private Timer mTimer;
    private Model_NetState model_netState = new Model_NetState();
    private NetListener mnetListener = new NetListener();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        model_netState.setConnected(Network_Helper.isOnline(getBaseContext()));
        model_netState.setOnline(Network_Helper.isConnectedbyPing(""));
        model_netState.setContext(this);
        model_netState.setUrl("");



        mTimer = new Timer();
        mTimer.schedule(
                new TimerTask() {
                    public void run() {
                        stopSplash();
                    }
                },
                mSplashTime);
    }
    @Override
    public void onStart(){
        super.onStart();
        RunStatListener();
    }
    private void stopSplash() {
        ((FieldApprover) getApplication()).setModel_netState(model_netState);
        ((FieldApprover) getApplication()).setMnetListener(mnetListener);
        finish();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent evt)
    {
        if(evt.getAction() == MotionEvent.ACTION_DOWN)
        {
            stopSplash();
        }
        return true;
    }

    public void RunStatListener() {
        if (mnetListener.getStatus() != AsyncTask.Status.RUNNING) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mnetListener.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, model_netState, model_netState);
            } else {
                mnetListener.execute(model_netState, model_netState);
            }
        }
    }
}
