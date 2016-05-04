package edu.mit.pawtch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Priscila Cortez on 5/4/2016.
 */
public class DecreaseFoodScore extends BroadcastReceiver{

    private SharedPreferences sharedPref;
    Context mContext;
    String TAG = "PAWTCH";
    int INTERVAL = 10000;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        Log.e(TAG, "WE ARE ABOUT TO CHANGE THE FEEDING WOOOOOOO!");
        int feedingScore = sharedPref.getInt("feedingScore",0);
        long lastUpdate = sharedPref.getLong("lastFeedUpdate", System.currentTimeMillis());
        long currentTime = System.currentTimeMillis();
        long difference = currentTime - lastUpdate;

        SharedPreferences.Editor editor = sharedPref.edit();

        if (difference >= INTERVAL && feedingScore > 0){
            int changeInFeeding = (int) difference/INTERVAL;
            int newFeedingScore = feedingScore - changeInFeeding;

            if (newFeedingScore < 0){
                newFeedingScore = 0;
            }

            Log.e(TAG,"FEEDING SCORE: " + feedingScore);
            editor.putInt("feedingScore", newFeedingScore);
            editor.putLong("lastFeedUpdate", System.currentTimeMillis());
            editor.apply();
            Log.e(TAG, "NEW FEEDING SCORE: " + Integer.toString(newFeedingScore));
        }
    }
}
