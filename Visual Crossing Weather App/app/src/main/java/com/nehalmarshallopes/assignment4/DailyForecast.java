package com.nehalmarshallopes.assignment4;

public class DailyForecast {

    private final String date;
    private final Double tempMin;
    private final Double tempMax;
    private final String description;
    private final int uvIndex;
    private final int precipProb;
    private final int iconResource;
    private final Double morningTemp;
    private final Double afternoonTemp;
    private final Double eveningTemp;
    private final Double nightTemp;

    public DailyForecast(String date, Double tempMin, Double tempMax, String description, int uvIndex, int precipProb, int iconResource, Double morningTemp, Double afternoonTemp, Double eveningTemp, Double nightTemp) {

        this.date = date;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.description = description;
        this.uvIndex = uvIndex;
        this.precipProb = precipProb;
        this.iconResource = iconResource;
        this.morningTemp = morningTemp;
        this.afternoonTemp = afternoonTemp;
        this.eveningTemp = eveningTemp;
        this.nightTemp = nightTemp;
    }

    public String getDate() {
        return date;
    }

    public Double getTempMin() {
        return tempMin;
    }

    public Double getTempMax() {
        return tempMax;
    }

    public String getDescription() {
        return description;
    }

    public int getUvIndex() {
        return uvIndex;
    }

    public int getPrecipProb() {
        return precipProb;
    }

    public int getIconResource() {
        return iconResource;
    }

    public Double getMorningTemp() {
        return morningTemp;
    }

    public Double getAfternoonTemp() {
        return afternoonTemp;
    }

    public Double getEveningTemp() {
        return eveningTemp;
    }

    public Double getNightTemp() {
        return nightTemp;
    }
}
