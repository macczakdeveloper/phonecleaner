package com.buzi.phonecleaner;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.buzi.phonecleaner.R;


public class WidgetBatery extends AppWidgetProvider {

    // our actions for our buttons
    public static String ACTION_WIDGET_WIFI = "ActionReceiverWifi";
    public static String ACTION_WIDGET_DATA = "ActionReceiverData";
    public static String ACTION_WIDGET_BRIGHTNESS = "ActionReceiverBrightness";
    public static String ACTION_WIDGET_VOLUMEN = "ActionReceiverVolumen";
    public static String ACTION_WIDGET_GPS = "ActionReceiverGps";
    public static String ACTION_WIDGET_TIME_OUT = "ActionReceiverTimeOut";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_batery);

        Intent active = new Intent(context, WidgetBatery.class);
        active.setAction(ACTION_WIDGET_WIFI);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.wifi, actionPendingIntent);

        active = new Intent(context, WidgetBatery.class);
        active.setAction(ACTION_WIDGET_DATA);
        actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.data, actionPendingIntent);

        active = new Intent(context, WidgetBatery.class);
        active.setAction(ACTION_WIDGET_BRIGHTNESS);
        actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.brightness, actionPendingIntent);

        active = new Intent(context, WidgetBatery.class);
        active.setAction(ACTION_WIDGET_VOLUMEN);
        actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.volumen, actionPendingIntent);

        active = new Intent(context, WidgetBatery.class);
        active.setAction(ACTION_WIDGET_GPS);
        actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.gps, actionPendingIntent);

        active = new Intent(context, WidgetBatery.class);
        active.setAction(ACTION_WIDGET_TIME_OUT);
        actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.time_out, actionPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_WIDGET_WIFI)) {
            Log.i("onReceive", ACTION_WIDGET_WIFI);
        } else if (intent.getAction().equals(ACTION_WIDGET_DATA)) {
            Log.i("onReceive", ACTION_WIDGET_DATA);
        } else if (intent.getAction().equals(ACTION_WIDGET_BRIGHTNESS)) {
            Log.i("onReceive", ACTION_WIDGET_BRIGHTNESS);
        }else if (intent.getAction().equals(ACTION_WIDGET_VOLUMEN)) {
            Log.i("onReceive", ACTION_WIDGET_VOLUMEN);
        }else if (intent.getAction().equals(ACTION_WIDGET_GPS)) {
            Log.i("onReceive", ACTION_WIDGET_GPS);
        }else if (intent.getAction().equals(ACTION_WIDGET_TIME_OUT)) {
            Log.i("onReceive", ACTION_WIDGET_TIME_OUT);
        }else {
            super.onReceive(context, intent);
        }
    }
}
