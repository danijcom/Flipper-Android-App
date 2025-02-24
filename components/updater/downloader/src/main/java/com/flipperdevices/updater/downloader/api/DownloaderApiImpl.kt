package com.flipperdevices.updater.downloader.api

import android.content.Context
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.preference.FlipperStorageProvider
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.downloader.di.DownloaderComponent
import com.flipperdevices.updater.downloader.model.ArtifactType
import com.flipperdevices.updater.downloader.model.FirmwareDirectoryListeningResponse
import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.DownloadProgress
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.VersionFiles
import com.squareup.anvil.annotations.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import java.io.File
import java.util.EnumMap
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

private const val JSON_URL = "https://update.flipperzero.one/firmware/directory.json"

@ContributesBinding(AppGraph::class, DownloaderApi::class)
class DownloaderApiImpl @Inject constructor(
    private val context: Context,
    private val client: HttpClient,
    private val downloadAndUnpackDelegate: DownloadAndUnpackDelegate
) : DownloaderApi, LogTagProvider {
    override val TAG = "DownloaderApi"

    init {
        ComponentHolder.component<DownloaderComponent>().inject(this)
    }

    override suspend fun getLatestVersion(): EnumMap<FirmwareChannel, VersionFiles> {
        val versionMap: EnumMap<FirmwareChannel, VersionFiles> =
            EnumMap(FirmwareChannel::class.java)

        val response = client.get(urlString = JSON_URL).body<FirmwareDirectoryListeningResponse>()

        verbose { "Receive response from server" }

        response.channels.map { channel ->
            channel.id to channel.versions.maxByOrNull { it.timestamp }
        }.filter { it.first != null && it.second != null }
            .map { it.first!! to it.second!! }
            .forEach { (channel, version) ->
                val updaterFile = version.files.find { it.type == ArtifactType.UPDATE_TGZ }
                    ?: return@forEach

                versionMap[channel.original] = VersionFiles(
                    FirmwareVersion(
                        channel.original,
                        version.version.clearVersion()
                    ),
                    updaterFile = DistributionFile(
                        updaterFile.url,
                        updaterFile.sha256
                    )
                )
            }

        verbose { "Result version map is $versionMap" }

        return versionMap
    }

    override fun download(
        distributionFile: DistributionFile,
        target: File,
        decompress: Boolean
    ): Flow<DownloadProgress> = channelFlow {
        info { "Request download $distributionFile" }
        if (decompress) {
            FlipperStorageProvider.useTemporaryFile(context) { tempFile ->
                downloadAndUnpackDelegate.download(
                    distributionFile, tempFile
                ) { processedBytes, totalBytes ->
                    send(DownloadProgress.InProgress(processedBytes, totalBytes))
                }
                info { "File downloaded in ${tempFile.absolutePath}" }

                downloadAndUnpackDelegate.unpack(tempFile, target)
                info {
                    "Unpack finished in ${target.absolutePath} ${target.listFiles()?.size} files"
                }
            }
        } else {
            downloadAndUnpackDelegate.download(
                distributionFile, target
            ) { processedBytes, totalBytes ->
                send(DownloadProgress.InProgress(processedBytes, totalBytes))
            }
            info { "File downloaded in ${target.absolutePath}" }
        }
    }
}

private fun String.clearVersion(): String {
    return replace("-rc", "").trim()
}
