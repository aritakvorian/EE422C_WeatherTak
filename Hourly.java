package com.takvorianAri.weatherapp_abt734;

import java.util.List;

public class Hourly {
    private String summary;
    private String icon;
    private String timezone;
    private List<DataPoint> hourList;

    public String getSummary(){
        return this.summary;
    }

    public void setSummary(String summary){
        this.summary = summary;
    }

    public String getIcon(){
        return this.icon;
    }

    public void setIcon(String icon){
        this.icon = icon;
    }

    public List<DataPoint> getHourList(){
        return this.hourList;
    }

    public void setHourList(List<DataPoint> hourList){
        this.hourList = hourList;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
