package com.asiawaters.fieldapprover;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;

import com.asiawaters.fieldapprover.classes.Model_NetState;
import com.asiawaters.fieldapprover.classes.Model_Person;
import com.asiawaters.fieldapprover.classes.NetListener;
import com.asiawaters.fieldapprover.classes.Network_Helper;

public class SplashActivity extends Activity {

    private int mSplashTime = 3500;
    private Timer mTimer;
    private Model_NetState model_netState = new Model_NetState();
    private NetListener mnetListener = new NetListener();
    private Model_Person person = null;
    private FieldApprover FA;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        model_netState.setConnected(Network_Helper.isOnline(getBaseContext()));
        model_netState.setOnline(Network_Helper.isConnectedbyPing(""));
        model_netState.setContext(this);
        model_netState.setUrl("");

        FA = ((com.asiawaters.fieldapprover.FieldApprover) getApplication());

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
        FA.setModel_netState(model_netState);
        FA.setMnetListener(mnetListener);

        startMainActivity();
    }


    @Override
    public boolean onTouchEvent(MotionEvent evt)
    {
        if(evt.getAction() == MotionEvent.ACTION_DOWN)
        {
            mTimer.cancel();
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

    @Override
    public void onResume() {
        super.onResume();
    }

    public void startMainActivity() {
        this.finish();
        Intent intent ;
        if (FA.getList_values() == null) intent = new Intent(getApplicationContext(), LoginActivity.class);
        else intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
