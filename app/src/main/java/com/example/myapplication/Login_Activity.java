package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_Activity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onStart() { //פעולה הבודקת האם המשתמש כבר רשום - אם כן ישר מעבירה אותו למסך המרכזי
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            startActivity(new Intent(Login_Activity.this, MainActivity.class));
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void register(View view) { //פעולה המאפשרת הרשמה של משתמש חדש במערכת ורושמת אותו ב-פייר בייס
        EditText emailEditText = findViewById(R.id.editEmailAddress);
        EditText passwordEditText = findViewById(R.id.editTextPassword);
        mAuth.signInWithEmailAndPassword(
                emailEditText.getText().toString(),passwordEditText.getText().toString())
        .addOnCompleteListener(Login_Activity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(Login_Activity.this, MainActivity.class));
                } else {
                    Toast.makeText(Login_Activity.this, "Register Failed", Toast.LENGTH_LONG).show();
                }
            }

            ////////////////////////////////////////////////////////////////////////////////////////////////////////
            public void login(View view) { //פעולה המאפשרת כניסה למשתמש קיים במערכת של ה- פייר בייס
                EditText emailEditText = findViewById(R.id.editEmailAddress);
                EditText passwordEditText = findViewById(R.id.editTextPassword);
                mAuth.signInWithEmailAndPassword(
                                emailEditText.getText().toString(), passwordEditText.getText().toString())
                        .addOnCompleteListener(Login_Activity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(Login_Activity.this, MainActivity.class));
                                } else {
                                    Toast.makeText(Login_Activity.this, "Login Failed", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    };
}
