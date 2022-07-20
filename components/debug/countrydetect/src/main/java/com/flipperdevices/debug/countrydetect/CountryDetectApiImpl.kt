package com.flipperdevices.debug.countrydetect

import androidx.compose.runtime.Composable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.debug.api.CountryDetectApi
import com.flipperdevices.debug.countrydetect.composable.ComposableCountryDetect
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class CountryDetectApiImpl @Inject constructor() : CountryDetectApi {
    @Composable
    override fun CountryDetect() {
        ComposableCountryDetect()
    }
}
