package com.buzi.phonecleaner;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;



import adapters.AdapterCallsMsg;
import helpers.EntryItem;
import helpers.InfoLlamadas;
import helpers.InfoMensajes;
import helpers.Item;
import helpers.SectionItem;
import helpers.TypefaceSpan;


public class CallsMsgActivity extends ActionBarActivity implements OnItemClickListener {
    private InfoLlamadas llamadas;
    private InfoMensajes mensajes;
    private ArrayList<Item> items = new ArrayList<Item>();
    private ListView listview = null;
    private Button limpiar = null;
    private Activity activity;
    private AdapterCallsMsg adapter;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_msg);

        activity = this;
        llamadas = new InfoLlamadas(this);
        mensajes = new InfoMensajes(this);
        limpiar = (Button) findViewById(R.id.limpiar);
        listview = (ListView) findViewById(R.id.listView_main);
        listview.addHeaderView(new View(this));
        listview.addFooterView(new View(this));

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        limpiar.setTypeface(typeface);
        barraTitulo();
        informacionParaListview();
        botonlimpiar();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.llamadas_mensajes, menu);
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
        actionbar.setIcon(R.drawable.call_msg_bar);
        SpannableString s = new SpannableString("Calls and messages");
        s.setSpan(new TypefaceSpan(this, "Roboto-Light.ttf"), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionbar.setTitle(s);
        actionbar.setDisplayUseLogoEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);
        //actionbar.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_passlock));
    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
        position = position - 1;
        EntryItem item = (EntryItem) items.get(position);
        CheckBox box = (CheckBox) view.findViewById(R.id.checkBox_ram);

        if (!item.isSelected()) {
            item.setSelected(true);
            box.setChecked(item.isSelected());
        } else {
            item.setSelected(false);
            box.setChecked(item.isSelected());
        }

    }


    public void informacionParaListview() {
        llamadas.obtenerDetallesLlamadas();
        mensajes.obtenerMensajesRecibidos();
        mensajes.obtenerMensajesEnviados();

        items.add(new SectionItem("Calls"));
        items.add(new EntryItem("Incoming calls", String.valueOf(llamadas.getLlamadasRecibidas().size()), R.drawable.call_recive));
        items.add(new EntryItem("Outgoing", String.valueOf(llamadas.getLlamadasSalientes().size()), R.drawable.call_send));
        items.add(new EntryItem("Missed calls", String.valueOf(llamadas.getLlamadasPerdidas().size()), R.drawable.call_cancel));

        items.add(new SectionItem("Message of Text"));
        items.add(new EntryItem("Received Messages", String.valueOf(mensajes.getMensajesRecibidos().size()), R.drawable.msg_recive));
        items.add(new EntryItem("Sent Messages", String.valueOf(mensajes.getMensajesEnviados().size()), R.drawable.msg_send));

        adapter = new AdapterCallsMsg(this, items);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
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
        private int totalRegistros;
        private int registrosLlamadas;
        private int registrosMensajes;
        ProgressDialog dialogo;

        public LimpiarDatos(Activity activity, ArrayList<Item> items) {
            this.activity = activity;
            this.items = items;
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
            int e = 0, s = 0, p = 0, r = 0, en = 0;
            for (int i = 0; i < items.size(); i++) {
                if (!items.get(i).isSection()) {
                    EntryItem ite = (EntryItem) items.get(i);
                    if (ite.isSelected()) {
                        if (ite.title.equals("Incoming calls")) {
                            e = llamadas.getLlamadasRecibidas().size();
                            llamadas.eliminarRegistrosLlamadas(llamadas.getLlamadasRecibidas());
                            ite.setCantidad("0");
                            ite.setSelected(false);
                        } else if (ite.title.equals("Outgoing")) {
                            s = llamadas.getLlamadasSalientes().size();
                            llamadas.eliminarRegistrosLlamadas(llamadas.getLlamadasSalientes());
                            ite.setCantidad("0");
                            ite.setSelected(false);
                        } else if (ite.title.equals("Missed calls")) {
                            p = llamadas.getLlamadasPerdidas().size();
                            llamadas.eliminarRegistrosLlamadas(llamadas.getLlamadasPerdidas());
                            ite.setCantidad("0");
                            ite.setSelected(false);
                        } else if (ite.title.equals("Received Messages")) {
                            r = mensajes.getMensajesRecibidos().size();
                            mensajes.eliminarMensajes(mensajes.getMensajesRecibidos());
                            ite.setCantidad("0");
                            ite.setSelected(false);
                        } else if (ite.title.equals("Sent Messages")) {
                            en = mensajes.getMensajesEnviados().size();
                            mensajes.eliminarMensajes(mensajes.getMensajesEnviados());
                            ite.setCantidad("0");
                            ite.setSelected(false);
                        }
                    }
                }
            }
            this.registrosMensajes = (r + en);
            this.registrosLlamadas = (e + s + p);
            this.totalRegistros = (e + s + p + r + en);
            return true;
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            dialogo.dismiss();
            adapter.notifyDataSetChanged();

            String msg = "Nothing to clean";

            /*try {
                builder = new AlertDialog.Builder(activity, R.style.DialogCustomTheme);
            } catch (NoSuchMethodError e) {
                builder = new AlertDialog.Builder(activity);
            }

            builder.setTitle("Your device looks cleaner!");
            builder.setIcon(R.drawable.dialog_clean_icon);*/

            if (this.registrosLlamadas != 0) {
                msg = msjRegistroLlamadas(registrosLlamadas) + "Total Deleted Records :" + String.valueOf(this.totalRegistros);
                //builder.setMessage(msjRegistroLlamadas(registrosLlamadas) + "Total Deleted Records :" + String.valueOf(this.totalRegistros));
            }

            if (this.registrosMensajes != 0) {
                msg = msjRegistroMensajes(registrosMensajes) + "Total Deleted Records :" + String.valueOf(this.totalRegistros);
                //builder.setMessage(msjRegistroMensajes(registrosMensajes) + "Total Deleted Records :" + String.valueOf(this.totalRegistros));
            }

            if ((this.registrosLlamadas != 0) && (this.registrosMensajes != 0)) {
                msg = msjRegistroLlamadas(registrosLlamadas) + msjRegistroMensajes(registrosMensajes) + "Total Deleted Records :" + String.valueOf(this.totalRegistros);
                //builder.setMessage(msjRegistroLlamadas(registrosLlamadas) + msjRegistroMensajes(registrosMensajes) + "Total Deleted Records :" + String.valueOf(this.totalRegistros));
            }

            /*builder
                    .setCancelable(false)
                    .setNeutralButton("Accept",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }
                    );
            AlertDialog alertDialog = builder.create();
            alertDialog.show();*/

            Manager.Dialog.showDialogMsg( CallsMsgActivity.this, "Your device looks cleaner!", msg);
        }


        public String msjRegistroLlamadas(int registros) {
            return "Are erased " + String.valueOf(registros) + " incoming / outgoing and missed calls" + "\n\n";
        }

        public String msjRegistroMensajes(int registros) {
            return "Are erased " + String.valueOf(registros) + " of text messages sent / received" + "\n\n";
        }
    }
}
