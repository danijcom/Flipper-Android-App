package com.flipperdevices.updater.screen.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.animatedDots
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.info.shared.getColorByChannel
import com.flipperdevices.info.shared.getTextByVersion
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.screen.R
import com.flipperdevices.updater.screen.model.FailedReason
import com.flipperdevices.updater.screen.model.UpdaterScreenState

@Composable
fun ComposableUpdateContent(
    updaterScreenState: UpdaterScreenState,
    onRetry: () -> Unit
) {
    if (updaterScreenState.version != null) {
        FirmwareVersionText(updaterScreenState.version)
    }

    when (updaterScreenState) {
        UpdaterScreenState.NotStarted -> ComposableInProgressIndicator(
            accentColor = LocalPallet.current.updateProgressGreen,
            secondColor = LocalPallet.current.updateProgressBackgroundGreen,
            iconId = null,
            percent = null
        )
        is UpdaterScreenState.CancelingSynchronization -> ComposableInProgressIndicator(
            accentColor = LocalPallet.current.accentSecond,
            secondColor = LocalPallet.current.updateProgressBackgroundBlue,
            iconId = null,
            percent = null
        )
        is UpdaterScreenState.DownloadingFromNetwork -> ComposableInProgressIndicator(
            accentColor = LocalPallet.current.updateProgressGreen,
            secondColor = LocalPallet.current.updateProgressBackgroundGreen,
            iconId = DesignSystem.drawable.ic_globe,
            percent = updaterScreenState.percent
        )
        is UpdaterScreenState.UploadOnFlipper -> ComposableInProgressIndicator(
            accentColor = LocalPallet.current.accentSecond,
            secondColor = LocalPallet.current.updateProgressBackgroundBlue,
            iconId = DesignSystem.drawable.ic_bluetooth,
            percent = updaterScreenState.percent
        )
        UpdaterScreenState.CancelingUpdate -> ComposableInProgressIndicator(
            accentColor = LocalPallet.current.accentSecond,
            secondColor = LocalPallet.current.updateProgressBackgroundBlue,
            iconId = null,
            percent = null
        )
        UpdaterScreenState.Rebooting -> ComposableInProgressIndicator(
            accentColor = LocalPallet.current.accentSecond,
            secondColor = LocalPallet.current.updateProgressBackgroundBlue,
            iconId = null,
            percent = null
        )
        is UpdaterScreenState.Failed -> when (updaterScreenState.failedReason) {
            FailedReason.UPLOAD_ON_FLIPPER -> ComposableFailedUploadContent()
            FailedReason.DOWNLOAD_FROM_NETWORK -> ComposableFailedDownloadContent(onRetry)
        }
        UpdaterScreenState.Finish -> return
    }

    DescriptionUpdateText(updaterScreenState)
}

@Composable
private fun FirmwareVersionText(version: FirmwareVersion) {
    val text = getTextByVersion(version)
    val textColor = getColorByChannel(version.channel)

    Text(
        modifier = Modifier.padding(bottom = 4.dp, start = 24.dp, end = 24.dp),
        text = text,
        color = textColor,
        style = LocalTypography.current.titleM18
    )
}

@Composable
private fun DescriptionUpdateText(
    updaterScreenState: UpdaterScreenState
) {
    val descriptionId = when (updaterScreenState) {
        is UpdaterScreenState.CancelingSynchronization -> R.string.update_stage_sync_canceling_desc
        UpdaterScreenState.CancelingUpdate -> R.string.update_stage_update_canceling_desc
        is UpdaterScreenState.DownloadingFromNetwork -> R.string.update_stage_downloading_desc
        UpdaterScreenState.Finish -> R.string.update_stage_update_canceling_desc
        UpdaterScreenState.NotStarted -> R.string.update_stage_starting_desc
        UpdaterScreenState.Rebooting -> R.string.update_stage_rebooting_desc
        is UpdaterScreenState.UploadOnFlipper -> R.string.update_stage_uploading_desc
        is UpdaterScreenState.Failed -> return
    }

    Text(
        modifier = Modifier.padding(horizontal = 12.dp),
        text = stringResource(descriptionId) + animatedDots(),
        style = LocalTypography.current.subtitleM12,
        color = LocalPallet.current.text30
    )
}
