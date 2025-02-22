package com.gultekinahmetabdullah.trainvoc.ui.screen.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.classes.word.Exam
import java.util.Locale

@Composable
fun QuizExamMenuScreen(onExamSelected: (QuizParameter) -> Unit) {
    Column(
        Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Quiz Category\nGrayed out categories are not available yet",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {

            items(WordLevel.entries.size) { index ->
                val level = WordLevel.entries[index]
                QuizCategoryCard(
                    title = level.name,
                    description = "Test your knowledge with ${level.name.lowercase(Locale.getDefault())} words",
                    color = level.color,
                    onClick = { onExamSelected(QuizParameter.Level(level)) }
                )
            }
            items(Exam.examTypes.size) { index ->
                val exam = Exam.examTypes[index]
                val color = Exam.examColors.entries.first { it.key == exam.exam }.value
                QuizCategoryCard(
                    title = exam.exam,
                    description = "Test your knowledge with ${exam.exam} words",
                    color = color,
                    onClick = { onExamSelected(QuizParameter.ExamType(exam)) }
                )
            }
        }
    }
}

@Composable
fun QuizCategoryCard(title: String, description: String, color: Color, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .size(150.dp)
            .clickable(
                enabled = if (title == "A1" || title == "A2" || title == "B1"
                    || title == "B2" || title == "C1" || title == "YDS"
                    || title == "Mixed"
                ) true else false
            )
            { onClick() },
        colors = if (title == "A1" || title == "A2" || title == "B1"
            || title == "B2" || title == "C1" || title == "YDS"
            || title == "Mixed"
        ) CardDefaults.cardColors(containerColor = color)
        else CardDefaults.cardColors(containerColor = Color.Gray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    }
}