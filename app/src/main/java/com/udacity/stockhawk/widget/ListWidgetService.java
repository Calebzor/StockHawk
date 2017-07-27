package com.udacity.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.Locale;

import static com.udacity.stockhawk.StockHawkApp.HISTORY_EXTRA_KEY;
import static com.udacity.stockhawk.StockHawkApp.SYMBOL_EXTRA_KEY;
import static com.udacity.stockhawk.data.DbHelper.COLUMN_PROJECTION;

public class ListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String SET_BACKGROUND_RESOURCE = "setBackgroundResource";

    private Context context;
    private Cursor cursor;

    ListRemoteViewsFactory(Context applicationContext) {
        context = applicationContext;
    }

    @Override
    public void onCreate() {
        // nothing to do
    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        // Get all plant info ordered by creation time
        cursor = context.getContentResolver().query(Contract.Quote.URI, COLUMN_PROJECTION, null,
                null, Contract.Quote.COLUMN_SYMBOL + Contract.Quote.SORT_ORDER_ASCENDING);
    }

    @Override
    public void onDestroy() {
        cursor.close();
    }

    @Override
    public int getCount() {
        if (cursor == null) {
            return 0;
        }
        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        String currencySymbol = context.getResources().getString(R.string.currency_symbol);
        cursor.moveToPosition(position);

        String symbol = cursor.getString(Contract.Quote.POSITION_SYMBOL);
        String price = cursor.getString(Contract.Quote.POSITION_PRICE);
        String percentageChanged = cursor.getString(Contract.Quote.POSITION_PERCENTAGE_CHANGE);
        String history = cursor.getString(Contract.Quote.POSITION_HISTORY);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.list_item_quote);
        String newPercent = String.format(Locale.US, "%.2f", Float.parseFloat(percentageChanged));

        views.setTextViewText(R.id.symbol, symbol);
        views.setTextViewText(R.id.price, currencySymbol + price);
        views.setTextViewText(R.id.change, addSignOnPrice(newPercent));
        if (Float.parseFloat(newPercent) > 0) {
            views.setInt(R.id.change, SET_BACKGROUND_RESOURCE, R.color.material_green_700);
        }
        else {
            views.setInt(R.id.change, SET_BACKGROUND_RESOURCE, R.color.material_red_700);
        }

        Intent fillInIntent = new Intent();
        Bundle extras = new Bundle();
        extras.putString(SYMBOL_EXTRA_KEY, symbol);
        extras.putString(HISTORY_EXTRA_KEY, history);
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.stockListItem, fillInIntent);

        return views;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private String addSignOnPrice(String percent) {
        StringBuilder newPercent = new StringBuilder(percent);
        if (Float.parseFloat(percent) > 0) {
            newPercent.insert(0, "+");
        }
        return newPercent.toString();
    }
}

