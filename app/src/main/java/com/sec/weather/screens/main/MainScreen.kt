package com.sec.weather.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sec.weather.R
import com.sec.weather.data.*
import com.sec.weather.utils.Constants
import com.sec.weather.utils.IconUtils
import com.sec.weather.utils.TimeUtils
import com.sec.weather.widgets.WeatherAppBar
import com.sec.weather.widgets.WeatherDetailRow
import com.sec.weather.widgets.WeatherStateImage

@Composable
fun MainScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val weatherNowData = produceState<DataOrException<WeatherNow, Exception>>(
        initialValue = DataOrException(loading = true),
        producer = {
            value = mainViewModel.getWeatherNow(location = Constants.DEFAULT_CITY_LOCATION)
        }).value
    val astronomySunData = produceState<DataOrException<AstronomySun, Exception>>(
        initialValue = DataOrException(loading = true),
        producer = {
            value = mainViewModel.getAstronomySun(location = Constants.DEFAULT_CITY_LOCATION)
        }).value
    val weather3dData = produceState<DataOrException<Weather3d, Exception>>(
        initialValue = DataOrException(loading = true),
        producer = {
            value = mainViewModel.getWeather3d(location = Constants.DEFAULT_CITY_LOCATION)
        }).value
    if (weatherNowData.loading == true && astronomySunData.loading == true && weather3dData.loading == true) {
        CircularProgressIndicator()
    } else if (weatherNowData.data != null && astronomySunData.data != null && weather3dData.data != null) {
        MainScaffold(
            navController = navController,
            weatherNowData = weatherNowData.data,
            astronomySunData = astronomySunData.data,
            weather3dData = weather3dData.data
        )
    }
}

@Composable
fun MainScaffold(
    navController: NavHostController,
    weatherNowData: WeatherNow,
    astronomySunData: AstronomySun,
    weather3dData: Weather3d
) {
    Scaffold(topBar = {
        WeatherAppBar(
            title = "浦东天气",
            onAddActionClicked = {

            },
            elevation = 5.dp
        )
    }) {
        MainContent(
            weatherNow = weatherNowData,
            astronomySun = astronomySunData,
            weather3d = weather3dData
        )
    }
}

@Composable
fun MainContent(weatherNow: WeatherNow, astronomySun: AstronomySun, weather3d: Weather3d) {
    Column(
        Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = TimeUtils.formatDate(weatherNow.now.obsTime),
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSecondary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(6.dp)
        )
        Surface(
            modifier = Modifier
                .padding(4.dp)
                .size(200.dp),
            shape = CircleShape,
            color = Color(0xFFFFC400)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherStateImage(imageUrl = IconUtils.getIcon(weatherNow.now.icon))
                Text(
                    text = "${weatherNow.now.temp}º",
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = weatherNow.now.text,
                    fontStyle = FontStyle.Italic
                )
            }
        }
        HumidityWindPressureRow(weatherNow)
        Divider()
        SunsetSunRiseRow(astronomySun)
        Text(
            text = "3天预报",
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Bold
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            color = Color(0xFFEEF1EF),
            shape = RoundedCornerShape(size = 14.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(2.dp),
                contentPadding = PaddingValues(1.dp)
            ) {
                items(items = weather3d.daily) { item: Daily ->
                    WeatherDetailRow(daily = item)
                }
            }
        }
    }
}

@Composable
fun SunsetSunRiseRow(astronomySun: AstronomySun) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            Image(
                painter = painterResource(id = R.drawable.sunrise),
                contentDescription = "sunrise",
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = TimeUtils.formatDateHHmm(astronomySun.sunrise),
                style = MaterialTheme.typography.caption
            )
        }
        Row {
            Text(
                text = TimeUtils.formatDateHHmm(astronomySun.sunset),
                style = MaterialTheme.typography.caption
            )
            Image(
                painter = painterResource(id = R.drawable.sunset),
                contentDescription = "sunset",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
fun HumidityWindPressureRow(weatherNow: WeatherNow) {
    Row(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.humidity),
                contentDescription = "humidity icon",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "相对湿度${weatherNow.now.humidity}%",
                style = MaterialTheme.typography.caption
            )
        }
        Row() {
            Icon(
                painter = painterResource(id = R.drawable.pressure),
                contentDescription = "pressure icon",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "大气压${weatherNow.now.pressure}hPa",
                style = MaterialTheme.typography.caption
            )
        }
        Row() {
            Icon(
                painter = painterResource(id = R.drawable.wind),
                contentDescription = "wind icon",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "风速${weatherNow.now.windSpeed}km/h",
                style = MaterialTheme.typography.caption
            )
        }
    }
}