package com.example.ssumeet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPwPage extends AppCompatActivity {
    public FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpwpage);
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        Button.OnClickListener onClickListener = new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.send_btn:
                        resetPw();
                    case R.id.back_btn:
                        Intent intent1 = new Intent(getApplicationContext(), LoginPage.class);
                        startActivity(intent1);
                }
            }
        };
        Button send_btn = (Button)findViewById(R.id.send_btn);
        send_btn.setOnClickListener(onClickListener);
        Button back_btn = (Button)findViewById(R.id.back_btn);
        back_btn.setOnClickListener(onClickListener);
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void resetPw() {
        String email = ((EditText)findViewById(R.id.id)).getText().toString();
        if(email.length()!=8) {
            startToast("학번을 다시 입력해주세요");
        }
        email = email + "@ssu.ac.kr";

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startToast("이메일이 전송됬습니다.");
                        }
                        else {
                            startToast("등록되지 않은 이메일입니다.");
                        }
                    }
                });
    }

}
