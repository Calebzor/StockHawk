package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ui.DetailViewActivity;
import com.udacity.stockhawk.ui.MainActivity;

public class WidgetProvider extends AppWidgetProvider {

    private static final String ACTION_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE";

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        String action = intent.getAction();
        if (ACTION_UPDATE.equals(action)) {
            int[] appWidgetIDs = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIDs, R.id.stockList);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Intent intent = new Intent(context, WidgetService.class);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
        rv.setRemoteAdapter(R.id.stockList, intent);
        rv.setEmptyView(R.id.stockList, R.id.defaultMessage);

        Intent detailIntent = new Intent(context, DetailViewActivity.class);
        PendingIntent detailPendingIntent = PendingIntent.getActivity(context, 0, detailIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setPendingIntentTemplate(R.id.stockList, detailPendingIntent);

        Intent openAppIntent = new Intent(context, MainActivity.class);
        PendingIntent openAppPendingIntent = PendingIntent.getActivity(context, 0, openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickPendingIntent(R.id.stockTitle, openAppPendingIntent);
        rv.setOnClickPendingIntent(R.id.defaultMessage, openAppPendingIntent);
        rv.setOnClickPendingIntent(R.id.widget_layout_parent, openAppPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, rv);

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
