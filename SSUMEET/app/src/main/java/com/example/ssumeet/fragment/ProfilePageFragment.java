package com.example.ssumeet.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ssumeet.R;
import com.example.ssumeet.UserPWActivity;
import com.example.ssumeet.common.Util9;
import com.example.ssumeet.model.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;

public class ProfilePageFragment extends Fragment {

    private static final int PICK_FROM_ALBUM = 1;

    private Button gallery_btn;
    private EditText name;
    private EditText age;
    private EditText major;
    private EditText interest;
    private EditText statusMsg;
    private boolean chat_permission;
    private boolean ranchat_permission;
    CheckBox chat_check;
    CheckBox ranchat_check;
    Button save_btn;
    Button changePW_btn;

    private UserModel userModel;
    private Uri userPhotoUri;

    FirebaseUser user;
    private String profilePath;
    private ImageView profile_image;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profilepage, container, false);

        name = view.findViewById(R.id.name);
        age = view.findViewById(R.id.age);
        major = view.findViewById(R.id.major);
        interest = view.findViewById(R.id.interest);
        statusMsg = view.findViewById(R.id.status_msg);
        //chat_check = chat_check.findViewById(R.id.chat_check);
        //ranchat_check = ranchat_check.findViewById(R.id.ranchat_check);


        /*save_btn = save_btn.findViewById(R.id.save_btn);
        save_btn.setOnClickListener(saveBtnClickListener);
        changePW_btn = changePW_btn.findViewById(R.id.changePW_btn);
        changePW_btn.setOnClickListener(changePWBtnClickListener);
        gallery_btn = gallery_btn.findViewById(R.id.gallery_btn);
        gallery_btn.setOnClickListener(userPhotoIVClickListener);
        profile_image = profile_image.findViewById(R.id.profile_image);*/

        getUserInfoFromServer();
        return view;
    }

    void getUserInfoFromServer(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(uid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userModel = documentSnapshot.toObject(UserModel.class);
                name.setText(userModel.getName());
                age.setText(userModel.getAge());
                major.setText(userModel.getMajor());
                interest.setText(userModel.getInterest());
                statusMsg.setText(userModel.getStatusMsg());
                if (userModel.getPhotoUrl()!= null && !"".equals(userModel.getPhotoUrl())) {
                    Glide.with(getActivity())
                            .load(FirebaseStorage.getInstance().getReference("userPhoto/"+userModel.getPhotoUrl()))
                            .into(profile_image);
                }
            }
        });
    }

    Button.OnClickListener userPhotoIVClickListener = new View.OnClickListener() {
        public void onClick(final View view) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, PICK_FROM_ALBUM);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==PICK_FROM_ALBUM && resultCode== getActivity().RESULT_OK) {
            profile_image.setImageURI(data.getData());
            userPhotoUri = data.getData();
        }
    }

    Button.OnClickListener saveBtnClickListener = new View.OnClickListener() {
        public void onClick(final View view) {
            userModel.setName(name.getText().toString());
            userModel.setAge(age.getText().toString());
            userModel.setMajor(major.getText().toString());
            userModel.setInterest(interest.getText().toString());
            userModel.setStatusMsg(statusMsg.getText().toString());

            final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            if (userPhotoUri!=null) {
                userModel.setPhotoUrl( uid );
            }

            db.collection("users").document(uid)
                    .set(userModel)
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

    Button.OnClickListener changePWBtnClickListener = new View.OnClickListener() {
        public void onClick(final View view) {
            startActivity(new Intent(getActivity(), UserPWActivity.class));
        }
    };







    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==PICK_FROM_ALBUM && resultCode== getActivity().RESULT_OK) {
            profile_image.setImageURI(data.getData());
            userPhotoUri = data.getData();
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
                                startToast(ProfilePageFragment.this, "회원정보를 보내는데 실패하였습니다.");
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

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void startToast(Activity activity, String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }*/
}
