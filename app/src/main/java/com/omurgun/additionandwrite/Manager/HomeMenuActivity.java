package com.omurgun.additionandwrite.Manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.omurgun.additionandwrite.Game.GameActivity;
import com.omurgun.additionandwrite.Game.ShowMaxScoresActivity;
import com.omurgun.additionandwrite.R;

public class HomeMenuActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String maxScore;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private Button btnGameStart, btnShowTopList;
    private TextView highScoreText;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_menu);
        translucentStatusBarFlag();
        init();
        getMaxScore();
        btnGameStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.start();
                Intent intent = new Intent(HomeMenuActivity.this, GameActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnShowTopList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.start();
                Intent intent = new Intent(HomeMenuActivity.this, ShowMaxScoresActivity.class);
                startActivity(intent);
            }
        });


    }
    private void init() {
        btnGameStart = findViewById(R.id.btnGameStart);
        highScoreText = findViewById(R.id.highScoreText);
        btnShowTopList = findViewById(R.id.btnShowTopList);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        player = MediaPlayer.create(HomeMenuActivity.this,R.raw.song);

    }
    private void getMaxScore() {
        sharedPreferences = this.getSharedPreferences("com.omurgun.additionandwrite", Context.MODE_PRIVATE);
        maxScore = String.valueOf(sharedPreferences.getInt("maxScore", 0));
        highScoreText.setText("High Score : "+maxScore);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uid = firebaseUser.getUid();
        DocumentReference docRef = firebaseFirestore.collection("Users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String maxScore = document.get("MaxScore").toString();
                        System.out.println("1 \t"+maxScore);
                        highScoreText.setText("High Score : " + maxScore);
                    } else {
                        System.out.println("2");
                    }
                } else {
                    System.out.println("3");
                }
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


