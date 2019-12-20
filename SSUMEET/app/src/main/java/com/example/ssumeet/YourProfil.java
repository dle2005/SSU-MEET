package com.example.ssumeet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ssumeet.model.ProfileModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class YourProfil extends AppCompatActivity {

    private ProfileModel ProfileModel;

    String yourUid;

    ImageView yourImage;
    TextView yourName;
    TextView yourStatus;
    TextView yourAge;
    TextView yourSubject;
    TextView yourInterest;
    Button addFriendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_profil);

        yourImage = findViewById(R.id.your_image);
        yourName = findViewById(R.id.your_name);
        yourStatus = findViewById(R.id.your_status);
        yourAge = findViewById(R.id.your_age);
        yourSubject = findViewById(R.id.your_subject);
        yourInterest = findViewById(R.id.your_interest);
        addFriendBtn = findViewById(R.id.addFriendBtn);

        yourUid = getIntent().getStringExtra("userUid");

        addFriendBtn.setOnClickListener(AddFriendBtn);

        getUserInfoFromServer();
    }

    void getUserInfoFromServer(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(uid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ProfileModel = documentSnapshot.toObject(ProfileModel.class);
                yourName.setText(ProfileModel.getName());
                yourAge.setText(ProfileModel.getAge());
                yourSubject.setText(ProfileModel.getSubject());
                yourInterest.setText(ProfileModel.getInterest());
                yourStatus.setText(ProfileModel.getStatusMsg());
                if (ProfileModel.getPhotoUrl()!= null && !"".equals(ProfileModel.getPhotoUrl())) {
                    Glide.with(YourProfil.this).load(FirebaseStorage.getInstance().getReference("userPhoto/"+ ProfileModel.getPhotoUrl())).into(yourImage);
                }
            }
        });
    }

    Button.OnClickListener AddFriendBtn = v -> {

    };
}
