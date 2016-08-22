package com.shamdroid.most3000words;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class StartTest extends AppCompatActivity implements View.OnClickListener {

    TextView txtWord, txtCurrentQ, txtAllQuesNum;

    Button btnA, btnB, btnC, btnD;

    sqlHelper sqlHelper;
    ArrayList<ArrayList> allData;
    Cursor cursor;

    int id, rightId, currentQues = 1, rightQuesNum = 0;

    int[] quesArr;
    int[] wrongAnsArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_test);

        initialize();
        sql();
        makeAQuestion();

        AdView mAdView = (AdView) findViewById(R.id.adStartTest);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }


    private void initialize() {
        txtWord = (TextView) findViewById(R.id.txtWord);
        txtAllQuesNum = (TextView) findViewById(R.id.txtAllQuesNum);
        txtCurrentQ = (TextView) findViewById(R.id.txtCurrentQ);

        btnA = (Button) findViewById(R.id.btnA);
        btnB = (Button) findViewById(R.id.btnB);
        btnC = (Button) findViewById(R.id.btnC);
        btnD = (Button) findViewById(R.id.btnD);


        sqlHelper = new sqlHelper(this);

        allData = new ArrayList<ArrayList>();

        txtCurrentQ.setText("1");
        txtAllQuesNum.setText(getIntent().getExtras().getInt("quesNum") + "");

        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);

        quesArr = new int[getIntent().getExtras().getInt("quesNum")];
        wrongAnsArr = new int[getIntent().getExtras().getInt("quesNum")];

    }

    public void sql() {
        try {
            sqlHelper.importIfNotExist();
            sqlHelper.openDataBase();

            if (getIntent().getExtras().getBoolean("isAll"))
                cursor = sqlHelper.getAll();
            else
                cursor = sqlHelper.getAllFav();

            cursor.moveToFirst();

            do {
                ArrayList single = new ArrayList();
                single.add(cursor.getInt(0));
                single.add(cursor.getString(1));
                single.add(cursor.getString(2));
                allData.add(single);
            } while (cursor.moveToNext());

            sqlHelper.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void makeAQuestion() {
        do
            id = new Random().nextInt(allData.size());
        while (isUsed(id));
        quesArr[currentQues - 1] = id;
        txtWord.setText(allData.get(id).get(1) + "");
        rightId = new Random().nextInt(3);

        int wrongN1, wrongN2, wrongN3;
        do {
            wrongN1 = new Random().nextInt(allData.size());
        } while (wrongN1 == id || wrongN1 == id -1 || wrongN1 == id + 1 );
        do {
            wrongN2 = new Random().nextInt(allData.size());
        } while (wrongN2 == id || wrongN2 == id -1 || wrongN2 == id + 1 || wrongN2 == wrongN1);
        do {
            wrongN3 = new Random().nextInt(allData.size());
        } while (wrongN3 == id || wrongN3 == wrongN1 || wrongN3 == id -1 || wrongN3 == id + 1  || wrongN3 == wrongN2);

        switch (rightId) {
            case 0:
                btnA.setText(allData.get(id).get(2) + "");
                btnB.setText(allData.get(wrongN1).get(2) + "");
                btnC.setText(allData.get(wrongN2).get(2) + "");
                btnD.setText(allData.get(wrongN3).get(2) + "");
                break;
            case 1:
                btnB.setText(allData.get(id).get(2) + "");
                btnA.setText(allData.get(wrongN1).get(2) + "");
                btnC.setText(allData.get(wrongN2).get(2) + "");
                btnD.setText(allData.get(wrongN3).get(2) + "");
                break;
            case 2:
                btnC.setText(allData.get(id).get(2) + "");
                btnB.setText(allData.get(wrongN1).get(2) + "");
                btnA.setText(allData.get(wrongN2).get(2) + "");
                btnD.setText(allData.get(wrongN3).get(2) + "");
                break;
            case 3:
                btnD.setText(allData.get(id).get(2) + "");
                btnB.setText(allData.get(wrongN1).get(2) + "");
                btnC.setText(allData.get(wrongN2).get(2) + "");
                btnA.setText(allData.get(wrongN3).get(2) + "");
                break;
        }

    }

    public boolean isUsed(int _ID) {
        for (int i = 0; i < quesArr.length; i++)
            if (quesArr[i] == _ID)
                return true;
        return false;
    }


    @Override
    public void onClick(View v) {

        int btnId = v.getId();

        switch (btnId) {

            case R.id.btnA:
                if (rightId == 0){
                    rightQuesNum++;
                    wrongAnsArr[currentQues - 1] = 0;
                }else {
                    wrongAnsArr[currentQues - 1] = (int) allData.get(id).get(0);
                }
                break;

            case R.id.btnB:
                if (rightId == 1){
                    rightQuesNum++;
                    wrongAnsArr[currentQues - 1] = 0;
                }else {
                    wrongAnsArr[currentQues - 1] = (int) allData.get(id).get(0);
                }
                break;

            case R.id.btnC:
                if (rightId == 2){
                    rightQuesNum++;
                    wrongAnsArr[currentQues - 1] = 0;
                }else {
                    wrongAnsArr[currentQues - 1] = (int) allData.get(id).get(0);
                }
                    break;

            case R.id.btnD:
                if (rightId == 3){
                    rightQuesNum++;
                    wrongAnsArr[currentQues - 1] = 0;
                }else{
                    wrongAnsArr[currentQues - 1] = (int) allData.get(id).get(0);
                }
                break;
        }

        currentQues++;

        if (currentQues <= getIntent().getExtras().getInt("quesNum")) {
            makeAQuestion();
            txtCurrentQ.setText(currentQues + "");
        } else {
            Intent intent = new Intent("com.shamdroid.longman3000words.TestResult");
            intent.putExtra("rightQuesNum", rightQuesNum);
            intent.putExtra("wrongAnsArr", wrongAnsArr);
            intent.putExtra("quesNum", getIntent().getExtras().getInt("quesNum"));
            startActivity(intent);
            this.finish();
        }
    }

}
