package com.gultekinahmetabdullah.trainvoc.ui.screen.dictionary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.viewmodel.WordViewModel
import kotlinx.coroutines.launch

@Composable
fun WordDetailScreen(wordId: String, wordViewModel: WordViewModel) {
    // Tüm detayları asenkron çekmek için state
    val coroutineScope = rememberCoroutineScope()
    var word by remember {
        mutableStateOf<com.gultekinahmetabdullah.trainvoc.classes.word.Word?>(
            null
        )
    }
    var statistic by remember {
        mutableStateOf<com.gultekinahmetabdullah.trainvoc.classes.word.Statistic?>(
            null
        )
    }
    var exams by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(wordId) {
        coroutineScope.launch {
            val detail = wordViewModel.getWordFullDetail(wordId)
            word = detail?.word
            statistic = detail?.statistic
            exams = detail?.exams ?: emptyList()
            isLoading = false
        }
    }

    if (isLoading) {
        Text(stringResource(id = R.string.loading))
        return
    }
    if (word == null) {
        Text(stringResource(id = R.string.word_not_found))
        return
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = word!!.word, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = word!!.meaning, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.level, word!!.level.toString()),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(
                    id = R.string.last_reviewed,
                    word!!.lastReviewed?.toString() ?: "-"
                ), style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (statistic != null) {
                Text(
                    text = stringResource(
                        id = R.string.stats,
                        statistic!!.correctCount,
                        statistic!!.wrongCount,
                        statistic!!.skippedCount
                    ), style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                // secondsSpent yerine timeSpent kullanıldı (örnek)
                Text(
                    text = stringResource(id = R.string.total_seconds, word!!.secondsSpent),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (exams.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.exam_history),
                    style = MaterialTheme.typography.titleSmall
                )
                exams.forEach { exam ->
                    Text(text = exam, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
