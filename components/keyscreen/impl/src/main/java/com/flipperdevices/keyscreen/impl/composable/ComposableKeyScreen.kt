package com.flipperdevices.keyscreen.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.keyedit.api.KeyEditApi
import com.flipperdevices.keyscreen.impl.model.KeyScreenState
import com.flipperdevices.keyscreen.impl.viewmodel.KeyScreenViewModel
import com.flipperdevices.nfceditor.api.NfcEditorApi

@Composable
fun ComposableKeyScreen(
    viewModel: KeyScreenViewModel,
    synchronizationUiApi: SynchronizationUiApi,
    keyScreenState: KeyScreenState = KeyScreenState.InProgress,
    keyEditApi: KeyEditApi,
    nfcEditorApi: NfcEditorApi,
    onBack: () -> Unit
) {
    when (keyScreenState) {
        KeyScreenState.InProgress -> ComposableKeyInitial()
        is KeyScreenState.Error -> ComposableKeyError(keyScreenState)
        is KeyScreenState.Ready -> ComposableKeyParsed(
            viewModel,
            keyScreenState,
            nfcEditorApi,
            synchronizationUiApi,
            onBack
        )
        is KeyScreenState.Editing -> keyEditApi.EditScreen(
            keyScreenState.flipperKey,
            keyScreenState.parsedKey,
            viewModel::onEditFinished
        )
    }
}

@Composable
private fun ComposableKeyInitial() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun ComposableKeyError(error: KeyScreenState.Error) {
    val errorText = stringResource(error.reason)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = errorText,
            fontWeight = FontWeight.Medium
        )
    }
}
