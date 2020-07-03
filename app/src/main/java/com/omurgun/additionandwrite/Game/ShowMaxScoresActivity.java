package com.omurgun.additionandwrite.Game;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.omurgun.additionandwrite.Model.User;
import com.omurgun.additionandwrite.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class ShowMaxScoresActivity extends AppCompatActivity {

    private ListView listView;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_max_scores);
        init();
        translucentStatusBarFlag();
        getDataFromFireStore();
    }
    private void init() {
        listView = findViewById(R.id.listView);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
    private void getDataFromFireStore() {
        final ArrayList<User> usersMaxScoreSort = new ArrayList<>();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uid = firebaseUser.getUid();
       firebaseFirestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
           @Override
           public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null)
                {
                    Toast.makeText(ShowMaxScoresActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_SHORT);
                }
                if(queryDocumentSnapshots !=null)
                {
                    for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments())
                    {
                        Map<String,Object> data = snapshot.getData();
                        String username = data.get("Username").toString();
                        String maxScore = data.get("MaxScore").toString();
                        usersMaxScoreSort.add(new User(username,maxScore));

                    }
                }
                if(!usersMaxScoreSort.isEmpty())
                {
                    Collections.sort(usersMaxScoreSort);
                }
                int i=0;
               String userNames[] = new String[usersMaxScoreSort.size()];
               String scores[] = new String[usersMaxScoreSort.size()];
               String numbers[] = new String[usersMaxScoreSort.size()];
                for (User user : usersMaxScoreSort)
                {
                    i++;
                    System.out.println(user.getUsername() +"\t"+user.getMaxScore());

                    userNames[i-1] =user.getUsername();
                    scores[i-1] = user.getMaxScore();
                    numbers[i-1] = String.valueOf(i);

                }
                showGetData(userNames,scores,numbers);

           }
       });
    }
    private void showGetData(String username[], String score[], String i[]) {
        MyAdapter adapter = new MyAdapter(this, username, score ,i);
        listView.setAdapter(adapter);
    }
    private class MyAdapter extends ArrayAdapter<String> {

        Context context;
        String username[];
        String score[];
        String number[];

        MyAdapter (Context c, String username[], String score[], String i[]) {
            super(c, R.layout.row, R.id.txt_username,username);
            this.context = c;
            this.username = username;
            this.score = score;
            this.number = i;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent, false);
            Button btnNumber = row.findViewById(R.id.btn_number);
            TextView myUsername = row.findViewById(R.id.txt_username);
            TextView myScore = row.findViewById(R.id.txt_score);

            btnNumber.setText(number[position]);
            myUsername.setText(username[position]);
            myScore.setText(score[position]);
            return row;
        }
    }
    private void translucentStatusBarFlag() {
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


}