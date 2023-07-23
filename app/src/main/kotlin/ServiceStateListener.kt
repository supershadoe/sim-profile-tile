@file:Suppress("DEPRECATION")

package dev.shadoe.pixel.tiles

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.telephony.PhoneStateListener
import android.telephony.ServiceState
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.S)
class ServiceStateListener(
    private val ts: TileService,
    private val tm: TelephonyManager
): TelephonyCallback(), TelephonyCallback.ServiceStateListener {
    override fun onServiceStateChanged(serviceState: ServiceState) {
        with(ts.qsTile) {
            subtitle = when (serviceState.state) {
                ServiceState.STATE_IN_SERVICE -> tm.networkOperatorName
                ServiceState.STATE_OUT_OF_SERVICE -> "Not connected"
                ServiceState.STATE_EMERGENCY_ONLY -> "Emergency calls only"
                ServiceState.STATE_POWER_OFF -> "Radio off: ✈?"
                else -> "Unknown state"
            }
            state =
                if (serviceState.state == ServiceState.STATE_IN_SERVICE)
                    Tile.STATE_ACTIVE
                else
                    Tile.STATE_INACTIVE
            updateTile()
        }
    }
}

@Deprecated("But cannot be removed to provide compatibility to Android 7-11")
class OldServiceStateListener(
    private val ts: TileService,
    private val tm: TelephonyManager
): PhoneStateListener() {
    companion object {
        @JvmStatic
        val LISTEN_SERVICE_STATE = PhoneStateListener.LISTEN_SERVICE_STATE
        @JvmStatic
        val LISTEN_NONE = PhoneStateListener.LISTEN_NONE
    }

    @Deprecated("Deprecated in Java")
    override fun onServiceStateChanged(serviceState: ServiceState?) {
        super.onServiceStateChanged(serviceState)
        with(ts.qsTile) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                subtitle = when (serviceState?.state) {
                    ServiceState.STATE_IN_SERVICE -> tm.networkOperatorName
                    ServiceState.STATE_OUT_OF_SERVICE -> "Not connected"
                    ServiceState.STATE_EMERGENCY_ONLY -> "Emergency calls only"
                    ServiceState.STATE_POWER_OFF -> "Radio off: ✈?"
                    else -> "Unknown state"
                }
            }
            state =
                if (serviceState?.state == ServiceState.STATE_IN_SERVICE)
                    Tile.STATE_ACTIVE
                else
                    Tile.STATE_INACTIVE
            updateTile()
        }
    }
}