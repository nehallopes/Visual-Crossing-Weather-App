package com.nehalmarshallopes.assignment4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nehalmarshallopes.assignment4.databinding.ActivityHourlyListBinding;

import java.util.List;
import java.util.Locale;

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherViewHolder> {

    private final List<HourlyWeather> hourlyWeatherList;
    private final Context context;

    public HourlyWeatherAdapter(Context context, List<HourlyWeather> hourlyWeatherList) {
        this.context = context;
        this.hourlyWeatherList = hourlyWeatherList;
    }

    @NonNull
    @Override
    public HourlyWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityHourlyListBinding binding = ActivityHourlyListBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HourlyWeatherViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyWeatherViewHolder holder, int pos) {

        HourlyWeather weather = hourlyWeatherList.get(pos);

        holder.binding.dayTextView.setText(weather.getDay());
        holder.binding.timeTextView.setText(weather.getTime());
        holder.binding.tempTextView.setText(String.format(Locale.getDefault(), "%d°F", weather.getTemperature()));
        holder.binding.descriptionTextView.setText(weather.getCondition());

        int iconID = context.getResources().getIdentifier(weather.getIcon().replace("-", "_"), "drawable", context.getPackageName());
        if (iconID != 0) {
            holder.binding.iconImageView.setImageResource(iconID);
        } else {
            holder.binding.iconImageView.setImageResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    public int getItemCount() {

        return hourlyWeatherList.size();
    }
}
