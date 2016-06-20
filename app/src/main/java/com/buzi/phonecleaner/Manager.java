package com.buzi.phonecleaner;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buzi.phonecleaner.R;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by USER on 03/06/2014.
 */
public class Manager {

    public static class SPreferences {

        public static String SP_NAME_EXCEPTION = "exception";
        public static String SP_LIST_EXCEPTION = "list";
        public static String SP_WIDGET_ON = "on";
        public static String SP_WIDGET_OFF = "off";
        public static String SP_WIDGET_NAME = "name";
        public static String SP_WIDGET_VALUE = "value";

        public static void defaultSharedP(Context context) {

            JSONArray jsonArray = new JSONArray();
            String[] array = context.getResources().getStringArray(R.array.packer_name_default);
            for (String anArray : array) {
                jsonArray.put(anArray);
            }
            SharedPreferences.Editor editor = context.getSharedPreferences(SPreferences.SP_NAME_EXCEPTION, Context.MODE_PRIVATE).edit();
            editor.putString(SPreferences.SP_LIST_EXCEPTION, jsonArray.toString());
            editor.commit();

        }

    }

    public static class AccessMemory {

        public static String MEM_TOTAL = "MemTotal";
        public static String MEM_FREE = "MemFree";
        public static String ALL_MEM_INFO = "MemFree";

        public static long getMemoryTotal() throws IOException {

            return getMemoryInfo(MEM_TOTAL);
        }

        public static long getMemoryFree() throws IOException {
            return getMemoryInfo(MEM_FREE);
        }

        public static long getMemoryInfo(String memoryType) throws IOException {

            String str1 = "/proc/meminfo";

            String str2;

            String[] arrayOfString;

            long initial_memory = -1L;

            FileReader localFileReader = new FileReader(str1);

            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);

            while ((str2 = localBufferedReader.readLine()) != null) {

                /*if(memoryType.equals(ALL_MEM_INFO)){
                    Log.i("TAG_MEMORY", str2);
                    continue;
                }*/

                if (str2.startsWith(memoryType)) {
                    break;
                }

            }

            Log.i("TAG_MEMORY", str2 + "");

            if (str2 != null) {
                arrayOfString = str2.split("\\s+");
                try {

                    //total Memory
                    initial_memory = Integer.valueOf(arrayOfString[1]).intValue();

                } catch (Exception e) {
                }
            }


            localBufferedReader.close();

            return initial_memory;
        }

    }

    public static class Dialog {

        public static void showDialogMsg(Context context, String title, String msg) {
            final android.app.Dialog customDialog = new android.app.Dialog(context);
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            customDialog.setCancelable(false);
            customDialog.setContentView(R.layout.dialog_msg);
            TextView titleTV = (TextView) customDialog.findViewById(R.id.title);
            TextView msgTV = (TextView) customDialog.findViewById(R.id.msg);
            Style.roboto(context, titleTV, msgTV);
            titleTV.setText(title);
            msgTV.setText(msg);

            RelativeLayout btnAccept = (RelativeLayout) customDialog.findViewById(R.id.btnAcceptDialog);
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customDialog.dismiss();
                }
            });

            customDialog.show();

        }

        public static void showDialogConfirmation(Context context, String title, String msg, final View.OnClickListener actionAcept, final View.OnClickListener actionCancel, final View.OnClickListener add) {
            final android.app.Dialog customDialog = new android.app.Dialog(context);
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            customDialog.setCancelable(false);
            customDialog.setContentView(R.layout.dialog_confirmation);
            TextView titleTV = (TextView) customDialog.findViewById(R.id.title);
            TextView msgTV = (TextView) customDialog.findViewById(R.id.msg);
            TextView addTV = (TextView) customDialog.findViewById(R.id.add_ignored_list);
            titleTV.setText(title);
            msgTV.setText(msg);
            Style.roboto(context, titleTV, msgTV,addTV);
            View.OnClickListener add_i = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    add.onClick(view);
                    customDialog.dismiss();
                }
            };

            addTV.setOnClickListener(add_i);

            View.OnClickListener acept = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actionAcept.onClick(view);
                    customDialog.dismiss();
                }
            };

            View.OnClickListener cancel = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actionCancel.onClick(view);
                    customDialog.dismiss();
                }
            };
            RelativeLayout btnAccept = (RelativeLayout) customDialog.findViewById(R.id.btnAcceptDialog);
            RelativeLayout btnCancel = (RelativeLayout) customDialog.findViewById(R.id.btnCancelDialog);
            btnAccept.setOnClickListener(acept);
            btnCancel.setOnClickListener(cancel);
            customDialog.show();
        }
    }

    public static class Style {

        public static void roboto(Context context, TextView... texts) {
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
            for (TextView textView : texts) {
                textView.setTypeface(typeface);
            }

        }
    }
}
