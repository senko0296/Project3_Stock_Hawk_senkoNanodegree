package com.sam_chordas.android.stockhawk.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.StocksService;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

public class StockHawkWidget extends AppWidgetProvider {

    public static final String EXTRA_ITEM = "item_pos";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Toast.makeText(context, R.string.fetching_data, Toast.LENGTH_SHORT).show();
        final int count = appWidgetIds.length;

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];

            Intent svcIntent = new Intent(context, StocksService.class);
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.stock_hawk_widget);

            remoteViews.setRemoteAdapter(R.id.stock_list, svcIntent);

            // !--START--!
            Intent intent = new Intent(context, StockHawkWidget.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_item, pendingIntent);
            // !--END--!

            final Intent onItemClick = new Intent(context, StockHawkWidget.class);
            onItemClick.setAction("stock_click");
            onItemClick.setData(Uri.parse(onItemClick
                    .toUri(Intent.URI_INTENT_SCHEME)));
            final PendingIntent onClickPendingIntent = PendingIntent
                    .getBroadcast(context, 0, onItemClick,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.stock_list,
                    onClickPendingIntent);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
            appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.stock_list);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("stock_click")) {
            Intent launchIntent = new Intent(context, MyStocksActivity.class);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }
}

