package com.buzi.phonecleaner;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.buzi.phonecleaner.R;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;

import adapters.AdapterHistory;
import helpers.EntryItem;
import helpers.InfoCache;
import helpers.Item;
import helpers.SectionItem;
import helpers.TypefaceSpan;
import io.display.sdk.Controller;
import io.display.sdk.EventListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.Browser;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.ClipboardManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class HistoryActivity extends ActionBarActivity implements OnItemClickListener {
    private ArrayList<Item> items = new ArrayList<Item>();
    private ListView listview = null;
    private Button limpiar = null;
    private AdapterHistory adapter;
    private AlertDialog.Builder builder;
    private AlertDialog.Builder builde;
    private Activity activity;

    private AdView mAdView;
    Controller ctrl;
    double d;
    private StartAppAd startAppAd = new StartAppAd(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        StartAppSDK.init(this, "205019997", true);
        String appKey = "aae559b9cb60c927584820b58367b2077e5b954500c6e5be";

        AdBuddiz.setPublisherKey("0776936a-cbad-4ae8-8032-163eeae94d04");
        AdBuddiz.cacheAds(this);

        randomizator();
        activity = this;
        limpiar = (Button) findViewById(R.id.limpiar);
        listview = (ListView) findViewById(R.id.listView_main);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        limpiar.setTypeface(typeface);

        listview.addHeaderView(new View(this));
        listview.addFooterView(new View(this));
        barraTitulo();
        botonlimpiar();
        informacionParaListview();

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.history, menu);
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
        ActionBar actionbar = this.getSupportActionBar();
        actionbar.setIcon(R.drawable.history_bar);
        SpannableString s = new SpannableString("Clear history");
        s.setSpan(new TypefaceSpan(this, "Roboto-Light.ttf"), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionbar.setTitle(s);
        actionbar.setDisplayUseLogoEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);
        //actionbar.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_passlock));
    }

    public void informacionParaListview() {
        items.add(new SectionItem("Browser"));
        items.add(new EntryItem("Browser", String.valueOf(numeroPaginasVisitadas()) + " Pag", R.drawable.web));

        items.add(new SectionItem("Downloads"));
        items.add(new EntryItem("Downloads", espacioDescargas(), R.drawable.downloads));
        items.add(new EntryItem("Records", espacioArchivos(), R.drawable.records));

        items.add(new SectionItem("Applications"));
        items.add(new EntryItem("Clipboard", tamanoPortapapeles(), R.drawable.clipboard));

        adapter = new AdapterHistory(this, items);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
    }

    public int numeroPaginasVisitadas() {
        String[] proj = new String[]{Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL};
        String sel = Browser.BookmarkColumns.BOOKMARK + " = 0"; // 0 = history, 1 = bookmark
        Cursor mCur = this.managedQuery(Browser.BOOKMARKS_URI, proj, sel, null, null);
        this.startManagingCursor(mCur);
        mCur.moveToFirst();
        int contador = 0;

        if (mCur.moveToFirst() && mCur.getCount() > 0) {
            while (mCur.isAfterLast() == false) {
                contador += 1;
                mCur.moveToNext();
            }
        }
        return contador;
    }

    public String espacioDescargas() {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        long tamanoDescarga = 0;
        if (file != null) {
            if (file.listFiles() != null) {
                for (File f : file.listFiles()) {
                    if (!f.isDirectory()) {
                        tamanoDescarga += f.length();
                    }
                }
            }
        }
        return new InfoCache().calculateSize(tamanoDescarga);
    }

    public void DeleteEspacioDescargas(File file) {
        if (!file.isDirectory()) {
            file.delete();
        }
    }

    public String espacioArchivos() {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        long tamanoDescarga = 0;
        if (file != null) {
            if (file.listFiles() != null) {
                for (File f : file.listFiles()) {
                    if (f.isDirectory()) {
                        for (File d : f.listFiles()) {
                            tamanoDescarga += d.length();
                        }
                    }
                }
            }
        }
        return new InfoCache().calculateSize(tamanoDescarga);
    }

    public void DeleteEspacioArchivos(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                DeleteEspacioArchivos(child);
            }
        }
        file.delete();
    }


    public String tamanoPortapapeles() {
        final ClipboardManager portapapeles = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (portapapeles != null && portapapeles.hasText()) {
            return new InfoCache().calculateSize(portapapeles.getText().length());
        } else {
            return "0 Bytes";
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {

        position = position - 1;
        EntryItem item = (EntryItem) items.get(position);
        CheckBox box = (CheckBox) view.findViewById(R.id.checkBox_ram);

        if (item.title.equals("Records")) {
            if (!item.isSelected()) {
                mensajeAlerta(item, box);
            } else {
                item.setSelected(false);
                box.setChecked(item.isSelected());
            }
        } else {
            if (!item.isSelected()) {
                item.setSelected(true);
                box.setChecked(item.isSelected());
            } else {
                item.setSelected(false);
                box.setChecked(item.isSelected());
            }
        }


    }

    @SuppressLint("NewApi")
    public void mensajeAlerta(final EntryItem item, final CheckBox box) {
        try {
            builder = new AlertDialog.Builder(this, R.style.DialogCustomTheme);
        } catch (NoSuchMethodError e) {
            builder = new AlertDialog.Builder(this);
        }

        builder.setTitle("Does media files?");
        builder.setIcon(R.drawable.alerta);
        builder.setMessage("It will remove the music files, video and photography in your Downloads folder." + "\n\n" + "Once deleted these files, can not be recovered.")

                .setPositiveButton("Include", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        item.setSelected(true);
                        box.setChecked(item.isSelected());
                    }
                })

                .setNegativeButton("Exclude", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        item.setSelected(false);
                        box.setChecked(item.isSelected());
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void botonlimpiar() {
        limpiar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean haySeleccion = false;
                for (int i = 0; i < items.size(); i++) {
                    if (!items.get(i).isSection()) {
                        EntryItem ite = (EntryItem) items.get(i);
                        if (ite.isSelected()) {
                            haySeleccion = true;
                        }
                    }
                }

                if (haySeleccion) {
                    new LimpiarDatos(activity, items).execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Need to select at least an item!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class LimpiarDatos extends AsyncTask<Void, Void, Boolean> {
        private Activity activity;
        private ArrayList<Item> items;
        ProgressDialog dialogo;
        boolean browserSeleccionado;
        boolean descargaSeleccionado;
        boolean archivosSeleccionado;
        boolean portapapelesSele;
        String espacioArchivos;
        String espacioDescarga;


        public LimpiarDatos(Activity activity, ArrayList<Item> items) {
            this.activity = activity;
            this.items = items;
            this.browserSeleccionado = false;
        }

        @Override
        protected void onPreExecute() {
            try {
                dialogo = new ProgressDialog(activity, R.style.DialogCustomTheme);
            } catch (NoSuchMethodError e) {
                dialogo = new ProgressDialog(activity);
            }

            dialogo.setMessage("Cleaning...");
            dialogo.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Looper.prepare();
            for (int i = 0; i < items.size(); i++) {
                if (!items.get(i).isSection()) {
                    EntryItem ite = (EntryItem) items.get(i);
                    if (ite.isSelected()) {
                        if (ite.title.equals("Browser")) {
                            this.browserSeleccionado = true;
                            Browser.clearHistory(getContentResolver());
                            ite.setCantidad("0 Pag");
                            ite.setSelected(false);
                        } else if (ite.title.equals("Downloads")) {
                            descargaSeleccionado = true;
                            espacioDescarga = espacioDescargas();
                            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                            for (File f : file.listFiles()) {
                                DeleteEspacioDescargas(f);
                            }
                            ite.setCantidad("0 Bytes");
                            ite.setSelected(false);
                        } else if (ite.title.equals("Records")) {
                            archivosSeleccionado = true;
                            espacioArchivos = espacioArchivos();
                            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                            for (File f : file.listFiles()) {
                                DeleteEspacioArchivos(f);
                            }
                            ite.setCantidad("0 Bytes");
                            ite.setSelected(false);
                        } else if (ite.title.equals("Clipboard")) {
                            portapapelesSele = true;
                            final ClipboardManager portapapeles = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            portapapeles.setText("");
                            ite.setCantidad("0 Bytes");
                            ite.setSelected(false);
                        }
                    }
                }
            }
            return true;
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            dialogo.dismiss();
            adapter.notifyDataSetChanged();

            try {
                builde = new AlertDialog.Builder(activity, R.style.DialogCustomTheme);
            } catch (NoSuchMethodError e) {
                builde = new AlertDialog.Builder(activity);
            }

            builde.setTitle("Your device has been cleaned!");
            builde.setIcon(R.drawable.dialog_clean_icon);

            String mensaje;
            String m1 = "", m2 = "", m3 = "", m4 = "";

            if (this.browserSeleccionado) {
                m1 = msjBrowser();
            }

            if (this.descargaSeleccionado) {
                m2 = msjDescarga(espacioDescarga);
            }

            if (this.archivosSeleccionado) {
                m3 = msjArchivos(espacioArchivos);
            }

            if (this.portapapelesSele) {
                m4 = msjPortapales();
            }

            mensaje = (m1 + m2 + m3 + m4);
            /*builde.setMessage(mensaje);
            builde
                    .setCancelable(false)
                    .setNeutralButton("Accept",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }
                    );
            AlertDialog alertDialog = builde.create();
            alertDialog.show();*/

            Manager.Dialog.showDialogMsg( HistoryActivity.this, "Your device has been cleaned!", mensaje);
        }
    }

    public String msjBrowser() {
        return "He wiped the History Browser" + "\n";
    }

    public String msjDescarga(String espacio) {
        return "He wiped " + espacio + " download History" + "\n";
    }

    public String msjArchivos(String espacio) {
        return "He wiped " + espacio + " history of media files" + "\n";
    }

    public String msjPortapales() {
        return "Historical wiped your clipboard";
    }

    public void onDestroy() {
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
         adbud();
        }

        else
        {
            adbud();
        }
    }


    public void ct()
    {     ctrl = io.display.sdk.Controller.getInstance();
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
