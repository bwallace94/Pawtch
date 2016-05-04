package edu.mit.pawtch;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
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

import org.w3c.dom.Text;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class GridViewFragmentActivity extends Activity {
    public static final String TAG = "PAWTCH";
    private static final int REQUEST_OAUTH = 1;
    private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";


    public GoogleApiClient mClient = null;
    private static final String AUTH_PENDING = "auth_state_pending";
    private static boolean authInProgress = false;

    public double numSteps = 3493.0;

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
        final myAdapter newAdapter = new myAdapter(this);
        newAdapter.notifyDataSetChanged();
        pager.setAdapter(newAdapter);
        newAdapter.notifyDataSetChanged();
        DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        dotsPageIndicator.setPager(pager);



//        View myView2 = findViewById(R.id.one_image_two_text);
//        myView2.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                newAdapter.notifyDataSetChanged();
//                return true;
//            }
//        });

        // Setting up alarms
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int interval = 60000;
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, System.currentTimeMillis(), interval, pendingIntent);
    }

    protected void onStart() {
        super.onStart();
        buildFitnessClient();
        mClient.connect();
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

                                    new InsertAndVerifyDataTask().execute();
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
        mClient.disconnect();
    }

    class InsertAndVerifyDataTask extends AsyncTask<Void, Void, Void> {

        private double goalSteps = 0;
        private double minExer = 0;
        private double goalExer = 0;

        public double getNumSteps() {
            return numSteps;
        }

        public double getGoalSteps() {
            return goalSteps;
        }

        public double getMinExer() {
            return minExer;
        }

        public double getGoalExer() {
            return goalExer;
        }

        @Override
        protected Void doInBackground(Void... params) {

            Calendar cal = Calendar.getInstance();
            Date now = new Date();
            cal.setTime(now);
            Log.e("BRIA || ", "FITNESS ACTIVITY " + FitnessActivities.class.getFields().toString());


            long endTime = cal.getTimeInMillis();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long startTime = cal.getTimeInMillis();

            // Get the steps
            PendingResult<DailyTotalResult> pendingStepsResult = Fitness.HistoryApi.readDailyTotal(
                    mClient,
                    DataType.AGGREGATE_STEP_COUNT_DELTA
            );

            DailyTotalResult stepResult = pendingStepsResult.await(1, TimeUnit.MINUTES);
            int steps = 0;
            Log.e(TAG, "STEP RESULT: " + stepResult.getStatus().isSuccess());
            if (stepResult.getStatus().isSuccess()) {
                DataSet stepSet = stepResult.getTotal();
                steps = stepSet.isEmpty() ? 0 : stepSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
            }

            numSteps = steps;

            // Get the goal for steps
            PendingResult<DailyTotalResult> pendingExerResult = Fitness.HistoryApi.readDailyTotal(
                    mClient,
                    DataType.TYPE_CALORIES_EXPENDED
            );

            DailyTotalResult exerResult = pendingExerResult.await(2, TimeUnit.MINUTES);
            double minutesOfExer = 0;
            Log.e(TAG, "EXERRESULT SUCCESSFUL? " + exerResult.getStatus().isSuccess());
            if (exerResult.getStatus().isSuccess()) {
                Log.e(TAG, "SUCCESSFUL MIN EXERCISE");
                DataSet exerSet = exerResult.getTotal();
                Log.e(TAG, "YOU ARE EXERCISING: " + exerSet.getDataPoints().get(0));
                minutesOfExer = exerSet.isEmpty() ? 0 : exerSet.getDataPoints().get(0).getValue(Field.FIELD_MIN).asInt();
            }

            minExer = minutesOfExer;

            DataReadRequest exerRequest = new DataReadRequest.Builder()
                    .read(DataType.TYPE_CALORIES_EXPENDED)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .build();

            DataReadResult exerReadResult = Fitness.HistoryApi.readData(mClient, exerRequest).await(1, TimeUnit.MINUTES);
            Log.e(TAG, "OTHER EXERRESULT: " + exerReadResult.getStatus().isSuccess());
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

        private SharedPreferences sharedPref;
        View[][] views = new View[3][4];
        ViewGroup myVG;
        final Context mContext;
        public double goalSteps = 10000.0;
        public double minExer = 21.0;
        public double goalExer = 60.0;

        public myAdapter(final Context context) {
            this.mContext = context;
            this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        }

        @Override
        public int getRowCount() {
            return 3;
        }

        @Override
        public int getColumnCount(int row) {
            if (row == 0) {
                return 1;
            } else if (row == 1) {
                return 3;
            }
            return 4;
        }

        @Override
        public int getCurrentColumnForRow(int row, int currentColumn) {
            return 0;
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, int row, int col) {
            View view;
            if (row == 0 && col == 0) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup, false);
                final TextView tv = (TextView) view.findViewById(R.id.pageTitle1);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon1);
                final TextView tv2 = (TextView) view.findViewById(R.id.upperTitle1);
                tv2.setText(" Pawtch");
                tv.setText("Mochi");
                iv.setImageResource(R.drawable.panda);
            } else if (row == 1 && col == 0) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup,false);
                final TextView tv = (TextView) view.findViewById(R.id.pageTitle1);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon1);
                final TextView tv2 = (TextView) view.findViewById(R.id.upperTitle1);
                tv2.setText(" Stats");
                setHappinessAndPicture(iv);
                int newHappinessScore = sharedPref.getInt("happinessScore",0);
                Log.e("BRIA: ", "STORED HAPPINESS || " + newHappinessScore);
                tv.setText("Happiness:  " + Integer.toString(newHappinessScore) + "%");
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("BRIA: ", "REACHES THE UPDATE HEART");
                        setHappinessAndPicture(iv);
                        int newHappinessScore = sharedPref.getInt("happinessScore", 0);
                        Log.e("BRIA: ", "STORED HAPPINESS || " + newHappinessScore);
                        tv.setText("Happiness:  " + Integer.toString(newHappinessScore) + "%");
                    }
                });
            } else if (row == 1 && col == 1) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_two_text, viewGroup, false);
                final TextView tv1 = (TextView) view.findViewById(R.id.pageTitle2);
                final TextView tv2 = (TextView) view.findViewById(R.id.FitInfo);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon2);
                final TextView tv3 = (TextView) view.findViewById(R.id.upperTitle2);
                // Calling API
                new InsertAndVerifyDataTask().execute();

                tv1.setText("Walking");
                tv2.setText((int)numSteps + "  ");
                tv3.setText(" Stats");
                iv.setImageResource(R.drawable.paw);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new InsertAndVerifyDataTask().execute();
                        Log.e("BRIA: ", "REACHES THE UPDATE WALKING");
                        tv2.setText((int) numSteps + "  ");
                    }
                });
            } else if (row == 1 && col == 3) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_two_text, viewGroup, false);
                final TextView tv1 = (TextView) view.findViewById(R.id.pageTitle2);
                final TextView tv2 = (TextView) view.findViewById(R.id.FitInfo);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon2);
                final TextView tv3 = (TextView) view.findViewById(R.id.upperTitle2);
                tv1.setText("Playing");
                tv2.setText("10 min  ");
                tv3.setText(" Stats"); 
                iv.setImageResource(R.drawable.dumbbell);
            } else if (row == 1 && col == 2) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup, false);
                final TextView tv = (TextView) view.findViewById(R.id.pageTitle1);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon1);
                final TextView tv2 = (TextView) view.findViewById(R.id.upperTitle1);
                int feedingScore = sharedPref.getInt("feedingScore", 0);
                tv2.setText(" Stats");
                tv.setText("Hunger: " + feedingScore + "/4");
                Log.e("BRIA: Feeding Score: ", Integer.toString(feedingScore));
                if (feedingScore == 0){
                    iv.setImageResource(R.drawable.meter1);
                }
                else if (feedingScore == 1){
                    iv.setImageResource(R.drawable.meter2);
                }
                else if (feedingScore == 2){
                    iv.setImageResource(R.drawable.meter3);
                }
                else if (feedingScore == 3){
                    iv.setImageResource(R.drawable.meter4);
                }
                else if (feedingScore == 4){
                    iv.setImageResource(R.drawable.meter5);
                }
                else{
                    iv.setImageResource(R.drawable.meter1);
                }
            } else if (row == 2 && col == 0) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup,false);
                final TextView tv = (TextView) view.findViewById(R.id.pageTitle1);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon1);
                final TextView tv2 = (TextView) view.findViewById(R.id.upperTitle1);
                tv2.setText(" Food");
                tv.setText("Scroll & click to feed!");
                iv.setImageResource(R.drawable.arrow);
            } else if (row == 2 && col == 1) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup,false);
                final TextView tv = (TextView) view.findViewById(R.id.pageTitle1);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon1);
                final TextView tv2 = (TextView) view.findViewById(R.id.upperTitle1);
                tv2.setText(" Food");
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
                final TextView tv2 = (TextView) view.findViewById(R.id.upperTitle1);
                tv2.setText(" Food");
                tv.setText("Water");
                iv.setClickable(true);
                iv.setImageResource(R.drawable.water);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateFoodScoreAndTime();
                    }
                });
            } else if (row == 2 && col == 3) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup,false);
                final TextView tv = (TextView) view.findViewById(R.id.pageTitle1);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon1);
                final TextView tv2 = (TextView) view.findViewById(R.id.upperTitle1);
                tv2.setText(" Food");
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
                final TextView tv2 = (TextView) view.findViewById(R.id.upperTitle1);
                tv2.setText(" Food");
                tv.setText("IGNORE");
            }
            viewGroup.addView(view);
            myVG = viewGroup;
            views[row][col] = view;
            return view;
        }

        @Override
        public void destroyItem(ViewGroup viewGroup, int row, int col, Object o) {
            viewGroup.removeView((View) o);
        }

        @Override
        public Point getItemPosition(Object o) {
            return POSITION_NONE;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
//            return view.equals(o);
        }

        public void updateFoodScoreAndTime() {
            int feedingScore = sharedPref.getInt("feedingScore", 0);
            int newFeedingScore = feedingScore + 1;
            String lastFeed = sharedPref.getString("lastFeedTime", "12:00");
            SharedPreferences.Editor editor = sharedPref.edit();

            if (feedingScore < 4){
                editor.putInt("feedingScore", newFeedingScore);
                editor.apply();
            } else {newFeedingScore = 4;}
            long currentTime = System.currentTimeMillis();
            editor.putString("lastFeedTime", Long.toString(currentTime));
            editor.apply();
            Toast.makeText(mContext,"You have fed your pet! Current feeding level: " + newFeedingScore + "/4",
                    Toast.LENGTH_SHORT).show();
        }

        public void setHappinessAndPicture(ImageView iv) {
            double feedingPercent = sharedPref.getInt("feedingScore", 0) / 4.0;
            double walkingPercent = numSteps/goalSteps;
//            double exercisePercent = minExer/goalExer;
            double happiness = (.5*walkingPercent + .5*feedingPercent)*100.0;
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("happinessScore", (int) happiness);
            editor.apply();
            editor.apply();
            Log.e("BRIA", "HAPPINESS || " + happiness);
            Log.e("BRIA", "HAPPINESS || " + (int) happiness);
            Log.e("BRIA", "NEWLY SET HAPPINESS || " + sharedPref.getInt("happinessScore", 0));
            if (0.0 <= happiness  && happiness < 10.0) {
                iv.setImageResource(R.drawable.heart0);
            } else if (10 <= happiness && happiness < 20.0) {
                iv.setImageResource(R.drawable.heart1);
            } else if (20 <= happiness && happiness < 30.0) {
                iv.setImageResource(R.drawable.heart2);
            } else if (30 <= happiness && happiness < 40.0) {
                iv.setImageResource(R.drawable.heart3);
            } else if (40 <= happiness && happiness < 50.0) {
                iv.setImageResource(R.drawable.heart4);
            } else if (50 <= happiness && happiness < 60.0) {
                iv.setImageResource(R.drawable.heart5);
            } else if (60 <= happiness && happiness < 70.0) {
                iv.setImageResource(R.drawable.heart6);
            } else if (70 <= happiness && happiness < 80.0) {
                iv.setImageResource(R.drawable.heart7);
            } else if (80 <= happiness && happiness < 90.0) {
                iv.setImageResource(R.drawable.heart8);
            } else {
                iv.setImageResource(R.drawable.heart9);
            }
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
