package com.omurgun.additionandwrite.RegisterAndLogin.WithFacebook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.omurgun.additionandwrite.Manager.HomeMenuActivity;
import com.omurgun.additionandwrite.R;
import com.omurgun.additionandwrite.RegisterAndLogin.LoginMainActivity;
import java.util.HashMap;


public class Facebook_RegisterActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private TextView txtUsername;
    private  String username;
    private TextView appName;
    private Button btnUsernameSave;
    private LoginButton loginButton;
    private Button btnLogin;
    private static final String TAG = "FacebookAuthentication";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth.AuthStateListener authStateListener;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook__register);
        init();
        translucentStatusBarFlag();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.start();
                Intent intent = new Intent(Facebook_RegisterActivity.this, LoginMainActivity.class);
                startActivity(intent);
            }
        });
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                player.start();
                Log.d(TAG, "onSuccess" + loginResult);
                handleFacebookToken(loginResult.getAccessToken());
                getUsername();


            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError" + error);

            }

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void init() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        btnLogin = findViewById(R.id.btnLogin);
        loginButton.setReadPermissions("email", "public_profile");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        txtUsername = findViewById(R.id.usernametxt);
        btnUsernameSave = findViewById(R.id.btnUsernameSave);
        appName = findViewById(R.id.appName);
        player = MediaPlayer.create(Facebook_RegisterActivity.this,R.raw.song);
    }
    private void goHomeMenu() {
        Toast.makeText(Facebook_RegisterActivity.this, "Your account has been successfully created", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Facebook_RegisterActivity.this, HomeMenuActivity.class);
        startActivity(intent);
        finish();
    }
    private void handleFacebookToken(AccessToken accessToken) {
        Log.d(TAG,"handleFacebookToken"+accessToken);
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());

        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Log.d(TAG,"sign in with credential : successful");
                }
                else
                {
                    Log.d(TAG,"sign in with credential : failture"+task.getException());
                    Toast.makeText(Facebook_RegisterActivity.this, "Login Failed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void getUsername() {
        txtUsername.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.INVISIBLE);
        btnUsernameSave.setVisibility(View.VISIBLE);
        appName.setVisibility(View.INVISIBLE);
        btnLogin.setVisibility(View.INVISIBLE);

       btnUsernameSave.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                username = txtUsername.getText().toString();

               if(!username.equals(null))
               {
                   System.out.println("username : "+username);
                   UserSaveToDatabase();
                   goHomeMenu();
               }
               else
               {
                   Toast.makeText(Facebook_RegisterActivity.this, "please enter username", Toast.LENGTH_SHORT).show();
               }

           }
       });


    }
    @Override
    protected void onStart() {
        super.onStart();
        //firebaseAuth.addAuthStateListener(authStateListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener != null)
        {
            //firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
    private void UserSaveToDatabase() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
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
                Toast.makeText(Facebook_RegisterActivity.this, "save..", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Facebook_RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void translucentStatusBarFlag() {
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


}
