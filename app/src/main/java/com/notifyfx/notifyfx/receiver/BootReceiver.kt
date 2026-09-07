package com.notifyfx.notifyfx.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.notifyfx.notifyfx.service.NotifyFXNotificationListenerService
import com.notifyfx.notifyfx.service.NotifyFXOverlayService

class BootReceiver : BroadcastReceiver() {

    private val TAG = "NotifyFXBootReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "BootReceiver: ${intent.action}")

        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                // Restart notification listener service
                context.startService(
                    Intent(context, NotifyFXNotificationListenerService::class.java)
                )

                // Restart overlay service
                context.startService(
                    Intent(context, NotifyFXOverlayService::class.java)
                )
            }
            Intent.ACTION_USER_PRESENT -> {
                // User unlocked device, ensure services are running
                context.startService(
                    Intent(context, NotifyFXNotificationListenerService::class.java)
                )
                context.startService(
                    Intent(context, NotifyFXOverlayService::class.java)
                )
            }
        }
    }
}