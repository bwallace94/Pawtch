package edu.mit.pawtch;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
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

public class GridViewFragmentActivity extends Activity {

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
        final Context mContext;

        public myAdapter(final Context context) {
            this.mContext = context;
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
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup,false);
                final TextView tv = (TextView) view.findViewById(R.id.pageTitle1);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon1);
                tv.setText("Mochi");
                iv.setImageResource(R.drawable.panda);
                Log.e("LOOK", (String) tv.getText());
            } else if (row == 0 && col == 1) {
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
            } else if (row == 2 && col == 1) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_two_text, viewGroup, false);
                final TextView tv1 = (TextView) view.findViewById(R.id.pageTitle2);
                final TextView tv2 = (TextView) view.findViewById(R.id.FitInfo);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon2);
                tv1.setText("Playing");
                tv2.setText("10 min  ");
                iv.setImageResource(R.drawable.dumbbell);
            } else if (row == 3 && col == 1) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.one_image_one_text, viewGroup,false);
                final TextView tv = (TextView) view.findViewById(R.id.pageTitle1);
                final ImageView iv = (ImageView) view.findViewById(R.id.icon1);
                tv.setText("Feeding");
                iv.setImageResource(R.drawable.meter);
            } else {

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
    }
}