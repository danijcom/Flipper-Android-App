package com.flipperdevices.debug.countrydetect.viewmodel

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.debug.countrydetect.di.CountryDetectComponent
import com.flipperdevices.debug.countrydetect.model.CountryInfo
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class CountryDetectViewModel : ViewModel() {
    private val countryInfoFlow = MutableStateFlow<CountryInfo>(CountryInfo())

    @Inject
    lateinit var context: Context

    init {
        ComponentHolder.component<CountryDetectComponent>().inject(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requestInformationFromSimCards(context)
        }
    }

    fun getCountryInfo(): StateFlow<CountryInfo> = countryInfoFlow

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestInformationFromSimCards(context: Context) {
        val telephonyManager = context.getSystemService(TelephonyManager::class.java)

        val simCountry = telephonyManager.simCountryIso
        val networkCountryIso = telephonyManager.networkCountryIso
        val isNetworkRoaming = telephonyManager.isNetworkRoaming

        countryInfoFlow.update {
            it.copy(
                isoBySim = simCountry,
                isoByNetwork = networkCountryIso,
                isRoaming = isNetworkRoaming
            )
        }
    }
}
