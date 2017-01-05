package com.android.prasad.weatherlive.commons.model;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by prasad.mukne on 1/2/2017.
 */
public class ConnectionService extends AsyncTask<String, Object, String>
{
    protected ApplicationService applicationService;

    private Context              activity;

    public ConnectionService(Context activity, ApplicationService applicationService)
    {
        this.activity = activity;
        this.applicationService = applicationService;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        applicationService.preExecute();
    }

    @Override
    protected String doInBackground(String... params)
    {
        StringBuilder responseStringBuilder=new StringBuilder("");
        try
        {
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(25000);
            connection.setReadTimeout(35000);
            //connection.setRequestProperty("", "");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                InputStream input = connection.getInputStream();
                byte []buffer=new byte[1];
                while(input.read(buffer)!=-1)
                {
                    responseStringBuilder.append(new String(buffer));
                }
                input.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return responseStringBuilder.toString();
    }

    @Override
    protected void onProgressUpdate(Object... values)
    {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String responseString)
    {
        super.onPostExecute(responseString);
        applicationService.postExecute(responseString);
    }
}
