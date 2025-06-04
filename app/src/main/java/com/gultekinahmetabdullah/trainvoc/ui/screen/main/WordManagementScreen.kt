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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.word.Exam
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.classes.word.WordAskedInExams
import com.gultekinahmetabdullah.trainvoc.viewmodel.WordViewModel

@Composable
fun WordManagementScreen(wordViewModel: WordViewModel) {
    val words = wordViewModel.words.collectAsState().value
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    val filteredWords = words.filter { word ->
        word.word.word.contains(searchQuery.text, ignoreCase = true) ||
                word.word.meaning.contains(searchQuery.text, ignoreCase = true)
    }

    val expendedNewWord = remember { mutableStateOf(false) }

    var wordInput by remember { mutableStateOf(TextFieldValue("")) }
    var meaningInput by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.word_management),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text(stringResource(id = R.string.search)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Make text fields collapsible
        Button(onClick = { expendedNewWord.value = !expendedNewWord.value }) {
            Text(stringResource(id = R.string.add_new_word))
        }

        if (expendedNewWord.value) {
            // Input Fields
            OutlinedTextField(
                value = wordInput,
                onValueChange = { wordInput = it },
                label = { Text(stringResource(id = R.string.word)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = meaningInput,
                onValueChange = { meaningInput = it },
                label = { Text(stringResource(id = R.string.explanation)) },
                modifier = Modifier.fillMaxWidth()
            )

            WordSelectionForm()

            Spacer(modifier = Modifier.height(8.dp))

            // Insert Button
            Button(
                onClick = {
                    if (wordInput.text.isNotBlank() && meaningInput.text.isNotBlank()) {
                        val newWord = Word(
                            word = wordInput.text,
                            meaning = meaningInput.text,
                        )
                        wordViewModel.insertWord(newWord)
                        wordInput = TextFieldValue("")
                        meaningInput = TextFieldValue("")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.add_word))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Words
        LazyColumn {
            items(filteredWords) { word ->
                WordCard(word)
            }
        }
    }
}

@Composable
fun WordCard(word: WordAskedInExams) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.word_colon, word.word.word),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(id = R.string.meaning_colon, word.word.meaning),
                style = MaterialTheme.typography.bodyMedium
            )
            // Add more details and stats here
            Text(
                text = stringResource(
                    id = R.string.level_colon,
                    word.word.level?.longName ?: stringResource(id = R.string.na)
                ),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(
                    id = R.string.category_colon,
                    word.exams.joinToString(", ") { it.exam }),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(
                    id = R.string.last_reviewed_colon,
                    word.word.lastReviewed ?: stringResource(id = R.string.na)
                ),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(id = R.string.stat_id_colon, word.word.statId),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordLevelComboBox() {
    var expanded by remember { mutableStateOf(false) }
    var selectedLevel by remember { mutableStateOf<WordLevel?>(null) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedLevel?.longName ?: stringResource(id = R.string.select_level),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(id = R.string.level)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textStyle = TextStyle.Default
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            WordLevel.entries.forEach { level ->
                DropdownMenuItem(
                    text = { Text(level.longName) },
                    onClick = {
                        selectedLevel = level
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordCategoryComboBox() {
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Exam?>(null) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedCategory?.exam ?: stringResource(id = R.string.select_category),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(id = R.string.category)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textStyle = TextStyle.Default
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Exam.examTypes.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.exam) },
                    onClick = {
                        selectedCategory = category
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun WordSelectionForm() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        WordLevelComboBox()
        WordCategoryComboBox()
    }
}