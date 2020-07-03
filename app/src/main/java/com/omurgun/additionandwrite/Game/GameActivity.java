package com.omurgun.additionandwrite.Game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.omurgun.additionandwrite.Manager.HomeMenuActivity;
import com.omurgun.additionandwrite.R;
import java.util.HashMap;
import java.util.Random;


public class GameActivity extends AppCompatActivity implements ExampleDialogListener {
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private int maxScore=0;
    private TextView scoreText;
    private TextView timeText;
    private TextView levelText;

    private ImageView imageView;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private ImageView imageView5;
    private ImageView imageView6;
    private ImageView imageView7;
    private ImageView imageView8;
    private ImageView imageView9;

    private ImageView[] imageArray;
    private Handler handler;
    private Runnable runnable;

    private int level;
    private int delay;
    private int time;
    private int sum;
    private int score;
    private int prev;

    private MediaPlayer playerInGame;
    private int pauseMusicPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        init();
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        sharedPreferences = this.getSharedPreferences("com.omurgun.additionandwrite", Context.MODE_PRIVATE);
        maxScore = sharedPreferences.getInt("maxScore", 0);
        musicPlay();
        hideImages();
        score = 0;
        game();

    }
    private void game() {
        new CountDownTimer((time*level)+700,1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                timeText.setText("Time: " + millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {

                timeText.setText("Time is up");
                handler.removeCallbacks(runnable);
                for (ImageView image : imageArray) {
                    image.setVisibility(View.INVISIBLE);
                }
                openDialog();
                System.out.println("bitti");
            }
        }.start();

    }
    private void init() {
        prev = -1;
        level = 1;
        delay = 2100;
        time = 5000;
        sum = 0;
        score = 0;
        timeText = findViewById(R.id.timeText);
        scoreText = findViewById(R.id.scoreText);
        levelText = findViewById(R.id.levelText);
        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView5 = findViewById(R.id.imageView5);
        imageView6 = findViewById(R.id.imageView6);
        imageView7 = findViewById(R.id.imageView7);
        imageView8 = findViewById(R.id.imageView8);
        imageView9 = findViewById(R.id.imageView9);
        imageArray = new ImageView[] {imageView, imageView2, imageView3, imageView4, imageView5, imageView6, imageView7, imageView8, imageView9};
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        playerInGame = MediaPlayer.create(GameActivity.this,R.raw.songgame);
    }
    private void increaseScore () {
        score = score +level*5;
        scoreText.setText("Score: " + score);
    }
    private void increaseLevel () {
        level++;
        levelText.setText("Level: " + level);
    }
    private void saveMaxScore() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uid = firebaseUser.getUid();
        String max;
        if (score > maxScore){
            maxScore = score;
             max = String.valueOf(maxScore);
            sharedPreferences.edit().putInt("maxScore", maxScore).apply();
            HashMap<String,Object> maxMap = new HashMap<>();
            maxMap.put("MaxScore",max);
            firebaseFirestore.collection("Users")
                    .document(uid).update(maxMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("kaydedildi ");
                    Toast.makeText(GameActivity.this, "save..", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(GameActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private void hideImages() {



        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {

                for (ImageView image : imageArray) {
                    image.setVisibility(View.INVISIBLE);
                }
                Random random = new Random();

                int i = random.nextInt(9);
                while (prev ==i)
                {
                    i = random.nextInt(9);

                }
                prev=i;
                imageArray[i].setVisibility(View.VISIBLE);
                sum+=i+1;




                System.out.println("toplam : "+ sum);
                System.out.println("delay : \t"+(delay-(level*100)));
                handler.postDelayed(this,delay-(level*100));

            }
        };
        handler.post(runnable);
    }
    private void openDialog() {
        ExampleDialog exampleDialog = new ExampleDialog();
        try {

            exampleDialog.show(getSupportFragmentManager(),"example dialog");
            exampleDialog.setCancelable(false);
        }
        catch (Exception e)
        {
            System.out.println("catchhh");
            finish();
        }

    }
    @Override
    public void applyTexts(String result) {
        int sumResult = Integer.parseInt(result);
        System.out.println("sonuc : "+sumResult);
        if(sumResult==sum)
        {
            System.out.println("dogru ");
            sum=0;
            increaseLevel();
            increaseScore();
            hideImages();
            game();
        }
        else
        {
            saveMaxScore();
            Toast.makeText(GameActivity.this, "True result : "+sum, Toast.LENGTH_SHORT).show();
            AlertDialog.Builder alert = new AlertDialog.Builder(GameActivity.this);
            alert.setTitle("Restart?");
            alert.setMessage("Are you sure to restart game?");
            alert.setCancelable(false);
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Toast.makeText(GameActivity.this, "MAX PUAN : "+score, Toast.LENGTH_SHORT).show();
                    //restart
                    Intent intent = getIntent();
                    musicStop();
                    startActivity(intent);
                    finish();

                }
            });

            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //Toast.makeText(GameActivity.this, "Game Over!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(GameActivity.this, "MAX PUAN : "+score, Toast.LENGTH_SHORT).show();
                    goLogin();
                }
            });
            alert.show();

        }
    }
    private void goLogin() {
        Intent intentLogin = new Intent(GameActivity.this, HomeMenuActivity.class);
        startActivity(intentLogin);
        musicStop();
        finish();

    }

    private void musicPlay() {
        if(playerInGame == null)
        {
            playerInGame.start();
        }
        else if(!playerInGame.isPlaying())
        {
            playerInGame.seekTo(pauseMusicPosition);
            playerInGame.start();
        }
    }
    private void musicPause() {
        if(playerInGame != null)
        {
            playerInGame.pause();
            pauseMusicPosition = playerInGame.getCurrentPosition();
        }
    }
    private void musicStop() {
        if(playerInGame != null)
        {
            playerInGame.stop();
            playerInGame = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        musicPause();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        musicPlay();
    }

}
