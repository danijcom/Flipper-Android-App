package com.flipperdevices.debug.countrydetect.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.debug.countrydetect.viewmodel.CountryDetectViewModel

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ComposableCountryDetect() {
    val countryViewModel = viewModel<CountryDetectViewModel>()
    val countryInfo by countryViewModel.getCountryInfo().collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Страна симкарты: ${countryInfo.isoBySim}",
            fontSize = 26.sp
        )
        Text(
            "Страна сети: ${countryInfo.isoByNetwork}",
            fontSize = 26.sp
        )
        Text(
            "В роуминге ли: ${countryInfo.isRoaming}",
            fontSize = 26.sp
        )
    }
}
