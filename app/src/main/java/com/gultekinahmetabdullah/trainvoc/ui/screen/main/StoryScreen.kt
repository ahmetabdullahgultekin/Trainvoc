import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
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
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            items(levels.size) { index ->
                val (level, isUnlocked) = levels.entries.elementAt(index)
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable(enabled = isUnlocked) {
                            onLevelSelected(level)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    LeafButton(
                        text = level.longName,
                        isUnlocked = isUnlocked,
                        onClick = { onLevelSelected(level) }
                    )
                    if (!isUnlocked) {
                        Text(
                            text = "Kilitli - Önceki seviyedeki tüm kelimeleri öğren!",
                            color = Color.Red,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(top = 8.dp)
                        )
                    }
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
        color = if (isUnlocked) androidx.compose.material3.MaterialTheme.colorScheme.primary else Color.Gray,
        modifier = Modifier
            .width(256.dp)
            .height(128.dp),
        tonalElevation = if (isUnlocked) 8.dp else 0.dp,
        shadowElevation = if (isUnlocked) 8.dp else 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = isUnlocked) { onClick() }
                .border(
                    width = 2.dp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                    shape = leafShape
                )
        ) {
            if (isUnlocked) {
                Text(
                    text,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_lock_24),
                    contentDescription = "Locked",
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
