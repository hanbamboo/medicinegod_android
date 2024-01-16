package com.daqin.medicinegod.CustomWidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.daqin.medicinegod.R;
import com.daqin.medicinegod.Utils.Utils;

public class MyWidget extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
//        Toast.makeText(context, "onReceive方法调用了", Toast.LENGTH_SHORT).show();
        Log.d("TAG", "onReceive方法调用了");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
//        Toast.makeText(context, "onUpdate方法调用了", Toast.LENGTH_SHORT).show();

        Log.d("TAG", "onUpdate方法调用了");
        //给Button绑定一个PendingIntent，当点击按钮是发送给Service发广播
        //当点击Button时，触发PendingIntent,发广播给MyService
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName provider = new ComponentName(context, MyWidget.class);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widght_medicine);
        int[] res = Utils.getMedicinesCount(context);
        views.setTextViewText(R.id.widght_all, String.valueOf(res[0]));
        views.setTextViewText(R.id.widght_green, String.valueOf(res[1]));
        views.setTextViewText(R.id.widght_orange, String.valueOf(res[2]));
        views.setTextViewText(R.id.widght_red, String.valueOf(res[3]));

        Intent numberIntent = new Intent("ACTION_MAKE_NUMBER");
        views.setOnClickPendingIntent(R.id.widght_fresh, PendingIntent.getBroadcast(context, 0, numberIntent, PendingIntent.FLAG_UPDATE_CURRENT));


        manager.updateAppWidget(provider, views);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
//        Toast.makeText(context, "onDeleted方法调用了", Toast.LENGTH_SHORT).show();

        Log.d("TAG", "onDeleted方法调用了");
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
//        Toast.makeText(context, "onEnabled方法调用了", Toast.LENGTH_SHORT).show();

        Log.d("TAG", "onEnabled方法调用了");
        //启动MyService
        Intent intent = new Intent(context, MyService.class);
        context.startService(intent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
//        Toast.makeText(context, "onDisabled方法调用了", Toast.LENGTH_SHORT).show();

        Log.d("TAG", "onDisabled方法调用了");
        //停止MyService
        Intent intent = new Intent(context, MyService.class);
        context.stopService(intent);
    }

}