package com.example.ssumeet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ssumeet.common.Util9;
import com.example.ssumeet.model.ProfileModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class YourProfil extends AppCompatActivity {

    private ProfileModel ProfileModel;
    private Uri userPhotoUri;

    Map<String, String> friends = new HashMap<>();
    String yourUid;
    String myUid;
    String yourGender;

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

        yourUid = getIntent().getStringExtra("yourUid");
        myUid = getIntent().getStringExtra("myUid");

        addFriendBtn.setOnClickListener(AddFriendBtn);

        getUserInfoFromServer();

        if(myUid == yourUid) addFriendBtn.setVisibility(View.INVISIBLE);
    }

    void getUserInfoFromServer(){

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(yourUid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ProfileModel = documentSnapshot.toObject(ProfileModel.class);
                yourName.setText(ProfileModel.getName());
                yourAge.setText(ProfileModel.getAge());
                yourSubject.setText(ProfileModel.getSubject());
                yourInterest.setText(ProfileModel.getInterest());
                yourStatus.setText(ProfileModel.getStatusMsg());
                yourImage.setImageURI(Uri.parse("userPhoto/"+ProfileModel.getPhotoUrl()));
                if (ProfileModel.getPhotoUrl()!= null && !"".equals(ProfileModel.getPhotoUrl())) {
                    Glide.with(YourProfil.this).load(FirebaseStorage.getInstance().getReference("userPhoto/"+ ProfileModel.getPhotoUrl())).into(yourImage);
                }
            }
        });
    }

    Button.OnClickListener AddFriendBtn = v -> {
        ProfileModel.setName(yourName.getText().toString());
        ProfileModel.setGender(yourGender);
        ProfileModel.setAge(yourAge.getText().toString());
        ProfileModel.setSubject(yourSubject.getText().toString());
        ProfileModel.setInterest(yourInterest.getText().toString());
        ProfileModel.setStatusMsg(yourStatus.getText().toString());

        final FirebaseFirestore db = FirebaseFirestore.getInstance();



        db.collection("users").document(myUid).collection("friends").document(yourUid)
                .set(ProfileModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("superdroid", "DocumentSnapshot successfully written!");
                        Glide.with(YourProfil.this)
                                .asBitmap()
                                .load(userPhotoUri)
                                .apply(new RequestOptions().override(150, 150))
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                        byte[] data = baos.toByteArray();
                                        FirebaseStorage.getInstance().getReference().child("userPhoto/" + yourUid).putBytes(data);

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("superdroid", "Error writing document", e);
                    }
                });
        /*db.collection("users").document(myUid).collection("friends").add(friends)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                });*/

    };
}
