package com.example.ssumeet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class StartingPage extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /*Intent mainIntent = new Intent(StartingPage.this, LoginPage.class);
        StartingPage.this.startActivity(mainIntent);*/


        //FirebaseAuth.getInstance().signOut();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = null;
                if (FirebaseAuth.getInstance().getCurrentUser()==null) {
                    mainIntent = new Intent(StartingPage.this, LoginPage.class);
                } else {
                    mainIntent = new Intent(StartingPage.this, MainPage.class);
                }
                StartingPage.this.startActivity(mainIntent);
                StartingPage.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
