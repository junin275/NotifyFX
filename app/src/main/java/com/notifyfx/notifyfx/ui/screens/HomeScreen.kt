package com.notifyfx.notifyfx.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.spacedBy
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.notifyfx.notifyfx.R
import com.notifyfx.notifyfx.model.NotificationStyle
import com.notifyfx.notifyfx.ui.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onOpenDesigner: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenAppStyles: () -> Unit,
    onRequestNotificationListener: () -> Unit,
    onRequestOverlayPermission: () -> Unit,
    onRequestBatteryOptimization: () -> Unit
) {
    val enabled by viewModel.settingsEnabled.observeAsState(true)
    val notificationListener by viewModel.notificationListenerEnabled.observeAsState(false)
    val overlayPermission by viewModel.overlayPermissionGranted.observeAsState(false)
    val batteryOptimization by viewModel.batteryOptimizationDisabled.observeAsState(false)

    val allPermissionsGranted = notificationListener && overlayPermission && batteryOptimization

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NotifyFX",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.foundation.layout.size(8.dp))
            Text(
                text = "Customize your notifications",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Status Card
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (allPermissionsGranted)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (allPermissionsGranted) "All permissions granted" else "Missing permissions",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (!allPermissionsGranted) {
                        Text(
                            text = "Setup required",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.foundation.layout.size(12.dp))
                PermissionRow(
                    title = "Notification Listener",
                    description = "Read and customize notifications",
                    granted = notificationListener,
                    onClick = onRequestNotificationListener
                )
                PermissionRow(
                    title = "Display Over Other Apps",
                    description = "Show customized notifications",
                    granted = overlayPermission,
                    onClick = onRequestOverlayPermission
                )
                PermissionRow(
                    title = "Battery Optimization",
                    description = "Keep service running in background",
                    granted = batteryOptimization,
                    onClick = onRequestBatteryOptimization
                )
            }
        }

        // Main Actions
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionCard(
                title = "Notification Designer",
                description = "Create and preview custom notification styles",
                icon = androidx.compose.material.icons.Icons.Default.DashboardCustomize,
                onClick = onOpenDesigner
            )
            ActionCard(
                title = "Per-App Styles",
                description = "Different styles for different apps",
                icon = androidx.compose.material.icons.Icons.Default.GridView,
                onClick = onOpenAppStyles
            )
            ActionCard(
                title = "Settings",
                description = "Configure NotifyFX behavior",
                icon = androidx.compose.material.icons.Icons.Default.Settings,
                onClick = onOpenSettings
            )
            ActionCard(
                title = "History",
                description = "View past notifications",
                icon = androidx.compose.material.icons.Icons.Default.History,
                onClick = onOpenHistory
            )
        }

        // Privacy Notice
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        ) {
            Text(
                text = "Your notifications are processed locally on this device. No internet connection is used for notification processing. No analytics, tracking, or data collection.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun PermissionRow(
    title: String,
    description: String,
    granted: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(text = description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (granted) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.CheckCircle,
                contentDescription = "Granted",
                tint = MaterialTheme.colorScheme.primary
            )
        } else {
            Button(
                onClick = onClick,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text("Grant", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(text = description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}