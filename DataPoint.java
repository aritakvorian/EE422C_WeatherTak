package com.takvorianAri.weatherapp_abt734;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DataPoint {
    private String time;
    private String date;
    private String summary;
    private String icon;
    private double precipChance;
    private double temperature;
    private double humidity;
    private double windSpeed;
    private String locationLabel;
    private String timeZone;

    private double tempMin;
    private double tempMax;


    public String getTime() {
        return time;
    }

    public String getDate(){
        return date;
    }

    public void setTime(long time, String timeZone) {
        Date dateTime = new Date(time*1000);
        SimpleDateFormat formatOne = new SimpleDateFormat("h:mm a");
        SimpleDateFormat formatTwo = new SimpleDateFormat("MMM-dd");
        formatOne.setTimeZone(TimeZone.getTimeZone(timeZone));
        formatTwo.setTimeZone(TimeZone.getTimeZone(timeZone));
        String timeOne = formatOne.format(dateTime);
        String timeTwo = formatTwo.format(dateTime);
        this.date = timeTwo;
        this.time = timeOne;
    }

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

    public double getPrecipChance() {
        return precipChance;
    }

    public void setPrecipChance(double precipChance) {
        this.precipChance = precipChance;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getLocationLabel() {
        return locationLabel;
    }

    public void setLocationLabel(String locationLabel) {
        this.locationLabel = locationLabel;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public double getTempMin() {
        return tempMin;
    }

    public void setTempMin(double tempMin) {
        this.tempMin = tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public void setTempMax(double tempMax) {
        this.tempMax = tempMax;
    }

    public int getIconId (){
        int iconId = R.drawable.clear_day;

        switch(icon) {
            case "clear-day":
                iconId = R.drawable.clear_day;
                break;
            case "clear-night":
                iconId = R.drawable.clear_night;
                break;
            case "rain":
                iconId = R.drawable.rain;
                break;
            case "snow":
                iconId = R.drawable.snow;
                break;
            case "sleet":
                iconId = R.drawable.sleet;
                break;
            case "wind":
                iconId = R.drawable.wind;
                break;
            case "fog":
                iconId = R.drawable.fog;
                break;
            case "cloudy":
                iconId = R.drawable.cloudy;
                break;
            case "partly-cloudy-day":
                iconId = R.drawable.partly_cloudy;
                break;
            case "partly-cloudy-night":
                iconId = R.drawable.cloudy_night;
                break;
        }
        return iconId;
    }
}
