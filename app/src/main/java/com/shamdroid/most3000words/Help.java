package com.shamdroid.most3000words;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class Help extends AppCompatActivity implements View.OnClickListener {

    Button btnRate , btnShare,btnOurApps;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        initialize();
    }

    private void initialize() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_help);
        setSupportActionBar(toolbar);
        btnRate       = (Button)  findViewById(R.id.btnRate);
        btnShare      = (Button)  findViewById(R.id.btnShare);
        btnOurApps    = (Button)  findViewById(R.id.btnOurApps);

        btnShare.setOnClickListener(this);
        btnRate.setOnClickListener(this);
        btnOurApps.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnRate :
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.shamdroid.longman3000words"));
                startActivity(intent);

                break;

            case R.id.btnShare :
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT,getResources().getString(R.string.share_message));
                startActivity(Intent.createChooser(share,"مشاركة باستخدام :"));
                break;

            case R.id.btnOurApps :
                Intent ourApps = new Intent(Intent.ACTION_VIEW);
                ourApps.setData(Uri.parse("https://play.google.com/store/apps/dev?id=8783728639171390079"));
                startActivity(ourApps);
                break;
        }
    }
}
