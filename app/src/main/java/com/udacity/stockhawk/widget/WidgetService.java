package com.udacity.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.DetailViewActivity;

import java.util.Locale;

import static com.udacity.stockhawk.StockHawkApp.HISTORY_EXTRA_KEY;
import static com.udacity.stockhawk.StockHawkApp.SYMBOL_EXTRA_KEY;
import static com.udacity.stockhawk.data.DbHelper.COLUMN_PROJECTION;

public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new StockWidgetViewsFactory(getApplicationContext()));
    }

    private class StockWidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private static final String SET_BACKGROUND_RESOURCE = "setBackgroundResource";

        private Cursor cursor;
        private Context context;

        StockWidgetViewsFactory(Context context) {
            this.context = context;
        }

        @Override
        public void onCreate() {
            query();
        }

        @Override
        public void onDataSetChanged() {
            query();
        }

        void query() {
            final long token = Binder.clearCallingIdentity();
            cursor = context.getContentResolver().query(Contract.Quote.URI, COLUMN_PROJECTION, null,
                    null, Contract.Quote.COLUMN_SYMBOL + Contract.Quote.SORT_ORDER_ASCENDING);
            Binder.restoreCallingIdentity(token);
        }

        @Override
        public void onDestroy() {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        @Override
        public int getCount() {
            return cursor != null ? cursor.getCount() : 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            String currencySymbol = context.getResources().getString(R.string.currency_symbol);

            if (!cursor.moveToPosition(position)) {
                return null;
            }
            String symbol = getCursorValue(Contract.Quote.POSITION_SYMBOL);
            String price = getCursorValue(Contract.Quote.POSITION_PRICE);
            String percent = getCursorValue(Contract.Quote.POSITION_PERCENTAGE_CHANGE);
            String history = getCursorValue(Contract.Quote.POSITION_HISTORY);

            RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.list_item_quote);
            String newPercent = String.format(Locale.US, "%.2f", Float.parseFloat(percent));

            row.setTextViewText(R.id.symbol, symbol);
            row.setTextViewText(R.id.price, currencySymbol + price);
            row.setTextViewText(R.id.change, addSignOnPrice(newPercent));
            row.setInt(R.id.change, SET_BACKGROUND_RESOURCE,
                    Float.parseFloat(percent) > 0 ? R.color.material_green_700 :
                            R.color.material_red_700);

            Intent intent = new Intent(context, DetailViewActivity.class);
            Bundle extras = new Bundle();
            extras.putString(SYMBOL_EXTRA_KEY, symbol);
            extras.putString(HISTORY_EXTRA_KEY, history);
            intent.putExtras(extras);

            row.setOnClickFillInIntent(R.id.stockListItem, intent);

            return row;
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
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        private String getCursorValue(int stringIndex) {
            return cursor.getString(stringIndex);
        }

        private String addSignOnPrice(String percent) {
            StringBuilder percentWithSign = new StringBuilder(percent);
            if (Float.parseFloat(percent) > 0) {
                percentWithSign.insert(0, "+");
            }
            return percentWithSign.toString();
        }
    }

}