package com.svs57.myworkerslistedit;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

public class MyWorker extends  Worker {
    static final String TAG = "workmng";
    public MyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork(){
        Log.d(TAG, "doWork start");
        try
        {
            for(int i=0;i<20;i++)
            {
                Log.d(TAG, "working "+ i);
                TimeUnit.SECONDS.sleep(1);
            }
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        Log.d(TAG, "doWork end");
        return Result.retry();
    }

}
