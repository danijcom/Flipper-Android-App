package com.flipperdevices.deeplink.impl.parser.delegates

import android.content.Context
import android.net.Uri
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.deeplink.impl.di.DeepLinkComponent
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import java.net.URLDecoder
import javax.inject.Inject

private const val SCHEME_FLIPPERKEY = "flipperkey"

class DeepLinkFlipperFormatSharing : DeepLinkParserDelegate, LogTagProvider {
    override val TAG = "DeepLinkFlipperFormatSharing"

    @Inject
    lateinit var parser: KeyParser

    init {
        ComponentHolder.component<DeepLinkComponent>().inject(this)
    }

    override suspend fun fromUri(context: Context, uri: Uri): Deeplink? {
        var pureUri = uri

        if (uri.scheme == SCHEME_FLIPPERKEY) {
            val query = uri.query
            val decodedQuery = URLDecoder.decode(query, "UTF-8")
            pureUri = Uri.parse(decodedQuery)
        }

        val (path, content) = parser.parseUri(pureUri) ?: return null
        return Deeplink(path, DeeplinkContent.FFFContent(path.name, content))
    }
}
