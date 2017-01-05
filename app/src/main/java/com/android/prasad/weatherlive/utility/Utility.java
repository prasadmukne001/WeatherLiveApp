package com.android.prasad.weatherlive.utility;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by prasad.mukne on 1/2/2017.
 */
public class Utility
{
    public static final String URL     = "http://api.openweathermap.org/data/2.5/weather?appid=4db13fc3a5e4ee7d859a12af836584f3&";
    public static final String CityURL = "http://api.openweathermap.org/data/2.5/weather?appid=4db13fc3a5e4ee7d859a12af836584f3&";

    public static String getWeatherFromUserLocation(String latitude,String longitude)
    {
        return URL+"lat="+latitude+"&lon="+longitude;
    }

    public static String getWeatherFromSearchedString(String searchString)
    {
        return URL+"q="+searchString;
    }

    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
            {
                for (int i = 0; i < info.length; i++)
                {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
