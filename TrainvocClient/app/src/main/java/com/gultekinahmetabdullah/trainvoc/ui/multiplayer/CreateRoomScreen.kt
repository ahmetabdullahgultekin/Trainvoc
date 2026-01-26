package com.gultekinahmetabdullah.trainvoc.ui.multiplayer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.multiplayer.websocket.RoomSettings
import com.gultekinahmetabdullah.trainvoc.ui.components.ButtonLoader
import com.gultekinahmetabdullah.trainvoc.viewmodel.RoomState

/**
 * Create Room Screen - Allows host to configure and create a new game room.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoomScreen(
    roomState: RoomState,
    onNavigateBack: () -> Unit,
    onCreateRoom: (playerName: String, avatarId: Int, password: String?, settings: RoomSettings) -> Unit,
    onRoomCreated: (roomCode: String) -> Unit
) {
    var playerName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var usePassword by remember { mutableStateOf(false) }

    // Game settings
    var questionDuration by remember { mutableIntStateOf(30) }
    var optionCount by remember { mutableIntStateOf(4) }
    var totalQuestions by remember { mutableIntStateOf(10) }
    var selectedLevel by remember { mutableStateOf("A1") }
    var hostPlays by remember { mutableStateOf(true) }

    val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")

    // Navigate when room is created
    LaunchedEffect(roomState) {
        if (roomState is RoomState.InLobby) {
            onRoomCreated(roomState.roomCode)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Room") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Player Info Section
            Text(
                text = "Your Info",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = playerName,
                onValueChange = { playerName = it.take(20) },
                label = { Text("Display Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Password Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = usePassword,
                    onCheckedChange = { usePassword = it }
                )
                Text("Require password to join")
            }

            if (usePassword) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it.take(20) },
                    label = { Text("Room Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Game Settings Section
            Text(
                text = "Game Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Difficulty Level
            Text(
                text = "Difficulty Level",
                style = MaterialTheme.typography.bodyMedium
            )
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                levels.forEachIndexed { index, level ->
                    SegmentedButton(
                        selected = selectedLevel == level,
                        onClick = { selectedLevel = level },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = levels.size)
                    ) {
                        Text(level)
                    }
                }
            }

            // Question Duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Timer, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Time per question: ${questionDuration}s")
            }
            Slider(
                value = questionDuration.toFloat(),
                onValueChange = { questionDuration = it.toInt() },
                valueRange = 10f..60f,
                steps = 4
            )

            // Total Questions
            OutlinedTextField(
                value = totalQuestions.toString(),
                onValueChange = {
                    totalQuestions = it.toIntOrNull()?.coerceIn(5, 30) ?: 10
                },
                label = { Text("Number of Questions") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Option Count
            Text(
                text = "Answer Options: $optionCount",
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = optionCount.toFloat(),
                onValueChange = { optionCount = it.toInt() },
                valueRange = 2f..6f,
                steps = 3
            )

            // Host Plays
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = hostPlays,
                    onCheckedChange = { hostPlays = it }
                )
                Text("Host participates in game")
            }

            Spacer(modifier = Modifier.weight(1f))

            // Create Button
            val isCreating = roomState is RoomState.Creating
            val canCreate = playerName.isNotBlank() && (!usePassword || password.isNotBlank())

            Button(
                onClick = {
                    val settings = RoomSettings(
                        questionDuration = questionDuration,
                        optionCount = optionCount,
                        level = selectedLevel,
                        totalQuestionCount = totalQuestions,
                        hostWantsToJoin = hostPlays
                    )
                    onCreateRoom(
                        playerName,
                        0, // Default avatar
                        if (usePassword) password else null,
                        settings
                    )
                },
                enabled = canCreate && !isCreating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isCreating) {
                    ButtonLoader(modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Creating Room...")
                } else {
                    Text("Create Room")
                }
            }
        }
    }
}
