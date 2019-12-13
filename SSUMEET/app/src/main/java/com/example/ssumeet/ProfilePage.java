package com.example.ssumeet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    AlertDialog listDialog;

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
                    case R.id.interest:
                        interest_dialog();
                }
            }
        };
        Button save_btn = (Button) findViewById(R.id.save_btn);
        save_btn.setOnClickListener(onClickListener);
        Button gallery_btn = (Button) findViewById(R.id.gallery_btn);
        gallery_btn.setOnClickListener(onClickListener);
        EditText interest = (EditText)findViewById(R.id.interest);
        interest.setOnClickListener(onClickListener);
        profile_image = findViewById(R.id.profile_image);

        final EditText name_et = (EditText)findViewById(R.id.name);
        final EditText age_et = (EditText)findViewById(R.id.age);
        final EditText subject_et = (EditText)findViewById(R.id.subject);
        final EditText interest_et = (EditText)findViewById(R.id.interest);
        final ImageView pthotoUrl_iv = (ImageView)findViewById(R.id.profile_image);
        final CheckBox chat_cb = (CheckBox)findViewById(R.id.chat_check);
        final CheckBox ranchat_cb = (CheckBox)findViewById(R.id.ranchat_check);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        String attach = intent.getStringExtra("Attach");
        if(!attach.equals("first_time")) {
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = task.getResult().getData().get("name").toString();
                            name_et.setText(name);
                            String age = task.getResult().getData().get("age").toString();
                            age_et.setText(age);
                            String subject = task.getResult().getData().get("subject").toString();
                            subject_et.setText(subject);
                            String interest = task.getResult().getData().get("interest").toString();
                            interest_et.setText(interest);
                            String photoUrl = task.getResult().getData().get("photoUrl").toString();
                            String chat_permission = task.getResult().getData().get("chat_permission").toString();
                            if(chat_permission.equals("true")) {
                                chat_cb.setChecked(true);
                            } else {
                                chat_cb.setChecked(false);
                            }
                            String ranchat_permission = task.getResult().getData().get("ranchat_permission").toString();
                            if(ranchat_permission.equals("true")) {
                                ranchat_cb.setChecked(true);
                            } else {
                                ranchat_cb.setChecked(false);
                            }
                        } else {
                        }
                    } else {
                    }
                }
            });

        }
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
                ProfileInfo profileInfo = new ProfileInfo(name, age, subject, interest, chat_permission, ranchat_permission);
                userInfoUpdate(profileInfo);
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

                                ProfileInfo profileInfo = new ProfileInfo(name, age, subject, interest, chat_permission, ranchat_permission);
                                userInfoUpdate(profileInfo);
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

    private void userInfoUpdate(ProfileInfo profileInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(user != null) {
            db.collection("users").document(user.getUid()).set(profileInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startToast("회원정보 등록이 완료되었습니다.");
                            finish();
                        }
                    });
        }
    }

    DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String[] data = getResources().getStringArray(R.array.interest_dialog);
            if (dialog == listDialog) {
                startToast(data[which] + " 선택하셨습니다.");
            }
            else if (dialog == listDialog && which == DialogInterface.BUTTON_POSITIVE) {
                EditText interest = (EditText)findViewById(R.id.interest);
                interest.setText(data[which]);
            }
        }
    };
    private void interest_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("관심사");
        builder.setSingleChoiceItems(R.array.interest_dialog, 0, dialogListener);
        builder.setPositiveButton("확인", dialogListener);
        builder.setNegativeButton("취소", null);
        listDialog = builder.create();
        listDialog.show();
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void startToast(Activity activity, String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }
}
