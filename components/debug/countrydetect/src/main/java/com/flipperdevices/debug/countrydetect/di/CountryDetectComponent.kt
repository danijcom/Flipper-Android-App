package com.flipperdevices.debug.countrydetect.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.debug.countrydetect.viewmodel.CountryDetectViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface CountryDetectComponent {
    fun inject(viewModel: CountryDetectViewModel)
}