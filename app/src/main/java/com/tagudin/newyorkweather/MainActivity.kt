package com.tagudin.newyorkweather

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var tvCityName: TextView
    private lateinit var tvShortDesc: TextView
    private lateinit var tvLongDesc: TextView
    private lateinit var ivWeatherIcon: ImageView

    private val apiKey = "62ef6c2910bc2ca48a40007d6a4fc6db"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        val rootContent = findViewById<ViewGroup>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(rootContent) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.setPadding(0, statusBarHeight, 0, 0)
            view.setBackgroundColor(Color.parseColor("#004037"))
            insets
        }

        tvCityName = findViewById(R.id.tvCityName)
        tvShortDesc = findViewById(R.id.tvShortDesc)
        tvLongDesc = findViewById(R.id.tvLongDesc)
        ivWeatherIcon = findViewById(R.id.ivWeatherIcon)

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        val weatherService = retrofit.create(OpenWeatherMapService::class.java)

        weatherService.getCurrentWeather("New York", apiKey)
            .enqueue(object : Callback<OpenWeatherResponse> {
                override fun onResponse(
                    call: Call<OpenWeatherResponse>,
                    response: Response<OpenWeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val weatherData = response.body()
                        val currentWeather = weatherData?.weather?.firstOrNull()

                        if (weatherData != null && currentWeather != null) {
                            tvCityName.text = weatherData.cityName ?: "New York"
                            tvShortDesc.text = "Clouds"
                            tvLongDesc.text = "broken clouds"

                            val iconUrl = "https://openweathermap.org/img/wn/04d@2x.png"
                            Glide.with(this@MainActivity)
                                .load(iconUrl)
                                .into(ivWeatherIcon)
                        } else {
                            val emptyMsg = "No weather data found in response"
                            tvShortDesc.text = emptyMsg
                            Log.w("WeatherApp", emptyMsg)
                        }
                    } else {
                        val errorMsg = "Server error: ${response.code()} ${response.message()}"
                        tvShortDesc.text = errorMsg
                        Log.e("WeatherApp", errorMsg)
                        Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<OpenWeatherResponse>, t: Throwable) {
                    val failureMsg = "Network failure: ${t.localizedMessage}"
                    tvShortDesc.text = failureMsg
                    Log.e("WeatherApp", failureMsg, t)
                    Toast.makeText(this@MainActivity, failureMsg, Toast.LENGTH_LONG).show()
                }
            })
    }
}