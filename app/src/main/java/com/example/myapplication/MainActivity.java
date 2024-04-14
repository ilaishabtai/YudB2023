package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.pojo.WeatherData;
import com.example.myapplication.services.LocationService;
import com.example.myapplication.services.OpenWeatherApiRetrofitClient;
import com.example.myapplication.services.WeatherApiService;
import com.example.myapplication.utils.WeatherUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    //    String API_KEY = BuildConfig.OPENWEATHER_API_KEY;
    String API_KEY = "dfb4398b9f339a0fa1e62dafdd95d4fd"; //מפתח api לשימוש במידע על מזג האוויר
    TextView debuggingTextView;
    FirebaseFirestore db = FirebaseFirestore.getInstance(); //יצירת משתנה שמקשר אותנו ל-Firestore
    TextView temperatureTv, cityTv, forecastDataTv, feelsLikeTv;
    int LOCATION_REFRESH_TIME = 15000; // עדכון המיקום כל 15 שניות
    int LOCATION_REFRESH_DISTANCE = 500; // עדכון המיקום כל 500 מטר

    private LocationService locationService; //הגדרת משתמש שישמש למיקום
    double lon = 37.8232036; //ערך לדוגמא עד להגדרת המשתנה לפי מיקום
    double lat = 32.96957; //ערך לדוגמא עד להגדרת המשתנה לפי מיקום

    int Temp = 0; //ערך לדוגמא לטמפ'
    @Override
    //////////////////////////////////////////////////////////////////////////////////
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        }
    );
        bindViews();
        locationService = new LocationService(this); //הגדרת המיקום
        locationService.requestLocationUpdates(location -> //שימוש במיקום לפי קווי אורך ורוחב
        {
            lat = location.getLatitude(); //קו רוחב - הגדרה
            lon = location.getLongitude(); //קו אורך - הגדרה
       //שימוש ב-api key על מנת להציג את התחזית
       WeatherApiService apiService = OpenWeatherApiRetrofitClient.getClient().create(WeatherApiService.class);
       Call<WeatherData> call = apiService.getCurrentWeatherByCoordinates(lat, lon, API_KEY);
       call.enqueue(new Callback<WeatherData>()
       {
           @Override
           public void onResponse(Call<WeatherData> call, Response<WeatherData> response)
           {
               if (response.isSuccessful())
               {
                  WeatherData weatherResponse = response.body();
                  if (weatherResponse != null)
                  {
                      Temp=((int) WeatherUtils.kelvinToCelsius(weatherResponse.getMain().getFeels_like()));
                      // מידע על מזג האוויר מוצג כאן:
                      String feels_like = String.valueOf(((int) WeatherUtils.kelvinToCelsius(weatherResponse.getMain().getFeels_like())));
                      String city = weatherResponse.getName();
                      String temp = String.valueOf((int) WeatherUtils.kelvinToCelsius(weatherResponse.getMain().getTemp()));
                      String forecastString = getForecastString(weatherResponse);
                      temperatureTv.setText(temp);
                      feelsLikeTv.setText(feels_like);
                      cityTv.setText(city);
                      forecastDataTv.setText(forecastString);
                      Log.d("WeatherData", weatherResponse.getWeather().toString());
                  }
               }
               else
               {
                   debuggingTextView.setText(response.message() + ": " + response.body() + ": " + response.code());
               }
           }
                @Override
                public void onFailure(Call<WeatherData> call, Throwable t)
                {

                }
            }
            );
            locationService.removeLocationUpdates(); //הסרת עדכוני מיקום - שיהיה חד פעמי לשימוש
        }
        );
        updateLoginLabel();
            }

    private void updateLoginLabel() {
        TextView loginText=findViewById(R.id.numberLogin);
        String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("logins").document(userId).get().addOnCompleteListener(task->{
            if(task.isSuccessful())
            {
                DocumentSnapshot Document=task.getResult();
                if(Document.exists())
                {
                    long LoginCount=Document.getLong("loginCount");
                    db.collection("logins").document(userId).update("loginCount",LoginCount+1);
                    loginText.setText(loginText.getText()+" "+LoginCount);
                }
                else
                {
                    Map<String,Object> user=new HashMap<>();
                    user.put("loginCount",1);
                    db.collection("logins").document(userId).set(user);
                    loginText.setText(loginText.getText()+" "+1);
                }
            }
        });


    }

    //////////////////////////////////////////////////////////////////////////////////
    private String getForecastString(WeatherData weatherResponse) //הצגת מידע על התחזית
    {
        StringBuilder responseString = new StringBuilder();

        if (weatherResponse.getMain() != null) //כל עוד יש מידע בתחזית
        {
            responseString.append(WeatherUtils.getHowColdHumanReadable(WeatherUtils.kelvinToCelsius(weatherResponse.getMain().getFeels_like())));
            responseString.append("\n");
        }

        if (weatherResponse.getWind() != null) //כל עוד יש מידע לגבי רוחות
        {
            responseString.append(WeatherUtils.windScale(weatherResponse.getWind().getSpeed()));
            responseString.append("\n");
        }

        return responseString.toString();
    }
    //////////////////////////////////////////////////////////////////////////////////
    private void bindViews()
    {
        temperatureTv = findViewById(R.id.tempratureCounter); //הגדרת מס' המעלות
        cityTv = findViewById(R.id.cityNameText); //הגדרת העיר - לפי המיקום
        forecastDataTv = findViewById(R.id.forecastDataTv); //הגדרת התחזית
        feelsLikeTv = findViewById(R.id.feelsLikeTv); //הגדרה איך המעלות מרגישות בפועל
    }
    //////////////////////////////////////////////////////////////////////////////////
    public void logout(View view) //מעבר לעמוד הכניסה
    {
       FirebaseAuth.getInstance().signOut();
       startActivity(new Intent(this, Login_Activity.class));
       finish();
    }
    //////////////////////////////////////////////////////////////////////////////////
    public void fitRec(View view) //מעבר לעמוד התאמת הלבוש
    {
        Intent intent= new Intent(this,FitRecActivity.class);
        intent.putExtra("Temp",Temp);
        startActivity(intent);
        finish();
    }
}