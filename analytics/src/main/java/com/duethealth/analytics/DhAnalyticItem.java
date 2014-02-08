package com.duethealth.analytics;

import nl.qbusict.cupboard.annotation.Column;

class DhAnalyticItem {

    private Long _id;
    private String event;
    @Column("event_data")
    private String eventData;
    @Column("logging_enabled")
    private boolean loggingEnabled;
    @Column("event_url")
    private String eventUrl;
    @Column("b")
    private String token;

    DhAnalyticItem() {
    }

    private DhAnalyticItem(Builder builder) {
        setEvent(builder.event);
        setEventData(builder.eventData);
        setLoggingEnabled(builder.loggingEnabled);
        setEventUrl(builder.eventUrl);
        setToken(builder.token);
    }

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        _id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEventData() {
        return eventData;
    }

    public void setEventData(String eventData) {
        this.eventData = eventData;
    }

    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public void setLoggingEnabled(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static final class Builder {
        private String event;
        private String eventData;
        private boolean loggingEnabled;
        private String eventUrl;
        private String token;

        public Builder() {
        }

        public Builder(DhAnalyticItem copy) {
            event = copy.event;
            eventData = copy.eventData;
            loggingEnabled = copy.loggingEnabled;
            eventUrl = copy.eventUrl;
            token = copy.token;
        }

        public Builder event(String event) {
            this.event = event;
            return this;
        }

        public Builder eventData(String eventData) {
            this.eventData = eventData;
            return this;
        }

        public Builder loggingEnabled(boolean loggingEnabled) {
            this.loggingEnabled = loggingEnabled;
            return this;
        }

        public Builder eventUrl(String eventUrl) {
            this.eventUrl = eventUrl;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public DhAnalyticItem build() {
            return new DhAnalyticItem(this);
        }
    }
}
