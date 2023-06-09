package com.example.gatherwifi_down;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * The wifi manager.
     */
    private WifiManager wifiManager;
    /**
     * The wifi info.
     */
    private WifiInfo wifiInfo;
    private static final int PERMISSIONS_REQUEST_CODE = 123;
    private TextView wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button floor1 = (Button) findViewById(R.id.floor1);
        Button floor2 = (Button) findViewById(R.id.floor2);
        Button floor3 = (Button) findViewById(R.id.floor3);
        Button west = (Button) findViewById(R.id.west);
        Button east = (Button) findViewById(R.id.east);
        wifi = (TextView) findViewById(R.id.wifi);

        // Set the wifi manager
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // Request necessary permissions
        requestPermissions();

        floor1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WIFI("floor1");
            }
        });

        floor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WIFI("floor2");
            }
        });

        floor3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WIFI("floor3");
            }
        });

        east.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WIFI("east");
            }
        });

        west.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WIFI("west");
            }
        });
    }
    private void requestPermissions() {
        // Check if Wi-Fi and location permissions are granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request permissions
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, PERMISSIONS_REQUEST_CODE);
        }
    }

    private void WIFI(String location) {
        // Check if the Wi-Fi feature is available
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "Please enable Wi-Fi", Toast.LENGTH_SHORT).show();
            return;
        }
        // Set text.
        wifi.setText("\n\tScan all access points:");
        // Set wifi manager.
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // Check if the necessary permissions are granted
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(android.Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.CHANGE_WIFI_STATE,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
            }, PERMISSIONS_REQUEST_CODE);
            return;
        }

        // Start a wifi scan
        boolean scanStarted = wifiManager.startScan();
        if (!scanStarted) {
            Toast.makeText(this, "Scan not started. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the scan results
        List<ScanResult> scanResults = wifiManager.getScanResults();

        // Display the scan results
        StringBuilder resultBuilder = new StringBuilder();
        for (ScanResult scanResult : scanResults) {
            String ssid = scanResult.SSID;
            int rssi = scanResult.level;

            // Append SSID and RSSI to the result string
            resultBuilder.append("SSID: ").append(ssid).append(", RSSI: ").append(rssi).append("\n");
        }

        // Update the text view with the collected RSS data
        wifi.setText(resultBuilder.toString());


        try {
            File documentsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(documentsDirectory, location + ".csv");
            FileWriter writer = new FileWriter(file, true);
            writer.append(resultBuilder.toString());
            writer.flush();
            writer.close();
            Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
        }
    }

}