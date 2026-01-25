package com.gultekinahmetabdullah.trainvoc.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.ui.theme.LockedLeaf
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.ui.theme.UnlockedLeaf
import com.gultekinahmetabdullah.trainvoc.viewmodel.LevelInfo
import com.gultekinahmetabdullah.trainvoc.viewmodel.StoryViewModel

@Composable
fun StoryScreen(
    viewModel: StoryViewModel,
    onLevelSelected: (WordLevel) -> Unit,
    onBack: () -> Unit
) {
    val levelInfos = viewModel.levelInfos.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.bg_4),
                contentScale = ContentScale.FillBounds
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = Spacing.mediumLarge),
            verticalArrangement = Arrangement.spacedBy(64.dp)
        ) {
            items(levelInfos.size, key = { index -> levelInfos[index].level }) { index ->
                val levelInfo = levelInfos[index]
                Box(
                    modifier = Modifier
                        .padding(horizontal = Spacing.mediumLarge),
                    contentAlignment = Alignment.Center
                ) {
                    LeafButton(
                        levelCode = levelInfo.level.name,
                        levelName = levelInfo.level.longName,
                        isUnlocked = levelInfo.isUnlocked,
                        learnedWords = levelInfo.learnedWords,
                        totalWords = levelInfo.totalWords,
                        progress = levelInfo.progress,
                        onClick = { onLevelSelected(levelInfo.level) }
                    )
                }
            }
        }
    }
}

@Composable
fun LeafButton(
    levelCode: String,
    levelName: String,
    isUnlocked: Boolean,
    learnedWords: Int,
    totalWords: Int,
    progress: Float,
    onClick: () -> Unit
) {
    val leafShape = remember {
        GenericShape { size, _ ->
            moveTo(size.width * 0.5f, 0f)
            quadraticTo(
                size.width * 1.2f, size.height * 1.5f, size.width * 0.5f, size.height * 3
            )
            quadraticTo(
                -size.width * 0.2f, size.height * 1.5f, size.width * 0.5f, 0f
            )
        }
    }

    Surface(
        shape = leafShape,
        color = if (isUnlocked) UnlockedLeaf else LockedLeaf,
        modifier = Modifier
            .width(300.dp)
            .height(150.dp),
        tonalElevation = 16.dp,
        shadowElevation = 16.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = isUnlocked) { onClick() }
        ) {
            if (isUnlocked) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(Spacing.small)
                ) {
                    // Level code (A1, A2, etc.)
                    Text(
                        text = levelCode,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    // Level name (Beginner, Elementary, etc.)
                    Text(
                        text = levelName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Progress bar
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .width(120.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.onPrimary,
                        trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
                    )
                    // Progress text
                    Text(
                        text = "$learnedWords/$totalWords words",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_lock_24),
                        contentDescription = "Locked level $levelCode",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "$levelCode - $levelName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                    )
                    Text(
                        text = stringResource(id = R.string.locked_level_warning),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(top = Spacing.small)
                            .fillMaxWidth(0.5f)
                    )
                }
            }
        }
    }
}
