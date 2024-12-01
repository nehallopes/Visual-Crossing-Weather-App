package com.nehalmarshallopes.assignment4;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nehalmarshallopes.assignment4.databinding.ActivityDailyForecastListBinding;

import java.util.List;
import java.util.Locale;

public class DailyForecastAdapter extends RecyclerView.Adapter<DailyForecastViewHolder> {

    private final List<DailyForecast> dailyForecastList;

    public DailyForecastAdapter(List<DailyForecast> dailyForecastList) {
        this.dailyForecastList = dailyForecastList;
    }

    @NonNull
    @Override
    public DailyForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ActivityDailyForecastListBinding binding = ActivityDailyForecastListBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new DailyForecastViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyForecastViewHolder holder, int pos) {

        DailyForecast forecast = dailyForecastList.get(pos);

        holder.dateTextView.setText(forecast.getDate());
        holder.tempHighLowTextView.setText(String.format(Locale.getDefault(), "%.0f°F / %.0f°F", forecast.getTempMax(), forecast.getTempMin()));
        holder.descriptionTextView.setText(forecast.getDescription());
        holder.uvIndexTextView.setText(String.format(Locale.getDefault(), "UV Index: %d", forecast.getUvIndex()));
        holder.precipProbabilityTextView.setText(String.format(Locale.getDefault(), "(%d%% precip.)", forecast.getPrecipProb()));
        holder.weatherIconImageView.setImageResource(forecast.getIconResource());
        holder.morningTempTextView.setText(String.format(Locale.getDefault(), "%.0f°F", forecast.getMorningTemp()));
        holder.afternoonTempTextView.setText(String.format(Locale.getDefault(), "%.0f°F", forecast.getAfternoonTemp()));
        holder.eveningTempTextView.setText(String.format(Locale.getDefault(), "%.0f°F", forecast.getEveningTemp()));
        holder.nightTempTextView.setText(String.format(Locale.getDefault(), "%.0f°F", forecast.getNightTemp()));

        ColorMaker.setColorGradient(holder.itemView, forecast.getTempMax(), "F");
    }

    @Override
    public int getItemCount() {
        return dailyForecastList.size();
    }
}
