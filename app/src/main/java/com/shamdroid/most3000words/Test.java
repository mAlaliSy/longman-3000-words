package com.shamdroid.most3000words;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;

public class Test extends AppCompatActivity {

    RadioButton radAllTest,radFavTest;
    Spinner spinQuesNum;
    Button btnStartTest;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initialize();
        listener();

        AdView mAdView = (AdView) findViewById(R.id.adTest);
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

    private void initialize() {
        radAllTest    = (RadioButton)   findViewById(R.id.radAllTest);
        radFavTest    = (RadioButton)   findViewById(R.id.radFavTest);

        spinQuesNum   = (Spinner)       findViewById(R.id.spinQuesNum);

        btnStartTest  = (Button)        findViewById(R.id.btnStartTest);
        toolbar = (Toolbar) findViewById(R.id.toolbar_test);
        setSupportActionBar(toolbar);

        String [] num = {"10","15","20"};

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,num);

        spinQuesNum.setAdapter(adapter);
    }

    public void listener(){

        btnStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int favCount = 0;
                sqlHelper sqlHelper = new sqlHelper(Test.this);
                try {
                    sqlHelper.importIfNotExist();
                    sqlHelper.openDataBase();
                    favCount = sqlHelper.getAllFav().getCount();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(radFavTest.isChecked() && favCount - 1 < Integer.valueOf(spinQuesNum.getSelectedItem().toString()) )
                    Toast.makeText(Test.this,"عدد الأسئلة المحفوظة قليل بالنسبة لعدد الأسئلة المختارة",Toast.LENGTH_LONG).show();
                else{
                Intent startTest = new Intent("com.shamdroid.longman3000words.StartTest");

                startTest.putExtra("isAll",radAllTest.isChecked());
                startTest.putExtra("quesNum",Integer.valueOf(spinQuesNum.getSelectedItem().toString()));
                startActivity(startTest);
                }
            }
        });
    }

}
