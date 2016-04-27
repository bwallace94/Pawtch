package edu.mit.pawtch;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.HistoryApi;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.wearable.Wearable;

import java.text.DateFormat;
import java.text.Format;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GridViewFragmentActivity extends FragmentActivity {
    public static final String TAG = "PAWTCH";
    private static final int REQUEST_OAUTH = 1;
    private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";


    public GoogleApiClient mClient = null;
    private static final String AUTH_PENDING = "auth_state_pending";
    private static boolean authInProgress = false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

//    Context context = this;
//    private SharedPreferences sharedPref = context.getSharedPreferences(
//            "edu.mit.pawtch.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
//    private SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pawtch);

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        //Google API Client
        buildFitnessClient();

        mClient.connect();

        final Resources res = getResources();
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);

        pager.setAdapter(new myAdapter(this));
        DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        dotsPageIndicator.setPager(pager);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    protected void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        buildFitnessClient();
        mClient.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "GridViewFragment Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://edu.mit.pawtch/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    protected void onResume() {
        super.onResume();

        buildFitnessClient();
    }

    private void buildFitnessClient() {
        Log.e(TAG, "In buildFitnessClient");
        if (mClient == null) {
            Log.e(TAG, "mClient is null");
            mClient = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .addApi(Fitness.HISTORY_API)
                            //.addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                    .addConnectionCallbacks(
                            new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(Bundle bundle) {
                                    Log.e(TAG, "Connected!!");

                                    // MAKE CALLS TO THE FITNESS APIs BELOW

                                    new InsertAndVerifyDataTask(mClient).execute();
                                }

                                @Override
                                public void onConnectionSuspended(int i) {
                                    if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                        Log.e(TAG, "Connection Lost. Cause: Network Lost.");
                                    } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                        Log.e(TAG, "Connection Lost. Reason: Service Disconnected");
                                    }
                                }
                            }
                    )
                    .useDefaultAccount()
                    .build();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "GridViewFragment Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://edu.mit.pawtch/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public GoogleApiClient getClient(){
        return mClient;
    }

    class InsertAndVerifyDataTask extends AsyncTask<Void, Void, Void> {

        public double numSteps = 0;
        public double goalSteps = 0;
        public double minExer = 0;
        public double goalExer = 0;
        /*private GoogleApiClient mClient;

        public InsertAndVerifyDataTask(GoogleApiClient client){
            mClient = client;
        }*/

        public double getNumSteps(){
            return numSteps;
        }

        public double getGoalSteps(){
            return goalSteps;
        }

        public double getMinExer(){
            return minExer;
        }

        public double getGoalExer(){
            return goalExer;
        }

        @Override
        protected Void doInBackground(Void... params) {

            Calendar cal = Calendar.getInstance();
            Date now = new Date();
            cal.setTime(now);

            long endTime = cal.getTimeInMillis();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long startTime = cal.getTimeInMillis();

            // Get the steps
            PendingResult<DailyTotalResult> pendingStepsResult = Fitness.HistoryApi.readDailyTotal(
                    mClient,
                    DataType.AGGREGATE_CALORIES_EXPENDED
            );

            DailyTotalResult stepResult = pendingStepsResult.await(1, TimeUnit.MINUTES);
            int steps = 0;
            Log.e(TAG,"STEP RESULT: " + stepResult.getStatus().isSuccess());
            if (stepResult.getStatus().isSuccess()){
                DataSet stepSet = stepResult.getTotal();
                steps = stepSet.isEmpty() ? -1 : stepSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
            }

            numSteps = steps;

            // Get the goal for steps
            PendingResult<DailyTotalResult> pendingExerResult = Fitness.HistoryApi.readDailyTotal(
                    mClient,
                    DataType.TYPE_CALORIES_EXPENDED
            );

            DailyTotalResult exerResult = pendingExerResult.await(2, TimeUnit.MINUTES);
            double minutesOfExer = 0;
            Log.e(TAG,"EXERRESULT SUCCESSFUL? " + exerResult.getStatus().isSuccess());
            if (exerResult.getStatus().isSuccess()){
                Log.e(TAG,"SUCCESSFUL MIN EXERCISE");
                DataSet exerSet = exerResult.getTotal();
                Log.e(TAG,"YOU ARE EXERCISING: " + exerSet.getDataPoints().get(0));
                minutesOfExer = exerSet.isEmpty() ? -1 : exerSet.getDataPoints().get(0).getValue(Field.FIELD_MIN).asInt();
            }

            minExer = minutesOfExer;

            DataReadRequest exerRequest = new DataReadRequest.Builder()
                    .read(DataType.TYPE_CALORIES_EXPENDED)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .build();

            DataReadResult exerReadResult = Fitness.HistoryApi.readData(mClient, exerRequest).await(1, TimeUnit.MINUTES);
            Log.e(TAG,"OTHER EXERRESULT: " + exerReadResult.getStatus().isSuccess());
            Log.e(TAG, "GOT RESULTS! :D");
            //Log.e(TAG, "Start Time: " + startTime);
            //Log.e(TAG, "End Time: " + endTime);
            Log.e(TAG, "Step Count: " + numSteps);
            Log.e(TAG, "Activity Min: " + minExer);
            //Log.e(TAG, "Aggregate Activity: " + aggregate_activity);

            return null;
        }
    }

    public class myAdapter extends GridPagerAdapter {

        public double numSteps = 0;
        private SharedPreferences sharedPref;
        final Context mContext;

        public myAdapter(final Context context) {
            this.mContext = context;
            this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        }

        @Override
        public int getRowCount() {
            return 4;
        }

        @Override
        public int getColumnCount(int row) {
            return 3;
        }

        @Override
        public int getCurrentColumnForRow(int row, int currentColumn) {
            return currentColumn;
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, int row, int col) {
            View view;
            if (row == 0 && col == 0) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup, false);
                final TextView tv = (TextView) view.findViewById(R.id.pageTitle1);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon1);
                tv.setText("Mochi");
                iv.setImageResource(R.drawable.panda);
            } else if (row == 1 && col == 0) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_two_text, viewGroup, false);
                final TextView tv1 = (TextView) view.findViewById(R.id.pageTitle2);
                final TextView tv2 = (TextView) view.findViewById(R.id.FitInfo);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon2);
                tv1.setText("Gender");
                tv2.setText("Male  ");
                iv.setImageResource(R.drawable.male);
            } else if (row == 2 && col == 0) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_two_text, viewGroup, false);
                final TextView tv1 = (TextView) view.findViewById(R.id.pageTitle2);
                final TextView tv2 = (TextView) view.findViewById(R.id.FitInfo);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon2);
                tv1.setText("Age");
                tv2.setText("1 month old ");
                iv.setImageResource(R.drawable.birthdaycake);
            } else if (row == 3 && col == 0) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup, false);
                final TextView tv = (TextView) view.findViewById(R.id.pageTitle1);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon1);
                tv.setText("Bio");
                iv.setImageResource(R.drawable.panda);
            } else if (row == 0 && col == 1) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup, false);
                final TextView tv = (TextView) view.findViewById(R.id.pageTitle1);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon1);
                tv.setText("Happiness");
                iv.setImageResource(R.drawable.heart);
            } else if (row == 1 && col == 1) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_two_text, viewGroup, false);
                final TextView tv1 = (TextView) view.findViewById(R.id.pageTitle2);
                final TextView tv2 = (TextView) view.findViewById(R.id.FitInfo);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon2);
                tv1.setText("Walking");
                tv2.setText("1000  ");
                iv.setImageResource(R.drawable.paw);
            } else if (row == 2 && col == 1) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_two_text, viewGroup, false);
                final TextView tv1 = (TextView) view.findViewById(R.id.pageTitle2);
                final TextView tv2 = (TextView) view.findViewById(R.id.FitInfo);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon2);
                tv1.setText("Playing");
                tv2.setText("10 min  ");
                iv.setImageResource(R.drawable.dumbbell);
            } else if (row == 3 && col == 1) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_two_text, viewGroup, false);
                final TextView tv1 = (TextView) view.findViewById(R.id.pageTitle2);
                final TextView tv2 = (TextView) view.findViewById(R.id.FitInfo);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon2);
                tv1.setText("Feeding");
                int feedingScore = sharedPref.getInt("feedingScore", 0);
                Log.e("BRIA: Feeding Score: ", Integer.toString(feedingScore));
                tv2.setText(Integer.toString(feedingScore));
                iv.setImageResource(R.drawable.meter);
            } else if (row == 0 && col == 2) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup, false);
                final TextView tv = (TextView) view.findViewById(R.id.pageTitle1);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon1);
                tv.setText("Scroll & click to feed!");
                iv.setImageResource(R.drawable.arrow);
            } else if (row == 1 && col == 2) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup, false);
                final TextView tv = (TextView) view.findViewById(R.id.pageTitle1);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon1);
                tv.setText("Bamboo");
                iv.setClickable(true);
                iv.setImageResource(R.drawable.bamboo);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateFoodScoreAndTime();
                    }
                });
            } else if (row == 2 && col == 2) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup, false);
                final TextView tv = (TextView) view.findViewById(R.id.pageTitle1);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon1);
                tv.setText("Water");
                iv.setClickable(true);
                iv.setImageResource(R.drawable.water);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateFoodScoreAndTime();
                    }
                });
            } else if (row == 3 && col == 2) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup, false);
                final TextView tv = (TextView) view.findViewById(R.id.pageTitle1);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon1);
                tv.setText("Ice Cream");
                iv.setClickable(true);
                iv.setImageResource(R.drawable.icecream);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateFoodScoreAndTime();
                    }
                });
            } else {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup, false);
                final TextView tv = (TextView) view.findViewById(R.id.pageTitle1);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon1);
                tv.setText("IGNORE");
            }
            viewGroup.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup viewGroup, int row, int col, Object o) {
            viewGroup.removeView((View) o);
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view.equals(o);
        }

        public void updateFoodScoreAndTime() {
            int feedingScore = sharedPref.getInt("feedingScore", 0);
            String lastFeed = sharedPref.getString("lastFeedTime", "12:00");
            SharedPreferences.Editor editor = sharedPref.edit();
            if (feedingScore < 5) {
                editor.putInt("feedingScore", feedingScore + 1);
                editor.apply();
            }
            long currentTime = System.currentTimeMillis();
            editor.putString("lastFeedTime", Long.toString(currentTime));
            editor.apply();
        }

        private void getAndUpdateSteps() {
            Calendar cal = Calendar.getInstance();
            Date now = new Date();
            cal.setTime(now);

            long endTime = cal.getTimeInMillis();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long startTime = cal.getTimeInMillis();

            PendingResult<DataReadResult> pendingResult = Fitness.HistoryApi.readData(
                    mClient,
                    new DataReadRequest.Builder()
                            .read(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                            .read(DataType.AGGREGATE_ACTIVITY_SUMMARY)
                            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                            .build()
            );

            DataReadResult readDataResult = pendingResult.await();
            DataSet step_count_cumulative = readDataResult.getDataSet(DataType.TYPE_STEP_COUNT_CUMULATIVE);
            DataSet aggregate_activity = readDataResult.getDataSet(DataType.AGGREGATE_ACTIVITY_SUMMARY);
        }
    }
}

