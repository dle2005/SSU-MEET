package com.example.ssumeet.post;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.ssumeet.ContentsItemView;
import com.example.ssumeet.GalleryActivity;
import com.example.ssumeet.R;
import com.example.ssumeet.model.ProfileModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import static com.example.ssumeet.post.Util.GALLERY_IMAGE;
import static com.example.ssumeet.post.Util.GALLERY_VIDEO;
import static com.example.ssumeet.post.Util.INTENT_MEDIA;
import static com.example.ssumeet.post.Util.INTENT_PATH;
import static com.example.ssumeet.post.Util.isImageFile;
import static com.example.ssumeet.post.Util.isStorageUrl;
import static com.example.ssumeet.post.Util.isVideoFile;
import static com.example.ssumeet.post.Util.showToast;
import static com.example.ssumeet.post.Util.storageUrlToName;

public class WritePostActivity extends BasicActivity {
    private static final String TAG = "WritePostActivity";
    private static final int PICK_FROM_ALBUM = 1;
    private FirebaseUser user;
    private StorageReference storageRef;
    private ArrayList<String> pathList = new ArrayList<>();
    private LinearLayout parent;
    private RelativeLayout buttonsBackgroundLayout;
    private RelativeLayout loaderLayout;
    private ImageView selectedImageVIew;
    private EditText selectedEditText;
    private EditText contentsEditText;
    private EditText titleEditText;
    private PostInfo postInfo;
    private int pathCount, successCount;

