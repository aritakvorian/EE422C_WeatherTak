package com.takvorianAri.weatherapp_abt734;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private LocationManager lm;

    public DataPoint current;
    public Hourly hourly;
    public Daily daily;
    public Hourly timeMachineHours;
    public double latitude;
    public double longitude;
    public String timeTimeMachine;

    public String APIKey = "e2a9625abadef7a882cdf235181a3091";
    //public double latitudeDouble = 30.2671;
    //public double longitudeDouble = -97.7430;

    public Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        button = (Button) findViewById(R.id.button2);

        List<Double> locs = new ArrayList<Double>();
        locs = find_Location(this);
        latitude = locs.get(0);
        longitude = locs.get(1);

        String latitudeString = String.valueOf(latitude);
        latitudeString = latitudeString.substring(0, latitudeString.indexOf('.')+5);
        String longitudeString = String.valueOf(longitude);
        longitudeString = longitudeString.substring(0, longitudeString.indexOf('.')+5);

        String forecastURL = "https://api.darksky.net/forecast/" + APIKey + "/" + latitudeString + "," + longitudeString;
        //String test = "https://api.darksky.net/forecast/" + APIKey + "/" + latitudeDouble + "," + longitudeDouble;

        if (isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forecastURL).build();
            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String JSONData = response.body().string();

                        if (response.isSuccessful()) {
                            current = getCurrentData(JSONData);
                            hourly = getHourlyData(JSONData);
                            daily = getDailyData(JSONData);

                            displayData(current, hourly, daily);

                        } else {
                            sendError();
                        }
                    }
                    catch (IOException e) {
                    }

                    catch (JSONException e){
                    }
                }
            });
        }

    }

    public void buttonClick(View view){

        TextView inputText = (TextView) findViewById(R.id.inputText);
        String input = inputText.getText().toString();

        timeTimeMachine = input.substring(11);

        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
            date = sdf.parse(input);
            if (!input.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        if (date == null) {
            Toast toast = Toast.makeText(this.getBaseContext(), "Invalid date format", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP|Gravity.LEFT, 250, 30);
            toast.show();
        } else {
            long epoch = date.getTime()/1000;

            String dataURl = "https://api.darksky.net/forecast/" + APIKey + "/" + latitude + "," + longitude + "," + epoch;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(dataURl).build();
            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String JSONData = response.body().string();

                        if (response.isSuccessful()) {
                            timeMachineHours = getHourlyData(JSONData);
                            displayTimeMachineData(timeMachineHours);
                        } else {
                            sendError();
                        }
                    }
                    catch (IOException e) {
                    }

                    catch (JSONException e){
                    }
                }
            });

        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;

        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }

        else {
            Toast.makeText(this, R.string.network_unavailable_message, Toast.LENGTH_LONG).show();
        }

        return isAvailable;
    }

    private void sendError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getSupportFragmentManager(), "error_dialog");
    }

    private DataPoint getCurrentData(String JSONData) throws JSONException {
        JSONObject forecast = new JSONObject(JSONData);
        String timezone = forecast.getString("timezone");

        JSONObject current = forecast.getJSONObject("currently");
        DataPoint data = new DataPoint();

        double test = forecast.getJSONObject("currently").getDouble("humidity");

        data.setHumidity(current.getDouble("humidity"));
        data.setIcon(current.getString("icon"));
        data.setLocationLabel("Austin, TX");
        data.setPrecipChance(current.getDouble("precipProbability"));
        data.setSummary(current.getString("summary"));
        data.setTemperature(current.getDouble("temperature"));
        data.setTimeZone(timezone);
        data.setWindSpeed(current.getDouble("windSpeed"));
        data.setTime(current.getLong("time"), timezone);

        return data;
    }

    private Hourly getHourlyData(String JSONData) throws JSONException {
        JSONObject forecast = new JSONObject(JSONData);
        String timezone = forecast.getString("timezone");
        JSONObject hours = forecast.getJSONObject("hourly");
        Hourly hourlyJSON = new Hourly();

        hourlyJSON.setSummary(hours.getString("summary"));
        hourlyJSON.setIcon(hours.getString("icon"));
        hourlyJSON.setTimezone(timezone);

        JSONArray hourArray = hours.getJSONArray("data");
        List<DataPoint> dataPointList = new ArrayList<DataPoint>();

        for (int i = 0; i<hourArray.length(); i++){
            JSONObject hour = hourArray.getJSONObject(i);

            DataPoint data = new DataPoint();

            data.setHumidity(hour.getDouble("humidity"));
            data.setIcon(hour.getString("icon"));
            data.setLocationLabel("Austin, TX");
            data.setPrecipChance(hour.getDouble("precipProbability"));
            data.setSummary(hour.getString("summary"));
            data.setTemperature(hour.getDouble("temperature"));
            data.setTimeZone(timezone);
            data.setWindSpeed(hour.getDouble("windSpeed"));
            data.setTime(hour.getLong("time"), timezone);

            dataPointList.add(data);

        }

        hourlyJSON.setHourList(dataPointList);
        return hourlyJSON;
    }

    private Daily getDailyData(String JSONData) throws JSONException {
        JSONObject forecast = new JSONObject(JSONData);
        String timezone = forecast.getString("timezone");
        JSONObject days = forecast.getJSONObject("daily");
        Daily dailyJSON = new Daily();

        dailyJSON.setSummary(days.getString("summary"));
        dailyJSON.setIcon(days.getString("icon"));
        dailyJSON.setTimezone(timezone);

        JSONArray daysArray = days.getJSONArray("data");
        List<DataPoint> dataPointList = new ArrayList<DataPoint>();

        for (int i = 0; i<daysArray.length(); i++){
            JSONObject day = daysArray.getJSONObject(i);

            DataPoint data = new DataPoint();

            data.setHumidity(day.getDouble("humidity"));
            data.setIcon(day.getString("icon"));
            data.setLocationLabel("Austin, TX");
            data.setPrecipChance(day.getDouble("precipProbability"));
            data.setSummary(day.getString("summary"));
            data.setTempMax(day.getDouble("temperatureMax"));
            data.setTempMin(day.getDouble("temperatureMin"));
            data.setTimeZone(timezone);
            data.setWindSpeed(day.getDouble("windSpeed"));
            data.setTime(day.getLong("time"), timezone);

            dataPointList.add(data);

        }

        dailyJSON.setDailyList(dataPointList);
        return dailyJSON;
    }

    private double getAverageTemp(List<DataPoint> hourlyData){
        double average = 0.0;

        for (int i = 0; i<hourlyData.size(); i++){
            average += hourlyData.get(i).getTemperature();
        }

        return average/hourlyData.size();
    }

    private void displayData(DataPoint current, Hourly hourly, Daily daily){

        final DataPoint currentFinal = current;
        final Hourly hourlyFinal = hourly;
        final Daily dailyFinal = daily;

        new Thread(){
            public void run() {
                int i = 0;
                while (i++ < 1000){
                    try {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                //current temp
                                TextView currentTemp = (TextView) findViewById(R.id.currentTemperature);
                                int currentTempInt = (int) Math.round(currentFinal.getTemperature());
                                currentTemp.setText(String.valueOf(currentTempInt));

                                //current image
                                ImageView currentImage = (ImageView) findViewById(R.id.currentIcon);
                                currentImage.setImageResource(currentFinal.getIconId());

                                //current time
                                TextView currentTime = (TextView) findViewById(R.id.timeValue);
                                currentTime.setText(currentFinal.getTime());

                                //current precip chance
                                TextView currentPrecipChance = (TextView) findViewById(R.id.precipChanceValue);
                                int precipChance = (int) Math.round(currentFinal.getPrecipChance());
                                String pC = precipChance + "%";
                                currentPrecipChance.setText(pC);

                                //current humidity
                                TextView currentHumidity = (TextView) findViewById(R.id.humidityValue);
                                currentHumidity.setText(String.valueOf(currentFinal.getHumidity()));

                                //current wind speed
                                TextView windSpeed = (TextView) findViewById(R.id.windSpeedValue);
                                int wS = (int) Math.round(currentFinal.getWindSpeed());
                                String wSpeed = wS + " m/s";
                                windSpeed.setText(wSpeed);

                                //48 average
                                TextView fourtyEight = (TextView) findViewById(R.id.fourtyEightValue);
                                int averageTemp = (int) Math.round(getAverageTemp(hourlyFinal.getHourList()));
                                fourtyEight.setText(String.valueOf(averageTemp));

                                //hour one values
                                TextView hourOneTime = (TextView) findViewById(R.id.hourOneText);
                                hourOneTime.setText(hourlyFinal.getHourList().get(1).getTime());

                                TextView hourOneTemp = (TextView) findViewById(R.id.hourOneTemp);
                                int hOT = (int) Math.round(hourlyFinal.getHourList().get(1).getTemperature());
                                String hOTString = String.valueOf(hOT);
                                hourOneTemp.setText(hOTString);

                                ImageView hourOneImage = (ImageView) findViewById(R.id.hourOneImage);
                                hourOneImage.setImageResource(hourlyFinal.getHourList().get(1).getIconId());

                                //hour two values
                                TextView hourTwoTime = (TextView) findViewById(R.id.hourTwoText);
                                hourTwoTime.setText(hourlyFinal.getHourList().get(2).getTime());

                                TextView hourTwoTemp = (TextView) findViewById(R.id.hourTwoTemp);
                                int hTT = (int) Math.round(hourlyFinal.getHourList().get(2).getTemperature());
                                String hTTString = String.valueOf(hTT);
                                hourTwoTemp.setText(hTTString);

                                ImageView hourTwoImage = (ImageView) findViewById(R.id.hourTwoImage);
                                hourTwoImage.setImageResource(hourlyFinal.getHourList().get(2).getIconId());

                                //hour three values
                                TextView hourThreeTime = (TextView) findViewById(R.id.hourThreeText);
                                hourThreeTime.setText(hourlyFinal.getHourList().get(3).getTime());

                                TextView hourThreeTemp = (TextView) findViewById(R.id.hourThreeTemp);
                                int hTTee = (int) Math.round(hourlyFinal.getHourList().get(3).getTemperature());
                                String hTTeeString = String.valueOf(hTTee);
                                hourThreeTemp.setText(hTTeeString);

                                ImageView hourThreeImage = (ImageView) findViewById(R.id.hourThreeImage);
                                hourThreeImage.setImageResource(hourlyFinal.getHourList().get(3).getIconId());

                                //hour four values
                                TextView hourFourTime = (TextView) findViewById(R.id.hourFourText);
                                hourFourTime.setText(hourlyFinal.getHourList().get(4).getTime());

                                TextView hourFourTemp = (TextView) findViewById(R.id.hourFourTemp);
                                int hFT = (int) Math.round(hourlyFinal.getHourList().get(4).getTemperature());
                                String hFTString = String.valueOf(hFT);
                                hourFourTemp.setText(hFTString);

                                ImageView hourFourImage = (ImageView) findViewById(R.id.hourFourImage);
                                hourFourImage.setImageResource(hourlyFinal.getHourList().get(4).getIconId());

                                //hour five values
                                TextView hourFiveTime = (TextView) findViewById(R.id.hourFiveText);
                                hourFiveTime.setText(hourlyFinal.getHourList().get(5).getTime());

                                TextView hourFiveTemp = (TextView) findViewById(R.id.hourFiveTemp);
                                int hFiT = (int) Math.round(hourlyFinal.getHourList().get(5).getTemperature());
                                String hFiTString = String.valueOf(hFiT);
                                hourFiveTemp.setText(hFiTString);

                                ImageView hourFiveImage = (ImageView) findViewById(R.id.hourFiveImage);
                                hourFiveImage.setImageResource(hourlyFinal.getHourList().get(5).getIconId());

                                //day zero values
                                TextView dayZero = (TextView) findViewById(R.id.dayZero);
                                String dayZeroString = dailyFinal.getDailyList().get(0).getDate() + ": " + Math.round(dailyFinal.getDailyList().get(0).getTempMax())
                                        + "/" + Math.round(dailyFinal.getDailyList().get(0).getTempMin());
                                dayZero.setText(dayZeroString);

                                ImageView dayZeroImage = (ImageView) findViewById(R.id.dayZeroImage);
                                dayZeroImage.setImageResource(dailyFinal.getDailyList().get(0).getIconId());

                                //day one values
                                TextView dayOne = (TextView) findViewById(R.id.dayOne);
                                String dayOneString = dailyFinal.getDailyList().get(1).getDate() + ": " + Math.round(dailyFinal.getDailyList().get(1).getTempMax())
                                        + "/" + Math.round(dailyFinal.getDailyList().get(1).getTempMin());
                                dayOne.setText(dayOneString);

                                ImageView dayOneImage = (ImageView) findViewById(R.id.dayOneImage);
                                dayOneImage.setImageResource(dailyFinal.getDailyList().get(1).getIconId());

                                //day two values
                                TextView dayTwo = (TextView) findViewById(R.id.dayTwo);
                                String dayTwoString = dailyFinal.getDailyList().get(2).getDate() + ": " + Math.round(dailyFinal.getDailyList().get(2).getTempMax())
                                        + "/" + Math.round(dailyFinal.getDailyList().get(2).getTempMin());
                                dayTwo.setText(dayTwoString);

                                ImageView dayTwoImage = (ImageView) findViewById(R.id.dayTwoImage);
                                dayTwoImage.setImageResource(dailyFinal.getDailyList().get(2).getIconId());

                                //day three values
                                TextView dayThree = (TextView) findViewById(R.id.dayThree);
                                String dayThreeString = dailyFinal.getDailyList().get(3).getDate() + ": " + Math.round(dailyFinal.getDailyList().get(3).getTempMax())
                                        + "/" + Math.round(dailyFinal.getDailyList().get(3).getTempMin());
                                dayThree.setText(dayThreeString);

                                ImageView dayThreeImage = (ImageView) findViewById(R.id.dayThreeImage);
                                dayThreeImage.setImageResource(dailyFinal.getDailyList().get(3).getIconId());

                                //day four values
                                TextView dayFour = (TextView) findViewById(R.id.dayFour);
                                String dayFourString = dailyFinal.getDailyList().get(4).getDate() + ": " + Math.round(dailyFinal.getDailyList().get(4).getTempMax())
                                        + "/" + Math.round(dailyFinal.getDailyList().get(4).getTempMin());
                                dayFour.setText(dayFourString);

                                ImageView dayFourImage = (ImageView) findViewById(R.id.dayFourImage);
                                dayFourImage.setImageResource(dailyFinal.getDailyList().get(4).getIconId());

                                //day five values
                                TextView dayFive = (TextView) findViewById(R.id.dayFive);
                                String dayFiveString = dailyFinal.getDailyList().get(5).getDate() + ": " + Math.round(dailyFinal.getDailyList().get(5).getTempMax())
                                        + "/" + Math.round(dailyFinal.getDailyList().get(5).getTempMin());
                                dayFive.setText(dayFiveString);

                                ImageView dayFiveImage = (ImageView) findViewById(R.id.dayFiveImage);
                                dayFiveImage.setImageResource(dailyFinal.getDailyList().get(5).getIconId());

                                //day six values
                                TextView daySix = (TextView) findViewById(R.id.daySix);
                                String daySixString = dailyFinal.getDailyList().get(6).getDate() + ": " + Math.round(dailyFinal.getDailyList().get(6).getTempMax())
                                        + "/" + Math.round(dailyFinal.getDailyList().get(6).getTempMin());
                                daySix.setText(daySixString);

                                ImageView daySixImage = (ImageView) findViewById(R.id.daySixImage);
                                daySixImage.setImageResource(dailyFinal.getDailyList().get(6).getIconId());

                                //day seven values
                                TextView daySeven = (TextView) findViewById(R.id.daySeven);
                                String daySevenString = dailyFinal.getDailyList().get(7).getDate() + ": " + Math.round(dailyFinal.getDailyList().get(7).getTempMax())
                                        + "/" + Math.round(dailyFinal.getDailyList().get(7).getTempMin());
                                daySeven.setText(daySevenString);

                                ImageView daySevenImage = (ImageView) findViewById(R.id.daySevenImage);
                                daySevenImage.setImageResource(dailyFinal.getDailyList().get(7).getIconId());
                            }
                        });
                        Thread.sleep(300);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }.start();





    }

    private void displayTimeMachineData(Hourly hourly){

        final Hourly hourlyFinal = timeMachineHours;

        new Thread(){
            public void run() {
                int i = 0;
                while (i++ < 1000){
                    try {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                String display = "At " + timeTimeMachine + " on " +
                                        timeMachineHours.getHourList().get(0).getDate() + ", the temperature was: ";
                                TextView timeMachineText = (TextView) findViewById(R.id.timeMachineText);
                                timeMachineText.setVisibility(View.VISIBLE);
                                timeMachineText.setText(display);

                                int temp = (int) Math.round(timeMachineHours.getHourList().get(0).getTemperature());
                                TextView timeMachineTemp = (TextView) findViewById(R.id.timeMachineTemp);
                                timeMachineTemp.setVisibility(View.VISIBLE);
                                timeMachineTemp.setText(String.valueOf(temp));

                                ImageView degreeSymbol = (ImageView) findViewById(R.id.Degree);
                                degreeSymbol.setVisibility(View.VISIBLE);

                                ImageView timeMachineImage = (ImageView) findViewById(R.id.timeMachineImage);
                                timeMachineImage.setVisibility(View.VISIBLE);
                                timeMachineImage.setImageResource(timeMachineHours.getHourList().get(0).getIconId());

                            }
                        });
                        Thread.sleep(300);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }.start();





    }


    public List<Double> find_Location(Context con) {

        String location_context = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) con.getSystemService(location_context);
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {

            try {
                locationManager.requestLocationUpdates(provider, 1000, 0,
                        new LocationListener() {

                            public void onLocationChanged(Location location) {}

                            public void onProviderDisabled(String provider) {}

                            public void onProviderEnabled(String provider) {}

                            public void onStatusChanged(String provider, int status,
                                                        Bundle extras) {}
                        });
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    List<Double> locs = new ArrayList<Double>();
                    locs.add(location.getLatitude());
                    locs.add(location.getLongitude());
                    return locs;
                }
                return null;
            }
            catch (SecurityException e){
            }
        }
        return null;
    }

}
