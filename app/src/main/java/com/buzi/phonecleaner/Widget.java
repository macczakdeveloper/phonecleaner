package com.buzi.phonecleaner;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.buzi.phonecleaner.R;

/*Creado por Sebastian Cipolat Buenos Aires Argentina 2012*/

public class Widget extends AppWidgetProvider {

    private static final String ACTION_cambiarlayout = "a_cambiarlayout";

    int status = 1;
    public String shprefreg = "MSG_switch_status";
    private Context context;
    private static RemoteViews remoteViews;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        SharedPreferences prefs = context.getSharedPreferences(Manager.SPreferences.SP_WIDGET_NAME, Context.MODE_PRIVATE);
        String mensaje = prefs.getString(Manager.SPreferences.SP_WIDGET_NAME, "error");

        if (mensaje == "error") {

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Manager.SPreferences.SP_WIDGET_VALUE, Manager.SPreferences.SP_WIDGET_OFF);
            editor.commit();
            mensaje = "off";

        }

        actualizarWidget(context, appWidgetManager, mensaje);

    }

    public static void actualizarWidget(Context context,
                                        AppWidgetManager appWidgetManager, String value) {

        ComponentName thisWidget;

        int lay_id=0;


        if (value.equals("on")) {
            // ON
            lay_id = R.layout.widget_on;
        }

        if (value.equals("off")) {
            // off
            lay_id = R.layout.widget;
        }

        // Vamos a acceder a la vista y cambiar el layout segun lay_id
        remoteViews = new RemoteViews(context.getPackageName(), lay_id);

        // identifica a nuestro widget
        thisWidget = new ComponentName(context, Widget.class);

        // Creamos un intent a nuestra propia clase
        Intent intent = new Intent(context, Widget.class);
        // seleccionamos la accion ACTION_cambiarlayout
        intent.setAction(ACTION_cambiarlayout);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, 0);

		/*
		 * Equivalente a setOnClickListener de un boton comun lo asocio con el
		 * layout1 ya que al tocar este se ejecutara la accion y con
		 * pendingIntent
		 */

        remoteViews.setOnClickPendingIntent(R.id.relative, pendingIntent);

        // actualizamos el widget
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(ACTION_cambiarlayout)) {

            this.context = context;
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            SharedPreferences prefs = context.getSharedPreferences(Manager.SPreferences.SP_WIDGET_NAME,
                    Context.MODE_PRIVATE);
            String mensaje = prefs.getString(Manager.SPreferences.SP_WIDGET_VALUE, Manager.SPreferences.SP_WIDGET_OFF);
            SharedPreferences.Editor editor = prefs.edit();

            if (mensaje.equals(Manager.SPreferences.SP_WIDGET_OFF)) {

                editor.putString(Manager.SPreferences.SP_WIDGET_VALUE, Manager.SPreferences.SP_WIDGET_ON);
                editor.commit();
                new LoadProcess().execute();
                AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
                actualizarWidget(context, widgetManager, Manager.SPreferences.SP_WIDGET_ON);

            } /*else if (mensaje.equals("off")) {
                editor.putString(shprefreg, "on");
                editor.commit();
                Log.e("! :)  shprefreg write -> ", "on");
                new_status = "on";

            }*/

            // Actualizamos el estado del widget.
            /*AppWidgetManager widgetManager = AppWidgetManager
                    .getInstance(context);

            actualizarWidget(context, widgetManager, new_status);*/

        }

        super.onReceive(context, intent);
    }

    private class LoadProcess extends AsyncTask<Void, Void, Boolean> {

        private int auxMemoryAcum = 0;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            SharedPreferences sharedPreferences = context.getSharedPreferences(Manager.SPreferences.SP_NAME_EXCEPTION, Context.MODE_PRIVATE);
            String lista = sharedPreferences.getString(Manager.SPreferences.SP_LIST_EXCEPTION, null);
            ArrayList<String> list = new ArrayList<String>();

            if(lista == null){
                Manager.SPreferences.defaultSharedP(context);
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

            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

            List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();

            if (processes == null) return false;

            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : processes) {

                if(list.indexOf(runningAppProcessInfo.processName) == -1){

                    if(!runningAppProcessInfo.processName.equals(context.getPackageName())){

                        int pides[] = new int[1];
                        pides[0] = runningAppProcessInfo.pid;
                        android.os.Debug.MemoryInfo[] memoryInfoArray = activityManager.getProcessMemoryInfo(pides);

                        auxMemoryAcum += (long) memoryInfoArray[0].getTotalPss();


                        for (int k = 0; k < runningAppProcessInfo.pkgList.length; k++) {
                            activityManager.killBackgroundProcesses(runningAppProcessInfo.pkgList[k]);
                        }

                        android.os.Process.killProcess(runningAppProcessInfo.pid);


                    }

                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            if (aBoolean) {

                SharedPreferences prefs = context.getSharedPreferences(Manager.SPreferences.SP_WIDGET_NAME,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(Manager.SPreferences.SP_WIDGET_VALUE,Manager.SPreferences.SP_WIDGET_OFF);
                editor.commit();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);

                actualizarWidget(context, widgetManager, Manager.SPreferences.SP_WIDGET_OFF);

                Toast.makeText(context, (auxMemoryAcum / 1024L) + " MB RAM released", Toast.LENGTH_SHORT).show();

            }

        }
    }

}
