package com.example.ssumeet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ssumeet.common.Util9;
import com.example.ssumeet.model.ProfileModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;

public class ProfilePage extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM = 1;
    private ImageView user_photo;
    private EditText user_name;
    private EditText user_gender;
    private EditText user_age;
    private EditText user_subject;
    private EditText user_interest;
    private EditText user_msg;

    private ProfileModel ProfileModel;
    private Uri userPhotoUri;

    AlertDialog listDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        user_name = findViewById(R.id.name);
        user_msg = findViewById(R.id.status);
        user_age = findViewById(R.id.age);
        user_subject = findViewById(R.id.subject);
        user_interest = findViewById(R.id.interest);
        user_photo = findViewById(R.id.profile_image);
        user_gender = findViewById(R.id.gender);


        user_interest.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                    interest_dialog();
                return false;
            }
        });
        user_gender.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                    gender_dialog();
                return false;
            }
        });
        Button saveBtn = findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(saveBtnClickListener);
        Button changeImage = findViewById(R.id.gallery_btn);
        changeImage.setOnClickListener(changeImageBtnClickListener);
        Button cancelBtn = findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(cancelBtnClickListener);

        getUserInfoFromServer();

    }

    void getUserInfoFromServer(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(uid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ProfileModel = documentSnapshot.toObject(ProfileModel.class);
                user_name.setText(ProfileModel.getName());
                user_gender.setText(ProfileModel.getGender());
                user_age.setText(ProfileModel.getAge());
                user_subject.setText(ProfileModel.getSubject());
                user_interest.setText(ProfileModel.getInterest());
                user_msg.setText(ProfileModel.getStatusMsg());
                if (ProfileModel.getPhotoUrl()!= null && !"".equals(ProfileModel.getPhotoUrl())) {
                    Glide.with(ProfilePage.this).load(FirebaseStorage.getInstance().getReference("userPhoto/"+ ProfileModel.getPhotoUrl())).into(user_photo);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            user_photo.setImageURI(data.getData());
            userPhotoUri = data.getData();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    Button.OnClickListener saveBtnClickListener = new View.OnClickListener() {

        public void onClick(final View view) {
            if (!validateForm()) return;
            ProfileModel.setName(user_name.getText().toString());
            ProfileModel.setGender(user_gender.getText().toString());
            ProfileModel.setAge(user_age.getText().toString());
            ProfileModel.setSubject(user_subject.getText().toString());
            ProfileModel.setInterest(user_interest.getText().toString());
            ProfileModel.setStatusMsg(user_msg.getText().toString());


            final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            if (userPhotoUri!=null) {
                ProfileModel.setPhotoUrl( uid );
            }

            db.collection("users").document(uid)
                    .set(ProfileModel)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (userPhotoUri==null) {
                                Util9.showMessage(ProfilePage.this, "Success to Save.");
                            } else {
                                // small image
                                Glide.with(ProfilePage.this)
                                        .asBitmap()
                                        .load(userPhotoUri)
                                        .apply(new RequestOptions().override(150, 150))
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                                byte[] data = baos.toByteArray();
                                                FirebaseStorage.getInstance().getReference().child("userPhoto/" + uid).putBytes(data);
                                                Util9.showMessage(ProfilePage.this, "Success to Save.");

                                            }
                                        });
                            }
                            Intent intent = new Intent(getApplicationContext(), MainPage.class);
                            startActivity(intent);
                        }
                    });
        }
    };

    Button.OnClickListener changeImageBtnClickListener = new View.OnClickListener() {
        public void onClick(final View view) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, PICK_FROM_ALBUM);
        }
    };

    Button.OnClickListener cancelBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), MainPage.class);
            startActivity(intent);
        }
    };

    EditText.OnClickListener interestEditTextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            interest_dialog();
        }
    };

    DialogInterface.OnClickListener interestdialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0:
                    user_interest.setText("공부");
                    break;
                case 1:
                    user_interest.setText("문화");
                    break;
                case 2:
                    user_interest.setText("운동");
                    break;
                case 3:
                    user_interest.setText("영화");
                    break;
                case 4:
                    user_interest.setText("독서");
                    break;
            }
        }
    };
    private void interest_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("관심사");
        builder.setSingleChoiceItems(R.array.interest_dialog, 0, interestdialogListener);
        builder.setPositiveButton("확인", null);
        builder.setNegativeButton("취소", null);
        listDialog = builder.create();
        listDialog.show();
    }

    DialogInterface.OnClickListener GenderdialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0:
                    user_gender.setText("여자");
                    break;
                case 1:
                    user_gender.setText("남자");
                    break;
            }
        }
    };
    private void gender_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("성별");
        builder.setSingleChoiceItems(R.array.gender_dialog, 0, GenderdialogListener);
        builder.setPositiveButton("확인", null);
        builder.setNegativeButton("취소", null);
        listDialog = builder.create();
        listDialog.show();
    }

    private boolean validateForm() {
        boolean valid = true;

        String userName = user_name.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            user_name.setError("이름을 입력해주세요.");
            valid = false;
        } else {
            user_name.setError(null);
        }

        Util9.hideKeyboard(ProfilePage.this);

        return valid;
    }

}
