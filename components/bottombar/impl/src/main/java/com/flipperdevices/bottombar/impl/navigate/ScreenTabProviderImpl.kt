package com.flipperdevices.bottombar.impl.navigate

import com.flipperdevices.archive.api.ArchiveApi
import com.flipperdevices.bottombar.impl.model.FlipperBottomTab
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.info.api.screen.InfoScreenProvider
import com.flipperdevices.settings.api.SettingsApi
import com.github.terrakok.cicerone.Screen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class ScreenTabProviderImpl @Inject constructor(
    private val infoScreenProvider: InfoScreenProvider,
    private val archiveApi: ArchiveApi,
    private val settingsApi: SettingsApi
) : ScreenTabProvider {
    override fun getScreen(tab: FlipperBottomTab): Screen {
        return when (tab) {
            FlipperBottomTab.DEVICE -> infoScreenProvider.deviceInformationScreen()
            FlipperBottomTab.ARCHIVE -> archiveApi.getArchiveScreen()
            FlipperBottomTab.OPTIONS -> settingsApi.getSettingsScreen()
        }
    }
}
