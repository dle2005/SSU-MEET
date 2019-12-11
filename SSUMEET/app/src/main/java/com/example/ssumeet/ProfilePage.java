package com.example.ssumeet;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ssumeet.model.ProfileModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfilePage extends BasicActivity {
    FirebaseUser user;
    private String profilePath;
    private ImageView profile_image;
    private String chat_permission;
    private String ranchat_permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilepage);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.save_btn:
                        profileUpdate();
                        break;
                    case R.id.gallery_btn:
                        Intent intent1 = new Intent(getApplicationContext(), Gallery.class);
                        startActivity(intent1);
                        break;

                }
            }
        };
        Button save_btn = (Button) findViewById(R.id.save_btn);
        save_btn.setOnClickListener(onClickListener);
        Button gallery_btn = (Button) findViewById(R.id.gallery_btn);
        gallery_btn.setOnClickListener(onClickListener);
        profile_image = findViewById(R.id.profile_image);
    }

    private void profileUpdate() {
        final String name = ((EditText)findViewById(R.id.name)).getText().toString();
        final String age = ((EditText)findViewById(R.id.age)).getText().toString();
        final String subject = ((EditText)findViewById(R.id.subject)).getText().toString();
        final String interest = ((EditText)findViewById(R.id.interest)).getText().toString();
        CheckBox chat_check = (CheckBox) findViewById(R.id.chat_check);
        CheckBox ranchat_check = (CheckBox) findViewById(R.id.ranchat_check);
        if(chat_check.isChecked()) { chat_permission = "true"; }
        else { chat_permission = "false"; }
        if(ranchat_check.isChecked()) { ranchat_permission = "true"; }
        else { ranchat_permission = "false"; }

        if (name.length() > 0 && age.length() > 0 && subject.length() > 0 && interest.length() > 0) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();
            final StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");
            user = FirebaseAuth.getInstance().getCurrentUser();

            if (profilePath == null) {
                ProfileModel profileModel = new ProfileModel(name, age, subject, interest, chat_permission, ranchat_permission);
                userInfoUpdate(profileModel);
            } else {
                try {
                    InputStream stream = new FileInputStream(new File(profilePath));
                    UploadTask uploadTask = mountainImagesRef.putStream(stream);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return mountainImagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();

                                ProfileModel profileModel = new ProfileModel(name, age, subject, interest, chat_permission, ranchat_permission);
                                userInfoUpdate(profileModel);
                            } else {
                                startToast(ProfilePage.this, "회원정보를 보내는데 실패하였습니다.");
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    Log.e("로그", "에러: " + e.toString());
                }
            }
        } else {
                startToast("회원정보를 입력해주세요.");
            }
    }

    private void userInfoUpdate(ProfileModel profileModel) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(user != null) {
            db.collection("users").document(user.getUid()).set(profileModel)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startToast("회원정보 등록이 완료되었습니다.");
                            finish();
                        }
                    });
        }
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void startToast(Activity activity, String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }
}
