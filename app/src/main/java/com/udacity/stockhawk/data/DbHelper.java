package com.udacity.stockhawk.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.udacity.stockhawk.data.Contract.Quote;

public class DbHelper extends SQLiteOpenHelper {

    private static final String REAL_NOT_NULL = " REAL NOT NULL, ";
    private static final String NAME = "StockHawk.db";
    private static final int VERSION = 1;

    public static final String[] COLUMN_PROJECTION =
            {Contract.Quote._ID, Contract.Quote.COLUMN_SYMBOL, Contract.Quote.COLUMN_PRICE,
                    Contract.Quote.COLUMN_ABSOLUTE_CHANGE, Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
                    Contract.Quote.COLUMN_HISTORY,};

    DbHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String builder = "CREATE TABLE " + Quote.TABLE_NAME + " (" + Quote._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + Quote.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                Quote.COLUMN_PRICE + REAL_NOT_NULL + Quote.COLUMN_ABSOLUTE_CHANGE + REAL_NOT_NULL +
                Quote.COLUMN_PERCENTAGE_CHANGE + REAL_NOT_NULL
                + Quote.COLUMN_HISTORY + " TEXT NOT NULL, "
                + "UNIQUE (" + Quote.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        db.execSQL(builder);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(" DROP TABLE IF EXISTS " + Quote.TABLE_NAME);

        onCreate(db);
    }
}
