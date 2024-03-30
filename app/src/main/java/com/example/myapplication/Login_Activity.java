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
    private FirebaseAuth mAuth; //יצירת משתמש של Firebase על מנת לבצע אימות משתמש במייל וסיסמא
    @Override
    //////////////////////////////////////////////////////////////////////////////////
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance(); //קישור המשתנה למערכת הAuth של Firebase
    }
    //////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onStart() //פעולה הבודקת האם המשתמש כבר רשום - אם כן ישר מעבירה אותו למסך המרכזי
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser(); //יצירת משתנה והגדרתו ככקבל ה-User הנוכחי
        if (currentUser!=null) //במידה ואין משתמש במערכת
        {
            startActivity(new Intent(Login_Activity.this, MainActivity.class)); //הכנסתו ל-Login
        }
    }
    //////////////////////////////////////////////////////////////////////////////////
    public void register(View view) //פעולה המאפשרת הרשמה של משתמש חדש במערכת ורושמת אותו ב-פייר בייס
    {
        EditText emailEditText = findViewById(R.id.editEmailAddress); //הגדרת משתנה שיקבל את ה-Email
        EditText passwordEditText = findViewById(R.id.editTextPassword); //הגדרת משתנה שיקבל את ה-Password
        mAuth.createUserWithEmailAndPassword( //קריאה לפעולה שיוצרת משתמש עם מייל וסיסמא
                emailEditText.getText().toString(),passwordEditText.getText().toString())
        .addOnCompleteListener(Login_Activity.this, new OnCompleteListener<AuthResult>() //במידה ומצליח מעביר אותו כ-Login
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful()) //במידה והפעולה עבדה
                {
                    startActivity(new Intent(Login_Activity.this, MainActivity.class)); //קשר אותו למסך המרכזי של האפליקציה
                }
                else
                {
                    Toast.makeText(Login_Activity.this, "Register Failed", Toast.LENGTH_LONG).show(); //הצג הודעה שההרשמה נכשלה
                }
            }
        }
        );
    };
    //////////////////////////////////////////////////////////////////////////////////
    public void login(View view) //פעולה המאפשרת כניסה למשתמש קיים במערכת של ה- פייר בייס
        {
        EditText emailEditText = findViewById(R.id.editEmailAddress); //יצירת משתנה שיכיל את ה-Email
        EditText passwordEditText = findViewById(R.id.editTextPassword); //יצירת משתנה שיכיל את ה-Password
        try
        {
            mAuth.signInWithEmailAndPassword( //קורא לפעולה שמכניסה את המייל והסיסמא ובודק אם המשתמש כבר רשום
                            emailEditText.getText().toString(), passwordEditText.getText().toString())
                    .addOnCompleteListener(Login_Activity.this, new OnCompleteListener<AuthResult>() //בודק אם עבד
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful()) //במידה והמשתמש כבר רשום
                            {
                                startActivity(new Intent(Login_Activity.this, MainActivity.class)); //מעביר אותו למסך המרכזי של האפליקציה
                            }
                            else
                            {
                                Toast.makeText(Login_Activity.this, "Login Failed", Toast.LENGTH_LONG).show(); //מציג הודעה שההתחברות נכשלה
                            }
                        }
                    }
                    );
        }
        catch (Exception e){Toast.makeText(Login_Activity.this, "Error", Toast.LENGTH_LONG).show();}
    }
}
