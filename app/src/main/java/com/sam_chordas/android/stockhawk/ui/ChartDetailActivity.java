package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;

public class ChartDetailActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Context mContext;
    private LineChart lineChart;
    private Cursor mCursor;
    private String quoteName;
    private int CURSOR_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_detail);
        lineChart = (LineChart) findViewById(R.id.linechart);
        lineChart.setDescription(getString(R.string.previous_stock_data));
        lineChart.setDescriptionTextSize(10f);
        lineChart.setDescriptionColor(ContextCompat.getColor(this, R.color.material_green_700));
        lineChart.getLegend().setTextColor(ContextCompat.getColor(this, R.color.material_red_700));
        quoteName = getIntent().getStringExtra("symbol");
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ? AND " + QuoteColumns.SYMBOL + " = ?",
                new String[]{"0", quoteName},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursor = cursor;
        ArrayList<Entry> vals = new ArrayList<Entry>();
        int i = 0;
        if (mCursor.moveToFirst()) {
            do {
                vals.add(new Entry(Float.parseFloat(mCursor.getString(2)), 6 - i));
                i++;
            } while (i < 7 && mCursor.moveToNext());
        }
        LineDataSet dataset = new LineDataSet(vals, quoteName);
        dataset.setLineWidth(2f);
        dataset.setCircleRadius(4f);
        dataset.setAxisDependency(YAxis.AxisDependency.LEFT);
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataset);
        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("");
        xVals.add("");
        xVals.add("");
        xVals.add("");
        xVals.add("");
        xVals.add("");
        xVals.add("");
        LineData lineData = new LineData(xVals, dataSets);
        lineData.setValueTextColor(ContextCompat.getColor(getApplicationContext(), R.color.material_green_700));
        lineData.setValueTextSize(10f);
        lineChart.setData(lineData);
        lineChart.invalidate();
        mCursor.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
