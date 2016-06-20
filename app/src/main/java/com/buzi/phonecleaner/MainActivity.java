package com.buzi.phonecleaner;

import java.io.File;
import java.util.Random;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.appsflyer.AppsFlyerLib;
import com.buzi.phonecleaner.R;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;

import adapters.AdapterGridview;

import ads.RateMyApp;
import helpers.TypefaceSpan;
import io.display.sdk.Controller;
import io.display.sdk.EventListener;

public class MainActivity extends ActionBarActivity implements
        OnItemClickListener {
    Controller ctrl;
    private GridView gridview;
    private TextView size;
    double d;
    private StartAppAd startAppAd = new StartAppAd(this);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, "205019997", true);

        setContentView(R.layout.activity_main);


        gridview = (GridView) findViewById(R.id.gridview);
        size = (TextView) findViewById(R.id.size);
        gridview.setOnItemClickListener(this);
        gridview.setAdapter(new AdapterGridview(this));
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        size.setTypeface(typeface);
        barraTitulo();
        size();
        ctrl = io.display.sdk.Controller.getInstance();
        ctrl.getInstance().init(this, "35");

        AppsFlyerLib.getInstance().startTracking(this.getApplication(), "tTNbf9UjL7Syigh3nesvAa");

        String appKey = "aae559b9cb60c927584820b58367b2077e5b954500c6e5be";
        AdBuddiz.cacheAds(this);
        randomizator();

        new RateMyApp(this).app_launched();
    }

    public void barraTitulo() {
        ActionBar actionbar = this.getSupportActionBar();
        actionbar.setIcon(R.drawable.ic_launcher);
        SpannableString s = new SpannableString("Phone Cleaner");
        s.setSpan(new TypefaceSpan(this, "Roboto-Light.ttf"), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionbar.setTitle(s);
        actionbar.setDisplayUseLogoEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(false);
    }

    public void size() {
        File path = Environment.getDataDirectory();
        espacio(path.getPath());
    }

    public void espacio(String path) {
        StatFs stat = new StatFs(path);
        @SuppressWarnings("deprecation")
        long blockSize = stat.getBlockSize();
        @SuppressWarnings("deprecation")
        long availableBlocks = stat.getAvailableBlocks();
        @SuppressWarnings("deprecation")
        long countBlocks = stat.getBlockCount();
        String fileSize = Formatter.formatFileSize(this, availableBlocks
                * blockSize);
        String maxSize = Formatter
                .formatFileSize(this, countBlocks * blockSize);
        size.setText("  " + fileSize + " / " + maxSize);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {

        d = Math.random();

        if (d < 0.7) {


        }

        switch (position) {
            case 0:
                startActivity(new Intent(this, CacheActivity.class));


                break;
            case 1:
                startActivity(new Intent(this, CallsMsgActivity.class));

                break;
            case 2:
                startActivity(new Intent(this, AppActivity.class));

                break;
            case 3:
                startActivity(new Intent(this, HistoryActivity.class));


                break;
            case 4:
                startActivity(new Intent(this, RamActivity.class));


                break;
            case 5:
                startActivity(new Intent(this, BateryActivity.class));

                break;
        }
    }

    public void onBackPressed() {

        MainActivity.this.finishAffinity();
    }


    public void randomizator() {
        int min = 1;
        int max = 100;

        Random r = new Random();
        int i1 = r.nextInt(max - min + 1) + min;

        if (i1 <= 20) {
            ct();
        } else if (i1 > 20 & i1 <= 40)
        {
            st();
        }
        else if (i1 > 41 & i1 <= 60)
        {
          ct();
        }

        else if (i1 > 61 & i1 <= 80)
        {
          adbud();
        }

        else
        {
            adbud();
        }
    }


    public void ct()
    {


        ctrl.setEventListener(new EventListener() {
            @Override
            public void onInit() {
                ctrl.showAd("1365");
                this.inactivate();
            }
        });


    }

    public void st()
    {
        startAppAd.showAd(); // show the ad
        startAppAd.loadAd(); // load the next ad
    }





    public void adbud()
    {
        AdBuddiz.showAd(this);
    }
}

