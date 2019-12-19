package com.example.ssumeet;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ssumeet.fragment.HomeFragment;

public class PostPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_page);

        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, homeFragment)
                .commit();



    }
}