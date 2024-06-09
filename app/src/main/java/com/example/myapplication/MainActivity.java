package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

    private static final String API_KEY = "dfb4398b9f339a0fa1e62dafdd95d4fd"; // OpenWeather API Key
    private static final int LOCATION_REFRESH_TIME = 15000; // Location update interval in milliseconds
    private static final int LOCATION_REFRESH_DISTANCE = 500; // Location update distance in meters

    private TextView debuggingTextView, temperatureTv, cityTv, forecastDataTv, feelsLikeTv;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LocationService locationService;

    private double lon = 37.8232036; // Default longitude
    private double lat = 32.96957; // Default latitude
    private int temp = 0; // Default temperature value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        applyWindowInsets();
        bindViews();
        initializeLocationService();
        updateLoginLabel();
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void bindViews() {
        //debuggingTextView = findViewById(R.id.debuggingTextView);
        temperatureTv = findViewById(R.id.tempratureCounter);
        cityTv = findViewById(R.id.cityNameText);
        forecastDataTv = findViewById(R.id.forecastDataTv);
        feelsLikeTv = findViewById(R.id.feelsLikeTv);
    }

    private void initializeLocationService() {
        locationService = new LocationService(this);
        locationService.requestLocationUpdates(location -> {
            lat = location.getLatitude();
            lon = location.getLongitude();
            fetchWeatherData(lat, lon);
            locationService.removeLocationUpdates(); // Single-use location update
        });
    }

    private void fetchWeatherData(double latitude, double longitude) {
        WeatherApiService apiService = OpenWeatherApiRetrofitClient.getClient().create(WeatherApiService.class);
        Call<WeatherData> call = apiService.getCurrentWeatherByCoordinates(latitude, longitude, API_KEY);
        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.isSuccessful()) {
                    WeatherData weatherData = response.body();
                    if (weatherData != null) {
                        updateWeatherUI(weatherData);
                    }
                } else {
                    debuggingTextView.setText("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                debuggingTextView.setText("Failure: " + t.getMessage());
            }
        });
    }

    private void updateWeatherUI(WeatherData weatherData) {
        temp = (int) WeatherUtils.kelvinToCelsius(weatherData.getMain().getFeels_like());
        String feelsLike = String.valueOf(temp);
        String city = weatherData.getName();
        String temperature = String.valueOf((int) WeatherUtils.kelvinToCelsius(weatherData.getMain().getTemp()));
        String forecast = getForecastString(weatherData);

        temperatureTv.setText(temperature);
        feelsLikeTv.setText(feelsLike);
        cityTv.setText(city);
        forecastDataTv.setText(forecast);

        Log.d("WeatherData", weatherData.getWeather().toString());
    }

    private String getForecastString(WeatherData weatherData) {
        StringBuilder forecast = new StringBuilder();

        if (weatherData.getMain() != null) {
            forecast.append(WeatherUtils.getHowColdHumanReadable(WeatherUtils.kelvinToCelsius(weatherData.getMain().getFeels_like())))
                    .append("\n");
        }

        if (weatherData.getWind() != null) {
            forecast.append(WeatherUtils.windScale(weatherData.getWind().getSpeed()))
                    .append("\n");
        }

        return forecast.toString();
    }

    private void updateLoginLabel() {
        TextView loginText = findViewById(R.id.numberLogin);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("logins").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    long loginCount = document.getLong("loginCount");
                    db.collection("logins").document(userId).update("loginCount", loginCount + 1);
                    loginText.setText("Login count: " + (loginCount + 1));
                } else {
                    Map<String, Object> user = new HashMap<>();
                    user.put("loginCount", 1);
                    db.collection("logins").document(userId).set(user);
                    loginText.setText("Login count: 1");
                }
            }
        });
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, Login_Activity.class));
        finish();
    }

    public void fitRec(View view) {
        Intent intent = new Intent(this, FitRecActivity.class);
        intent.putExtra("Temp", temp);
        startActivity(intent);
        finish();
    }
}