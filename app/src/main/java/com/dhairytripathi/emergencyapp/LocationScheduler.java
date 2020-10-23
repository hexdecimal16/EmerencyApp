package com.dhairytripathi.emergencyapp;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import static com.dhairytripathi.emergencyapp.ClientFragment.idText;

@SuppressLint("SpecifyJobSchedulerIdRange")
public class LocationScheduler extends JobService {

    private static final String TAG = "LocationScheduler";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FusedLocationProviderClient fusedLocationClient;
    private boolean jobCancelled = false;
    private static String id;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "onStartJob");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(task -> {
                    if(jobCancelled)
                        return;
                    if(task.isSuccessful()) {
                        Location location = task.getResult();
                        if(location != null) {
                            Log.i("MainActivity", location.toString());
                            HashMap<String , Object> data = new HashMap<>();
                            data.put("Altitude", location.getAltitude());
                            data.put("Accuracy", location.getAccuracy());
                            data.put("Bearing", location.getBearing());
                            data.put("Latitude", location.getLatitude());
                            data.put("Longitude", location.getLongitude());
                            data.put("HasSpeed", location.hasSpeed());
                            data.put("Speed", location.getSpeed());
                            data.put("DescribeContents", location.describeContents());
                            data.put("Provider", location.getProvider());
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                data.put("BearingAccuracyDegrees", location.getBearingAccuracyDegrees());
                            }
                            data.put("ElapsedRealtimeNanos", location.getElapsedRealtimeNanos());
                            data.put("Time", location.getTime());
                            data.put("Extras", location.getExtras());
                            data.put("ElapsedRealtimeUncertaintyNanos", location.getElapsedRealtimeUncertaintyNanos());
                            Log.i("EmergencyApp", "idText: " + idText);
                            db.collection("locations").document(idText).set(data)
                            .addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()) {
                                    Log.d(TAG, "DATA UPDATED TO CLOUD");
                                } else {
                                    Log.e("EmergencyApp", "Error:\n" + task1.getException().getMessage());
                                }
                            });
                        }
                    }
                });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "onStopJob");
        jobCancelled = true;
        return true;
    }
}
