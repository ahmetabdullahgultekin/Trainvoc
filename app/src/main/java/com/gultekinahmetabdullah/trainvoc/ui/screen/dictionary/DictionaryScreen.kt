package com.gultekinahmetabdullah.trainvoc.ui.screen.dictionary

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import com.gultekinahmetabdullah.trainvoc.viewmodel.WordViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DictionaryScreen(navController: NavController, wordViewModel: WordViewModel) {
    var search by remember { mutableStateOf("") }
    val filteredWords by wordViewModel.filteredWords.collectAsState()

    // Arama değiştikçe filtrele
    LaunchedEffect(search) {
        wordViewModel.filterWords(search)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text(stringResource(id = R.string.search_word)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn {
                items(filteredWords) { word ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { navController.navigate(Route.wordDetail(word.word)) }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(word.word, style = MaterialTheme.typography.titleMedium)
                            Text(word.meaning, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
