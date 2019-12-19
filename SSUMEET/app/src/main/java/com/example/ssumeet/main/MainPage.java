package com.example.ssumeet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.ssumeet.chat.ChatActivity;
import com.example.ssumeet.chat.SelectUserActivity;
import com.example.ssumeet.fragment.ChatRoomFragment;
import com.example.ssumeet.fragment.HomeFragment;
import com.example.ssumeet.fragment.UserListFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class MainPage extends AppCompatActivity {

    private MainPage.SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private FloatingActionButton makeRoomBtn;
    private FloatingActionButton ranChatBtn;
    private FloatingActionButton p2pChatBtn;
    private FloatingActionButton menuBtn;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new MainPage.SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition()==1) {     // char room
                    //makeRoomBtn.setVisibility(View.VISIBLE);
                    makeRoomBtn.show();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //makeRoomBtn.setVisibility(View.INVISIBLE);
                makeRoomBtn.hide();
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        sendRegistrationToServer();

        ranChatBtn = findViewById(R.id.ranChatBtn);
        ranChatBtn.hide();
        ranChatBtn.setOnClickListener(v ->  {
            RandomChatting();
        });


        makeRoomBtn = findViewById(R.id.makeRoomBtn);
        //makeRoomBtn.setVisibility(View.INVISIBLE);
        makeRoomBtn.hide();
        makeRoomBtn.setOnClickListener(v ->
                startActivity(new Intent(v.getContext(), SelectUserActivity.class)));

        if(user == null) {
            Intent intent = new Intent(getApplicationContext(), RegisterPage.class);
            startActivity(intent);
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference documentReference = db.collection("users").document(user.getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document != null) {
                            if(document.exists()) {
                            } else {
                                Intent intent = new Intent(getApplicationContext(), ProfilePage.class);
                                startActivity(intent);
                            }
                        }
                    }
                }
            });
        }
    }

    private void RandomChatting() {

        String myUid = user.getUid();

        FirebaseFirestore userdb = FirebaseFirestore.getInstance();
        userdb.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            int i = 0;
            String[] userUid;
            int size;

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                size = task.getResult().size();
                userUid = new String[size];

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Log.d("superdroid", document.getId() + " => " + document.getData());
                        userUid[i++] = document.getId();
                    }
                } else {
                    Log.d("superdroid", "Error getting documents: ", task.getException());
                }
            }
        });

        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("toUid", user.getUid());
        startActivity(intent);

    }


    void sendRegistrationToServer() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        FirebaseFirestore.getInstance().collection("users").document(uid).set(map, SetOptions.merge());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;

        switch (id) {
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(this, LoginPage.class);
                this.startActivity(intent);
                this.finish();

                return true;

            case R.id.action_profile:
                intent = new Intent(this, ProfilePage.class);
                this.startActivity(intent);
                this.finish();

                return true;

        }

        /*//noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginPage.class);
            this.startActivity(intent);
            this.finish();

            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return new UserListFragment();
                case 1: return new ChatRoomFragment();
                default: return new HomeFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}