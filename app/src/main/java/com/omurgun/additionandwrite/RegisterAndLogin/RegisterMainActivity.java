package com.omurgun.additionandwrite.RegisterAndLogin;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.omurgun.additionandwrite.Manager.HomeMenuActivity;
import com.omurgun.additionandwrite.Manager.RegisterManagerActivity;
import com.omurgun.additionandwrite.R;

public class RegisterMainActivity extends AppCompatActivity {
    private Button btnLogin, btnRegister;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_main);
        init();
        translucentStatusBarFlag();
        if(checkCurrentUser()) {
            goHomeMenu();
        }
        else {
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    player.start();
                    Intent intentLogin = new Intent(RegisterMainActivity.this,LoginMainActivity.class);
                    startActivity(intentLogin);
                    finish();
                }
            });


            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    player.start();
                    Intent intentRegister = new Intent(RegisterMainActivity.this, RegisterManagerActivity.class);
                    startActivity(intentRegister);
                    finish();
                }
            });
        }
    }
    private void init(){
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        player = MediaPlayer.create(RegisterMainActivity.this,R.raw.song);

    }
    private boolean checkCurrentUser() {

        boolean result = false;
        if(firebaseUser!= null)
        {
            result =true;
        }

        return result;
    }
    private void goHomeMenu() {
        Intent intentLogin = new Intent(RegisterMainActivity.this, HomeMenuActivity.class);
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