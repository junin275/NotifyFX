package com.notifyfx.notifyfx.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notifyfx.notifyfx.R
import com.notifyfx.notifyfx.ui.AppStylesViewModel

@Composable
fun AppStylesScreen(viewModel: AppStylesViewModel) {
    val appStyles by viewModel.appStyles.observeAsState(emptyList())
    val recentApps by viewModel.recentApps.observeAsState(emptyList())
    val allStyles by viewModel.allStyles.observeAsState(emptyList())

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TopAppBar(
            title = { Text("Per-App Styles") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            navigationIcon = {
                androidx.compose.material3.IconButton(onClick = { /* navigate back */ }) {
                    androidx.compose.material3.Icon(imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        androidx.compose.foundation.layout.Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Configure different notification styles for different apps",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            if (recentApps.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Apps,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.foundation.layout.size(16.dp))
                        Text(
                            text = "No apps with recent notifications",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.foundation.layout.size(8.dp))
                        Text(
                            text = "Notifications from apps will appear here once received",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recentApps) { app ->
                        AppStyleRow(
                            app = app,
                            currentStyleId = appStyles.find { it.packageName == app.packageName }?.styleId,
                            allStyles = allStyles,
                            onStyleChange = { styleId ->
                                viewModel.setAppStyle(app.packageName, styleId)
                            },
                            onRemove = {
                                viewModel.removeAppStyle(app.packageName)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppStyleRow(
    app: AppStylesViewModel.AppInfo,
    currentStyleId: String?,
    allStyles: List<com.notifyfx.notifyfx.model.NotificationStyle>,
    onStyleChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    val currentStyle = allStyles.find { it.id == currentStyleId } ?: allStyles.firstOrNull() ?: com.notifyfx.notifyfx.model.NotificationStyle.getDefault()
    var showDropdown by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (app.icon != null) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.graphics.BitmapPainter(
                            (app.icon as android.graphics.drawable.BitmapDrawable).bitmap
                        ),
                        contentDescription = app.appName,
                        modifier = Modifier.size(40.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Apps,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Column {
                    Text(text = app.appName, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Text(text = currentStyle.name, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Style selector dropdown
            Box {
                androidx.compose.material3.OutlinedButton(
                    onClick = { showDropdown = !showDropdown }
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Style", fontSize = 14.sp)
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ExpandMore,
                            contentDescription = null
                        )
                    }
                }
                androidx.compose.material3.DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = false }
                ) {
                    allStyles.forEach { style ->
                        androidx.compose.material3.DropdownMenuItem(
                            onClick = {
                                onStyleChange(style.id)
                                showDropdown = false
                            },
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = style.name, fontSize = 14.sp)
                                    if (style.id == currentStyleId) {
                                        androidx.compose.material3.Icon(
                                            imageVector = androidx.compose.material.icons.Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}