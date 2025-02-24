package com.flipperdevices.archive.shared.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.flipperdevices.archive.shared.utils.ExtractKeyMetaInformation
import com.flipperdevices.bridge.dao.api.model.FlipperFileType.Companion.colorByFlipperFileType
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.core.ui.ktx.ComposableKeyType
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ComposableKeyCard(
    modifier: Modifier,
    synchronizationContent: (@Composable () -> Unit)?,
    flipperKeyParsed: FlipperKeyParsed,
    typeColor: Color = colorByFlipperFileType(flipperKeyParsed.fileType),
    onCardClicked: () -> Unit
) {
    Card(
        modifier = modifier
            .padding(horizontal = 14.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onCardClicked
            )
    ) {
        Column(Modifier.padding(bottom = 8.dp)) {
            ComposableKeyCardContent(
                flipperKeyParsed,
                typeColor,
                synchronizationContent
            )
        }
    }
}

@Composable
private fun ComposableKeyCardContent(
    flipperKeyParsed: FlipperKeyParsed,
    typeColor: Color,
    synchronizationContent: (@Composable () -> Unit)?
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        ComposableKeyType(flipperKeyParsed.fileType, colorKey = typeColor)
        val protocol = ExtractKeyMetaInformation.extractProtocol(flipperKeyParsed)
        if (protocol != null) {
            Text(
                modifier = Modifier.padding(horizontal = 14.dp),
                text = protocol,
                color = LocalPallet.current.text12,
                style = LocalTypography.current.subtitleR12
            )
        }
        if (synchronizationContent != null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                synchronizationContent()
            }
        }
    }
    Text(
        modifier = Modifier.padding(
            top = 8.dp,
            start = 8.dp,
            end = 8.dp
        ),
        text = flipperKeyParsed.keyName,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = LocalTypography.current.bodyR14
    )

    val notes = flipperKeyParsed.notes ?: return

    SelectionContainer {
        Text(
            modifier = Modifier.padding(
                top = 6.dp,
                start = 8.dp,
                end = 8.dp
            ),
            text = notes,
            style = LocalTypography.current.bodyR14,
            color = LocalPallet.current.text30,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
