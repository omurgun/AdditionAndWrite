package com.omurgun.additionandwrite.RegisterAndLogin.WithEmailAndPassword;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.omurgun.additionandwrite.Manager.HomeMenuActivity;
import com.omurgun.additionandwrite.R;
import com.omurgun.additionandwrite.RegisterAndLogin.LoginMainActivity;
import java.util.HashMap;

public class EAP_RegisterActivity extends AppCompatActivity {

    private EditText txtPassword,txtEmail,txtUsername;
    private Button btnRegister,btnLogin;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private String username;
    private MediaPlayer player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_a_p__register);
        init();
        translucentStatusBarFlag();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.start();
                createNewAccount();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.start();
                Intent intent = new Intent(EAP_RegisterActivity.this, LoginMainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void init(){

        txtPassword = findViewById(R.id.passwordtxt);
        txtEmail = findViewById(R.id.emailtxt);
        txtUsername = findViewById(R.id.usernametxt);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        player = MediaPlayer.create(EAP_RegisterActivity.this,R.raw.song);


    }
    private void createNewAccount() {

        final String password = txtPassword.getText().toString();
        final String email = txtEmail.getText().toString();
        username = txtUsername.getText().toString();
        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this,"Username Cannot Be Empty!",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Email Cannot Be Empty!",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Password Cannot Be Empty!",Toast.LENGTH_LONG).show();
        }
        else
        {
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    UserSaveToDatabase();
                    goHomeMenu();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String message = e.getLocalizedMessage().toString();
                    Toast.makeText(EAP_RegisterActivity.this,"Registration failed"+message,Toast.LENGTH_LONG).show();
                }
            });
        }

    }
    private void UserSaveToDatabase() {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        String userEmail = firebaseUser.getEmail();
        String uid = firebaseUser.getUid();

        HashMap<String,Object> maxMap = new HashMap<>();
        maxMap.put("Email",userEmail);
        maxMap.put("Username",username);
        maxMap.put("Uid",uid);
        maxMap.put("MaxScore",0);
        maxMap.put("date", FieldValue.serverTimestamp());
        firebaseFirestore.collection("Users")
                .document(uid).set(maxMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("kaydedildi ");
                Toast.makeText(EAP_RegisterActivity.this, "save..", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EAP_RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void goHomeMenu() {
        Toast.makeText(EAP_RegisterActivity.this,"Your account has been successfully created",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(EAP_RegisterActivity.this, HomeMenuActivity.class);
        startActivity(intent);
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