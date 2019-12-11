package com.example.ssumeet.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ssumeet.model.ProfileModel;
import com.example.ssumeet.R;
import com.example.ssumeet.common.Util9;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;

public class ProfileFragment extends Fragment {
    private static final int PICK_FROM_ALBUM = 1;
    private ImageView user_photo;
    private EditText user_name;
    private EditText user_age;
    private EditText user_subject;
    private EditText user_interest;
    private EditText user_msg;

    private ProfileModel ProfileModel;
    private Uri userPhotoUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profilefragment, container, false);

        user_name = view.findViewById(R.id.name);
        user_msg = view.findViewById(R.id.status);
        user_age = view.findViewById(R.id.age);
        user_subject = view.findViewById(R.id.subject);
        user_interest = view.findViewById(R.id.interest);
        user_photo = view.findViewById(R.id.profile_image);

        Button saveBtn = view.findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(saveBtnClickListener);
        Button changeImage = view.findViewById(R.id.gallery_btn);
        changeImage.setOnClickListener(changeImageBtnClickListener);

        getUserInfoFromServer();
        return view;
    }

    void getUserInfoFromServer(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(uid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ProfileModel = documentSnapshot.toObject(ProfileModel.class);
                user_name.setText(ProfileModel.getName());
                user_age.setText(ProfileModel.getAge());
                user_subject.setText(ProfileModel.getSubject());
                user_interest.setText(ProfileModel.getInterest());
                user_msg.setText(ProfileModel.getStatusMsg());
                if (ProfileModel.getPhotoUrl()!= null && !"".equals(ProfileModel.getPhotoUrl())) {
                    Glide.with(getActivity())
                            .load(FirebaseStorage.getInstance().getReference("userPhoto/"+ ProfileModel.getPhotoUrl()))
                            .into(user_photo);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==PICK_FROM_ALBUM && resultCode== getActivity().RESULT_OK) {
            user_photo.setImageURI(data.getData());
            userPhotoUri = data.getData();
        }
    }

    Button.OnClickListener saveBtnClickListener = new View.OnClickListener() {
        public void onClick(final View view) {
            if (!validateForm()) return;
            ProfileModel.setName(user_name.getText().toString());
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
                                Util9.showMessage(getActivity(), "Success to Save.");
                            } else {
                                // small image
                                Glide.with(getContext())
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
                                                Util9.showMessage(getActivity(), "Success to Save.");
                                            }
                                        });
                            }
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


    private boolean validateForm() {
        boolean valid = true;

        String userName = user_name.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            user_name.setError("Required.");
            valid = false;
        } else {
            user_name.setError(null);
        }

        String userMsg = user_msg.getText().toString();
        if (TextUtils.isEmpty(userMsg)) {
            user_msg.setError("Required.");
            valid = false;
        } else {
            user_msg.setError(null);
        }
        Util9.hideKeyboard(getActivity());

        return valid;
    }

}
