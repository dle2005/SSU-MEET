package com.example.ssumeet.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ssumeet.MainPage;
import com.example.ssumeet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {

    private EditText user_id;

    SharedPreferences sharedPreferences;

    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpage);
        mAuth = FirebaseAuth.getInstance();

        user_id = findViewById(R.id.id);

        Button.OnClickListener onClickListener = v -> {
            switch (v.getId()) {
                case R.id.login_btn:
                    signIN();
                    break;
                case R.id.register_btn:
                    Intent intent = new Intent(getApplicationContext(), RegisterPage.class);
                    startActivity(intent);
                    break;
                case R.id.resetPw_btn:
                    Intent intent1 = new Intent(getApplicationContext(), ResetPwPage.class);
                    startActivity(intent1);
                    break;
            }
        };
        Button login_btn = (Button) findViewById(R.id.login_btn);
        login_btn.setOnClickListener(onClickListener);
        Button register_btn = (Button) findViewById(R.id.register_btn);
        register_btn.setOnClickListener(onClickListener);
        TextView resetPw_btn = (TextView) findViewById(R.id.resetPw_btn);
        resetPw_btn.setOnClickListener(onClickListener);

        sharedPreferences = getSharedPreferences("ssumeet", Activity.MODE_PRIVATE);
        String id = sharedPreferences.getString("user_id", "");
        if (!"".equals(id)) {
            user_id.setText(id.substring(0, 8));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void signIN() {
        String email = ((EditText) findViewById(R.id.id)).getText().toString();
        if (email.length() != 8) {
            startToast("학번을 다시 입력해주세요");
            return;
        }
        email = email + "@ssu.ac.kr";
        String password = ((EditText) findViewById(R.id.pw)).getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sharedPreferences.edit().putString("user_id", user_id.getText().toString() + "@ssu.ac.kr").commit();
                            FirebaseUser user = mAuth.getCurrentUser();
                            startToast("로그인에 성공하였습니다.");
                            Intent intent = new Intent(getApplicationContext(), MainPage.class);
                            startActivity(intent);
                        } else {
                            if (task.getException() != null) {
                                startToast("없는 아이디 이거나 비밀번호가 틀립니다.");
                            }
                        }
                    }
                });
    }

}
