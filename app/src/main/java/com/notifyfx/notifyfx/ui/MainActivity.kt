package com.notifyfx.notifyfx.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.notifyfx.notifyfx.NotifyFXApp
import com.notifyfx.notifyfx.R
import com.notifyfx.notifyfx.data.INotificationRepository
import com.notifyfx.notifyfx.data.NotifyFXSettings
import com.notifyfx.notifyfx.data.NotificationRepositoryImpl
import com.notifyfx.notifyfx.di.AppModule
import com.notifyfx.notifyfx.ui.OverlayViewModel
import com.notifyfx.notifyfx.ui.screens.HomeScreen
import com.notifyfx.notifyfx.util.isNotificationListenerEnabled
import com.notifyfx.notifyfx.util.isOverlayPermissionGranted
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var notificationRepository: INotificationRepository
    @Inject lateinit var settings: NotifyFXSettings
    @Inject lateinit var database: com.notifyfx.notifyfx.data.NotifyFXDatabase

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    HomeScreen(
                        viewModel = viewModel,
                        onOpenDesigner = { startActivity(Intent(this, DesignerActivity::class.java)) },
                        onOpenSettings = { startActivity(Intent(this, SettingsActivity::class.java)) },
                        onOpenHistory = { startActivity(Intent(this, HistoryActivity::class.java)) },
                        onOpenAppStyles = { startActivity(Intent(this, AppStylesActivity::class.java)) },
                        onRequestNotificationListener = { requestNotificationListenerPermission() },
                        onRequestOverlayPermission = { requestOverlayPermission() },
                        onRequestBatteryOptimization = { requestBatteryOptimization() }
                    )
                }
            }
        }
    }

    private fun requestNotificationListenerPermission() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        startActivity(intent)
    }

    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivity(intent)
            }
        }
    }

    private fun requestBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = getSystemService(android.os.PowerManager::class.java)
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:$packageName"))
                startActivity(intent)
            }
        }
    }
}

@AndroidEntryPoint
class DesignerActivity : ComponentActivity() {
    @Inject lateinit var settings: NotifyFXSettings
    @Inject lateinit var styleRepository: com.notifyfx.notifyfx.data.IStyleRepository

    private val viewModel: DesignerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                com.notifyfx.notifyfx.ui.screens.DesignerScreen(
                    viewModel = viewModel,
                    onSaveStyle = { style ->
                        lifecycleScope.launch {
                            styleRepository.saveStyle(style)
                        }
                    }
                )
            }
        }
    }
}

@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {
    @Inject lateinit var settings: NotifyFXSettings
    @Inject lateinit var notificationRepository: INotificationRepository
    @Inject lateinit var styleRepository: com.notifyfx.notifyfx.data.IStyleRepository

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                com.notifyfx.notifyfx.ui.screens.SettingsScreen(
                    viewModel = viewModel,
                    onRequestNotificationListener = { requestNotificationListenerPermission() },
                    onRequestOverlayPermission = { requestOverlayPermission() },
                    onRequestBatteryOptimization = { requestBatteryOptimization() }
                )
            }
        }
    }

    private fun requestNotificationListenerPermission() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        startActivity(intent)
    }

    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivity(intent)
            }
        }
    }

    private fun requestBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = getSystemService(android.os.PowerManager::class.java)
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:$packageName"))
                startActivity(intent)
            }
        }
    }
}

@AndroidEntryPoint
class HistoryActivity : ComponentActivity() {
    @Inject lateinit var historyRepository: com.notifyfx.notifyfx.data.IHistoryRepository

    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                com.notifyfx.notifyfx.ui.screens.HistoryScreen(viewModel = viewModel)
            }
        }
    }
}

@AndroidEntryPoint
class AppStylesActivity : ComponentActivity() {
    @Inject lateinit var styleRepository: com.notifyfx.notifyfx.data.IStyleRepository

    private val viewModel: AppStylesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                com.notifyfx.notifyfx.ui.screens.AppStylesScreen(viewModel = viewModel)
            }
        }
    }
}