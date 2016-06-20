package com.buzi.phonecleaner;

import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
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

import adapters.AdapterApp;
import helpers.InfoAplicaciones;
import helpers.InfoApp;
import helpers.InfoCache;
import helpers.TypefaceSpan;
import io.display.sdk.Controller;
import io.display.sdk.EventListener;

public class AppActivity extends ActionBarActivity implements OnItemClickListener {
    static ArrayList<InfoApp> apps;
    private ListView lista;
    private AdapterApp adaptador;
    private Button desintalar;
    private AppDeleted detectarAppDelete;
    private IntentFilter filter;
    private IntentFilter intentFilter;
    private InfoAplicaciones app;
    private Handler handler;
    private InfoApp infoapp;
    private Activity activity;
    private TextView numapp;
    private TextView espacioOcupado;
    private int contador;

    private AdView mAdView;
    Controller ctrl;

    double d;
    private StartAppAd startAppAd = new StartAppAd(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_app);

        StartAppSDK.init(this, "205019997", true);


        String appKey = "aae559b9cb60c927584820b58367b2077e5b954500c6e5be";
        AdBuddiz.setPublisherKey("0776936a-cbad-4ae8-8032-163eeae94d04");
        AdBuddiz.cacheAds(this);

randomizator();
        activity = this;
        lista = (ListView) findViewById(R.id.list);
        desintalar = (Button) findViewById(R.id.button1);
        numapp = (TextView) findViewById(R.id.numeroapp);
        espacioOcupado = (TextView) findViewById(R.id.cantidad);
        TextView texto = (TextView) findViewById(R.id.texto);
        TextView espacio = (TextView) findViewById(R.id.espacio);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        desintalar.setTypeface(typeface);
        texto.setTypeface(typeface);
        espacio.setTypeface(typeface);
        numapp.setTypeface(typeface);
        espacioOcupado.setTypeface(typeface);
        setProgressBarIndeterminateVisibility(true);
        lista.setOnItemClickListener(this);
        lista.addHeaderView(new View(this));
        lista.addFooterView(new View(this));
        barraTitulo();
        cargarAplicaciones();
        iniciarHilo();
        desintalarApps();

        // Detecta si una aplicación ha sido desintalada.
        detectarAppDelete = new AppDeleted();
        filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_DELETE);
        filter.addDataScheme("package");
        registerReceiver(detectarAppDelete, filter);

        // Permite remover de la lista la aplicación que ha sido desintalada.
        intentFilter = new IntentFilter();
        intentFilter.addAction("QUITAR");
        this.registerReceiver(intentReceiver, intentFilter);

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
        getMenuInflater().inflate(R.menu.list_app, menu);
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

    }

    public void barraTitulo() {
        ActionBar actionbar = this.getSupportActionBar();
        actionbar.setIcon(R.drawable.app_bar);
        SpannableString s = new SpannableString("Application Manager");
        s.setSpan(new TypefaceSpan(this, "Roboto-Light.ttf"), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionbar.setTitle(s);
        actionbar.setDisplayUseLogoEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

    }


    public void desintalarApps() {
        desintalar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = false;
                for (int i = 0; i < apps.size(); i++) {
                    if (apps.get(i).isSelected()) {
                        Intent intent = new Intent(Intent.ACTION_DELETE);
                        intent.setData(Uri.parse("package:" + apps.get(i).getPname()));
                        startActivity(intent);
                        flag = true;
                    }
                }

                if (!flag) {
                    Toast.makeText(getApplicationContext(), "Need to select an application!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        try {
            builder = new AlertDialog.Builder(activity, R.style.DialogCustomTheme);
        } catch (NoSuchMethodError e) {
            builder = new AlertDialog.Builder(activity);
        }

        builder.setTitle(apps.get(position).getAppname());
        builder.setIcon(apps.get(position).getIcon());

        String msg =
                "Version:" + apps.get(position).getVersionName() + "\n" +
                        "Date:  " + apps.get(position).getFechaInstalled() + "\n" +
                        "Space:" + apps.get(position).getInstallSize();


        builder.setMessage(msg)
                .setPositiveButton("Uninstall", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_DELETE);
                        intent.setData(Uri.parse("package:" + apps.get(position).getPname()));
                        startActivity(intent);
                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();*/
    }

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            actualizarLista();
        }
    };

    public void actualizarLista() {
        for (int i = 0; i < apps.size(); i++) {
            if (apps.get(i).isDelete()) {
                apps.remove(i);
                adaptador.notifyDataSetChanged();
                i--;
            }
        }
    }

    public void iniciarHilo() {
        apps = new ArrayList<InfoApp>();
        app = new InfoAplicaciones(this, getPackageManager().getInstalledPackages(0));
        app.setHandler(handler);
        app.start();
    }

    @SuppressLint("HandlerLeak")
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
                infoapp.setPname(i.getPname());
                infoapp.setAppname(i.getAppname());
                infoapp.setDataApp(i.getDataApp());
                infoapp.setCacheApp(i.getCacheApp());
                infoapp.setVersionName(i.getVersionName());
                infoapp.setInstallSize(i.getInstallSize());
                infoapp.setFechaInstalled(i.getFechaInstalled());
                infoapp.setEspacioOcupadoCacheCodigo(i.getEspacioOcupadoCacheCodigo());

                apps.add(infoapp);
                adaptador = new AdapterApp(activity, 0, apps);
                lista.setAdapter(adaptador);
                numapp.setText(String.valueOf(contador));
                espacioOcupado.setText(new InfoCache().calculateSize(InfoAplicaciones.totalespacioOcupado));
            }
        };
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(intentReceiver);
        unregisterReceiver(detectarAppDelete);
        AppActivity.this.finish();
        super.onDestroy();
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




