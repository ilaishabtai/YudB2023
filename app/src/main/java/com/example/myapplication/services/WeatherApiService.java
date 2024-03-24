package com.example.myapplication.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.myapplication.pojo.WeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService  {

    // Example query: GET https://api.openweathermap.org/data/2.5/weather?lat=31?lon=32?appid=appid
    @GET("weather")
    Call<WeatherData> getCurrentWeatherByCoordinates(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("appid") String apiKey
    );

    // Example query:
    @GET("weather")
    Call<WeatherData> getCurrentWeatherByCityAndCountry(
            @Query("q") String cityCountry, // city,country
            @Query("appid") String apiKey
    );

}