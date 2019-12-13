package com.example.ssumeet;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class RegisterPage extends AppCompatActivity implements View.OnClickListener, Dialog.OnCancelListener{
    public FirebaseAuth mAuth;
    String srand;
    EditText pw, pwCheck;
    ImageView setImage;

    LayoutInflater dialog;
    View dialogLayout;
    Dialog authDialog;

    TextView time_counter;
    EditText emailAuth_number, checkNum;
    Button emailAuth_btn;
    CountDownTimer countDownTimer;
    final int MILLISINFUTURE = 300 * 1000;
    final int COUNT_DOWN_INTERVAL = 1000;

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
        Button back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(onClickListener);
        Button check_btn = findViewById(R.id.check_btn);
        check_btn.setOnClickListener(onClickListener);
        Button register_btn = findViewById(R.id.register_btn);
        register_btn.setOnClickListener(onClickListener);

        pw = (EditText)findViewById(R.id.pw);
        pwCheck = (EditText)findViewById(R.id.pwCheck);
        setImage = (ImageView)findViewById(R.id.setImage);

        pwCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(pw.getText().toString().equals(pwCheck.getText().toString())) {
                    setImage.setImageResource(R.drawable.back);
                } else {
                    setImage.setImageResource(R.drawable.back);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
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
        emailAddress = emailAddress + "@soongsil.ac.kr";

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());
        try {
            GMailSender gMailSender = new GMailSender("dle0129@gmail.com", "ehdrhs8615");
            gMailSender.sendMail("SSU MEET 인증메일입니다.", srand, emailAddress);
            dialog = LayoutInflater.from(this);
            dialogLayout = dialog.inflate(R.layout.auth_dialog, null);
            authDialog = new Dialog(this);
            authDialog.setContentView(dialogLayout);
            authDialog.setCanceledOnTouchOutside(false);
            authDialog.setOnCancelListener(this);
            authDialog.show();
            countDownTimer();
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
        email = email + "@ssu.ac.kr";
        String checkNum = ((EditText)findViewById(R.id.checkNum)).getText().toString();
        startToast(checkNum);
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
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                startToast("회원가입에 성공하였습니다.");
                                finish();
                            } else {
                                if (task.getException() != null) {
                                    startToast(task.getException().toString());
                                }
                            }
                        }
                    });
        }
    }

    public void countDownTimer() {
        time_counter = (TextView) dialogLayout.findViewById(R.id.emailAuth_time_counter);
        emailAuth_number = (EditText) dialogLayout.findViewById(R.id.emailAuth_number);
        emailAuth_btn = (Button) dialogLayout.findViewById(R.id.emailAuth_btn);

        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                long emailAuthCount = millisUntilFinished / 1000;

                if ((emailAuthCount - ((emailAuthCount / 60) * 60)) >= 10) {
                    time_counter.setText((emailAuthCount / 60) + " : " + (emailAuthCount - ((emailAuthCount / 60) * 60)));
                } else {
                    time_counter.setText((emailAuthCount / 60) + " : 0" + (emailAuthCount - ((emailAuthCount / 60) * 60)));
                }
            }
            @Override
            public void onFinish() {
                authDialog.cancel();
            }
        }.start();
        emailAuth_btn.setOnClickListener(this);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        countDownTimer.cancel();
    }

    @Override
    public void onClick(View v) {
        emailAuth_btn = (Button) dialogLayout.findViewById(R.id.emailAuth_btn);
        checkNum = (EditText) findViewById(R.id.checkNum);
        emailAuth_number = (EditText)dialogLayout.findViewById(R.id.emailAuth_number);
        switch (v.getId()) {
            case R.id.emailAuth_btn:
                checkNum.setText(emailAuth_number.getText().toString());
                authDialog.dismiss();
                break;
        }
    }
}
