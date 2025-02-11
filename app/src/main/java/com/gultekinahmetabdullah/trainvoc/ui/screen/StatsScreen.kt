package com.gultekinahmetabdullah.trainvoc.ui.screen//package com.gultekinahmetabdullah.trainvoc.ui
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.gultekinahmetabdullah.trainvoc.viewmodel.WordViewModel
//
//@Composable
//fun WordStatsScreen(wordViewModel: WordViewModel = viewModel()) {
//    val totalAnswers by wordViewModel.getTotalAnswers().observeAsState(0)
//    val correctPercentage by wordViewModel.getCorrectPercentage().observeAsState(0.0)
//    val wrongPercentage by wordViewModel.getWrongPercentage().observeAsState(0.0)
//    val skippedPercentage by wordViewModel.getSkippedPercentage().observeAsState(0.0)
//    val leastKnownWords by wordViewModel.getLeastKnownWords().observeAsState(emptyList())
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        Text(text = "Word Statistics", style = MaterialTheme.typography.headlineLarge)
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "Total Answers: $totalAnswers")
//        Text(text = "Correct Percentage: ${"%.2f".format(correctPercentage)}%")
//        Text(text = "Wrong Percentage: ${"%.2f".format(wrongPercentage)}%")
//        Text(text = "Skipped Percentage: ${"%.2f".format(skippedPercentage)}%")
//        Spacer(modifier = Modifier.height(16.dp))
//        Text(text = "Least Known Words:")
//        leastKnownWords.forEach { word ->
//            Text(text = "- ${word.word}: ${word.numberOfCorrectAnswers} correct answers")
//        }
//    }
//}
