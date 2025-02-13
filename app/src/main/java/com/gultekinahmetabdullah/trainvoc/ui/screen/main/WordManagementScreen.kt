package com.gultekinahmetabdullah.trainvoc.ui.screen.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.classes.Word
import com.gultekinahmetabdullah.trainvoc.viewmodel.WordViewModel

@Composable
fun WordManagementScreen(wordViewModel: WordViewModel) {
    val words = wordViewModel.words.collectAsState().value

    var wordInput by remember { mutableStateOf(TextFieldValue("")) }
    var explanationInput by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Word Management", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Input Fields
        OutlinedTextField(
            value = wordInput,
            onValueChange = { wordInput = it },
            label = { Text("Word") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = explanationInput,
            onValueChange = { explanationInput = it },
            label = { Text("Explanation") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Insert Button
        Button(
            onClick = {
                if (wordInput.text.isNotBlank() && explanationInput.text.isNotBlank()) {
                    val newWord = Word(
                        word = wordInput.text,
                        meaning = explanationInput.text,
                        correctCount = 0,
                        wrongCount = 0,
                    )
                    wordViewModel.insertWord(newWord)
                    wordInput = TextFieldValue("")
                    explanationInput = TextFieldValue("")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Word")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Words
        LazyColumn {
            items(words) { word ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Word: ${word.word}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Explanation: ${word.meaning}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
