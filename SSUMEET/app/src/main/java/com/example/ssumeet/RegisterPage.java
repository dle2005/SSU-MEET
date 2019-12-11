package com.example.ssumeet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ssumeet.model.ProfileModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class RegisterPage extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    private EditText user_id;
    private ImageView x_img;
    private ImageView check_img;

    public FirebaseAuth mAuth;
    String srand;
    String pw;
    String pwCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerpage);

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        Button back_btn = (Button) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(onClickListener);
        Button check_btn = (Button) findViewById(R.id.check_btn);
        check_btn.setOnClickListener(onClickListener);
        Button register_btn = (Button) findViewById(R.id.register_btn);
        register_btn.setOnClickListener(onClickListener);

        x_img = findViewById(R.id.x_img);
        check_img = findViewById(R.id.check_img);

        pw = ((EditText)findViewById(R.id.pw)).getText().toString();
        pwCheck = ((EditText)findViewById(R.id.pwCheck)).getText().toString();
        if(pw.equals(pwCheck)) {
            x_img.setVisibility(View.INVISIBLE);
            check_img.setVisibility(View.VISIBLE);
        }

        sharedPreferences = getSharedPreferences("ssumeet", Activity.MODE_PRIVATE);
    }

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
        emailAddress = emailAddress + "@soongsil.ac.kr";

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());
        try {
            GMailSender gMailSender = new GMailSender("dle0129@gmail.com", "ehdrhs8615");
            gMailSender.sendMail("SSU MEET 인증메일입니다.", srand, emailAddress);
        } catch (SendFailedException e) {
            Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (MessagingException e) {
            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void register() {
        String email = ((EditText)findViewById(R.id.id)).getText().toString();
        pw = ((EditText)findViewById(R.id.pw)).getText().toString();
        pwCheck = ((EditText)findViewById(R.id.pwCheck)).getText().toString();

        email = email + "@ssu.ac.kr";
        final String id = email;

        String checkNum = ((EditText)findViewById(R.id.checkNum)).getText().toString();

        if(!pw.equals(pwCheck)) {
            startToast("비밀번호가 일치하지 않습니다.");
        }
        else if(!checkNum.equals(srand)){
            startToast("인증번호가 일치하지 않습니다.");
        }
        else if(email.length() == 0) {
            startToast("이메일을 입력해주세요.");
        }
        else {
            mAuth.createUserWithEmailAndPassword(email, pw)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sharedPreferences.edit().putString("user_id", id).commit();
                                final String uid = FirebaseAuth.getInstance().getUid();

                                ProfileModel profileModel = new ProfileModel();
                                profileModel.setUid(uid);
                                profileModel.setUserid(id);

                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                db.collection("users").document(uid)
                                        .set(profileModel)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent  intent = new Intent(RegisterPage.this, MainPage.class);
                                                startActivity(intent);
                                                finish();
                                                Log.d(String.valueOf(R.string.app_name), "DocumentSnapshot added with ID: " + uid);
                                            }
                                        });

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
