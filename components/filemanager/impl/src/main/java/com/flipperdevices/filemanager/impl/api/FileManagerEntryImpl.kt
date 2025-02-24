package com.flipperdevices.filemanager.impl.api

import android.net.Uri
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.filemanager.api.navigation.FileManagerEntry
import com.flipperdevices.filemanager.impl.composable.ComposableFileManagerDownloadScreen
import com.flipperdevices.filemanager.impl.composable.ComposableFileManagerScreen
import com.flipperdevices.filemanager.impl.composable.ComposableFileManagerUploadedScreen
import com.flipperdevices.filemanager.impl.model.ShareFile
import com.flipperdevices.filemanager.impl.viewmodels.FileManagerViewModel
import com.flipperdevices.filemanager.impl.viewmodels.ReceiveViewModel
import com.flipperdevices.filemanager.impl.viewmodels.ShareViewModel
import com.squareup.anvil.annotations.ContributesBinding
import java.io.File
import javax.inject.Inject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tangle.viewmodel.compose.tangleViewModel

private const val ROUTE = "@filemanager"
internal const val PATH_KEY = "path"
internal const val CONTENT_KEY = "content"
internal const val FILE_PATH_KEY = "file_path"
private const val FILE_MANAGER_ROUTE = "filemanager?path={$PATH_KEY}"
private const val FILE_MANAGER_UPLOAD_ROUTE =
    "filemanagerupload?path={$PATH_KEY}&content={$CONTENT_KEY}"
private const val FILE_MANAGER_DOWNLOAD_ROUTE =
    "filemanagerdownload?path={$PATH_KEY}&filepath={$FILE_PATH_KEY}"

@ContributesBinding(AppGraph::class)
class FileManagerEntryImpl @Inject constructor(
    private val deepLinkParser: DeepLinkParser
) : FileManagerEntry {
    private val fileManagerArguments = listOf(
        navArgument(PATH_KEY) {
            type = NavType.StringType
            nullable = false
        }
    )
    private val uploadArguments = fileManagerArguments.plus(
        navArgument(CONTENT_KEY) {
            type = DeeplinkContentType()
            nullable = false
        }
    )
    private val downloadArguments = fileManagerArguments.plus(
        navArgument(FILE_PATH_KEY) {
            type = ShareFileType()
            nullable = false
        }
    )

    override fun fileManagerDestination(
        path: String
    ) = "filemanager?path=${Uri.encode(path)}"

    override fun uploadFile(
        path: String,
        deeplinkContent: DeeplinkContent
    ) = "filemanagerupload?path=${Uri.encode(path)}" +
        "&content=${Uri.encode(Json.encodeToString(deeplinkContent))}"

    private fun downloadFileDestination(
        file: ShareFile,
        pathToDirectory: String = File(file.flipperFilePath).absoluteFile.parent ?: "/"
    ) = "filemanagerdownload?path=${Uri.encode(pathToDirectory)}" +
        "&filepath=${Uri.encode(Json.encodeToString(file))}"

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(startDestination = fileManagerDestination(), route = ROUTE) {
            composable(FILE_MANAGER_ROUTE, fileManagerArguments) {
                val fileManagerViewModel: FileManagerViewModel = tangleViewModel()
                val fileManagerState by fileManagerViewModel.getFileManagerState().collectAsState()
                ComposableFileManagerScreen(
                    fileManagerState = fileManagerState,
                    onOpenFolder = {
                        if (it.isDirectory) {
                            navController.navigate(fileManagerDestination(it.path))
                        } else navController.navigate(downloadFileDestination(ShareFile(it)))
                    },
                    deepLinkParser = deepLinkParser,
                    onUploadFile = {
                        navController.navigate(
                            uploadFile(
                                fileManagerState.currentPath,
                                it
                            )
                        )
                    }
                )
            }
            composable(FILE_MANAGER_UPLOAD_ROUTE, uploadArguments) {
                val fileManagerViewModel: FileManagerViewModel = tangleViewModel()
                val fileManagerState by fileManagerViewModel.getFileManagerState().collectAsState()

                val receiveViewModel: ReceiveViewModel = tangleViewModel()
                val shareState by receiveViewModel.getReceiveState().collectAsState()

                if (shareState.processCompleted) {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }

                ComposableFileManagerUploadedScreen(
                    fileManagerState,
                    shareState
                ) {
                    navController.popBackStack()
                }
            }
            composable(FILE_MANAGER_DOWNLOAD_ROUTE, downloadArguments) {
                val fileManagerViewModel: FileManagerViewModel = tangleViewModel()
                val fileManagerState by fileManagerViewModel.getFileManagerState().collectAsState()

                val shareViewModel: ShareViewModel = tangleViewModel()
                val shareState by shareViewModel.getShareState().collectAsState()

                if (shareState.processCompleted) {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }

                ComposableFileManagerDownloadScreen(
                    fileManagerState,
                    shareState
                ) {
                    navController.popBackStack()
                }
            }
        }
    }
}
