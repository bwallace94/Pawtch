package edu.mit.pawtch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Priscila Cortez on 5/4/2016.
 */
public class DecreaseFoodScore extends AsyncTask{

    private SharedPreferences sharedPref;
    final Context mContext;
    String TAG = "PAWTCH";

    public DecreaseFoodScore(final Context context){
        this.mContext = context;
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this.mContext);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        while(true){
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Log.e(TAG, "Coundn't sleep...");
            }
            Log.e(TAG, "We are in background and sleeping!");
            int feedingScore = sharedPref.getInt("feedingScore",0);
            Long lastUpdate = sharedPref.getLong("lastFeedUpdate", System.currentTimeMillis());
            Long currentTime = System.currentTimeMillis();
            Long difference = currentTime - lastUpdate;

            SharedPreferences.Editor editor = sharedPref.edit();

            if (difference >= 10000){
                if (feedingScore > 0){
                    Log.e(TAG,"FEEDING SCORE: " + feedingScore);
                    editor.putInt("feedingScore", feedingScore - 1);
                    editor.putLong("lastFeedUpdate", System.currentTimeMillis());
                    editor.apply();
                    Log.e(TAG, "NEW FEEDING SCORE: " + Integer.toString(feedingScore - 1));
                }
            }
        }
    }
}
