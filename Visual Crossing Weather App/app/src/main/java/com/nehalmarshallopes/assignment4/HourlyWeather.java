package com.nehalmarshallopes.assignment4;

public class HourlyWeather {

    private String day;
    private String time;
    private int temperature;
    private String condition;
    private String icon;

    public HourlyWeather(String day, String time, int temperature, String condition, String icon) {
        this.day = day;
        this.time = time;
        this.temperature = temperature;
        this.condition = condition;
        this.icon = icon;
    }

    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    public int getTemperature() {
        return temperature;
    }

    public String getCondition() {
        return condition;
    }

    public String getIcon() {
        return icon;
    }
}
