package com.buzi.phonecleaner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.buzi.phonecleaner.R;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;

import adapters.AdapterCache;
import helpers.InfoApp;
import helpers.InfoCache;
import helpers.TypefaceSpan;
import io.display.sdk.Controller;
import io.display.sdk.EventListener;

@SuppressLint("HandlerLeak")
public class CacheActivity extends ActionBarActivity implements OnItemClickListener {
    private ListView lista;
    private InfoCache app;
    private AdapterCache adaptador;
    private List<InfoApp> appsList;
    private InfoApp infoapp;
    private Handler handler;
    private static Activity activity;
    private Button limpiarCache;
    private AlertDialog.Builder builder;
    private String cache;
    private TextView nohaycache;
    private TextView numapp;
    private TextView espacioOcupado;
    private int contador;
    private static final String SCHEME = "package";
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    private static final String APP_PKG_NAME_22 = "pkg";
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

    private AdView mAdView;

    Controller ctrl;
    double d;
    private StartAppAd startAppAd = new StartAppAd(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_cache);

        StartAppSDK.init(this, "205019997", true);
        String appKey = "aae559b9cb60c927584820b58367b2077e5b954500c6e5be";
        AdBuddiz.setPublisherKey("0776936a-cbad-4ae8-8032-163eeae94d04");
        AdBuddiz.cacheAds(this);

        randomizator();

        activity = this;
        lista = (ListView) findViewById(R.id.list);
        limpiarCache = (Button) findViewById(R.id.button1);
        nohaycache = (TextView) findViewById(R.id.cache);
        numapp = (TextView) findViewById(R.id.numeroapp);
        espacioOcupado = (TextView) findViewById(R.id.cantidad);
        TextView texto = (TextView) findViewById(R.id.texto);
        TextView espacio = (TextView) findViewById(R.id.espacio);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        limpiarCache.setTypeface(typeface);
        texto.setTypeface(typeface);
        espacio.setTypeface(typeface);
        numapp.setTypeface(typeface);
        nohaycache.setTypeface(typeface);
        espacioOcupado.setTypeface(typeface);
        lista.addHeaderView(new View(this));
        lista.addFooterView(new View(this));
        lista.setOnItemClickListener(this);
        barraTitulo();
        cargarAplicaciones();
        iniciarHilo();
        limpiarCache();
        setSupportProgressBarIndeterminateVisibility(true);

        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                mAdView.setVisibility(View.GONE);
            }

        });

        mAdView.loadAd(new AdRequest.Builder().build());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cache, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                startActivity(new Intent(this, MainActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
        //return true;
    }

    public void barraTitulo() {
        android.support.v7.app.ActionBar actionbar = this.getSupportActionBar();
        actionbar.setIcon(R.drawable.cache_bar);
        SpannableString s = new SpannableString("Clear Cache");
        s.setSpan(new TypefaceSpan(this, "Roboto-Light.ttf"), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionbar.setTitle(s);
        actionbar.setDisplayUseLogoEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    public void limpiarCache() {
        limpiarCache.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (appsList.size() > 0) {
                    new LiberarCache().execute();
                } else {
                    Toast.makeText(getApplicationContext(), "There is not data to clear", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void iniciarHilo() {
        appsList = new ArrayList<InfoApp>();
        app = new InfoCache(this, getPackageManager().getInstalledPackages(0));
        app.setHandler(handler);
        app.start();
    }

    public void cargarAplicaciones() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                contador += 1;
                Bundle bundle;
                bundle = msg.getData();
                infoapp = new InfoApp();
                InfoApp i = bundle.getParcelable("info");
                infoapp.setIcon(i.getIcon());
                infoapp.setTotal(i.getTotal());
                infoapp.setPname(i.getPname());
                infoapp.setAppname(i.getAppname());
                infoapp.setDataApp(i.getDataApp());
                infoapp.setCacheApp(i.getCacheApp());
                infoapp.setInstallSize(i.getInstallSize());

                appsList.add(infoapp);
                cache = new InfoCache().calculateSize(InfoCache.totalCacheALiberar);
                adaptador = new AdapterCache(activity, 0, appsList);
                lista.setAdapter(adaptador);

                nohaycache.setVisibility(View.GONE);
                numapp.setText(String.valueOf(contador));
                espacioOcupado.setText(cache);
            }
        };

    }

    public class LiberarCache extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            PackageManager pm = getPackageManager();
            Method[] methods = pm.getClass().getDeclaredMethods();
            for (Method m : methods) {
                if (m.getName().equals("freeStorageAndNotify")) {
                    try {
                        long desiredFreeStorage = Long.MAX_VALUE;
                        m.invoke(pm, desiredFreeStorage, null);
                    } catch (Exception e) {
                    }
                    break;
                }
            }

            for (int i = 0; i < appsList.size(); i++) {
                appsList.get(i).setCacheApp("0 Bytes");
            }
            return null;
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            actulizarLista();
            nohaycache.setVisibility(View.VISIBLE);
            numapp.setText("0");
            espacioOcupado.setText("0 Bytes");
/*            try {
                builder = new AlertDialog.Builder(activity, R.style.DialogCustomTheme);
            } catch (NoSuchMethodError e) {
                builder = new AlertDialog.Builder(activity);
            }*/

            Manager.Dialog.showDialogMsg(CacheActivity.this, "Your device has been cleaned!","Cleaned " + cache + " History Cache of your applications");

/*            builder.setTitle("Your device has been cleaned!");
            builder.setIcon(R.drawable.dialog_clean_icon);
            builder.setMessage("Cleaned " + cache + " History Cache of your applications");

            builder
                    .setCancelable(false)
                    .setNeutralButton("Aceptar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }
                    );
            AlertDialog alertDialog = builder.create();
            alertDialog.show();*/
        }
    }

    public void actulizarLista() {
        for (int i = 0; i < appsList.size(); i++) {
            if (appsList.get(i).getCacheApp().equals("0 Bytes")) {
                appsList.remove(i);
                i--;
                adaptador.notifyDataSetChanged();
            }
        }
    }

    @SuppressLint("InlinedApi")
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        if (position == 0) {
            return;
        }
        showInstalledAppDetails(this, appsList.get(position - 1).getPname());
    }

    @SuppressLint("InlinedApi")
    public static void showInstalledAppDetails(Context contexto, String packageName) {
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // above 2.3
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else { // below 2.3
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        contexto.startActivity(intent);
    }

    public void onDestroy() {
        super.onDestroy();
        //  this.adView.destroy();
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


