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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterPage extends AppCompatActivity {
    public FirebaseAuth mAuth;
    String srand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerpage);
        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.back_btn:
                        Intent intent1 = new Intent(getApplicationContext(), LoginPage.class);
                        startActivity(intent1);
                        break;
                    case R.id.check_btn:
                        check();
                        break;
                    case R.id.register_btn:
                        register();
                        break;
                }
            }
        };
        Button back_btn = (Button) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(onClickListener);
        Button check_btn = (Button) findViewById(R.id.check_btn);
        check_btn.setOnClickListener(onClickListener);
        Button register_btn = (Button) findViewById(R.id.register_btn);
        register_btn.setOnClickListener(onClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void check() {
        double rand = (Math.random()*900000)+100000;
        int arand = (int)rand;
        srand = Integer.toString(arand);
        String emailAddress = ((EditText) findViewById(R.id.id)).getText().toString();
        emailAddress = emailAddress + "@ssu.ac.kr";
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("plain/Text");
        email.putExtra(Intent.EXTRA_EMAIL, emailAddress);
        email.putExtra(Intent.EXTRA_SUBJECT, "SSU MEET 이메일 인증");
        email.putExtra(Intent.EXTRA_TEXT, "SSU MEET 본인확인 인증번호는"+srand+"입니다.");
        startActivity(email);
    }

    private void register() {
        String email = ((EditText)findViewById(R.id.id)).getText().toString();
        email = email + "@ssu.ac.kr";
        String pw = ((EditText)findViewById(R.id.pw)).getText().toString();
        String pwCheck = ((EditText)findViewById(R.id.pwCheck)).getText().toString();
        String checkNum = ((EditText)findViewById(R.id.checkNum)).getText().toString();
        if(!pw.equals(pwCheck)) {
            startToast("비밀번호가 일치하지 않습니다.");
        }
        else if(!checkNum.equals(srand)){
            startToast("인증번호가 일치하지 않습니다.");
        }
        else {
            mAuth.createUserWithEmailAndPassword(email, pw)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                startToast("회원가입에 성공하였습니다.");
                            } else {
                                if (task.getException() != null) {
                                    startToast(task.getException().toString());
                                }
                            }
                        }
                    });
        }
        }
}
