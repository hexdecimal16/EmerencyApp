package com.dhairytripathi.emergencyapp;

import android.Manifest;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class ClientFragment extends Fragment {
    private View v;
    private EditText id;
    private SwitchCompat switchStatus;
    private Spinner spinner;
    private int duration = 15;
    private Activity activity;
    private Context context;
    private boolean status = false;
    private static final String TAG = "EmergencyApp";
    public static String idText = "";
    private SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "OnOffPref";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_client, container, false);
        init();
        initListener();
        return v;
    }

    private void init() {
        sharedpreferences = getContext().getSharedPreferences(MyPREFERENCES , Context.MODE_PRIVATE);
        id = v.findViewById(R.id.etClientID);
        switchStatus = v.findViewById(R.id.switch1);
        spinner = v.findViewById(R.id.spinner);
        status = sharedpreferences.getBoolean("status", false);
        idText = sharedpreferences.getString("id", "");
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.duration, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION },
                    1);
        }
        activity = getActivity();
        context = getContext();
        if(status) {
            switchStatus.setChecked(true);
        } else {
            switchStatus.setChecked(false);
        }
        if(idText.length() != 0) {
            id.setText(idText);
        }
    }

    private void initListener() {
        switchStatus.setOnCheckedChangeListener((compoundButton, b) -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            if (b) {
                if(id.length() > 0) {
                    idText = id.getText().toString();
                    scheduleJob();
                    status = true;
                    editor.putBoolean("status", true);
                    editor.putString("id", idText);
                } else {
                    switchStatus.setChecked(false);
                    Toast.makeText(context, "Set ID first", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("status", false);
                }
            } else {
                cancelJob();
                editor.putBoolean("status", false);
            }
            editor.apply();
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
        JobScheduler scheduler = (JobScheduler)  context.getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Log.d(TAG, "Service cancelled");
    }

    private void scheduleJob() {
        ComponentName componentName = new ComponentName(context, LocationScheduler.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setPersisted(true)
                .setPeriodic(duration * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler)  context.getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if(resultCode == JobScheduler.RESULT_SUCCESS ) {
            Log.d(TAG, "Location updated successfully");
        } else {
            Log.d(TAG, "Location updated failed");
        }
    }
}
