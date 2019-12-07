package com.example.ssumeet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ssumeet.common.Util9;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {
    public FirebaseAuth mAuth;
    private EditText user_id;
    private EditText user_pw;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpage);
        mAuth = FirebaseAuth.getInstance();

        user_id = findViewById(R.id.id);
        user_pw = findViewById(R.id.pw);
        Button login_btn = (Button)findViewById(R.id.login_btn);
        Button register_btn = (Button)findViewById(R.id.register_btn);
        TextView resetPw_btn = (TextView)findViewById(R.id.resetPw_btn);

        login_btn.setOnClickListener(onClickListener);
        register_btn.setOnClickListener(onClickListener);
        resetPw_btn.setOnClickListener(onClickListener);

        sharedPreferences = getSharedPreferences("ssumeet", Activity.MODE_PRIVATE);
        String id = sharedPreferences.getString("user_id" + "@ssu.ac.kr", "");
        if (!"".equals(id)) {
            user_id.setText(id);
        }
    }

    Button.OnClickListener onClickListener = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
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
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void signIN() {
        String email = ((EditText)findViewById(R.id.id)).getText().toString();
        if(email.length()!=8) {
            startToast("학번을 다시 입력해주세요");
            return;
        }
        email = user_id.getText().toString() + "@ssu.ac.kr";
        String password = user_pw.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sharedPreferences.edit().putString("user_id", user_id.getText().toString() + "@ssu.ac.kr").commit();
                            startToast("로그인에 성공하였습니다.");
                            Intent intent = new Intent(getApplicationContext(), MainPage.class);
                            startActivity(intent);
                        } else {
                            if(task.getException() != null) {
                                startToast("없는 아이디 이거나 비밀번호가 틀립니다.");
                                //Util9.showMessage(getApplicationContext(), task.getException().getMessage());
                            }
                        }
                    }
        });
    }

}
