package com.nehalmarshallopes.assignment4;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import java.util.Locale;

public class ColorMaker {

    public static void setColorGradient(View view, double tempIn, String unitLetter) {

        double temp = tempIn;

        if (unitLetter.equalsIgnoreCase("C")) {
            temp = (tempIn * 9 / 5) + 32;
        }

        int[] colors = getTemperatureColor((int) temp);
        String startColorString = String.format(Locale.getDefault(), "#FF%02x%02x%02x", colors[0], colors[1], colors[2]);
        int startColor = Color.parseColor(startColorString);
        String endColorString = String.format(Locale.getDefault(), "#99%02x%02x%02x", colors[0], colors[1], colors[2]);
        int endColor = Color.parseColor(endColorString);

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{startColor, endColor});

        view.setBackground(gd);
    }

    private static int[] getTemperatureColor(int temperature) {

        int[] rgb = new int[3];

        if (temperature < 40) {
            rgb[0] = 0;
            rgb[1] = 0;
            rgb[2] = 40 + temperature * 3;
        } else if (temperature <= 81) {
            rgb[1] = (int) (temperature * 1.5);
            rgb[0] = rgb[1] / 2;
            rgb[2] = rgb[0] / 2;
        } else {
            rgb[0] = 40 + temperature;
            rgb[1] = 0;
            rgb[2] = 0;
        }
        return rgb;
    }
}

