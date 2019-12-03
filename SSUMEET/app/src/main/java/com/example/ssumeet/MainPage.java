package com.example.ssumeet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button1:
                        Intent intent = new Intent(getApplicationContext(), ProfilePage.class);
                        startActivity(intent);
                        break;
                    case R.id.button2:
                        Intent intent1 = new Intent(getApplicationContext(), WritePost.class);
                        startActivity(intent1);
                        break;
                }
            }
        };
        Button button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(onClickListener);
        Button button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(onClickListener);

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
}
