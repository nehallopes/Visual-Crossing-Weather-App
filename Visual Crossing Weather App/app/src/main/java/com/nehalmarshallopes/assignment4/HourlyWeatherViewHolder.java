package com.nehalmarshallopes.assignment4;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nehalmarshallopes.assignment4.databinding.ActivityHourlyListBinding;

public class HourlyWeatherViewHolder extends RecyclerView.ViewHolder {

    public final ActivityHourlyListBinding binding;

    public HourlyWeatherViewHolder(@NonNull ActivityHourlyListBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