    private com.example.ssumeet.model.ProfileModel ProfileModel;
    private String user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        parent = findViewById(R.id.contentsLayout);
        buttonsBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);
        loaderLayout = findViewById(R.id.loaderLyaout);
        contentsEditText = findViewById(R.id.contentsEditText);
        titleEditText = findViewById(R.id.titleEditText);

        findViewById(R.id.check).setOnClickListener(onClickListener);
        findViewById(R.id.image).setOnClickListener(changeImageBtnClickListener);
        findViewById(R.id.video).setOnClickListener(onClickListener);
        findViewById(R.id.imageModify).setOnClickListener(onClickListener);
        findViewById(R.id.videoModify).setOnClickListener(onClickListener);
        findViewById(R.id.delete).setOnClickListener(onClickListener);

        buttonsBackgroundLayout.setOnClickListener(onClickListener);
        contentsEditText.setOnFocusChangeListener(onFocusChangeListener);
        titleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    selectedEditText = null;
                }
            }
        });

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo");
        postInit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    String path = data.getStringExtra(INTENT_PATH);
                    pathList.add(path);

                    ContentsItemView contentsItemView = new ContentsItemView(this);

                    if (selectedEditText == null) {
                        parent.addView(contentsItemView);
                    } else {
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            if (parent.getChildAt(i) == selectedEditText.getParent()) {
                                parent.addView(contentsItemView, i + 1);
                                break;
                            }
                        }
                    }

                    contentsItemView.setImage(path);
                    contentsItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageVIew = (ImageView) v;
                        }
                    });

                    contentsItemView.setOnFocusChangeListener(onFocusChangeListener);
                }
                break;
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    String path = data.getStringExtra(INTENT_PATH);
                    pathList.set(parent.indexOfChild((View) selectedImageVIew.getParent()) - 1, path);
                    Glide.with(this).load(path).override(1000).into(selectedImageVIew);
                }
                break;
        }
    }
    Button.OnClickListener changeImageBtnClickListener = new View.OnClickListener() {
        public void onClick(final View view) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, PICK_FROM_ALBUM);
        }
    };
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.check:
                    storageUpload();
                    break;
                case R.id.video:
                    myStartActivity(GalleryActivity.class, GALLERY_VIDEO, 0);
                    break;
                case R.id.buttonsBackgroundLayout:
                    if (buttonsBackgroundLayout.getVisibility() == View.VISIBLE) {
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }
                    break;
                case R.id.imageModify:
                    myStartActivity(GalleryActivity.class, GALLERY_IMAGE, 1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.videoModify:
                    myStartActivity(GalleryActivity.class, GALLERY_VIDEO, 1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.delete:
                    final View selectedView = (View) selectedImageVIew.getParent();
                    String path = pathList.get(parent.indexOfChild(selectedView) - 1);
                    if(isStorageUrl(path)){
                        StorageReference desertRef = storageRef.child("posts/" + postInfo.getId() + "/" + storageUrlToName(path));
                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showToast(WritePostActivity.this, "파일을 삭제하였습니다.");
                                pathList.remove(parent.indexOfChild(selectedView) - 1);
                                parent.removeView(selectedView);
                                buttonsBackgroundLayout.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                showToast(WritePostActivity.this, "파일을 삭제하는데 실패하였습니다.");
                            }
                        });
                    }else{
                        pathList.remove(parent.indexOfChild(selectedView) - 1);
                        parent.removeView(selectedView);
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                selectedEditText = (EditText) v;
            }
        }
    };

    private void storageUpload() {
        final String title = ((EditText) findViewById(R.id.titleEditText)).getText().toString();
        if (title.length() > 0) {
            loaderLayout.setVisibility(View.VISIBLE);
            final ArrayList<String> contentsList = new ArrayList<>();
            final ArrayList<String> formatList = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            final DocumentReference documentReference = postInfo == null ? firebaseFirestore.collection("posts").document() : firebaseFirestore.collection("posts").document(postInfo.getId());
            final Date date = postInfo == null ? new Date() : postInfo.getCreatedAt();
            for (int i = 0; i < parent.getChildCount(); i++) {
                LinearLayout linearLayout = (LinearLayout) parent.getChildAt(i);
                for (int ii = 0; ii < linearLayout.getChildCount(); ii++) {
                    View view = linearLayout.getChildAt(ii);
                    if (view instanceof EditText) {
                        String text = ((EditText) view).getText().toString();
                        if (text.length() > 0) {
                            contentsList.add(text);
                            formatList.add("text");
                        }
                    } else if (!isStorageUrl(pathList.get(pathCount))) {
                        String path = pathList.get(pathCount);
                        successCount++;
                        contentsList.add(path);
                        if(isImageFile(path)){
                            formatList.add("image");
                        }else if (isVideoFile(path)){
                            formatList.add("video");
                        }else{
                            formatList.add("text");
                        }
                        String[] pathArray = path.split("\\.");
                        final StorageReference mountainImagesRef = storageRef.child("posts/" + documentReference.getId() + "/" + pathCount + "." + pathArray[pathArray.length - 1]);
                        try {
                            InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (contentsList.size() - 1)).build();
                            UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                                    mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            successCount--;
                                            contentsList.set(index, uri.toString());
                                            if (successCount == 0) {
                                                PostInfo postInfo = new PostInfo(title, contentsList, formatList, user.getUid(), date);
                                                storeUpload(documentReference, postInfo);

                                            }
                                        }
                                    });
                                }
                            });
                        } catch (FileNotFoundException e) {
                            Log.e("로그", "에러: " + e.toString());
                        }
                        pathCount++;
                    }
                }
            }
            if (successCount == 0) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(uid);
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ProfileModel = documentSnapshot.toObject(ProfileModel.class);
                        user_name = ProfileModel.getName();
                    }
                });
                Log.d("publisher", user_name);
                storeUpload(documentReference, new PostInfo(title, contentsList, formatList, user_name, date));
            }        } else {
            showToast(WritePostActivity.this, "제목을 입력해주세요.");
        }
    }


    private void storeUpload(DocumentReference documentReference, final PostInfo postInfo) {
        documentReference.set(postInfo.getPostInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        loaderLayout.setVisibility(View.GONE);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("postinfo", postInfo);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        loaderLayout.setVisibility(View.GONE);
                    }
                });
    }

    private void postInit() {
        if (postInfo != null) {
            titleEditText.setText(postInfo.getTitle());
            ArrayList<String> contentsList = postInfo.getContents();
            for (int i = 0; i < contentsList.size(); i++) {
                String contents = contentsList.get(i);
                if (isStorageUrl(contents)) {
                    pathList.add(contents);
                    ContentsItemView contentsItemView = new ContentsItemView(this);
                    parent.addView(contentsItemView);

                    contentsItemView.setImage(contents);
                    contentsItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageVIew = (ImageView) v;
                        }
                    });

                    contentsItemView.setOnFocusChangeListener(onFocusChangeListener);
                    if (i < contentsList.size() - 1) {
                        String nextContents = contentsList.get(i + 1);
                        if (!isStorageUrl(nextContents)) {
                            contentsItemView.setText(nextContents);
                        }
                    }
                } else if (i == 0) {
                    contentsEditText.setText(contents);
                }
            }
        }
    }

    private void myStartActivity(Class c, int media, int requestCode) {
        Intent intent = new Intent(this, c);
        intent.putExtra(INTENT_MEDIA, media);
        startActivityForResult(intent, requestCode);
    }
}
