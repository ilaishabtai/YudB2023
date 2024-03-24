package com.example.myapplication.utils;

public class WeatherUtils
{
    public static double kelvinToCelsius(double tempKelvin) {
        return tempKelvin - 273.15;
    }

    public static String getHowColdHumanReadable(double tempCelsius) {
        String returnString;
        if (tempCelsius > 40) {
            returnString = "Extremely hot";
        } else if (tempCelsius > 30) {
            returnString = "Hot";
        } else if (tempCelsius > 20) {
            returnString = "Very nice";
        } else if (tempCelsius > 10){
            returnString = "Cold";
        } else {
            returnString = "Freezing";
        }

        return returnString;

    }

    // Convert humidity to human-readable scale
    public static String humidityScale(int humidity) {
        if (humidity <= 30) {
            return "Not humid";
        } else if (humidity <= 60) {
            return "Moderately humid";
        } else {
            return "Very humid";
        }
    }


    // Convert visibility to human-readable scale
    public static String visibilityScale(int visibility) {
        if (visibility >= 10000) {
            return "Very visible";
        } else if (visibility >= 5000) {
            return "Moderately visible";
        } else {
            return "Low visibility";
        }
    }

    // Convert rain amount to human-readable scale
    public static String rainScale(double rainAmount) {
        if (rainAmount > 1) {
            return "Heavy rain";
        } else if (rainAmount > 0) {
            return "Light rain";
        } else {
            return "No rain";
        }
    }

    // Convert cloudiness to human-readable scale
    public static String cloudinessScale(int cloudiness) {
        if (cloudiness >= 75) {
            return "Very cloudy";
        } else if (cloudiness >= 50) {
            return "Partly cloudy";
        } else {
            return "Mostly sunny";
        }
    }

    // Convert wind speed to human-readable scale
    public static String windScale(double windSpeed) {
        if (windSpeed >= 10) {
            return "Very windy";
        } else if (windSpeed >= 5) {
            return "Moderately windy";
        } else {
            return "Not very windy";
        }
    }
}
