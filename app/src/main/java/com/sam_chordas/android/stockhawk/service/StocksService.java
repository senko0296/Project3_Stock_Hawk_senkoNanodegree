package com.sam_chordas.android.stockhawk.service;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.widgets.StockHawkWidget;

/**
 * Created by anirudhraghunath on 19/06/16.
 */
public class StocksService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StocksViewsFactory(this.getApplicationContext());
    }

    class StocksViewsFactory implements RemoteViewsFactory {
        private Cursor mCursor;
        private Context mContext;

        public StocksViewsFactory(Context context) {
            this.mContext = context;
        }

        @Override
        public void onCreate() {
            begin();
        }

        @Override
        public void onDataSetChanged() {
            begin();
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.list_item_widget);
            if (mCursor != null && !mCursor.isAfterLast()) {
                views.setTextViewText(R.id.stock_symbol, mCursor.getString(mCursor.getColumnIndex("symbol")));
                views.setTextViewText(R.id.bid_price, mCursor.getString(mCursor.getColumnIndex("bid_price")));
                views.setTextViewText(R.id.change, mCursor.getString(mCursor.getColumnIndex("change")));

                Bundle extras = new Bundle();
                extras.putInt(StockHawkWidget.EXTRA_ITEM, position);
                Intent fillInIntent = new Intent();
                fillInIntent.putExtras(extras);
                views.setOnClickFillInIntent(R.id.list_item_layout, fillInIntent);

                mCursor.moveToNext();
            }
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return new RemoteViews(mContext.getPackageName(), R.layout.widget_loading_layout);
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

        public void begin() {
            final long token = Binder.clearCallingIdentity();
            try {
                mCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                                QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);
            } finally {
                Binder.restoreCallingIdentity(token);
            }

            if (mCursor != null && mCursor.getCount() > 0)
                mCursor.moveToFirst();
        }
    }
}
