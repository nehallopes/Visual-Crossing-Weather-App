package com.nehalmarshallopes.assignment4;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nehalmarshallopes.assignment4.databinding.ActivityDailyForecastBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DailyForecastActivity extends AppCompatActivity {

    private static final String TAG = "DailyForecastActivity";
    private static final String API_KEY = "API_KEY";
    private ActivityDailyForecastBinding binding;
    private DailyForecastAdapter dailyForecastAdapter;
    private List<DailyForecast> dailyForecastList;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDailyForecastBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String location = getIntent().getStringExtra("location");
        binding.titleTextView.setText(location + " 15-Day Forecast");

        dailyForecastList = new ArrayList<>();
        dailyForecastAdapter = new DailyForecastAdapter(dailyForecastList);
        binding.dailyRecyclerView.setAdapter(dailyForecastAdapter);
        binding.dailyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        queue = Volley.newRequestQueue(this);

        DownloadDailyWeather(location);
    }

    private void DownloadDailyWeather(String location) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https")
                .authority("weather.visualcrossing.com")
                .appendPath("VisualCrossingWebServices")
                .appendPath("rest")
                .appendPath("services")
                .appendPath("timeline")
                .appendPath(location)
                .appendQueryParameter("unitGroup", "us")
                .appendQueryParameter("key", API_KEY);

        String url = uriBuilder.build().toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        parseDailyWeather(response);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Parsing error: ", e);
                    }
                },
                error -> Log.e(TAG, "Error fetching data: ", error));

        queue.add(jsonObjectRequest);
    }

    private void parseDailyWeather(JSONObject response) throws JSONException {
        dailyForecastList.clear();

        JSONArray daysArray = response.getJSONArray("days");
        for (int i = 0; i < daysArray.length(); i++) {
            JSONObject day = daysArray.getJSONObject(i);

            String date = day.getString("datetime");
            try {
                SimpleDateFormat ip = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat op = new SimpleDateFormat("EEEE, MM/dd", Locale.getDefault());
                date = op.format(ip.parse(date));
            } catch (Exception e) {
                e.printStackTrace();
            }

            double tempMax = day.getDouble("tempmax");
            double tempMin = day.getDouble("tempmin");
            String description = day.getString("description");
            int uvIndex = day.getInt("uvindex");
            int precipProbability = day.getInt("precipprob");

            JSONArray hoursArray = day.getJSONArray("hours");
            double morningTemp = hoursArray.getJSONObject(8).getDouble("temp");
            double afternoonTemp = hoursArray.getJSONObject(13).getDouble("temp");
            double eveningTemp = hoursArray.getJSONObject(17).getDouble("temp");
            double nightTemp = hoursArray.getJSONObject(23).getDouble("temp");

            String icon = day.getString("icon").replace("-", "_");
            int iconResource = getResources().getIdentifier(icon, "drawable", getPackageName());
            if (iconResource == 0) {
                iconResource = R.drawable.alert;
            }

            DailyForecast forecast = new DailyForecast(
                    date,
                    tempMin,
                    tempMax,
                    description,
                    uvIndex,
                    precipProbability,
                    iconResource,
                    morningTemp,
                    afternoonTemp,
                    eveningTemp,
                    nightTemp
            );
            dailyForecastList.add(forecast);
        }
        dailyForecastAdapter.notifyDataSetChanged();
    }
}
