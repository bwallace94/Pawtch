package edu.mit.pawtch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(arg0);
        int feedingScore = sharedPref.getInt("feedingScore", 0);
        if (feedingScore > 0){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("feedingScore", feedingScore-1);
            editor.apply();
        }
    }

}
