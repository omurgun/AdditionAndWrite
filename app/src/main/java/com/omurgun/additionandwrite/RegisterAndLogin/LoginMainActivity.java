package com.omurgun.additionandwrite.RegisterAndLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.omurgun.additionandwrite.Manager.HomeMenuActivity;
import com.omurgun.additionandwrite.Manager.RegisterManagerActivity;
import com.omurgun.additionandwrite.R;

public class LoginMainActivity extends AppCompatActivity {

    private Button btnLogin,btnRegister;
    private EditText txtEmail,txtPassword;
    private FirebaseAuth firebaseAuth;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
        init();
        translucentStatusBarFlag();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.start();
                loginUser();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.start();
                Intent intent = new Intent(LoginMainActivity.this, RegisterManagerActivity.class);
                startActivity(intent);
            }
        });
    }
    private void init() {
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        txtEmail = findViewById(R.id.emailtxt);
        txtPassword = findViewById(R.id.passwordtxt);
        firebaseAuth = FirebaseAuth.getInstance();
        player = MediaPlayer.create(LoginMainActivity.this,R.raw.song);


    }
    private void loginUser() {

        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Email Cannot Be Empty!", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Password Cannot Be Empty!", Toast.LENGTH_LONG).show();
        }
        else
        {
            btnLogin.setEnabled(false);
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    goLogin();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(LoginMainActivity.this, "Login Failed!", Toast.LENGTH_LONG).show();
                    btnLogin.setEnabled(true);
                }
            });
        }
    }
    private void goLogin() {
        Intent intentLogin = new Intent(LoginMainActivity.this, HomeMenuActivity.class);
        startActivity(intentLogin);
        finish();
    }
    private void translucentStatusBarFlag() {
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}