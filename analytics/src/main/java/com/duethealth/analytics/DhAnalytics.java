package com.duethealth.analytics;

import android.app.AlarmManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class DhAnalytics {
    private static final String TAG = "DhAnalytics";

    private static DhAnalytics sInstance;
    private Context mContext;
    private String mBaseUrl;

    public static class Events {
        public static final String APP_START = "appStart";
        public static final String CONTENT_VIEW = "contentView";
        public static final String LINK_CLICK = "linkClick";
    }

    public static class Extras {
        public static final String EVENT = "event";
        public static final String AUTH_TOKEN = "auth_token";
        public static final String LOGGING_ENABLED = "logging_enabled";
        public static final String ID = "id";
        public static final String EVENT_DATA = "event_data";
        public static final String EVENT_NAME = "event_name";
    }

    public static class Result {
        @SerializedName("success")
        public boolean success;
        @SerializedName("message")
        public String message;
    }

    private DhAnalytics(Context context) {
        mContext = context;
    }

    private DhAnalytics() {
    }

    public static DhAnalytics getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DhAnalytics(context);
        }
        return sInstance;
    }

    private static DhAnalytics getInstance() {
        if (sInstance == null) {
            sInstance = new DhAnalytics();
        }
        return sInstance;
    }

    public static DhAnalytics getInstance(Context context, String baseUrl) {
        if (sInstance == null) {
            sInstance = new DhAnalytics(context);
        }
        if (TextUtils.isEmpty(sInstance.getBaseUrl())) {
            sInstance.setBaseUrl(baseUrl);
        }
        return sInstance;
    }

    public void setBaseUrl(String url) {
        mBaseUrl = url;
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public static void logEvent(String event) {
        logEvent(event, null, null, false);
    }

    public static void logEvent(String event, String eventData) {
        logEvent(event, eventData, null, false);
    }

    public static void logEvent(String event, String eventData, String authToken) {
        logEvent(event, eventData, authToken, false);
    }

    public static void logEvent(final String event, final String eventData, final String authToken, final boolean loggingEnabled) {
        getInstance();
        if (TextUtils.isEmpty(sInstance.getBaseUrl()) || sInstance.getContext() == null) {
            Log.w(TAG, "Tried to log an event without a URL.");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                DhAnalyticItem item = new DhAnalyticItem.Builder()
                        .event(event).eventData(eventData).loggingEnabled(loggingEnabled)
                        .token(authToken).build();
                SQLiteDatabase db = new AnalyticsSQLiteHelper(sInstance.getContext()).getWritableDatabase();
                if (db == null) {
                    Log.e(TAG, "Created database is null.");
                    return;
                }
                long id = cupboard().withDatabase(db).put(item);
                if (loggingEnabled) {
                    Log.d(TAG, "Item entered into Analytics DB. ID: " + id);
                }
                db.close();
                setAlarmToSync(sInstance.getContext());
            }
        });
    }

    public static final long INTERVAL_DAY = 86400;

    private static void setAlarmToSync(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.setInexactRepeating();
    }

}