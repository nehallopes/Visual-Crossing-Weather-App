package com.nehalmarshallopes.assignment4;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nehalmarshallopes.assignment4.databinding.ActivityDailyForecastListBinding;

public class DailyForecastViewHolder extends RecyclerView.ViewHolder {

    public TextView dateTextView;
    public TextView tempHighLowTextView;
    public TextView descriptionTextView;
    public TextView uvIndexTextView;
    public TextView precipProbabilityTextView;
    public ImageView weatherIconImageView;
    public TextView morningTempTextView;
    public TextView afternoonTempTextView;
    public TextView eveningTempTextView;
    public TextView nightTempTextView;

    public DailyForecastViewHolder(@NonNull ActivityDailyForecastListBinding binding) {

        super(binding.getRoot());

        dateTextView = binding.textView;
        tempHighLowTextView = binding.highLowTextView;
        descriptionTextView = binding.descTextView;
        uvIndexTextView = binding.UVTextView;
        precipProbabilityTextView = binding.probabilityTextView;
        weatherIconImageView = binding.imageView;
        morningTempTextView = binding.mTempTextView;
        afternoonTempTextView = binding.aTempTextView;
        eveningTempTextView = binding.eTempTextView;
        nightTempTextView = binding.nTempTextView;
    }
}
