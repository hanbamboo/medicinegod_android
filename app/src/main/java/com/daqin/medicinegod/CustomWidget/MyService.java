package com.daqin.medicinegod.CustomWidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import com.daqin.medicinegod.R;
import com.daqin.medicinegod.Utils.Utils;

import java.util.Random;

/**
 * 用来与widget进行交互的Service 提供Widget中显示的数字
 */

public class MyService extends Service {
    MyReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        //动态注册广播接收器
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("ACTION_MAKE_NUMBER");
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        //注销广播接收器
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * 广播接收器
     */
    private static class MyReceiver extends BroadcastReceiver {
        // 接收到Widget发送的广播
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("ACTION_MAKE_NUMBER".equals(intent.getAction())) {

                AppWidgetManager manager = AppWidgetManager.getInstance(context);
                ComponentName provider = new ComponentName(context, MyWidget.class);
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widght_medicine);
                //设置要显示的TextView，及显示的内容
                int[] res = Utils.getMedicinesCount(context);

                views.setTextViewText(R.id.widght_all, String.valueOf(res[0]));
                views.setTextViewText(R.id.widght_green, String.valueOf(res[1]));
                views.setTextViewText(R.id.widght_orange, String.valueOf(res[2]));
                views.setTextViewText(R.id.widght_red, String.valueOf(res[3]));
                // 发送一个系统广播
                manager.updateAppWidget(provider, views);
            }
        }

    }

}