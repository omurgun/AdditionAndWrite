package com.omurgun.additionandwrite.Manager;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import com.omurgun.additionandwrite.R;
import com.omurgun.additionandwrite.RegisterAndLogin.WithEmailAndPassword.EAP_RegisterActivity;
import com.omurgun.additionandwrite.RegisterAndLogin.WithFacebook.Facebook_RegisterActivity;
import com.omurgun.additionandwrite.RegisterAndLogin.WithGoogle.Google_RegisterActivity;

public class RegisterManagerActivity extends AppCompatActivity {

    private Button btnEmailAndPassword;
    private Button btnGoogle;
    private Button btnFacebook;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_manager);
        init();
        translucentStatusBarFlag();
        btnEmailAndPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.start();
                Intent intentLogin = new Intent(RegisterManagerActivity.this, EAP_RegisterActivity.class);
                startActivity(intentLogin);
                finish();
            }
        });
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.start();
                Intent intentLogin = new Intent(RegisterManagerActivity.this, Google_RegisterActivity.class);
                startActivity(intentLogin);
                finish();
            }
        });

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.start();
                Intent intentLogin = new Intent(RegisterManagerActivity.this, Facebook_RegisterActivity.class);
                startActivity(intentLogin);
                finish();
            }
        });


    }
    private void init() {
        btnEmailAndPassword = findViewById(R.id.btnEmailAndPassword);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
        player = MediaPlayer.create(RegisterManagerActivity.this,R.raw.song);
    }
    private void translucentStatusBarFlag() {
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


}