package com.shamdroid.most3000words;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Locale;

public class ShowWord extends AppCompatActivity {

    ScrollView scrollView;

    TextView txtEnP, txtArP;

    ImageButton shBtnShare, shBtnCopy, shBtnAPlus, shBtnAMinus, shBtnNext, shBtnPrev;

    Button btnSpeechUS, btnSpeechUK;

    ToggleButton tbtnFav;

    SharedPreferences settings;

    int id, adSho;

    Cursor cursor;

    sqlHelper sqlHelper;

    TextToSpeech tts;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_word);

        initialize();

        listener();

        sql();

        AdView mAdView = (AdView) findViewById(R.id.adShowWords);
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
        toolbar = (Toolbar) findViewById(R.id.toolbar_showWords);
        setSupportActionBar(toolbar);
        id = this.getIntent().getExtras().getInt("id");

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        txtEnP = (TextView) findViewById(R.id.txtEnP);
        txtArP = (TextView) findViewById(R.id.txtArP);

        shBtnShare = (ImageButton) findViewById(R.id.shBtnShare);
        shBtnCopy = (ImageButton) findViewById(R.id.shBtnCopy);
        shBtnAPlus = (ImageButton) findViewById(R.id.shBtnAPlus);
        shBtnAMinus = (ImageButton) findViewById(R.id.shBtnAMinus);

        shBtnNext = (ImageButton) findViewById(R.id.shBtnNext);
        shBtnPrev = (ImageButton) findViewById(R.id.shBtnPrev);

        btnSpeechUK = (Button) findViewById(R.id.btnSpeechUK);
        btnSpeechUS = (Button) findViewById(R.id.btnSpeechUS);

        tbtnFav = (ToggleButton) findViewById(R.id.tbtnFav);

        settings = getSharedPreferences("settings", MODE_PRIVATE);

        txtEnP.setTextSize(TypedValue.COMPLEX_UNIT_PX, settings.getFloat("text_size", 45));
        txtArP.setTextSize(TypedValue.COMPLEX_UNIT_PX, settings.getFloat("text_size", 45));

        sqlHelper = new sqlHelper(this);

        adSho = 0;
    }


    public void listener() {

        shBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String word;
                String[] arr = txtEnP.getText().toString().split(" ");
                word = arr[0];
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, word);
                startActivity(Intent.createChooser(share, "مشاركة الكلمة بواسطة :"));
            }
        });

        ///////

        shBtnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String word;
                String[] arr = txtEnP.getText().toString().split(" ");
                word = arr[0];
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText("Word", word));
                Toast.makeText(ShowWord.this, "تم النسخ", Toast.LENGTH_LONG).show();
            }
        });

        ///////

        shBtnAPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtEnP.getTextSize() >= 70)
                    Toast.makeText(ShowWord.this, "هذا أكبر حجم ممكن !!", Toast.LENGTH_LONG).show();
                else {
                    txtEnP.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtEnP.getTextSize() + 5);
                    txtArP.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtEnP.getTextSize() + 5);
                    settings.edit().putFloat("text_size", txtEnP.getTextSize()).apply();

                }
            }
        });

        ////////

        shBtnAMinus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (txtEnP.getTextSize() <= 25)
                    Toast.makeText(ShowWord.this, "هذا أصغر حجم ممكن !!", Toast.LENGTH_LONG).show();
                else {
                    txtEnP.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtEnP.getTextSize() - 5);
                    txtArP.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtEnP.getTextSize() - 5);
                    settings.edit().putFloat("text_size", txtEnP.getTextSize()).apply();
                }

            }
        });


        tbtnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tbtnFav.isChecked()) {
                    sqlHelper.openDataBase();
                    sqlHelper.addToFav(id);
                    sqlHelper.close();
                    Toast.makeText(ShowWord.this, "تمت إضافتها للكلمات المحفوظة", Toast.LENGTH_SHORT).show();
                } else {
                    sqlHelper.openDataBase();
                    sqlHelper.removeFromFav(id);
                    Toast.makeText(ShowWord.this, "تمت إزالتها من الكلمات المحفوظة", Toast.LENGTH_SHORT).show();
                    sqlHelper.close();
                }
            }
        });


        /**** ****/
        shBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlHelper.openDataBase();
                if (id == sqlHelper.getNum())
                    Toast.makeText(ShowWord.this, "آخر كلمة في القائمة", Toast.LENGTH_SHORT).show();
                else {
                    id++;
                    refresh();
                }
                sqlHelper.close();
            }
        });

        shBtnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlHelper.openDataBase();
                if (id == 1)
                    Toast.makeText(ShowWord.this, "أول كلمة في القائمة", Toast.LENGTH_SHORT).show();
                else {
                    id--;
                    refresh();
                }
                sqlHelper.close();
            }
        });


        btnSpeechUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts = new TextToSpeech(ShowWord.this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {

                        if (status == TextToSpeech.SUCCESS) {
                            tts.setLanguage(Locale.US);
                            String[] arr = txtEnP.getText().toString().split(" ");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                tts.speak(arr[0], TextToSpeech.QUEUE_FLUSH, null, null);
                            else
                                tts.speak(arr[0], TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                });
            }
        });

        btnSpeechUK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts = new TextToSpeech(ShowWord.this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {

                        if (status == TextToSpeech.SUCCESS) {
                            tts.setLanguage(Locale.UK);
                            String[] arr = txtEnP.getText().toString().split(" ");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                tts.speak(arr[0], TextToSpeech.QUEUE_FLUSH, null, null);
                            else
                                tts.speak(arr[0], TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                });
            }
        });


    }

    public void showScroll() {
        scrollView.animate().alpha(1.0f).setDuration(500).start();
    }

    public void hideScroll() {
        scrollView.animate().alpha(0.0f).setDuration(500).start();

    }

    public void refresh() {
        hideScroll();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                showScroll();
                sql();
            }
        }, 500);

        adSho++;
        if (adSho % 6 == 0) {
            //refreshAds();
        }
    }


    public void sql() {

        cursor = null;
        sqlHelper.openDataBase();
        cursor = sqlHelper.getData(id);

        if (cursor.moveToFirst() && cursor != null) {

            txtEnP.setText(cursor.getString(1));
            txtArP.setText(cursor.getString(2));

            if (cursor.getInt(3) == 1)
                tbtnFav.setChecked(true);
            else
                tbtnFav.setChecked(false);
        }

    }

    @Override
    protected void onPause() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }
}
