package com.takvorianAri.weatherapp_abt734;

import java.util.List;

public class Daily {
    private String summary;
    private String icon;
    private String timezone;
    private List<DataPoint> dailyList;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public List<DataPoint> getDailyList() {
        return dailyList;
    }

    public void setDailyList(List<DataPoint> dailyList) {
        this.dailyList = dailyList;
    }
}
