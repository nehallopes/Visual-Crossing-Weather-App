package com.nehalmarshallopes.assignment4;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.nehalmarshallopes.assignment4.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocation;
    private static final int LOCATION_REQUEST = 111;
    private ActivityMainBinding binding;
    private RequestQueue queue;
    private static final String API_KEY = "API_KEY";
    private static String locationString = "Chicago, IL";
    private List<HourlyWeather> hourlyWeatherList = new ArrayList<>();
    private HourlyWeatherAdapter hourlyWeatherAdapter;
    private ConnectivityManager connectivityManager;
    private boolean Fahrenheit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.main.setOnRefreshListener(() -> {
            downloadWeather(locationString);
            determineLocation();

            binding.main.setRefreshing(false);
        });

        fusedLocation = LocationServices.getFusedLocationProviderClient(this);

        queue = Volley.newRequestQueue(this);

        hourlyWeatherAdapter = new HourlyWeatherAdapter(this, hourlyWeatherList);
        binding.hourlyRecyclerView.setAdapter(hourlyWeatherAdapter);
        binding.hourlyRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        connectivityManager = getSystemService(ConnectivityManager.class);

        Network currentNetwork = connectivityManager.getActiveNetwork();
        if (currentNetwork == null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("No Internet Connection")
                    .setIcon(R.drawable.alert)
                    .setMessage("This App requires an internet connection to function properly. Please check your connection and try again.")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
        }
        else {
            determineLocation();
        }

        binding.dailyImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, DailyForecastActivity.class);
            intent.putExtra("location", locationString);
            startActivity(intent);
        });

        binding.locationImage.setOnClickListener(v -> enterLocation());

        binding.targetImage.setOnClickListener(v -> {
            determineLocation();
        });

        binding.unitesImage.setOnClickListener(v -> {
            if (Fahrenheit) {
                binding.unitesImage.setImageResource(R.drawable.units_c);
                Fahrenheit = false;

            } else{
                binding.unitesImage.setImageResource(R.drawable.units_f);
                Fahrenheit = true;
            }
        });

        binding.mapImage.setOnClickListener(v -> {

            Uri mapUri = Uri.parse("geo:" + locationString);

            Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                makeErrorAlert("No Application found that handles ACTION_VIEW (geo) intents");
            }
        });
    }

    private void makeErrorAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setTitle("App-resolving error");
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }

    private void determineLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }

        fusedLocation.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        locationString = String.format(Locale.getDefault(), "%.5f,%.5f", location.getLatitude(), location.getLongitude());
                        downloadWeather(locationString);
                    } else {
                        Toast.makeText(this, "Unable to retrieve location", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error retrieving location", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                determineLocation();
            } else {
                binding.resolvedAddressTextView.setText("Location permission denied, using default location.");
            }
        }
    }

    private String getPlaceName(Location loc) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        StringBuilder sb = new StringBuilder();
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                String addr = addresses.get(0).getAddressLine(0);
                sb.append(String.format(
                        Locale.getDefault(),
                        "%s%n%nProvider: %s%n%n%.5f, %.5f",
                        addr, loc.getProvider(),
                        loc.getLatitude(), loc.getLongitude()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private void downloadWeather(String locationString) {
        binding.progressBar.setVisibility(View.VISIBLE);

        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority("weather.visualcrossing.com")
                .appendPath("VisualCrossingWebServices")
                .appendPath("rest")
                .appendPath("services")
                .appendPath("timeline")
                .appendPath(locationString)
                .appendQueryParameter("key", API_KEY)
                .build();

        String urlToUse = uri.toString();

        Response.Listener<JSONObject> listener = this::parseWeather;

        Response.ErrorListener error = error1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Location Error")
                    .setIcon(R.drawable.alert)
                    .setMessage("The specified location '" + locationString + "' could not be resolved. Please try a different location.")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
            binding.progressBar.setVisibility(View.GONE);
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlToUse, null, listener, error);
        queue.add(jsonObjectRequest);
    }

    private void parseWeather(JSONObject response) {
        try {
            double latitude = response.getDouble("latitude");
            double longitude = response.getDouble("longitude");
            String resolvedAddress = response.getString("resolvedAddress");

            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm a", Locale.getDefault());
            String currentDateTime = sdf.format(new Date());
            String displayText = String.format("%s, %s", resolvedAddress, currentDateTime);
            binding.resolvedAddressTextView.setText(displayText);

            JSONObject currentConditions = response.getJSONObject("currentConditions");
            int currentTemp = currentConditions.getInt("temp");
            int feelsLike = currentConditions.getInt("feelslike");
            int humidity = currentConditions.getInt("humidity");
            int  windGust = currentConditions.getInt("windgust");
            int windSpeed = currentConditions.getInt("windspeed");
            int windDir = currentConditions.getInt("winddir");
            double visibility = currentConditions.getDouble("visibility");
            int cloudCover = currentConditions.getInt("cloudcover");
            int uvIndex = currentConditions.getInt("uvindex");
            String conditions = currentConditions.getString("conditions");
            String icon = currentConditions.getString("icon");
            long sunriseEpoch = currentConditions.getLong("sunriseEpoch");
            long sunsetEpoch = currentConditions.getLong("sunsetEpoch");

            if (!Fahrenheit) {
                currentTemp = (int) ((currentTemp - 32) * 5.0 / 9.0);
                feelsLike = (int) ((feelsLike - 32) * 5.0 / 9.0);
            }

            binding.currentTempTextView.setText(String.format(Locale.getDefault(), "%d°F", currentTemp));
            binding.currentFeelsLikeTextView.setText(String.format(Locale.getDefault(), "Feels Like %d°F", feelsLike));
            binding.currentHumidityTextView.setText(String.format(Locale.getDefault(), "Humidity: %d%%", humidity));
            binding.currentVisibilityTextView.setText(String.format(Locale.getDefault(), "Visibility: %.1f mi", visibility));
            binding.currentUVIndexTextView.setText(String.format(Locale.getDefault(), "UV Index: %d", uvIndex));
            binding.currentCloudCoverTextView.setText(String.format(Locale.getDefault(), "%s (%d%% clouds)", conditions, cloudCover));

            String windDirection = getDirection(windDir);
            binding.currentWindDirTextView.setText(String.format(Locale.getDefault(), "Winds: %s at %d mph gusting to %d mph", windDirection, windSpeed, windGust));

            SimpleDateFormat sdf1 = new SimpleDateFormat("h:mm a", Locale.getDefault());

            binding.currentSunriseEpochTextView.setText(String.format(Locale.getDefault(), "Sunrise: %s", sdf1.format(new Date(sunriseEpoch * 1000))));
            binding.currentSunsetEpochTextView.setText(String.format(Locale.getDefault(), "Sunset: %s", sdf1.format(new Date(sunsetEpoch * 1000))));

            String newIcon = icon.replace("-", "_");
            int iconID = getResources().getIdentifier(newIcon, "drawable", getPackageName());
            if (iconID != 0) {
                binding.currentImage.setImageResource(iconID);
            } else {
                binding.currentImage.setImageResource(R.drawable.alert);
            }

            long currentTimeEpoch = System.currentTimeMillis() / 1000;

            hourlyWeatherList.clear();

            TreeMap<String, Double> tempPoints = new TreeMap<>();

            JSONArray daysArray = response.getJSONArray("days");
            for (int i = 0; i < daysArray.length(); i++) {
                JSONObject day = daysArray.getJSONObject(i);

                if (i < 3) {
                    JSONArray hoursArray = day.getJSONArray("hours");
                    for (int j = 0; j < hoursArray.length(); j++) {
                        JSONObject hour = hoursArray.getJSONObject(j);
                        String hourTime = hour.getString("datetime");
                        long hourDatetimeEpoch = hour.getLong("datetimeEpoch");
                        if (hourDatetimeEpoch >= currentTimeEpoch) {
                            double hourTemp = hour.getDouble("temp");
                            String hourConditions = hour.getString("conditions");
                            String hourIcon = hour.getString("icon");

                            tempPoints.put(hourTime, hourTemp);

                            HourlyWeather hourlyWeather = new HourlyWeather(
                                    dayFormater(hourDatetimeEpoch),
                                    timeFormater(hourDatetimeEpoch),
                                    (int) hourTemp,
                                    hourConditions,
                                    hourIcon.replace("-", "_")
                            );
                            hourlyWeatherList.add(hourlyWeather);
                        }
                    }
                }
            }

            ChartMaker chartMaker = new ChartMaker(this, binding);
            chartMaker.makeChart(tempPoints, System.currentTimeMillis());

            if (Fahrenheit) {
                ColorMaker.setColorGradient(binding.getRoot(), currentTemp, "F");
            }
            else {
                ColorMaker.setColorGradient(binding.getRoot(), currentTemp, "C");
            }

            binding.progressBar.setVisibility(View.GONE);

            hourlyWeatherAdapter.notifyDataSetChanged();

        } catch (JSONException e) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Weather Data Error")
                    .setIcon(R.drawable.alert)
                    .setMessage("There was an error retrieving the weather data. Please try again later.")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();

            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private String getDirection(double degrees) {

        if (degrees >= 337.5 || degrees < 22.5)
            return "N";
        if (degrees >= 22.5 && degrees < 67.5)
            return "NE";
        if (degrees >= 67.5 && degrees < 112.5)
            return "E";
        if (degrees >= 112.5 && degrees < 157.5)
            return "SE";
        if (degrees >= 157.5 && degrees < 202.5)
            return "S";
        if (degrees >= 202.5 && degrees < 247.5)
            return "SW";
        if (degrees >= 247.5 && degrees < 292.5)
            return "W";
        if (degrees >= 292.5 && degrees < 337.5)
            return "NW";
        return "X";
    }

    private String dayFormater(long epoch) {

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());

        Calendar today = Calendar.getInstance();
        today.setTime(new Date());

        int todayYear = today.get(Calendar.YEAR);
        int todayDayOfYear = today.get(Calendar.DAY_OF_YEAR);

        Calendar dateToFormat = Calendar.getInstance();
        dateToFormat.setTime(new Date(epoch * 1000));

        int formatYear = dateToFormat.get(Calendar.YEAR);
        int formatDayOfYear = dateToFormat.get(Calendar.DAY_OF_YEAR);

        if (todayYear == formatYear && todayDayOfYear == formatDayOfYear) {
            return "Today";
        } else {
            return sdf.format(new Date(epoch * 1000));
        }
    }

    private String timeFormater(long epoch) {

        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return sdf.format(new Date(epoch * 1000));
    }

    private void enterLocation() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter a Location");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_dialog, null);
        EditText locationInput = dialogView.findViewById(R.id.locationEditText);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String userLocation = locationInput.getText().toString().trim();
            if (!userLocation.isEmpty()) {
                locationString = userLocation;
                downloadWeather(locationString);
            } else {
                Toast.makeText(this, "Please enter a valid location", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void displayChartTemp(float time, float tempVal) {

        SimpleDateFormat sdf =
                new SimpleDateFormat("h a", Locale.US);
        Date d = new Date((long) time);
        binding.charTempTextView.setText(
                String.format(Locale.getDefault(),
                        "%s, %.0f°",
                        sdf.format(d), tempVal));
        binding.charTempTextView.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                runOnUiThread(() -> binding.charTempTextView.setVisibility(View.GONE));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}