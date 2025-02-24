package com.flipperdevices.bridge.service.api

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.manager.delegates.FlipperConnectionInformationApi
import com.flipperdevices.bridge.api.manager.service.FlipperInformationApi
import com.flipperdevices.bridge.api.manager.service.FlipperRpcInformationApi
import com.flipperdevices.bridge.api.manager.service.FlipperVersionApi

/**
 * Provides access to the API operation of the device
 * Underhood creates a service and connects to it
 *
 * You can get instance by FlipperServiceProvider
 */
interface FlipperServiceApi {

    /**
     * Provide information about flipper name, device id
     */
    val flipperInformationApi: FlipperInformationApi

    /**
     * Provide information about current connection state
     */
    val connectionInformationApi: FlipperConnectionInformationApi

    /**
     * Returns an API for communicating with Flipper via a request-response structure.
     */
    val requestApi: FlipperRequestApi

    /**
     * Returns wrapper of request api which provide device info from flipper
     */
    val flipperRpcInformationApi: FlipperRpcInformationApi

    /**
     * Provides information about the version of the API on Flipper. Null if not received.
     */
    val flipperVersionApi: FlipperVersionApi

    /**
     * If not force disconnect, try connect again
     */
    fun connectIfNotForceDisconnect()

    /**
     * @param isForce if false restore connect after connectIfNotForceDisconnect
     */
    suspend fun disconnect(isForce: Boolean = true)

    suspend fun reconnect()
}
