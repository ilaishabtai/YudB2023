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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    //    String API_KEY = BuildConfig.OPENWEATHER_API_KEY;
    String API_KEY = "dfb4398b9f339a0fa1e62dafdd95d4fd";
    TextView debuggingTextView;
    TextView temperatureTv, cityTv, forecastDataTv, feelsLikeTv;
    int LOCATION_REFRESH_TIME = 15000; // 15 seconds to update
    int LOCATION_REFRESH_DISTANCE = 500; // 500 meters to update

    private LocationService locationService;
    double lon = 37.8232036;
    double lat = 32.96957;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindViews();

        locationService = new LocationService(this);
        locationService.requestLocationUpdates(location -> {
            lat = location.getLatitude();
            lon = location.getLongitude();
       /**
       * Create the API service with the Retrofit Client and the WeatherApiService
       */
       WeatherApiService apiService = OpenWeatherApiRetrofitClient.getClient().create(WeatherApiService.class);
       Call<WeatherData> call = apiService.getCurrentWeatherByCoordinates(lat, lon, API_KEY);
       call.enqueue(new Callback<WeatherData>() {
           @Override
           public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
               if (response.isSuccessful()) {
                  WeatherData weatherResponse = response.body();
                  if (weatherResponse != null) {
                      // Handle weather data here
                      String feels_like = String.valueOf(((int) WeatherUtils.kelvinToCelsius(weatherResponse.getMain().getFeels_like())));
                      String city = weatherResponse.getName();
                      String temp = String.valueOf((int) WeatherUtils.kelvinToCelsius(weatherResponse.getMain().getTemp()));
                      String forecastString = getForecastString(weatherResponse);
                      temperatureTv.setText(temp);
                      feelsLikeTv.setText("Feels like: " + feels_like);
                      cityTv.setText(city);
                      forecastDataTv.setText(forecastString);
                      Log.d("WeatherData", weatherResponse.getWeather().toString());
                  }
               } else {
                   debuggingTextView.setText(response.message() + ": " + response.body() + ": " + response.code());

               }
           }

                @Override
                public void onFailure(Call<WeatherData> call, Throwable t) {

                }
            });






            locationService.removeLocationUpdates();
        });

            }

    private String getForecastString(WeatherData weatherResponse) {
        StringBuilder responseString = new StringBuilder();

        if (weatherResponse.getMain() != null) {
            responseString.append(WeatherUtils.getHowColdHumanReadable(WeatherUtils.kelvinToCelsius(weatherResponse.getMain().getFeels_like())));
            responseString.append("\n");
        }

        if (weatherResponse.getWind() != null) {
            responseString.append(WeatherUtils.windScale(weatherResponse.getWind().getSpeed()));
            responseString.append("\n");
        }

        return responseString.toString();
    }

    private void bindViews() {
        temperatureTv = findViewById(R.id.tempratureCounter);
        cityTv = findViewById(R.id.cityNameText);
        forecastDataTv = findViewById(R.id.forecastDataTv);
        feelsLikeTv = findViewById(R.id.feelsLikeTv);
    }
    public void logout(View view) {
       FirebaseAuth.getInstance().signOut();
       startActivity(new Intent(this, Login_Activity.class));
       finish();
    }
}