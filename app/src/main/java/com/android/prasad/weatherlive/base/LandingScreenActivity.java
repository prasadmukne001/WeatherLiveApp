package com.android.prasad.weatherlive.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.prasad.weatherlive.R;
import com.android.prasad.weatherlive.commons.bo.WeatherMapBo;
import com.android.prasad.weatherlive.commons.model.ApplicationService;
import com.android.prasad.weatherlive.commons.model.ConnectionService;
import com.android.prasad.weatherlive.utility.Utility;
import com.android.prasad.weatherlive.utility.WeatherLiveAppConstants;
import com.google.gson.Gson;

/**
 * Created by prasad.mukne on 1/2/2017.
 */

public class LandingScreenActivity extends AppCompatActivity implements LocationListener
{
    private static final long MIN_DISTANCE_CHANGE_TO_UPDATES =0 ; // 5

    private static final long MIN_TIME_ELAPSED_BETWEEN_UPDATES = 0;//1000 * 4 * 1

    protected LocationManager locationManager;

    private String latitude;

    private String longitude;

    private RelativeLayout snackBarRelativeLayout,dayNightRelativeLayout;
    private TextView retryTextView, temperatureTextView, dayNightTextView,
            /*locationNameTextView,*/ weatherDescriptionTextView, pressureTextView, humidityTextView,
            windSpeedTextView;
    private Button refreshButton;
    private ImageView searchButtonImageView;
    private EditText searchEditText;
    private ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_screen);
        initialiseUI();
        setLocationListener();
        if (Utility.isNetworkAvailable(this))
        {
            getUserLocationWeather(WeatherLiveAppConstants.LOCATION);
        }
        else
        {
            snackBarRelativeLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        getLocation();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void setLocationListener()
    {
        try
        {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!isGPSEnabled)
            {
                new AlertDialog.Builder(this).setIcon(R.drawable.ic_alert).setTitle("Alert")
                        .setMessage("Please turn on your GPS and set it to high accuracy.")
                        .setPositiveButton("Turn on", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }

                        }).setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent)
                    {
                        return false;
                    }
                }).setCancelable(false).show();

            }
            else
            {
                getLocation();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void getLocation()
    {
        try {
            //isWifiEnabled = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Location location = null;
            if (isGPSEnabled)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_DISTANCE_CHANGE_TO_UPDATES,
                        MIN_TIME_ELAPSED_BETWEEN_UPDATES, this);
                if (locationManager != null)
                {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null)
                    {
                        latitude = ""+location.getLatitude();
                        longitude = ""+location.getLongitude();
                    }
                    else
                    {
                        if (isNetworkEnabled)
                        {
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                    MIN_DISTANCE_CHANGE_TO_UPDATES, MIN_TIME_ELAPSED_BETWEEN_UPDATES, this);
                            if (locationManager != null)
                            {
                                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                if (location != null)
                                {
                                    latitude = ""+location.getLatitude();
                                    longitude = ""+location.getLongitude();
                                }
                            }
                            else
                            {
                                Toast.makeText(this, "Location Not Found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
            else if (isNetworkEnabled)
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_DISTANCE_CHANGE_TO_UPDATES,
                        MIN_TIME_ELAPSED_BETWEEN_UPDATES, this);
                if (locationManager != null)
                {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null)
                    {
                        latitude = ""+location.getLatitude();
                        longitude = ""+location.getLongitude();
                    }
                    else
                    {
                        Toast.makeText(this, "Location Not Found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else
            {
                Toast.makeText(this, "Enable GPS", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialiseUI()
    {
        try
        {
            snackBarRelativeLayout = (RelativeLayout) findViewById(R.id.snackBarRelativeLayout);
            snackBarRelativeLayout.setVisibility(View.GONE);
            dayNightRelativeLayout= (RelativeLayout) findViewById(R.id.dayNightRelativeLayout);
            temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
            dayNightTextView = (TextView) findViewById(R.id.dayNightTextView);
            //locationNameTextView = (TextView) findViewById(R.id.locationNameTextView);
            weatherDescriptionTextView = (TextView) findViewById(R.id.weatherDescriptionTextView);
            pressureTextView = (TextView) findViewById(R.id.pressureTextView);
            humidityTextView = (TextView) findViewById(R.id.humidityTextView);
            windSpeedTextView = (TextView) findViewById(R.id.windSpeedTextView);
            refreshButton=(Button) findViewById(R.id.refreshButton);
            retryTextView = (TextView) findViewById(R.id.retryTextView);
            loadingProgressBar= (ProgressBar) findViewById(R.id.loadingProgressBar);
            searchEditText=(EditText)findViewById(R.id.searchEditText);
            searchButtonImageView= (ImageView) findViewById(R.id.searchButtonImageView);
            searchButtonImageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(!searchEditText.getText().toString().trim().equals(""))
                    {
                        if (Utility.isNetworkAvailable(LandingScreenActivity.this))
                        {
                            getUserLocationWeather(WeatherLiveAppConstants.MANUAL);
                        }
                        else
                        {
                            snackBarRelativeLayout.setVisibility(View.VISIBLE);
                        }
                    }
                    else
                    {
                        Toast.makeText(LandingScreenActivity.this, "Please enter city name to search.", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            retryTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    if (Utility.isNetworkAvailable(LandingScreenActivity.this))
                    {
                        searchEditText.setText("");
                        getUserLocationWeather(WeatherLiveAppConstants.LOCATION);
                    }
                    else
                    {
                        snackBarRelativeLayout.setVisibility(View.VISIBLE);
                    }

                }
            });

            refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    if (Utility.isNetworkAvailable(LandingScreenActivity.this))
                    {
                        searchEditText.setText("");
                        getUserLocationWeather(WeatherLiveAppConstants.LOCATION);
                    }
                    else
                    {
                        snackBarRelativeLayout.setVisibility(View.VISIBLE);
                    }

                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void getUserLocationWeather(String userMethod)
    {
        try
        {
            ConnectionService connectionService = new ConnectionService(this, new ApplicationService()
            {
                @Override
                public void preExecute()
                {
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                }

                @Override
                public void postExecute(String response)
                {
                    try
                    {
                        Log.d("response", "" + response);
                        if(!response.trim().equals(""))
                        {
                            Gson gson = new Gson();
                            WeatherMapBo weatherMapBo = gson.fromJson(response, WeatherMapBo.class);
                            Log.d("response", "" + weatherMapBo);
                            double roundOff = Math.round((Double.parseDouble(weatherMapBo.getMain().getTemp())-273.15) * 100.0) / 100.0;
                            temperatureTextView.setText(weatherMapBo.getName()+" " + (roundOff)+" c.");
                            String daytimeString="";
                            Double currentTime=Double.parseDouble(weatherMapBo.getDt());
                            Double sunsetTime=Double.parseDouble(weatherMapBo.getSys().getSunset());
                            Double sunriseTime=Double.parseDouble(weatherMapBo.getSys().getSunrise());
                            if(currentTime < sunriseTime )
                            {
                                daytimeString="It is Night time.";
                            }
                            else if(currentTime>sunriseTime && currentTime < sunsetTime)
                            {
                                daytimeString="It is Day time.";
                            }
                            else if(currentTime>sunsetTime)
                            {
                                daytimeString="It is Night time.";
                            }
                            else
                            {
                                daytimeString="It is Day time.";
                            }
                            if(daytimeString.contains("Night"))
                            {
                                dayNightRelativeLayout.setBackgroundResource(R.drawable.night);
                            }
                            else
                            {
                                dayNightRelativeLayout.setBackgroundResource(R.drawable.day);
                            }

                            dayNightTextView.setText(daytimeString);
                            //locationNameTextView.setText("Location is "+weatherMapBo.getName()+".");
                            weatherDescriptionTextView.setText(weatherMapBo.getWeather()[0].getDescription()+".");
                            pressureTextView.setText("Pressure is "+weatherMapBo.getMain().getPressure()+".");
                            humidityTextView.setText("Humidity is "+weatherMapBo.getMain().getHumidity()+".");
                            windSpeedTextView.setText("Wind speed is "+weatherMapBo.getWind().getSpeed()+".");
                        }
                        else
                        {
                            Toast.makeText(LandingScreenActivity.this, R.string.some_error_occurred, Toast.LENGTH_LONG).show();
                        }
                        loadingProgressBar.setVisibility(View.INVISIBLE);
                        snackBarRelativeLayout.setVisibility(View.INVISIBLE);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            String url = "";
            if(userMethod.equals(WeatherLiveAppConstants.LOCATION))
            {
                url=Utility.getWeatherFromUserLocation(latitude, longitude);
            }
            else if(userMethod.equals(WeatherLiveAppConstants.MANUAL))
            {
                url=Utility.getWeatherFromSearchedString(searchEditText.getText().toString().replace(" ",""));
            }
            Log.d("request", " url " + url);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                connectionService.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
            else
                connectionService.execute(url);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed()
    {
        showExitDialog();
    }

    private void showExitDialog()
    {
        new AlertDialog.Builder(this).setIcon(R.drawable.ic_alert).setTitle("Confirm")
                .setMessage("Are you sure you want to exit the application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finish();
                        System.exit(0);
                    }

                }).setNegativeButton("No", null).show();
    }

}
