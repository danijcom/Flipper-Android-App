package com.flipperdevices.debug.countrydetect.model

data class CountryInfo(
    val isoBySim: String? = null,
    val isoByNetwork: String? = null,
    val isRoaming: Boolean? = null
)
