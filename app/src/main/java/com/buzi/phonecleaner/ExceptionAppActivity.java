package com.buzi.phonecleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.buzi.phonecleaner.R;

import adapters.AdapterRamExcApp;
import helpers.InfoAppOnRam;
import helpers.TypefaceSpan;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by USER on 02/06/2014.
 */
public class ExceptionAppActivity extends ActionBarActivity {

    private List<InfoAppOnRam> appsList = null;
    private AdapterRamExcApp adapterRam = null;
    private List<Integer> pids = null;
    private List<String> packages = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_exc_app);

        ListView listView = (ListView) findViewById(R.id.list);

        appsList = new LinkedList<InfoAppOnRam>();

        pids = new LinkedList<Integer>();

        packages = new LinkedList<String>();

        adapterRam = new AdapterRamExcApp(ExceptionAppActivity.this, 0, appsList);

        listView.setAdapter(adapterRam);

        barraTitulo();

        new LoadProcess().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public void barraTitulo() {
        android.support.v7.app.ActionBar actionbar = this.getSupportActionBar();
        actionbar.setIcon(R.drawable.ic_icon_service);
        SpannableString s = new SpannableString("Ignore List");
        s.setSpan(new TypefaceSpan(this, "Roboto-Light.ttf"), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionbar.setTitle(s);
        actionbar.setDisplayUseLogoEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                startActivity(new Intent(this, RamActivity.class));
                return true;
            case R.id.ignore_list:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private class LoadProcess extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            setSupportProgressBarIndeterminateVisibility(true);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            SharedPreferences sharedPreferences = getSharedPreferences(Manager.SPreferences.SP_NAME_EXCEPTION, MODE_PRIVATE);
            String lista = sharedPreferences.getString(Manager.SPreferences.SP_LIST_EXCEPTION, null);
            ArrayList<String> list = new ArrayList<String>();

            if(lista == null){
                Manager.SPreferences.defaultSharedP(ExceptionAppActivity.this);
                lista = sharedPreferences.getString(Manager.SPreferences.SP_LIST_EXCEPTION, null);
            }

            try {

                JSONArray jsonArray = new JSONArray(lista);

                for(int i = 0; i < jsonArray.length(); i++){
                    list.add(jsonArray.getString(i));
                }

            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }

            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

            List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();

            if (processes == null) return false;

            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : processes) {

                if(list.indexOf(runningAppProcessInfo.processName) != -1){

                    packages.add(runningAppProcessInfo.processName);

                    pids.add(runningAppProcessInfo.pid);

                }
            }

            PackageManager pack = getPackageManager();

            for (int index = 0; index < pids.size(); index++) {

                int pides[] = new int[1];
                pides[0] = pids.get(index);
                android.os.Debug.MemoryInfo[] memoryInfoArray = activityManager.getProcessMemoryInfo(pides);

                if ((memoryInfoArray != null) && (memoryInfoArray.length > 0)) {

                    ApplicationInfo applicationInfo = null;
                    String packName = packages.get(index);
                    try {
                        applicationInfo = pack.getApplicationInfo(packName, PackageManager.GET_META_DATA);

                    } catch (PackageManager.NameNotFoundException e) {
                        //Log.d("Executed services", packName);
                    }
                    String name;
                    Drawable icon = null;
                    if (applicationInfo != null) {
                        name = (String) pack.getApplicationLabel(applicationInfo);

                        try {
                            icon = pack.getApplicationIcon(packName);
                        } catch (PackageManager.NameNotFoundException e) {
                            Log.d("Executed services", "No icon");
                        }

                        Log.d("Executed app", name + " - " + packName);


                        final InfoAppOnRam infoAppOnRam = new InfoAppOnRam(packName, name, icon, (long) memoryInfoArray[0].getTotalPss(), pides[0]);

                        LayoutInflater layoutInflater = (LayoutInflater) ExceptionAppActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        infoAppOnRam.view = layoutInflater.inflate(R.layout.item_list_exc_app, null);

                        Typeface typeface = Typeface.createFromAsset(ExceptionAppActivity.this.getAssets(), "fonts/Roboto-Light.ttf");

                        TextView appName = (TextView) infoAppOnRam.view.findViewById(R.id.app_name);

                        TextView delete = (TextView) infoAppOnRam.view.findViewById(R.id.delete);

                        ImageView imageView = (ImageView) infoAppOnRam.view.findViewById(R.id.app_icon);

                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Thread thread =  new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        SharedPreferences sharedPreferences = getSharedPreferences(Manager.SPreferences.SP_NAME_EXCEPTION, Context.MODE_PRIVATE);
                                        String lista = sharedPreferences.getString(Manager.SPreferences.SP_LIST_EXCEPTION, null);

                                        if(lista == null){
                                            Manager.SPreferences.defaultSharedP(ExceptionAppActivity.this);
                                            lista = sharedPreferences.getString(Manager.SPreferences.SP_LIST_EXCEPTION, null);
                                        }

                                        try {
                                            JSONArray jsonArray = new JSONArray(lista);
                                            JSONArray newJsonArray = new JSONArray();

                                            for(int i =0; i < jsonArray.length(); i++){
                                                if(!jsonArray.getString(i).equals(infoAppOnRam.getPackageName())){
                                                    newJsonArray.put(jsonArray.getString(i));
                                                }
                                            }

                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString(Manager.SPreferences.SP_LIST_EXCEPTION, newJsonArray.toString());
                                            editor.commit();
                                            appsList.remove(infoAppOnRam);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    adapterRam.notifyDataSetChanged();

                                                }
                                            });

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                thread.start();

                            }
                        });


                        appName.setTypeface(typeface);

                        delete.setTypeface(typeface);

                        appName.setText(infoAppOnRam.getAppName());

                        if (infoAppOnRam.getDrawableIcon() != null)
                            imageView.setImageDrawable(infoAppOnRam.getDrawableIcon());

                        appsList.add(infoAppOnRam);

                    } else {
                        name = packName;
                        Log.d("Executed services", name);
                    }
                }

            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            if (aBoolean) {

                adapterRam.notifyDataSetChanged();

            }

            setSupportProgressBarIndeterminateVisibility(false);
        }
    }

}
