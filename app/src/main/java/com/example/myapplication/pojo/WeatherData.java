package com.example.myapplication.pojo;

import java.util.List;

public class WeatherData {
    private List<Weather> weather;
    private MainData main;
    private int visibility;
    private WindData wind;
    private RainData rain;
    private CloudsData clouds;
    private String name;

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    public MainData getMain() {
        return main;
    }

    public void setMain(MainData main) {
        this.main = main;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public WindData getWind() {
        return wind;
    }

    public void setWind(WindData wind) {
        this.wind = wind;
    }

    public RainData getRain() {
        return rain;
    }

    public void setRain(RainData rain) {
        this.rain = rain;
    }

    public CloudsData getClouds() {
        return clouds;
    }

    public void setClouds(CloudsData clouds) {
        this.clouds = clouds;
    }

    public WeatherData(List<Weather> weather, MainData main, int visibility, WindData wind, RainData rain, CloudsData clouds, String name) {
        this.weather = weather;
        this.main = main;
        this.visibility = visibility;
        this.wind = wind;
        this.rain = rain;
        this.clouds = clouds;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
