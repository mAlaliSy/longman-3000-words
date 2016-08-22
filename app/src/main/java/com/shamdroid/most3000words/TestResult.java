package com.shamdroid.most3000words;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;
import java.util.ArrayList;

public class TestResult extends AppCompatActivity {

    Toolbar toolbar ;
    TextView txtGrade , txtGradeText , txtWrongAnswers;

    ListView lvWrongAnswers;

    float percentage;

    int []  wrongAnsArr;

    Cursor cursor;

    ArrayList<String> data;

    sqlHelper sqlHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        initialize();
        fillLv();

        AdView mAdView = (AdView) findViewById(R.id.adResult);
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
        toolbar = (Toolbar) findViewById(R.id.toolbar_test_result);
        setSupportActionBar(toolbar);
        txtGrade           = (TextView)    findViewById(R.id.txtGrade);
        txtGradeText       = (TextView)    findViewById(R.id.txtGradeText);
        txtWrongAnswers    = (TextView)    findViewById(R.id.txtWrongAnswers);

        lvWrongAnswers     = (ListView)    findViewById(R.id.lvWrongAnswers);

        percentage = (float) Math.floor((float)getIntent().getExtras().getInt("rightQuesNum") / getIntent().getExtras().getInt("quesNum") * 100 );

        wrongAnsArr = getIntent().getExtras().getIntArray("wrongAnsArr");

        sqlHelper = new sqlHelper(this);
        data      = new ArrayList<String>();

        txtGrade.setText(percentage + " %");

        if(percentage == 100.0){
            txtGradeText.setText(getResources().getString(R.string.percent100));
            txtWrongAnswers.setVisibility(View.GONE);
            lvWrongAnswers.setVisibility(View.GONE);
        }else if(percentage >= 90 )
            txtGradeText.setText(getResources().getString(R.string.percent90));
        else if(percentage >= 80 )
            txtGradeText.setText(getResources().getString(R.string.percent80));
        else if(percentage > 70 )
            txtGradeText.setText(getResources().getString(R.string.percent70));
        else
            txtGradeText.setText(getResources().getString(R.string.percentFailed));
    }


    public void fillLv(){
        sqlHelper = new sqlHelper(this);
        try {
            sqlHelper.importIfNotExist();
            sqlHelper.openDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i = 0 ; i < wrongAnsArr.length;i++){
            if(wrongAnsArr[i] != 0){
                cursor = null ;
                cursor = sqlHelper.getData(wrongAnsArr[i]);
                cursor.moveToFirst();
                data.add( cursor.getString(1) + " | " + cursor.getString(2) );
            }
        }
        sqlHelper.close();

        ArrayAdapter listAdapter = new ArrayAdapter(this,R.layout.lv_result,R.id.txtLv,data);
        lvWrongAnswers.setAdapter(listAdapter);
    }

}
