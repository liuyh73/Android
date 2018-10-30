package com.example.liuyh73.healthyfoods;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class StaticReceiver extends BroadcastReceiver {
    private static final String STATICACTION = "com.example.liuyh73.healthyfoods.MyStaticFilter";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(STATICACTION)) {
            /*
             * Notification 可以提供持久地通知，位于手机最上层的状态通知栏中
             * 开启Notification主要涉及以下3个类：
             * 1. Notification.Builder：用于动态设置Notification的一些属性
             * 2. NotificationManager：负责将Notification在状态栏显示出来或取消
             * 3. Notification：设置Notification的相关属性
             */
            Bundle extras = intent.getExtras();
            Collection food = (Collection) extras.get("Food notified");
            int icon = (int) extras.get("Icon");
            // 获取状态通知栏管理器
            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            // 实例化通知栏构造器 Notification.Builder
            Notification.Builder notificationBuilder = new Notification.Builder(context);
            // 对builder进行配置
            notificationBuilder.setContentTitle("今日推荐")  //设置通知栏标题：发件人
                                .setContentText(food.getName()) //设置通知栏显示的内容
                                .setTicker("您有一条新通知")    //通知首次出现在通知栏上，带上升动画效果
                                .setPriority(Notification.PRIORITY_DEFAULT)   //设置通知优先级
                                .setWhen(System.currentTimeMillis())    // 设置通知产生的时间，一般为系统获取的事件
                                .setSmallIcon(icon)      // 设置icon
                                .setAutoCancel(true);                  // 设置这个标志当用户点机面板就可以将通知取消
            // 绑定intent，点击图标能够进入某个activity
            Intent intent1 = new Intent(context, Detail.class);
            intent1.putExtras(extras);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setContentIntent(pendingIntent);

            // 绑定Notification， 发送通知请求
            Notification notification = notificationBuilder.build();
            notificationManager.notify(0, notification);
        }
    }
}
