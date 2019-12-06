package com.example.ssumeet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class WritePost extends AppCompatActivity {

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writepost);

        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.insert_btn:
                        writePost();
                        break;
                    case R.id.image_btn:
                        startActivity(Gallery.class, 0, 0);
                        break;
                    case R.id.video_btn:
                        startActivity(Gallery.class, 1, 0);
                        break;
                }
            }
        };
        Button insert_btn = (Button)findViewById(R.id.insert_btn);
        insert_btn.setOnClickListener(onClickListener);
        Button image_btn = (Button)findViewById(R.id.image_btn);
        image_btn.setOnClickListener(onClickListener);
        Button video_btn = (Button)findViewById(R.id.video_btn);
        video_btn.setOnClickListener(onClickListener);
    }

    private void writePost() {
        final String title = ((EditText) findViewById(R.id.title)).getText().toString();
        final String content = ((EditText) findViewById(R.id.content)).getText().toString();

        if (title.length() > 0 && content.length() > 0) {
            user = FirebaseAuth.getInstance().getCurrentUser();
            WritePostInfo writePostInfo = new WritePostInfo(title, content, user.getUid());
            uploader(writePostInfo);
        } else {
            startToast("제목 혹은 내용을 입력해주세요.");
        }
    }
        private void uploader(WritePostInfo writePostInfo) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("posts").add(writePostInfo)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void startActivity(Class c, int media, int requestCode) {
        Intent intent = new Intent(this, c);
        intent.putExtra("media", media);
        startActivityForResult(intent, requestCode);
    }
}
