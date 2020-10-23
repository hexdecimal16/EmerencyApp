package com.dhairytripathi.emergencyapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class AdminFragment extends Fragment {
    private EditText id;
    private TextView latitude;
    private TextView longitude;
    private TextView time;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button map;
    private Button refresh;
    private View v;
    private String latData, lonData;
    long timeData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_admin, container, false);
        init();
        initListeners();
        return v;
    }

    private void init() {
        id = v.findViewById(R.id.etAdminID);
        latitude = v.findViewById(R.id.tvLatitude);
        longitude = v.findViewById(R.id.tvLongitude);
        time = v.findViewById(R.id.tvTime);
        map = v.findViewById(R.id.btnMap);
        refresh = v.findViewById(R.id.btnRefresh);
    }

    private void initListeners() {
        map.setOnClickListener(view -> {
            Uri mapUri = Uri.parse("geo:0,0?q=" + latData + "," + lonData);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });
        refresh.setOnClickListener(view -> {
            if(id.length() > 0) {
                getData();
            } else  {
                Toast.makeText(getContext(), "Please set id first", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getData() {
        db.collection("locations").document(id.getText().toString()).get()
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful()) {
                       DocumentSnapshot document = task.getResult();
                       if(document.exists()) {
                           try {
                               latData = String.valueOf(document.get("Latitude"));
                               lonData = String.valueOf(document.get("Longitude"));
                               timeData = ((Long) document.get("Time")).longValue();
                           } catch (Exception e) {
                               e.printStackTrace();
                           }
                           latitude.setText(latData);
                           longitude.setText(lonData);
                           time.setText(String.valueOf(new Date(timeData)));
                       } else {
                           Toast.makeText(getContext(), "Sorry, no result found for this id", Toast.LENGTH_SHORT).show();
                       }
                   } else {
                       Toast.makeText(getContext(), "Sorry, no result found for this id", Toast.LENGTH_SHORT).show();
                       Log.e("EmergencyApp", "Got error \n" + task.getException().getMessage());
                   }
                });
    }
}
