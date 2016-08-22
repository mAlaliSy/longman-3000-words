package com.shamdroid.most3000words;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class Main extends AppCompatActivity implements View.OnClickListener {


    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    Words words;
    favorite favorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();


        if (getSharedPreferences("settings", MODE_PRIVATE).getBoolean("isFirst", true)) {
            startActivity(new Intent("com.shamdroid.longman3000words.Help"));
            getSharedPreferences("settings", MODE_PRIVATE).edit().putBoolean("isFirst", false).apply();
        }

        AdView mAdView = (AdView) findViewById(R.id.adMain);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        final InterstitialAd interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(getResources().getString(R.string.iniId));
        AdRequest adRequest2 = new AdRequest.Builder().build();
        interstitial.loadAd(adRequest2);
        interstitial.setAdListener(new AdListener() {
                                       public void onAdLoaded() {
                                           if (interstitial.isLoaded())
                                               interstitial.show();
                                       }
                                   }
        );

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSharedPreferences("settings", MODE_PRIVATE).edit().putInt("last_lv_id", 0).apply();
    }

    public void initialize() {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);

        setSupportActionBar(toolbar);

        tabLayout = (TabLayout) findViewById(R.id.mainTab);
        words = new Words(this);
        favorite = new favorite(this);
        words.setUpdateableFragment(favorite);
        favorite.setUpdateableFragment(words);

        viewPager = (ViewPager) findViewById(R.id.mainViewPager);

        viewPager.setAdapter(new ViewPagerAdabter(getSupportFragmentManager()));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (viewPager.getCurrentItem() == 0)
                    favorite.update();
                else
                    words.update();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_quiz:
                startActivity(new Intent(Main.this, Test.class));
                break;
            case R.id.menu_help:
                startActivity(new Intent(Main.this, Help.class));
                break;

        }
        return true;
    }

    @Override
    public void onClick(View v) {

    }


    class ViewPagerAdabter extends FragmentPagerAdapter {

        public ViewPagerAdabter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return (position == 0 ? words : favorite);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return (position == 0 ? "الكلمات" : "الكلمات المحفوظة");
        }

    }

}
