package com.duethealth.analytics;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders.Any.B;

/**
 * Handles logging an event to the Analytics API. You must subclass this and
 * override the {@link #getUrl()} in order for this to work properly. This has
 * the following requirements:
 * <ol>
 * <li>You must pass in an event using the "event" bundle extra key. See
 * {@link DhAnalytics.Extras#EVENT}.</li>
 * <li>You must pass in an auth token using the "auth_token" bundle extra key.
 * See {@link DhAnalytics.Extras#AUTH_TOKEN}.
 * </ol>
 * <h2>Usage</h2>
 * <p>
 * Subclass this class and implement the {@link #getUrl()} method. Then, launch
 * the Service passing in the required extras. This class will handle the rest.
 * Make sure you add the service to the manifest.
 * </p>
 * <h2>Callbacks</h2>
 * <p>
 * If you want, you can override the following methods.
 * <ul>
 * <li>{@link #onSuccess()} - Called when the analytic call was successfully
 * made.</li>
 * <li>{@link #onFailure()} - Called if "success":false is returned from the
 * call</li>
 * <li>{@link #onError(Exception)} - Called if there is an error. The exception
 * passed in is sometimes null.</li>
 * </ul>
 * </p>
 * <h2>Network</h2>
 * <p>
 * This uses Ion to make the network call. You don't have to do anything special
 * to add Ion.
 * </p>
 * <h2>Adding To Your Project</h2>
 * <p>
 * Simply add the jar.
 * </p>
 */
public abstract class DhAnalyticsIntentService extends IntentService {
	public static final String TAG = "DhAnalyticsIntentService";

	private String mUrl;

	public DhAnalyticsIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras == null) {
			return;
		}
		final String event = extras.getString(DhAnalytics.Extras.EVENT);
		String authToken = extras.getString(DhAnalytics.Extras.AUTH_TOKEN);
		if (TextUtils.isEmpty(authToken)) {
			authToken = "";
		}
		Log.d(TAG, "Logging event. Event: " + event);

		String eventData = extras.getString(DhAnalytics.Extras.EVENT_DATA);
		boolean loggingEnabled = extras.getBoolean(DhAnalytics.Extras.LOGGING_ENABLED);

		mUrl = AnalyticsApi.constructUrl(getUrl());
		JsonObject o = buildJsonFromBundle(extras);
		// if (!TextUtils.isEmpty(eventData)) {
		// o.addProperty(DhAnalytics.Extras.EVENT_DATA, eventData);
		// }
		Log.d(TAG, "JSON: " + o.toString());
		if (loggingEnabled) {
			logUrl();
			Log.d(TAG, "Json parameters: " + (o == null ? "null" : o.toString()));
		}
		B builder = Ion.with(getApplicationContext(), mUrl).addHeader("Authorization", authToken);
		if (loggingEnabled) {
			builder.setLogging(TAG, Log.VERBOSE);
		}
		try {
			try {
				builder.setJsonObjectBody(o).asString().get();
			} catch (JsonSyntaxException e) {
				Log.e(TAG, "There was an error casting the response.");
				Log.e(TAG, "", e);
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		} catch (ClassCastException e1) {
			e1.printStackTrace();
		}
		// This code was causing a crash with a class cast exception.
		// builder.setJsonObjectBody(o).asJsonObject().setCallback(new
		// FutureCallback<JsonObject>() {
		// @Override
		// public void onCompleted(Exception e, JsonObject result) {
		// if (e != null) {
		// Log.e(TAG, "There was an exception logging the event: " + event, e);
		// logUrl();
		// onError(e);
		// return;
		// }
		// if (result == null) {
		// Log.e(TAG, "There was an error logging the event: " + event +
		// ". The result was null.");
		// logUrl();
		// onError(null);
		// return;
		// }
		// Gson gson = new Gson();
		// try {
		// DhAnalytics.Result response = gson.fromJson(result,
		// DhAnalytics.Result.class);
		// if (response == null) {
		// Log.d(TAG, "There was an error logging the event: " + event +
		// ". The serialized object was null. JSON: " + result.toString());
		// logUrl();
		// onError(null);
		// return;
		// }
		// if (response.success) {
		// Log.d(TAG, "Logging the event succeeded.");
		// onSuccess();
		// } else {
		// Log.d(TAG, "Logging the event failed. Message: " + response.message);
		// onFailure();
		// }
		// } catch (JsonSyntaxException e2) {
		// Log.d(TAG, "There was an error logging the event: " + event +
		// ". The serialization failed. JSON: " + result.toString(), e2);
		// logUrl();
		// onError(e2);
		// return;
		// }
		// }
		// });
	}

	private void logUrl() {
		Log.d(TAG, "Requested URL: " + mUrl);
	}

	protected JsonObject buildJsonFromBundle(Bundle bundle) {
		JsonObject o = new JsonObject();
		Set<String> keySet = bundle.keySet();
		Iterator<String> iterator = keySet.iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			if (TextUtils.equals(key, DhAnalytics.Extras.LOGGING_ENABLED) || TextUtils.equals(key, DhAnalytics.Extras.AUTH_TOKEN)) {
				continue;
			}
			try {
				String valueStr = bundle.getString(key);
				if (valueStr != null) {
					o.addProperty(key, valueStr);
					continue;
				}
				int valueInt = bundle.getInt(key);
				if (valueInt > 0) {
					o.addProperty(key, valueInt);
					continue;
				}
				boolean valueBool = bundle.getBoolean(key);
				if (valueBool) {
					o.addProperty(key, valueBool);
					continue;
				}
			} catch (Exception e) {
				try {
					int valueInt = bundle.getInt(key);
					o.addProperty(key, valueInt);
				} catch (Exception e2) {
					try {
						boolean valueBool = bundle.getBoolean(key);
						o.addProperty(key, valueBool);
					} catch (Exception e3) {
						try {
							float valueFloat = bundle.getFloat(key);
							o.addProperty(key, valueFloat);
						} catch (Exception e4) {
							Log.e(TAG, "Error converting bundle to JSON. Key that failed:" + key, e4);
						}
					}
				}
			}
		}
		return o;
	}

	public abstract String getUrl();

	/**
	 * Called if there is an error. The exception passed in is sometimes null.
	 * 
	 * @param e
	 *            The exception that may have occurred. This is sometimes null.
	 */
	protected void onError(Exception e) {
	}

	/**
	 * Called when the analytic call was successfully made.
	 */
	protected void onSuccess() {
	}

	/**
	 * Called if "success":false is returned from the call
	 */
	protected void onFailure() {
	}
}
