package com.dhairytripathi.emergencyapp;

import android.Manifest;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_SYNC_DATA = "SyncLocation";
    private static final String SYNC_DATA_WORK_NAME = "Location";
    private SwitchCompat switchStatus;
    private Spinner spinner;
    private int duration = 15;
    private Activity activity;
    private Context context;
    private boolean status = false;
    private static final String TAG = "EmergencyApp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initListener();
    }

    private void init() {
        switchStatus = findViewById(R.id.switch1);
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.duration, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION },
                    1);
        }
        activity = this;
        context = this;
    }

    private void initListener() {
        switchStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    scheduleJob();
                    status = true;
                } else {
                    cancelJob();
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 1: duration = 15;
                        if(status) {
                            scheduleJob();
                        }
                        break;
                    case 2: duration = 30;
                        if(status) {
                            scheduleJob();
                        }
                        break;
                    case 3: duration = 45;
                        if(status) {
                            scheduleJob();
                        }
                        break;
                    case 4: duration = 60;
                        if(status) {
                            scheduleJob();
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void cancelJob() {
        JobScheduler scheduler = (JobScheduler)  getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Log.d(TAG, "Service cancelled");
    }

    private void scheduleJob() {
        ComponentName componentName = new ComponentName(context, LocationScheduler.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setPersisted(true)
                .setPeriodic(duration * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler)  getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if(resultCode == JobScheduler.RESULT_SUCCESS ) {
            Log.d(TAG, "Location updated successfully");
        } else {
            Log.d(TAG, "Location updated failed");
        }
    }
}

