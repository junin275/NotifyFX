package com.notifyfx.notifyfx.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.spacedBy
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notifyfx.notifyfx.R
import com.notifyfx.notifyfx.ui.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onRequestNotificationListener: () -> Unit,
    onRequestOverlayPermission: () -> Unit,
    onRequestBatteryOptimization: () -> Unit
) {
    val enabled by viewModel.enabled.observeAsState(true)
    val hideOriginal by viewModel.hideOriginal.observeAsState(false)
    val notificationHistory by viewModel.notificationHistory.observeAsState(true)
    val historyRetentionDays by viewModel.historyRetentionDays.observeAsState(30)
    val playSound by viewModel.playSound.observeAsState(true)
    val vibration by viewModel.vibration.observeAsState(true)
    val autoExpand by viewModel.autoExpand.observeAsState(false)
    val showOnLockScreen by viewModel.showOnLockScreen.observeAsState(true)
    val showInLandscape by viewModel.showInLandscape.observeAsState(true)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TopAppBar(
            title = { Text("Settings") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            navigationIcon = {
                androidx.compose.material3.IconButton(onClick = { /* navigate back */ }) {
                    androidx.compose.material.Icon(imageVector = androidx.compose.material.icons.default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        androidx.compose.foundation.layout.Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Notifications Section
            SettingsSection("Notifications") {
                SettingRow(
                    title = "Enable NotifyFX",
                    description = "Intercept and customize system notifications",
                    icon = androidx.compose.material.icons.default.NotificationsActive,
                    trailing = {
                        Switch(checked = enabled, onCheckedChange = viewModel::setEnabled)
                    }
                )
                SettingRow(
                    title = "Hide Original Notifications",
                    description = "Remove notifications from system shade when customized",
                    icon = androidx.compose.material.icons.default.VisibilityOff,
                    trailing = {
                        Switch(checked = hideOriginal, onCheckedChange = viewModel::setHideOriginal)
                    },
                    enabled = enabled
                )
                SettingRow(
                    title = "Play Notification Sound",
                    description = "Play sound when notification arrives",
                    icon = androidx.compose.material.icons.default.VolumeUp,
                    trailing = {
                        Switch(checked = playSound, onCheckedChange = viewModel::setPlaySound)
                    },
                    enabled = enabled
                )
                SettingRow(
                    title = "Vibration",
                    description = "Vibrate on new notification",
                    icon = androidx.compose.material.icons.default.Vibration,
                    trailing = {
                        Switch(checked = vibration, onCheckedChange = viewModel::setVibration)
                    },
                    enabled = enabled
                )
                SettingRow(
                    title = "Auto-expand on new notification",
                    description = "Automatically expand the custom notification",
                    icon = androidx.compose.material.icons.default.KeyboardArrowDown,
                    trailing = {
                        Switch(checked = autoExpand, onCheckedChange = viewModel::setAutoExpand)
                    },
                    enabled = enabled
                )
            }

            // Appearance Section
            SettingsSection("Appearance") {
                SettingRow(
                    title = "Show on Lock Screen",
                    description = "Display notifications when device is locked",
                    icon = androidx.compose.material.icons.default.Lock,
                    trailing = {
                        Switch(checked = showOnLockScreen, onCheckedChange = viewModel::setShowOnLockScreen)
                    }
                )
                SettingRow(
                    title = "Show in Landscape",
                    description = "Display notifications in landscape orientation",
                    icon = androidx.compose.material.icons.default.ScreenRotation,
                    trailing = {
                        Switch(checked = showInLandscape, onCheckedChange = viewModel::setShowInLandscape)
                    }
                )
            }

            // History Section
            SettingsSection("History") {
                SettingRow(
                    title = "Notification History",
                    description = "Keep local history of received notifications",
                    icon = androidx.compose.material.icons.default.History,
                    trailing = {
                        Switch(checked = notificationHistory, onCheckedChange = viewModel::setNotificationHistory)
                    }
                )
                if (notificationHistory) {
                    SettingRow(
                        title = "History Retention",
                        description = "Days to keep notification history",
                        icon = androidx.compose.material.icons.default.Schedule,
                        trailing = {
                            Text(text = "$historyRetentionDays days", fontSize = 14.sp)
                        },
                        onClick = {
                            // TODO: Show picker
                        }
                    )
                }
            }

            // Permissions Section
            SettingsSection("Permissions") {
                SettingRow(
                    title = "Notification Listener",
                    description = "Required to read notifications",
                    icon = androidx.compose.material.icons.default.Security,
                    trailing = {
                        Button(onClick = onRequestNotificationListener) { Text("Grant") }
                    }
                )
                SettingRow(
                    title = "Display Over Other Apps",
                    description = "Required to show custom notifications",
                    icon = androidx.compose.material.icons.default.Layers,
                    trailing = {
                        Button(onClick = onRequestOverlayPermission) { Text("Grant") }
                    }
                )
                SettingRow(
                    title = "Battery Optimization",
                    description = "Disable to keep service running",
                    icon = androidx.compose.material.icons.default.BatteryAlert,
                    trailing = {
                        Button(onClick = onRequestBatteryOptimization) { Text("Disable") }
                    }
                )
            }

            // Danger Zone
            SettingsSection("Danger Zone") {
                SettingRow(
                    title = "Clear All Notifications",
                    description = "Remove all active custom notifications",
                    icon = androidx.compose.material.icons.default.DeleteSweep,
                    trailing = {
                        Button(onClick = { viewModel.clearAllNotifications() }, colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                            Text("Clear")
                        }
                    }
                )
            }

            // About
            SettingsSection("About") {
                SettingRow(
                    title = "Version",
                    description = "NotifyFX 1.0.0",
                    icon = androidx.compose.material.icons.default.Info
                )
                SettingRow(
                    title = "Privacy",
                    description = "Your data stays on your device",
                    icon = androidx.compose.material.icons.default.PrivacyTip,
                    onClick = {
                        // TODO: Show privacy dialog
                    }
                )
                SettingRow(
                    title = "Open Source",
                    description = "View source code on GitHub",
                    icon = androidx.compose.material.icons.default.Code,
                    onClick = {
                        // TODO: Open GitHub
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 12.dp))
            content()
        }
    }
}

@Composable
fun SettingRow(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    trailing: @Composable () -> Unit = {},
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    val alpha = if (enabled) 1f else 0.5f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .alpha(alpha),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material.Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = alpha)
            )
            Column {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(text = description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        trailing()
    }
    if (onClick != null) {
        androidx.compose.material3.Text(
            text = "",
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
                .padding(top = -12.dp)
        )
    }
}