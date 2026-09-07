package com.notifyfx.notifyfx.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.notifyfx.notifyfx.NotifyFXApp
import com.notifyfx.notifyfx.data.INotificationRepository
import com.notifyfx.notifyfx.data.NotifyFXSettings
import com.notifyfx.notifyfx.model.NotificationModel
import com.notifyfx.notifyfx.util.NotificationParser
import com.notifyfx.notifyfx.util.runCatchingLogged
import com.notifyfx.notifyfx.util.runSuspendCatchingLogged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotifyFXNotificationListenerService : NotificationListenerService() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, error ->
        Log.e(TAG, "Unhandled notification-listener coroutine failure", error)
    }
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default + coroutineExceptionHandler)
    private val mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate + coroutineExceptionHandler)

    @Inject lateinit var notificationRepository: INotificationRepository
    @Inject lateinit var settings: NotifyFXSettings

    private var currentEnabled = true
    private var currentHideOriginal = false

    companion object {
        @Volatile var isSystemConnected: Boolean = false
            private set
        private const val TAG = "NotifyFXNotificationListener"
        private const val NOTIFICATION_ID = 8106
        private const val CHANNEL_ID = "notifyfx_service"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")

        // Collect settings
        serviceScope.launch {
            runSuspendCatchingLogged(TAG, "Settings collector failed") {
                settings.settingsFlow.subscribe { prefs ->
                    currentEnabled = prefs[NotifyFXSettings.SettingsKeys.ENABLED] ?: true
                    currentHideOriginal = prefs[NotifyFXSettings.SettingsKeys.HIDE_ORIGINAL] ?: false
                }
            }
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        isSystemConnected = false
        serviceScope.cancel()
        mainScope.cancel()
        super.onDestroy()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        runCatchingLogged(TAG, "onNotificationPosted callback failed") {
            if (!currentEnabled) return@runCatchingLogged
            if (sbn.packageName == packageName) return@runCatchingLogged

            // Filter unwanted notifications
            if (NotificationParser.shouldFilter(sbn, packageManager)) {
                Log.d(TAG, "Filtered out: ${sbn.key}")
                return@runCatchingLogged
            }

            // Handle group summaries
            val isGroupSummary = NotificationParser.isGroupSummary(sbn.notification)
            if (isGroupSummary) {
                if (currentHideOriginal) {
                    suppressSystemNotification(sbn.key)
                }
                return@runCatchingLogged
            }

            // Suppress from system shade if enabled
            if (currentHideOriginal) {
                suppressSystemNotification(sbn.key)
            }

            serviceScope.launch {
                runSuspendCatchingLogged(TAG, "NotificationPosted async failed") {
                    handleNotificationPosted(sbn)
                }
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification, rankingMap: android.service.notification.NotificationListenerService.RankingMap?, reason: Int) {
        runCatchingLogged(TAG, "onNotificationRemoved callback failed") {
            Log.d(TAG, "onNotificationRemoved: key=${sbn.key} pkg=${sbn.packageName} reason=$reason")
            if (sbn.packageName == packageName) return@runCatchingLogged

            serviceScope.launch {
                runSuspendCatchingLogged(TAG, "NotificationRemoved handling failed") {
                    // Small delay to handle rapid updates
                    kotlinx.coroutines.delay(100L)
                    notificationRepository.removeNotification(sbn.key)
                }
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        runCatchingLogged(TAG, "onNotificationRemoved fallback callback failed") {
            Log.d(TAG, "onNotificationRemoved (no reason): key=${sbn.key} pkg=${sbn.packageName}")
            if (sbn.packageName == packageName) return@runCatchingLogged

            serviceScope.launch {
                runSuspendCatchingLogged(TAG, "NotificationRemoved (no reason) handling failed") {
                    kotlinx.coroutines.delay(100L)
                    notificationRepository.removeNotification(sbn.key)
                }
            }
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        isSystemConnected = true
        Log.d(TAG, "onListenerConnected")

        serviceScope.launch {
            runSuspendCatchingLogged(TAG, "ListenerConnected failed") {
                val active = activeNotifications?.toList()
                    ?.filter { it.packageName != packageName }
                    ?.filterNot { NotificationParser.shouldFilter(it, packageManager) }
                    .orEmpty()

                Log.d(TAG, "ListenerConnected: ${active.size} active notifications")

                active.forEach { sbn ->
                    val isGroupSummary = NotificationParser.isGroupSummary(sbn.notification)
                    if (!isGroupSummary) {
                        if (currentHideOriginal) {
                            suppressSystemNotification(sbn.key)
                        }
                        handleNotificationPosted(sbn)
                    }
                }
            }
        }
    }

    override fun onListenerDisconnected() {
        isSystemConnected = false
        super.onListenerDisconnected()
        Log.w(TAG, "onListenerDisconnected - attempting rebind")
        requestRebind(android.content.ComponentName(this, NotifyFXNotificationListenerService::class.java))
    }

    private fun handleNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName == packageName) return

        val notification = sbn.notification

        // Parse notification into our internal model
        val model = NotificationParser.parse(sbn, packageManager)

        // Check if it's a new notification
        val isNew = notificationRepository.notifications.value.none { it.key == sbn.key }

        // Post to repository
        val autoExpand = isNew && currentHideOriginal
        notificationRepository.postNotification(model, autoExpand)

        // Play notification sound if enabled and new
        if (isNew) {
            playNotificationSound(sbn)
        }

        Log.d(TAG, "Posted: ${model.appName} - ${model.title}")
    }

    /**
     * Suppress notification from system shade using cancelNotification
     */
    private fun suppressSystemNotification(key: String) {
        if (!currentEnabled || !currentHideOriginal) return

        runCatchingLogged(TAG, "sync cancel failed") {
            cancelNotification(key)
        }

        // Retry with delays for reliability
        mainScope.launch {
            runSuspendCatchingLogged(TAG, "Notification suppression retries failed") {
                repeat(3) { attempt ->
                    kotlinx.coroutines.delay(100L * (attempt + 1))
                    val stillActive = runCatchingLogged(TAG, "Failed checking stillActive") {
                        activeNotifications?.any { it.key == key }
                    } ?: false
                    if (!stillActive) {
                        Log.d(TAG, "Successfully suppressed after ${attempt + 1} attempts: $key")
                        return@runSuspendCatchingLogged
                    }
                    runCatchingLogged(TAG, "cancel retry $attempt failed") {
                        cancelNotification(key)
                    }
                }
                Log.w(TAG, "Failed to suppress after retries: $key")
            }
        }
    }

    private fun playNotificationSound(sbn: StatusBarNotification) {
        runCatchingLogged(TAG, "Failed to play notification sound") {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as? android.media.AudioManager
            if (audioManager == null || audioManager.ringerMode != android.media.AudioManager.RINGER_MODE_NORMAL) return@runCatchingLogged

            val notification = sbn.notification
            var soundUri: android.net.Uri? = null
            var audioAttributes: android.media.AudioAttributes? = null

            // Check notification channel (Android 8+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = notification.channel?.let { notificationManager.getNotificationChannel(it) }
                if (channel != null) {
                    if (channel.importance < NotificationManager.IMPORTANCE_DEFAULT) return@runCatchingLogged
                    val chSound = channel.sound
                    if (chSound != null && chSound != android.net.Uri.EMPTY) {
                        soundUri = chSound
                        audioAttributes = channel.audioAttributes
                    }
                }
            }

            // Fallback to notification payload
            if (soundUri == null) {
                @Suppress("DEPRECATION")
                val notifSound = notification.sound
                @Suppress("DEPRECATION")
                val defaults = notification.defaults

                if (notifSound != null && notifSound != android.net.Uri.EMPTY) {
                    soundUri = notifSound
                } else if ((defaults and Notification.DEFAULT_SOUND) != 0) {
                    soundUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
                }
                audioAttributes = notification.audioAttributes
            }

            soundUri?.let { uri ->
                var ringtone = runCatching { android.media.RingtoneManager.getRingtone(applicationContext, uri) }.getOrNull()
                if (ringtone == null) {
                    val fallbackUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
                    ringtone = runCatching { android.media.RingtoneManager.getRingtone(applicationContext, fallbackUri) }.getOrNull()
                }
                ringtone?.apply {
                    audioAttributes = audioAttributes ?: android.media.AudioAttributes.Builder()
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                    play()
                }
            }
        }
    }
}