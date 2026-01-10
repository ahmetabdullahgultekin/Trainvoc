import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.gultekinahmetabdullah.trainvoc.viewmodel.StoryViewModel

@Composable
fun StoryScreen(
    viewModel: StoryViewModel,
    onLevelSelected: (WordLevel) -> Unit,
    onBack: () -> Unit
) {
    val levels = viewModel.levels.collectAsState().value

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
            items(levels.size, key = { index -> levels.entries.elementAt(index).key }) { index ->
                val (level, isUnlocked) = levels.entries.elementAt(index)
                Box(
                    modifier = Modifier
                        .padding(horizontal = Spacing.mediumLarge),
                    contentAlignment = Alignment.Center
                ) {
                    LeafButton(
                        text = level.longName,
                        isUnlocked = isUnlocked,
                        onClick = { onLevelSelected(level) }
                    )
                }
            }
        }
    }
}

@Composable
fun LeafButton(text: String, isUnlocked: Boolean, onClick: () -> Unit) {
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
            /*.border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
                *//*shape = leafShape*//*
                )*/
        ) {
            if (isUnlocked) {
                Text(
                    text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_lock_24),
                    contentDescription = "Locked",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = stringResource(id = R.string.locked_level_warning),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(top = Spacing.small)
                        .fillMaxWidth(0.5f)
                )
            }
        }
    }
}
