package com.android.prasad.weatherlive.splash;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.prasad.weatherlive.R;
import com.android.prasad.weatherlive.base.LandingScreenActivity;
import com.android.prasad.weatherlive.utility.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by prasad.mukne on 1/2/2017.
 */
public class SplashScreenActivity extends AppCompatActivity
{


    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

    }

    @Override
    protected void onResume()
    {
        try
        {
            super.onResume();
            if (!Utility.isNetworkAvailable(this))
            {
                new AlertDialog.Builder(this).setIcon(R.drawable.ic_alert).setTitle("Alert")
                        .setMessage(R.string.no_internet_message)
                        .setPositiveButton(R.string.turn_on, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                startActivity(intent);
                            }

                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                }).setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                        return false;
                    }
                }).setCancelable(false).show();

            }
            else
            {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        showPermissions();
                    }
                }, 2000);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void showPermissions()
    {
        try
        {
            int currentApiVersion = Build.VERSION.SDK_INT;
            if (currentApiVersion > Build.VERSION_CODES.LOLLIPOP_MR1)
            {
                List<String> permissionsNeeded = new ArrayList<String>();

                final List<String> permissionsList = new ArrayList<String>();
                if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
                    permissionsNeeded.add("GPS Fine Location");
                if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
                    permissionsNeeded.add("GPS Coarse Location");

                if (permissionsList.size() > 0)
                {
                    if (permissionsNeeded.size() > 0)
                    {
                        String message = "You need to grant access to " + permissionsNeeded.get(0);
                        for (int i = 1; i < permissionsNeeded.size(); i++)
                            message = message + ", " + permissionsNeeded.get(i);
                        showMessageOKCancel(message, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                        return;
                    }

                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                    return;
                }
                else
                {
                    Intent intent = new Intent(SplashScreenActivity.this, LandingScreenActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            else
            {
                Intent intent = new Intent(SplashScreenActivity.this, LandingScreenActivity.class);
                startActivity(intent);
                finish();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean addPermission(List<String> permissionsList, String permission)
    {
        try
        {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
            {
                permissionsList.add(permission);

                if (!shouldShowRequestPermissionRationale(permission))
                    return false;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        try
        {
            switch (requestCode)
            {
                case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                {
                    Map<String, Integer> perms = new HashMap<String, Integer>();

                    perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                    perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);

                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);

                    if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        Intent intent = new Intent(SplashScreenActivity.this, LandingScreenActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(SplashScreenActivity.this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                        showMessageOKCancel(getResources().getString(R.string.accept_permissions),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showPermissions();
                                    }
                                });

                    }
                }
                break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener)
    {
        try
        {
            new AlertDialog.Builder(SplashScreenActivity.this).setMessage(message).setPositiveButton("OK", okListener)
                    .setNegativeButton("Cancel", null).create().show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
