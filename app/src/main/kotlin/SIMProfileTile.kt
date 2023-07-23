package dev.shadoe.pixel.tiles

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.TileService
import android.telephony.TelephonyManager

class SIMProfileTileService : TileService() {
    private val telephonyManager by lazy {
        getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }
    private val serviceStateListener by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            ServiceStateListener(this, telephonyManager)
        else null
    }
    private val oldServiceStateListener by lazy {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
            OldServiceStateListener(this, telephonyManager)
        else null
    }

    override fun onStartListening() {
        super.onStartListening()
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            telephonyManager.registerTelephonyCallback(
                mainExecutor,
                serviceStateListener!!
            )
        else
            telephonyManager.listen(
                oldServiceStateListener,
                OldServiceStateListener.LISTEN_SERVICE_STATE
            )
    }

    override fun onStopListening() {
        super.onStopListening()
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            telephonyManager.unregisterTelephonyCallback(serviceStateListener!!)
        else
            telephonyManager.listen(
                oldServiceStateListener,
                OldServiceStateListener.LISTEN_NONE
            )
    }

    @SuppressLint("StartActivityAndCollapseDeprecated")
    override fun onClick() {
        super.onClick()
        Intent(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                Settings.ACTION_MANAGE_ALL_SIM_PROFILES_SETTINGS
            else
                Settings.ACTION_WIRELESS_SETTINGS
        )
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                    startActivityAndCollapse(
                        PendingIntent.getActivity(
                            applicationContext,
                            1,
                            it,
                            PendingIntent.FLAG_IMMUTABLE,
                        )
                    )
                else {
                    @Suppress("DEPRECATION")
                    startActivityAndCollapse(it)
                }
            }
    }
}