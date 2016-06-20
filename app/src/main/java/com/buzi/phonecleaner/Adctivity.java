package com.buzi.phonecleaner;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;

import java.util.Random;

import io.display.sdk.Controller;
import io.display.sdk.EventListener;

public class Adctivity extends Activity {
    Controller ctrl;

    double d;
    private StartAppAd startAppAd = new StartAppAd(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StartAppSDK.init(this, "205019997", true);
        setContentView(R.layout.activity_adctivity);
        String appKey = "aae559b9cb60c927584820b58367b2077e5b954500c6e5be";
        AdBuddiz.setPublisherKey("0776936a-cbad-4ae8-8032-163eeae94d04");
        AdBuddiz.cacheAds(Adctivity.this);



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
           adbud();
        }

        else if (i1 > 61 & i1 <= 80)
        {
           st();
        }

        else
        {
            adbud();
        }
    }


    public void ct()
    {

        io.display.sdk.Controller.getInstance().showAd(this, "1365");
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



