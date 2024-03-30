package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Random;

public class FitRecActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextToSpeech textToSpeech;
    private TextView recommendationTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fit_rec);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        textToSpeech=new TextToSpeech(this,listner->{}); //יצירת משתנה להגדרת הtps
        recommendationTv=findViewById(R.id.RecommendationTv); //הגדרת ההמלצה כמשתנה לפי הטקסט
        recommendationTv.setOnClickListener(view->{

            textToSpeech.speak(recommendationTv.getText().toString(),TextToSpeech.QUEUE_FLUSH,null,null); //הפעלת הדיבור ברגע שלוחצים על המלל בהמלצת הלבוש
        });
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(intent!=null) {
            int recived = extras.getInt("Temp");
            try {
                String range = translateToRange(recived);
                getRecommendation(range);
            }catch (Exception e){
                recommendationTv.setText(e.getMessage());
            }
        }

    }
    public String translateToRange(int number) { //הגדרת הטווח של התאמת הלבוש מ-1 עד 10
        int lowerBound = (number / 10) * 10;
        int upperBound = lowerBound + 10;
        return lowerBound + "-" + upperBound;
    }
    public void getRecommendation(String temp) //פעולה המוציאה פריט לבוש מה- firestore ומציגה אותו כטקסט
    {
        db.collection("clothes").document(temp).get().addOnCompleteListener(task->{
            DocumentSnapshot documentSnapshot= task.getResult();
            if(documentSnapshot.exists())
            {
                Map<String,Object> data = documentSnapshot.getData();
                if(data!=null && !data.isEmpty())
                {
                    Random random = new Random();
                    int randomIndex= random.nextInt(data.size());
                    Object randomValue= data.get(String.valueOf(randomIndex));
                    if(randomValue!=null)
                    {
                        recommendationTv.setText(randomValue.toString());
                    }
                    else
                    {
                        recommendationTv.setText("Error");
                    }

                }
            }
        });

    }
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, Login_Activity.class));
        finish();
    }
    public void backMain(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}