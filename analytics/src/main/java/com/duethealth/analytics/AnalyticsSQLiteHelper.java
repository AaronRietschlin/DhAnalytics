package com.duethealth.analytics;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

class AnalyticsSQLiteHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "dh_analytics_v2.db";
    private static final int DB_VERSION = 1;

    static {
        cupboard().register(DhAnalyticItem.class);
    }

    public AnalyticsSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cupboard().withDatabase(db).upgradeTables();
    }
}
