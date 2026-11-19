package com.tagudin.newyorkweather

import com.squareup.moshi.Json

data class OpenWeatherResponse(
    @Json(name = "name") val cityName: String,
    @Json(name = "weather") val weather: List<WeatherDescription>
)

data class WeatherDescription(
    @Json(name = "main") val shortDescription: String,
    @Json(name = "description") val longDescription: String,
    @Json(name = "icon") val iconCode: String
)