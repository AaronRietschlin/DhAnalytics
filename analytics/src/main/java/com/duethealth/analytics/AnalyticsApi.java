package com.duethealth.analytics;

public class AnalyticsApi {

	public static final String BASE_URL = "http://nchresidents.duethealth.com/";
	public static final String PATH_ANALYTICS = "analytics/";

	public String getBaseUrl() {
		return BASE_URL + "api";
	}

	public static String constructUrl(String baseurl) {
		return baseurl + PATH_ANALYTICS;
	}

}
