package com.example.weatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.R
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@SuppressLint("UnrememberedMutableState")
@Composable
fun MapPage(viewModel: MainViewModel) {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(Color.Gray)
            .wrapContentSize(Alignment.Center)
    ) {
        val recife = LatLng(-8.05, -34.9)
        val caruaru = LatLng(-8.27, -35.98)
        val joaopessoa = LatLng(-7.12, -34.84)
        val camPosState = rememberCameraPositionState ()

        val context = LocalContext.current
        val hasLocationPermission by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED
            )
        }

        GoogleMap (modifier = Modifier.fillMaxSize(), onMapClick = {
            viewModel.add( it) },
            cameraPositionState = camPosState, properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
            uiSettings = MapUiSettings(myLocationButtonEnabled = true)
            ) {
            viewModel.cities.forEach {
                if (it.location != null) {
                    LaunchedEffect(it.name) {
                        if (it.weather == null) {
                            viewModel.loadWeather(it.name)
                        }
                    }
                    LaunchedEffect(it.weather) {
                        if (it.weather != null && it.weather!!.bitmap == null) {
                            viewModel.loadBitmap(it.name)
                        }
                    }
                    val image = it.weather?.bitmap ?:
                    getDrawable(context, R.drawable.loading)!!
                        .toBitmap()
                    val marker = BitmapDescriptorFactory
                        .fromBitmap(image.scale(120,120))
                    Marker( state = MarkerState(position = it.location),
                        title = it.name,
                        icon = marker,
                        snippet = it.weather?.desc?:"Carregando..."
                    )
                }

            }


            Marker(
                state = MarkerState(position = recife),
                title = "Recife",
                snippet = "Marcador em Recife",
                icon = BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_BLUE)
            )

            Marker(
                state = MarkerState(position = caruaru),
                title = "caruaru",
                snippet = "Marcador em caruaru",
                icon = BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_RED)
            )

            Marker(
                state = MarkerState(position = joaopessoa),
                title = "joaopessoa",
                snippet = "Marcador em joaopessoa",
                icon = BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_VIOLET)
            )



        }
    }
}