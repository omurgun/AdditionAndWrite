package com.omurgun.additionandwrite.Manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
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
    private Button btnGameStart, btnShowTopList,btnEarnHeart;
    private TextView highScoreText;
    private MediaPlayer player;
    private int heart;
    private AdView adView;
    private InterstitialAd mInterstitialAd;
    private RewardedAd rewardedAd;
    private ImageView heart_1,heart_2,heart_3;
    private ImageView heart_1_free,heart_2_free,heart_3_free;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_menu);
        translucentStatusBarFlag();
        init();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        bannerAds();
        InterstitialAds();
        rewardedAds();
        getMaxScore();

        getHeart();
        btnGameStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(heart>0)
                {
                    player.start();
                    Intent intent = new Intent(HomeMenuActivity.this, GameActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(HomeMenuActivity.this, "your hearts are free", Toast.LENGTH_SHORT).show();
                }

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
        btnEarnHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(heart<=3)
                {
                    seeRewardedAds();

                }
                else
                {
                        Toast.makeText(HomeMenuActivity.this, "your heart is full", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void addHeart() {
        btnEarnHeart.setVisibility(View.INVISIBLE);
        heart+=1;
        heartShow(heart);
        sharedPreferences.edit().putInt("heart", heart).apply();
    }

    private void init() {
        btnGameStart = findViewById(R.id.btnGameStart);
        highScoreText = findViewById(R.id.highScoreText);
        btnShowTopList = findViewById(R.id.btnShowTopList);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        player = MediaPlayer.create(HomeMenuActivity.this,R.raw.song);
        adView = findViewById(R.id.adView);
        heart_1 = findViewById(R.id.heart_1_image);
        heart_2 = findViewById(R.id.heart_2_image);
        heart_3 = findViewById(R.id.heart_3_image);
        heart_1_free = findViewById(R.id.heart_1_image_free);
        heart_2_free = findViewById(R.id.heart_2_image_free);
        heart_3_free = findViewById(R.id.heart_3_image_free);
        btnEarnHeart = findViewById(R.id.btnEarnHeart);


    }
    private void  getHeart() {
        heart = sharedPreferences.getInt("heart", 3);
        heartShow(heart);
        System.out.println("getHeart : "+heart);
        if(heart<=2)
        {
            btnEarnHeart.setVisibility(View.VISIBLE);
        }
        if(heart == 0)
        {

            seeRewardedAds();
        }
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
    private void bannerAds() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
    private void InterstitialAds() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-9651456806715960/8149495405");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }
    private void openInterstitialAds() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            System.out.println("olmadı ");
        }
    }
    private void seeRewardedAds() {
        if (rewardedAd.isLoaded()) {
            System.out.println("load olmuş");
            RewardedAdCallback adCallback = new RewardedAdCallback() {
                @Override
                public void onRewardedAdOpened() {
                    // Ad opened.
                    addHeart();
                }

                @Override
                public void onRewardedAdClosed() {
                    // Ad closed.
                }

                @Override
                public void onUserEarnedReward(@NonNull RewardItem reward) {
                    // User earned reward.
                }

                @Override
                public void onRewardedAdFailedToShow(int errorCode) {
                    // Ad failed to display.
                }
            };
            rewardedAd.show(HomeMenuActivity.this, adCallback);
        } else {
            Log.d("TAG", "The rewarded ad wasn't loaded yet.");
        }
    }
    private void rewardedAds() {
        rewardedAd = new RewardedAd(this, "ca-app-pub-9651456806715960/6836413731");

        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
    }
    private void heartShow(int heart) {
        System.out.println("heart show : "+ heart);
        if(heart == 0)
        {
            heart_1.setVisibility(View.INVISIBLE);
            heart_2.setVisibility(View.INVISIBLE);
            heart_3.setVisibility(View.INVISIBLE);
            heart_1_free.setVisibility(View.VISIBLE);
            heart_2_free.setVisibility(View.VISIBLE);
            heart_3_free.setVisibility(View.VISIBLE);
        }
        else if(heart == 1)
        {
            heart_1.setVisibility(View.VISIBLE);
            heart_2.setVisibility(View.INVISIBLE);
            heart_3.setVisibility(View.INVISIBLE);
            heart_1_free.setVisibility(View.INVISIBLE);
            heart_2_free.setVisibility(View.VISIBLE);
            heart_3_free.setVisibility(View.VISIBLE);
        }
        else if(heart == 2)
        {
            heart_1.setVisibility(View.VISIBLE);
            heart_2.setVisibility(View.VISIBLE);
            heart_3.setVisibility(View.INVISIBLE);
            heart_1_free.setVisibility(View.INVISIBLE);
            heart_2_free.setVisibility(View.INVISIBLE);
            heart_3_free.setVisibility(View.VISIBLE);
        }
        else if(heart == 3)
        {
            heart_1.setVisibility(View.VISIBLE);
            heart_2.setVisibility(View.VISIBLE);
            heart_3.setVisibility(View.VISIBLE);
            heart_1_free.setVisibility(View.INVISIBLE);
            heart_2_free.setVisibility(View.INVISIBLE);
            heart_3_free.setVisibility(View.INVISIBLE);
        }
    }
}


