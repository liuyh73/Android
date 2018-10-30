package com.example.liuyh73.healthyfoods;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class DynamicReceiver extends BroadcastReceiver {
    private static final String DYNAMICACTION = "com.example.liuyh73.healthyfoods.MyDynamicFilter";
    private static final String WIDGETDYNAMICACTION = "com.example.liuyh73.healthyfoods.MyWidgetDynamicFilter";
    // private static int count=1;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(DYNAMICACTION)){
            Bundle extras = intent.getExtras();
            Collection food = (Collection) extras.get("Collecting food");
            int icon = (int) extras.get("Icon");
            // 获取状态通知栏管理器
            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            // 实例化通知栏构造器 Notification.Builder
            Notification.Builder notificationBuilder = new Notification.Builder(context);
            // 对builder进行配置
            notificationBuilder.setContentTitle("已收藏")  //设置通知栏标题：发件人
                    .setContentText(food.getName()) //设置通知栏显示的内容
                    .setTicker("您有一条新通知")    //通知首次出现在通知栏上，带上升动画效果
                    .setPriority(Notification.PRIORITY_DEFAULT)   //设置通知优先级
                    .setWhen(System.currentTimeMillis())    // 设置通知产生的时间，一般为系统获取的事件
                    .setSmallIcon(icon)      // 设置icon
                    .setAutoCancel(true);                  // 设置这个标志当用户点机面板就可以将通知取消

            // 绑定intent，点击图标能够进入某个activity
            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.putExtra("Collections", "true");
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1,PendingIntent.FLAG_CANCEL_CURRENT);
            notificationBuilder.setContentIntent(pendingIntent);

            // 绑定Notification， 发送通知请求
            Notification notification = notificationBuilder.build();
            notificationManager.notify(0, notification);
            // notificationManager.notify(count++, notification);
        }
        if(intent.getAction().equals(WIDGETDYNAMICACTION)) {
            Log.i("WIDGETDYNAMICACTION", WIDGETDYNAMICACTION);
            Bundle extras = intent.getExtras();
            Collection food = (Collection) extras.get("Collecting food");
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.my_widget);
            remoteViews.setTextViewText(R.id.widgetText, "已收藏 "+food.getName());

            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.putExtra("Collections", "true");
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widgetLogo, pendingIntent);

            ComponentName componentName = new ComponentName(context, MyWidget.class);
            appWidgetManager.updateAppWidget(componentName, remoteViews);
        }
    }
}
