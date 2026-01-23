package com.gultekinahmetabdullah.trainvoc.ui.multiplayer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.multiplayer.data.RoomListItem
import com.gultekinahmetabdullah.trainvoc.viewmodel.RoomState

/**
 * Join Room Screen - Shows available rooms and allows joining with a room code.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinRoomScreen(
    roomState: RoomState,
    availableRooms: List<RoomListItem>,
    onNavigateBack: () -> Unit,
    onJoinRoom: (roomCode: String, playerName: String, avatarId: Int, password: String?) -> Unit,
    onRefreshRooms: () -> Unit,
    onRoomJoined: (roomCode: String) -> Unit
) {
    var playerName by remember { mutableStateOf("") }
    var roomCode by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var selectedRoom by remember { mutableStateOf<RoomListItem?>(null) }

    // Refresh rooms on load
    LaunchedEffect(Unit) {
        onRefreshRooms()
    }

    // Navigate when room is joined
    LaunchedEffect(roomState) {
        if (roomState is RoomState.InLobby) {
            onRoomJoined(roomState.roomCode)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Join Room") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onRefreshRooms) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Player Name Input
            OutlinedTextField(
                value = playerName,
                onValueChange = { playerName = it.take(20) },
                label = { Text("Display Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Room Code Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = roomCode,
                    onValueChange = { roomCode = it.uppercase().take(6) },
                    label = { Text("Room Code") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                val isJoining = roomState is RoomState.Joining
                val canJoin = playerName.isNotBlank() && roomCode.length >= 4

                Button(
                    onClick = {
                        // Check if room requires password
                        val room = availableRooms.find { it.roomCode == roomCode }
                        if (room?.hasPassword == true) {
                            selectedRoom = room
                            showPasswordDialog = true
                        } else {
                            onJoinRoom(roomCode, playerName, 0, null)
                        }
                    },
                    enabled = canJoin && !isJoining
                ) {
                    if (isJoining) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Join")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            // Available Rooms Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Available Rooms",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${availableRooms.size} rooms",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (availableRooms.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No rooms available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Create your own or enter a room code",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableRooms) { room ->
                        RoomCard(
                            room = room,
                            enabled = playerName.isNotBlank() && roomState !is RoomState.Joining,
                            onClick = {
                                roomCode = room.roomCode
                                if (room.hasPassword) {
                                    selectedRoom = room
                                    showPasswordDialog = true
                                } else {
                                    onJoinRoom(room.roomCode, playerName, 0, null)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Password Dialog
    if (showPasswordDialog && selectedRoom != null) {
        AlertDialog(
            onDismissRequest = {
                showPasswordDialog = false
                selectedRoom = null
                password = ""
            },
            title = { Text("Enter Password") },
            text = {
                Column {
                    Text("Room '${selectedRoom?.roomCode}' requires a password.")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedRoom?.let { room ->
                            onJoinRoom(room.roomCode, playerName, 0, password)
                        }
                        showPasswordDialog = false
                        password = ""
                    }
                ) {
                    Text("Join")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPasswordDialog = false
                        selectedRoom = null
                        password = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun RoomCard(
    room: RoomListItem,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = room.roomCode,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (room.hasPassword) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password protected",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Host: ${room.hostName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Level: ${room.level}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${room.playerCount}/${room.maxPlayers}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
