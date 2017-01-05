package com.android.prasad.weatherlive.commons.model;

/**
 * Created by prasad.mukne on 1/2/2017.
 */
public interface ApplicationService
{
    public void preExecute();

    public void postExecute(String response);
}
