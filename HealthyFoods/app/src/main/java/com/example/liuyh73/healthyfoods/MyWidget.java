package com.example.liuyh73.healthyfoods;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class MyWidget extends AppWidgetProvider {
    private static final String WIDGETSTATIC = "com.example.liuyh73.healthyfoods.MyWidgetStaticFilter";
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_widget);
        views.setTextViewText(R.id.widgetText, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.my_widget);
        remoteViews.setTextViewText(R.id.widgetText, widgetText);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        remoteViews.setOnClickPendingIntent(R.id.widgetLogo, pendingIntent);
        ComponentName componentName = new ComponentName(context, MyWidget.class);
        appWidgetManager.updateAppWidget(componentName, remoteViews);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
//
    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context, intent);
        if(intent.getAction().equals(WIDGETSTATIC)){
            Log.i("*******onReceive*******", WIDGETSTATIC);
            if(intent.getExtras()!=null){
                Collection recommendFood = (Collection)intent.getExtras().get("Recommend Food");
                Log.i("Recommend Food", recommendFood.getName());
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.my_widget);
                remoteViews.setTextViewText(R.id.widgetText, "今日推荐 "+recommendFood.getName());

                Intent intent1 = new Intent(context, Detail.class);
                intent1.putExtra("Recommend Food", recommendFood);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.widgetLogo, pendingIntent);

                ComponentName componentName = new ComponentName(context, MyWidget.class);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }
        }
    }
}

