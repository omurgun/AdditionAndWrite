package com.omurgun.additionandwrite.RegisterAndLogin.WithGoogle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.omurgun.additionandwrite.Manager.HomeMenuActivity;
import com.omurgun.additionandwrite.R;
import com.omurgun.additionandwrite.RegisterAndLogin.LoginMainActivity;


import java.util.HashMap;

public class Google_RegisterActivity extends AppCompatActivity {
    private TextView txtUsername;
    private String username;
    private TextView appName;
    private Button btnUsernameSave;
    private SignInButton signInButton;
    private Button btnLogin;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private int RC_SIGN_IN;
    private FirebaseAuth.AuthStateListener authStateListener;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google__register);
        init();
        translucentStatusBarFlag();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.start();
                Intent intent = new Intent(Google_RegisterActivity.this, LoginMainActivity.class);
                startActivity(intent);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();

            }

        });
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            }
        };
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            getUsername();
            handleSignInResult(task);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        signInButton = findViewById(R.id.login_button);
        btnLogin = findViewById(R.id.btnLogin);
        firebaseFirestore = FirebaseFirestore.getInstance();
        RC_SIGN_IN = 2;
        txtUsername = findViewById(R.id.usernametxt);
        btnUsernameSave = findViewById(R.id.btnUsernameSave);
        appName = findViewById(R.id.appName);
        player = MediaPlayer.create(Google_RegisterActivity.this,R.raw.song);
    }
    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Toast.makeText(Google_RegisterActivity.this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(account);
        } catch (ApiException e) {
            Toast.makeText(Google_RegisterActivity.this, "Signed In Failed", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }
    private void FirebaseGoogleAuth(GoogleSignInAccount accountToken) {

        AuthCredential authCredential = GoogleAuthProvider.getCredential(accountToken.getIdToken(), null);
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(Google_RegisterActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                } else {
                    Toast.makeText(Google_RegisterActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void goHomeMenu() {
        Toast.makeText(Google_RegisterActivity.this, "Your account has been successfully created", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Google_RegisterActivity.this, HomeMenuActivity.class);
        startActivity(intent);
        finish();
    }
    private void getUsername() {
        txtUsername.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.INVISIBLE);
        btnUsernameSave.setVisibility(View.VISIBLE);
        appName.setVisibility(View.INVISIBLE);

        btnUsernameSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = txtUsername.getText().toString();

                if (!username.equals(null)) {
                    System.out.println("username : " + username);
                    UserSaveToDatabase();
                    goHomeMenu();
                } else {
                    Toast.makeText(Google_RegisterActivity.this, "please enter username", Toast.LENGTH_SHORT).show();
                }

            }
        });
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
                Toast.makeText(Google_RegisterActivity.this, "save..", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Google_RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
