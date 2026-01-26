package com.gultekinahmetabdullah.trainvoc.ui.multiplayer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.multiplayer.websocket.ConnectionState
import com.gultekinahmetabdullah.trainvoc.ui.components.InlineLoader
import com.gultekinahmetabdullah.trainvoc.ui.components.LoaderSize

/**
 * Multiplayer Home Screen - Entry point for multiplayer games.
 *
 * Shows connection status and options to:
 * - Create a new game room
 * - Join an existing game room
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiplayerHomeScreen(
    connectionState: ConnectionState,
    onNavigateBack: () -> Unit,
    onCreateRoom: () -> Unit,
    onJoinRoom: () -> Unit,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit
) {
    // Auto-connect when screen loads
    LaunchedEffect(Unit) {
        if (connectionState is ConnectionState.Disconnected) {
            onConnect()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Multiplayer") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Connection Status Card
            ConnectionStatusCard(
                connectionState = connectionState,
                onConnect = onConnect,
                onDisconnect = onDisconnect
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Play with Friends",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Challenge your friends in real-time vocabulary battles!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            val isConnected = connectionState is ConnectionState.Connected

            MultiplayerActionButton(
                icon = Icons.Default.Add,
                title = "Create Room",
                subtitle = "Host a new game",
                enabled = isConnected,
                onClick = onCreateRoom
            )

            Spacer(modifier = Modifier.height(16.dp))

            MultiplayerActionButton(
                icon = Icons.Default.Group,
                title = "Join Room",
                subtitle = "Enter a room code",
                enabled = isConnected,
                onClick = onJoinRoom
            )

            Spacer(modifier = Modifier.weight(1f))

            // Info text
            Text(
                text = "Compete against up to 8 players in fast-paced vocabulary quizzes. Answer correctly and quickly to earn more points!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun ConnectionStatusCard(
    connectionState: ConnectionState,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit
) {
    val (icon, text, color, isError) = when (connectionState) {
        is ConnectionState.Connected -> Quadruple(
            Icons.Default.Wifi,
            "Connected",
            MaterialTheme.colorScheme.primary,
            false
        )
        is ConnectionState.Connecting -> Quadruple(
            Icons.Default.Wifi,
            "Connecting...",
            MaterialTheme.colorScheme.tertiary,
            false
        )
        is ConnectionState.Disconnected -> Quadruple(
            Icons.Default.WifiOff,
            "Disconnected",
            MaterialTheme.colorScheme.error,
            true
        )
        is ConnectionState.Error -> Quadruple(
            Icons.Default.WifiOff,
            connectionState.message,
            MaterialTheme.colorScheme.error,
            true
        )
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isError)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Server Status",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            when (connectionState) {
                is ConnectionState.Disconnected, is ConnectionState.Error -> {
                    TextButton(onClick = onConnect) {
                        Text("Reconnect")
                    }
                }
                is ConnectionState.Connecting -> {
                    InlineLoader(
                        size = LoaderSize.tiny,
                        modifier = Modifier.size(24.dp)
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun MultiplayerActionButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
