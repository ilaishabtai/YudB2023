package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;
import java.util.Map;

public class FitRecActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextToSpeech textToSpeech;
    private TextView recommendationTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_rec);

        initializeTextToSpeech();
        initializeUI();

        handleIntent(getIntent());
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.UK);
            }
        });
    }

    private void initializeUI() {
        recommendationTv = findViewById(R.id.RecommendationTv);
        recommendationTv.setOnClickListener(view -> textToSpeech.speak(
                recommendationTv.getText().toString(),
                TextToSpeech.QUEUE_FLUSH,
                null,
                null
        ));
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                int received = extras.getInt("Temp");
                try {
                    getRecommendation(String.valueOf(received));
                } catch (Exception e) {
                    recommendationTv.setText(e.getMessage());
                }
            }
        }
    }

    public String translateToRange(int number) {
        int lowerBound = (number / 10) * 10;
        int upperBound = lowerBound + 10;
        return lowerBound + "-" + upperBound;
    }

    public void getRecommendation(String temp) {
        db.collection("clothes").document("0-40").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Map<String, Object> data = documentSnapshot.getData();
                    if (data != null && !data.isEmpty()) {
                        Object value = data.get(temp);
                        if (value != null) {
                            recommendationTv.setText(value.toString());
                        } else {
                            recommendationTv.setText("No recommendation found.");
                        }
                    }
                } else {
                    recommendationTv.setText("No data found.");
                }
            } else {
                recommendationTv.setText("Error fetching data.");
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