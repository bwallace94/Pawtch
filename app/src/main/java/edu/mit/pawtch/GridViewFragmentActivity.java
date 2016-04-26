package edu.mit.pawtch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.Image;
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

public class GridViewFragmentActivity extends Activity {

//    Context context = this;
//    private SharedPreferences sharedPref = context.getSharedPreferences(
//            "edu.mit.pawtch.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
//    private SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pawtch);
        final Resources res = getResources();
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);

        pager.setAdapter(new myAdapter(this));
        DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        dotsPageIndicator.setPager(pager);
    }

    public class myAdapter extends GridPagerAdapter {
        private SharedPreferences sharedPref;
        final Context mContext;

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
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup,false);
                final TextView tv = (TextView) view.findViewById(R.id.pageTitle1);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon1);
                tv.setText("Mochi");
                iv.setImageResource(R.drawable.panda);
            } else if (row == 0 && col == 1) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_two_text, viewGroup, false);
                final TextView tv1 = (TextView) view.findViewById(R.id.pageTitle2);
                final TextView tv2 = (TextView) view.findViewById(R.id.FitInfo);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon2);
                tv1.setText("Gender");
                tv2.setText("Male  ");
                iv.setImageResource(R.drawable.male);
            } else if (row == 0 && col == 2) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_two_text, viewGroup, false);
                final TextView tv1 = (TextView) view.findViewById(R.id.pageTitle2);
                final TextView tv2 = (TextView) view.findViewById(R.id.FitInfo);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon2);
                tv1.setText("Age");
                tv2.setText("1 month old ");
                iv.setImageResource(R.drawable.birthdaycake);
            } else if (row == 0 && col == 3) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup,false);
                final TextView tv = (TextView) view.findViewById(R.id.pageTitle1);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon1);
                tv.setText("Bio");
                iv.setImageResource(R.drawable.panda);
            } else if (row == 1 && col == 0) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup,false);
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
            } else if (row == 1 && col == 2) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_two_text, viewGroup, false);
                final TextView tv1 = (TextView) view.findViewById(R.id.pageTitle2);
                final TextView tv2 = (TextView) view.findViewById(R.id.FitInfo);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon2);
                tv1.setText("Playing");
                tv2.setText("10 min  ");
                iv.setImageResource(R.drawable.dumbbell);
            } else if (row == 1 && col == 3) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_two_text, viewGroup,false);
                final TextView tv1 = (TextView) view.findViewById(R.id.pageTitle2);
                final TextView tv2 = (TextView) view.findViewById(R.id.FitInfo);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon2);
                tv1.setText("Feeding");
                int feedingScore = sharedPref.getInt("feedingScore", 0);
                Log.e("BRIA: Feeding Score: ", Integer.toString(feedingScore));
                tv2.setText(Integer.toString(feedingScore));
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
                tv.setText("Scroll & click to feed!");
                iv.setImageResource(R.drawable.arrow);
            } else if (row == 2 && col == 1) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup,false);
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
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup,false);
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
            } else if (row == 2 && col == 3) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup,false);
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
            }
            else {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup,false);
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

        public void updateFoodScoreAndTime(){
            int feedingScore = sharedPref.getInt("feedingScore", 0);
            int newFeedingScore = feedingScore + 1;
            String lastFeed = sharedPref.getString("lastFeedTime", "12:00");
            SharedPreferences.Editor editor = sharedPref.edit();
            if (feedingScore < 4){
                editor.putInt("feedingScore", newFeedingScore);
                editor.apply();
            }
            long currentTime= System.currentTimeMillis();
            editor.putString("lastFeedTime", Long.toString(currentTime));
            editor.apply();
            Toast.makeText(mContext,"You have fed your pet! Current feeding level: " + newFeedingScore,
                    Toast.LENGTH_SHORT).show();
        }
    }
}