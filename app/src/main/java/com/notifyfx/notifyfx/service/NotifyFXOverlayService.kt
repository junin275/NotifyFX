package com.notifyfx.notifyfx.service

import android.accessibilityservice.AccessibilityService
import android.app.ActivityOptions
import android.app.KeyguardManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.notifyfx.notifyfx.R
import com.notifyfx.notifyfx.data.INotificationRepository
import com.notifyfx.notifyfx.data.NotificationCommand
import com.notifyfx.notifyfx.data.NotifyFXSettings
import com.notifyfx.notifyfx.data.SettingsKeys
import com.notifyfx.notifyfx.model.NotificationModel
import com.notifyfx.notifyfx.overlay.NotificationOverlayView
import com.notifyfx.notifyfx.overlay.OverlayViewTreeOwners
import com.notifyfx.notifyfx.ui.MainActivity
import com.notifyfx.notifyfx.ui.OverlayViewModel
import com.notifyfx.notifyfx.util.runCatchingLogged
import com.notifyfx.notifyfx.util.runSuspendCatchingLogged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotifyFXOverlayService : AccessibilityService() {

    private lateinit var windowManager: WindowManager
    @Inject lateinit var settings: NotifyFXSettings
    @Inject lateinit var notificationRepository: INotificationRepository
    private var overlayView: ComposeView? = null
    private val overlayOwners = OverlayViewTreeOwners()
    private lateinit var viewModel: OverlayViewModel
    private var isLockScreenActive: Boolean = false
    private var systemEventReceiverRegistered = false
    private var screenStateReceiverRegistered = false
    private var foregroundStarted = false
    @Volatile private var destroyed = false
    private var isWindowVisible: Boolean = false
    private var lastParams: WindowManager.LayoutParams? = null

    private val serviceScope = kotlinx.coroutines.CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate + CoroutineExceptionHandler { _, error ->
            Log.e(TAG, "Unhandled overlay coroutine failure", error)
        }
    )

    private val screenStateReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            runCatchingLogged(TAG, "Screen-state callback failed") {
                if (destroyed || !::viewModel.isInitialized) return@runCatchingLogged
                val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
                when (intent.action) {
                    Intent.ACTION_SCREEN_ON -> {
                        overlayOwners.resume()
                        isLockScreenActive = keyguardManager?.isKeyguardLocked == true
                        updateWindowVisibility()
                    }
                    Intent.ACTION_SCREEN_OFF -> {
                        overlayOwners.pause()
                        isLockScreenActive = true
                        updateWindowVisibility()
                    }
                    Intent.ACTION_USER_PRESENT -> {
                        isLockScreenActive = false
                        updateWindowVisibility()
                    }
                }
            }
        }
    }

    override fun onAccessibilityEvent(event: android.view.accessibility.AccessibilityEvent?) {
        runCatchingLogged(TAG, "onAccessibilityEvent failed") {
            if (destroyed || !::viewModel.isInitialized) return@runCatchingLogged
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
            val locked = keyguardManager?.isKeyguardLocked == true
            if (isLockScreenActive != locked) {
                isLockScreenActive = locked
                updateWindowVisibility()
            }
        }
    }

    override fun onInterrupt() {
        // Required override, no-op
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        runCatchingLogged(TAG, "onConfigurationChanged failed") {
            if (destroyed || !::viewModel.isInitialized) return@runCatchingLogged
            updateWindowVisibility()
        }
    }

    override fun onCreate() {
        super.onCreate()
        destroyed = false
        Log.d(TAG, "onCreate")

        runCatchingLogged(TAG, "createNotificationChannel failed") {
            createNotificationChannel()
        }

        val resolvedWindowManager = runCatchingLogged(TAG, "WindowManager initialization failed") {
            getSystemService(WindowManager::class.java)
        }
        if (resolvedWindowManager == null) {
            Log.e(TAG, "WindowManager is unavailable; overlay cannot start")
            return
        }
        windowManager = resolvedWindowManager

        val initializedViewModel = runCatchingLogged(TAG, "Overlay ViewModel initialization failed") {
            overlayOwners.resume()
            ViewModelProvider(
                overlayOwners,
                OverlayViewModel.provideFactory(settings, notificationRepository)
            )[OverlayViewModel::class.java]
        }
        if (initializedViewModel == null) {
            Log.e(TAG, "Overlay ViewModel is unavailable; overlay cannot start")
            return
        }
        viewModel = initializedViewModel

        val screenFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        runCatchingLogged(TAG, "registerReceiver screenStateReceiver failed") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(screenStateReceiver, screenFilter, Context.RECEIVER_EXPORTED)
            } else {
                @Suppress("UnspecifiedRegisterReceiverFlag")
                registerReceiver(screenStateReceiver, screenFilter)
            }
            screenStateReceiverRegistered = true
        }

        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        isLockScreenActive = keyguardManager?.isKeyguardLocked == true

        serviceScope.launch {
            runSuspendCatchingLogged(TAG, "Settings collector failed") {
                settings.settingsFlow.collect { prefs ->
                    if (destroyed) return@collect
                    val enabled = prefs[NotifyFXSettings.SettingsKeys.ENABLED] ?: true
                    val showOnLock = prefs[NotifyFXSettings.SettingsKeys.SHOW_ON_LOCK_SCREEN] ?: true
                    val showInLandscape = prefs[NotifyFXSettings.SettingsKeys.SHOW_IN_LANDSCAPE] ?: true
                    viewModel.updateSettings(enabled, showOnLock, showInLandscape)
                }
            }
        }

        serviceScope.launch {
            runSuspendCatchingLogged(TAG, "Notifications collector failed") {
                viewModel.notifications.collectLatest {
                    if (destroyed) return@collectLatest
                    updateWindowVisibility()
                }
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        isSystemConnected = true
        if (destroyed || !::viewModel.isInitialized) return
        serviceScope.launch {
            runSuspendCatchingLogged(TAG, "Service reconnect failed") {
                val prefs = settings.snapshot()
                val enabled = prefs[NotifyFXSettings.SettingsKeys.ENABLED] ?: true
                if (enabled) {
                    startOverlaySession()
                } else {
                    stopOverlaySession()
                }
            }
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        isSystemConnected = false
        return true
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        runCatchingLogged(TAG, "onTaskRemoved recovery failed") {
            if (!destroyed && ::viewModel.isInitialized) {
                val enabled = viewModel.settingsEnabled.value == true
                if (enabled) {
                    ensureForegroundStarted()
                    ensureOverlayWindow()
                }
            }
        }
    }

    override fun onDestroy() {
        if (destroyed) return
        destroyed = true
        isSystemConnected = false

        if (screenStateReceiverRegistered) {
            runCatchingLogged(TAG, "unregisterReceiver screenStateReceiver failed") {
                unregisterReceiver(screenStateReceiver)
            }
            screenStateReceiverRegistered = false
        }

        removeOverlayWindow()
        stopForegroundSafely()
        runCatchingLogged(TAG, "Overlay owners destroy failed") {
            overlayOwners.destroy()
        }
        super.onDestroy()
    }

    private fun startOverlaySession() {
        if (destroyed || !::windowManager.isInitialized || !::viewModel.isInitialized) return
        ensureForegroundStarted()
        ensureOverlayWindow()
        updateWindowVisibility()
    }

    private fun stopOverlaySession() {
        removeOverlayWindow()
        stopForegroundSafely()
        if (::viewModel.isInitialized) {
            viewModel.clearNotifications()
        }
    }

    private fun ensureForegroundStarted() {
        if (foregroundStarted || destroyed) return
        runCatchingLogged(TAG, "startForeground failed") {
            startForeground(NOTIFICATION_ID, buildNotification())
            foregroundStarted = true
        }
    }

    private fun stopForegroundSafely() {
        if (!foregroundStarted) return
        runCatchingLogged(TAG, "stopForeground failed") {
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        }
        foregroundStarted = false
    }

    private fun ensureOverlayWindow() {
        if (destroyed || overlayView != null || !::windowManager.isInitialized || !::viewModel.isInitialized) return
        try {
            overlayView = ComposeView(this).apply {
                val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
                val isLocked = keyguardManager?.isKeyguardLocked == true
                isLockScreenActive = isLocked
                val isLandscape = resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
                val isHidden = (viewModel.settingsShowOnLockScreen.value != true && isLocked) || (isLandscape && viewModel.settingsShowInLandscape.value != true)
                visibility = if (isHidden) android.view.View.GONE else android.view.View.VISIBLE

                installOverlayViewTreeOwners()
                isFocusable = true
                isFocusableInTouchMode = true
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
                setContent {
                    NotificationOverlayView(
                        viewModel = this@NotifyFXOverlayService.viewModel,
                        onNotificationClick = { notification -> openNotification(notification) },
                        onActionClick = { notification, actionIndex -> performAction(notification, actionIndex) },
                        onDismiss = { notification -> dismissNotification(notification) }
                    )
                }
            }
            runCatchingLogged(TAG, "windowManager.addView failed") {
                windowManager.addView(overlayView, createLayoutParams())
            } ?: run {
                overlayView = null
            }
        } catch (e: Exception) {
            Log.e(TAG, "ensureOverlayWindow fatal", e)
            overlayView = null
        }
    }

    private fun createLayoutParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }.also {
            lastParams = it
        }
    }

    private fun updateWindowVisibility() {
        if (destroyed || !::windowManager.isInitialized || !::viewModel.isInitialized) return
        val view = overlayView ?: return

        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        val isLocked = keyguardManager?.isKeyguardLocked == true
        isLockScreenActive = isLocked
        viewModel.isLocked.value = isLocked

        val isLandscape = resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
        val isHidden = (viewModel.settingsShowOnLockScreen.value != true && isLocked) || (isLandscape && viewModel.settingsShowInLandscape.value != true)
        val hasNotifications = viewModel.notifications.value.isNotEmpty()
        val enabled = viewModel.settingsEnabled.value == true

        val shouldShow = enabled && hasNotifications && !isHidden
        val targetVisibility = if (shouldShow) android.view.View.VISIBLE else android.view.View.GONE

        if (view.visibility != targetVisibility) {
            view.visibility = targetVisibility
            isWindowVisible = shouldShow
        }
    }

    private fun removeOverlayWindow() {
        val view = overlayView ?: return
        overlayView = null
        lastParams = null
        isWindowVisible = false
        if (!::windowManager.isInitialized) return
        runCatchingLogged(TAG, "Failed to remove view") {
            if (view.isAttachedToWindow) {
                windowManager.removeViewImmediate(view)
            }
        }
    }

    private fun openNotification(notification: NotificationModel) {
        if (notification.contentIntent != null) {
            sendIntentWithOptions(notification.contentIntent!!)
        } else {
            runCatchingLogged(TAG, "Failed to launch package activity") {
                val launchIntent = packageManager.getLaunchIntentForPackage(notification.packageName)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(launchIntent)
                } else {
                    Toast.makeText(this, "Opening ${notification.appName}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        notificationRepository.removeNotificationsForPackage(notification.packageName)
        viewModel.collapse()
    }

    private fun performAction(notification: NotificationModel, actionIndex: Int) {
        val actions = notification.actions
        if (actionIndex in actions.indices) {
            val action = actions[actionIndex]
            if (action.isQuickReply) {
                // TODO: Show inline reply UI
                viewModel.showQuickReply(notification, action)
            } else if (action.pendingIntent != null) {
                sendIntentWithOptions(action.pendingIntent!!)
                notificationRepository.removeNotification(notification.key)
            }
        }
    }

    private fun dismissNotification(notification: NotificationModel) {
        notificationRepository.removeNotification(notification.key)
        notificationRepository.sendCommand(NotificationCommand.CancelNotification(notification.key))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "NotifyFX Overlay",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps the NotifyFX overlay running"
                setShowBadge(false)
            }
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            nm?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("NotifyFX is active")
            .setContentText("Customizing your notifications")
            .setSmallIcon(R.drawable.ic_stat_notifyfx)
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setShowWhen(false)
            .build()
    }

    private fun ComposeView.installOverlayViewTreeOwners() {
        setViewTreeLifecycleOwner(overlayOwners)
        setViewTreeViewModelStoreOwner(overlayOwners)
        setViewTreeSavedStateRegistryOwner(overlayOwners)
    }

    companion object {
        @Volatile var isSystemConnected: Boolean = false
            private set
        private const val TAG = "NotifyFXOverlayService"
        private const val NOTIFICATION_ID = 8106
        private const val CHANNEL_ID = "notifyfx_overlay"
    }
}

fun Context.sendIntentWithOptions(pendingIntent: PendingIntent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        try {
            pendingIntent.send(this, 0, null, null, null, 0, null)
        } catch (e: Exception) {
            Log.e("NotifyFX", "Failed to send intent", e)
        }
    } else {
        try {
            pendingIntent.send()
        } catch (e: Exception) {
            Log.e("NotifyFX", "Failed to send intent", e)
        }
    }
}