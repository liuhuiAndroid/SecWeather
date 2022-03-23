package com.sec.weather.repository

import com.sec.weather.data.AstronomySun
import com.sec.weather.data.DataOrException
import com.sec.weather.data.Weather3d
import com.sec.weather.data.WeatherNow
import com.sec.weather.network.WeatherApi
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val api: WeatherApi) {

    suspend fun getWeatherNow(location: String): DataOrException<WeatherNow, Exception> {
        val response = try {
            api.weatherNow(location = location)
        } catch (e: Exception) {
            return DataOrException(exception = e)
        }
        return DataOrException(data = response)
    }

    suspend fun getAstronomySun(location: String): DataOrException<AstronomySun, Exception> {
        val response = try {
            api.astronomySun(location = location)
        } catch (e: Exception) {
            return DataOrException(exception = e)
        }
        return DataOrException(data = response)
    }

    suspend fun getWeather3d(location: String): DataOrException<Weather3d, Exception> {
        val response = try {
            api.weather3d(location = location)
        } catch (e: Exception) {
            return DataOrException(exception = e)
        }
        return DataOrException(data = response)
    }
}