package com.flipperdevices.settings.impl.composable.category

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.elements.CategoryElement
import com.flipperdevices.settings.impl.composable.elements.ClickableElement
import com.flipperdevices.settings.impl.composable.elements.GrayDivider
import com.flipperdevices.settings.impl.composable.elements.SimpleElement
import com.flipperdevices.settings.impl.composable.elements.SwitchableElement
import com.flipperdevices.settings.impl.model.NavGraphRoute
import com.flipperdevices.settings.impl.viewmodels.DebugViewModel
import com.flipperdevices.settings.impl.viewmodels.SettingsViewModel

@Composable
fun DebugCategory(
    settings: Settings,
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    debugViewModel: DebugViewModel = viewModel()
) {
    CardCategory {
        Column {
            CategoryElement(
                titleId = R.string.debug_options,
                descriptionId = R.string.debug_options_desc,
                state = settings.enabledDebugSettings,
                onSwitchState = settingsViewModel::onSwitchDebug
            )
            if (settings.enabledDebugSettings) {
                ClickableElement(
                    titleId = R.string.debug_stress_test,
                    descriptionId = R.string.debug_stress_test_desc,
                    onClick = {
                        navController.navigate(NavGraphRoute.StressTest.name) {
                            popUpTo(NavGraphRoute.Settings.name)
                        }
                    }
                )
                GrayDivider()
                SimpleElement(
                    titleId = R.string.debug_start_synchronization,
                    onClick = { debugViewModel.onStartSynchronization() }
                )
                GrayDivider()
                ClickableElement(
                    titleId = R.string.debug_connection_to_another,
                    descriptionId = R.string.debug_connection_to_another_desc,
                    onClick = { debugViewModel.onOpenConnectionScreen() }
                )
                GrayDivider()
                SwitchableElement(
                    titleId = R.string.debug_ignored_unsupported_version,
                    descriptionId = R.string.debug_ignored_unsupported_version_desc,
                    state = settings.ignoreUnsupportedVersion,
                    onSwitchState = debugViewModel::onSwitchIgnoreSupportedVersion
                )
                GrayDivider()
                SwitchableElement(
                    titleId = R.string.debug_ignored_update_version,
                    descriptionId = R.string.debug_ignored_update_version_desc,
                    state = settings.alwaysUpdate,
                    onSwitchState = debugViewModel::onSwitchIgnoreUpdaterVersion
                )
                GrayDivider()
                SwitchableElement(
                    titleId = R.string.debug_shake2report,
                    descriptionId = R.string.debug_shake2report_desc,
                    state = settings.shakeToReport,
                    onSwitchState = debugViewModel::onSwitchShakeToReport
                )
            }
        }
    }
}
