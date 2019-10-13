package com.maker.SnakeSnap;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ImageSliderActivity extends AppCompatActivity {
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<fasb> imageModelArrayList;

    private int[] myImageList = new int[]{R.drawable.snakebite1, R.drawable.snakebite2,
            R.drawable.snakebite3};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.fasb);

        imageModelArrayList = new ArrayList<>();
        imageModelArrayList = populateList();

        init();

    }

    private ArrayList<fasb> populateList(){

        ArrayList<fasb> list = new ArrayList<>();

        for(int i = 0; i < 3; i++){
            fasb imageModel = new fasb();
            imageModel.setImage_drawable(myImageList[i]);
            list.add(imageModel);
        }

        return list;
    }

    private void init() {

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImage_Adapter(ImageSliderActivity.this,imageModelArrayList));

        CirclePageIndicator indicator = (CirclePageIndicator)
                findViewById(R.id.indicator);

        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

        //Set circle indicator radius
        indicator.setRadius(5 * density);

        NUM_PAGES =imageModelArrayList.size();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }
}
