package com.example.sharefiles;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class introduction extends AppCompatActivity {
    TextView textView;
    Toolbar m_toolbar;
    AdView LI_topAd,LI_bottomAd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.introduction_activity);
        textView = findViewById(R.id.tv_terms);
        m_toolbar = (Toolbar) findViewById(R.id.toolbar);
        m_toolbar.setTitle("How to use App");
        setSupportActionBar(m_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textView.setText(Html.fromHtml(constants.termsAndUse));
        LI_topAd=findViewById(R.id.LI_topAd);
        LI_bottomAd=findViewById(R.id.LI_bottomAd);
        MobileAds.initialize(this,getString(R.string.ApAdId));
        AdRequest adRequest=new AdRequest.Builder().build();
        LI_topAd.loadAd(adRequest);
        LI_bottomAd.loadAd(adRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menu item selected
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
