package com.gultekinahmetabdullah.trainvoc.ui.screen.main

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import kotlin.math.min

@Composable
fun StoryScreen() {

    val levels = remember {
        WordLevel.entries.map { it.name }
    }
    var unlockedLevel by remember { mutableIntStateOf(1) }

    Box(modifier = Modifier.fillMaxSize()) {

        // Give a curved animal path string
        val pathStrToRight = remember { "M2,6 A 6 4 20 0 1 10 10" }
        val pathStrToLeft = remember { "M1,5 A 5 3 20 0 0 8 8" }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(64.dp),
        ) {
            item {
                Text(text = "Story Screen", modifier = Modifier.padding(16.dp))
            }
            item {
                // Coming soon
                Text(text = "Coming soon", modifier = Modifier.padding(16.dp))
            }
            item {
                LeafButton(
                    text = levels[0],
                    isUnlocked = 0 < unlockedLevel,
                    onClick = { if (0 < unlockedLevel) unlockedLevel++ }
                )
            }
            item {
                AnimatedPath(
                    pathStr = pathStrToRight,
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 8.dp,
                    easing = EaseInOutSine,
                    duration = 5000,
                    modifier = Modifier.padding(8.dp)
                )
            }
            item {
                AnimatedPath(
                    pathStr = pathStrToLeft,
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 8.dp,
                    easing = EaseInOutSine,
                    duration = 5000,
                    modifier = Modifier.padding(8.dp)
                )
            }
            item {
                LeafButton(
                    text = levels[1],
                    isUnlocked = 1 < unlockedLevel,
                    onClick = { if (1 < unlockedLevel) unlockedLevel++ }
                )
            }
            item {
                LeafButton(
                    text = levels[2],
                    isUnlocked = 2 < unlockedLevel,
                    onClick = { if (2 < unlockedLevel) unlockedLevel++ }
                )
            }
            item {
                AnimatedPath(
                    pathStr = pathStrToRight,
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 8.dp,
                    easing = EaseInOutSine,
                    duration = 5000,
                    modifier = Modifier.padding(8.dp)
                )
            }
            item {
                AnimatedPath(
                    pathStr = pathStrToLeft,
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 8.dp,
                    easing = EaseInOutSine,
                    duration = 5000,
                    modifier = Modifier.padding(8.dp)
                )
            }
            item {
                LeafButton(
                    text = levels[3],
                    isUnlocked = 3 < unlockedLevel,
                    onClick = { if (3 < unlockedLevel) unlockedLevel++ }
                )
            }
            item {
                LeafButton(
                    text = levels[4],
                    isUnlocked = 4 < unlockedLevel,
                    onClick = { if (4 < unlockedLevel) unlockedLevel++ }
                )
            }
            item {
                AnimatedPath(
                    pathStr = pathStrToRight,
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 8.dp,
                    easing = EaseInOutSine,
                    duration = 5000,
                    modifier = Modifier.padding(8.dp)
                )
            }
            item {
                AnimatedPath(
                    pathStr = pathStrToLeft,
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 8.dp,
                    easing = EaseInOutSine,
                    duration = 5000,
                    modifier = Modifier.padding(8.dp)
                )
            }
            item {
                LeafButton(
                    text = levels[5],
                    isUnlocked = 5 < unlockedLevel,
                    onClick = { if (5 < unlockedLevel) unlockedLevel++ }
                )
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
        color = if (isUnlocked) Color(0xFF66BB6A) else Color.Gray,
        modifier = Modifier
            .size(100.dp, 60.dp)
            .fillMaxSize()
            .clickable(enabled = isUnlocked) { onClick() },
        tonalElevation = if (isUnlocked) 8.dp else 0.dp,
        shadowElevation = if (isUnlocked) 8.dp else 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 2.dp,
                    color = Color.White,
                    shape = leafShape
                )
        ) {
            if (isUnlocked) {
                Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_lock_24),
                    contentDescription = "Locked", tint = Color.White
                )
            }
        }
    }
}

@Composable
fun AnimatedPath(
    pathStr: String,
    color: Color,
    strokeWidth: Dp,
    easing: Easing,
    duration: Int,
    modifier: Modifier = Modifier
) {
    with(LocalDensity.current) {
        BoxWithConstraints(
            modifier
        ) {
            var path by remember { mutableStateOf(Path()) }

            val strokeWidthPx = strokeWidth.toPx()
            //Here we transform the path string in path and make it fill the screen
            LaunchedEffect(pathStr, strokeWidthPx) {
                val tmpPath = PathParser().parsePathString(pathStr).toPath()

                tmpPath.fillBounds(strokeWidthPx, constraints.maxWidth, constraints.maxHeight)

                path = tmpPath
            }

            val pathMeasure = remember { PathMeasure() }

            pathMeasure.setPath(path, false)

            val infiniteTransition = rememberInfiniteTransition(label = "Path infinite transition")

            // Animating infinitely a float between 0f and the path length
            val progress by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = pathMeasure.length,
                animationSpec = infiniteRepeatable(
                    animation = tween(duration, easing = easing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "Path animation"
            )

            /*            val transitionState = remember {
                            MutableTransitionState(false).apply { targetState = true }
                        }

                        val transition = rememberTransition(transitionState, label = "Path one time transition")

                        // Animating a float between 0f and the path length
                        val progress by transition.animateFloat(
                            transitionSpec = { tween(duration, easing = easing) },
                            label = "Path animation"
                        ) { state ->
                            if (state) pathMeasure.length else 0f
                        }*/

            // Create a intermediate path from 0f to progress
            val animatedPath = remember {
                derivedStateOf {
                    val destination = Path()
                    pathMeasure.setPath(path, false)
                    pathMeasure.getSegment(0f, progress, destination)
                    destination
                }
            }

            // Draw the path
            Canvas(modifier = Modifier.fillMaxWidth()) {
                drawPath(
                    animatedPath.value,
                    color,
                    style = Stroke(
                        width = strokeWidthPx,
                        miter = Stroke.DefaultMiter,
                        cap = Stroke.DefaultCap,
                        join = Stroke.DefaultJoin,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                )
            }
        }
    }
}

fun Path.fillBounds(strokeWidthPx: Float, maxWidth: Int, maxHeight: Int) {
    val pathSize = getBounds()
    val matrix = Matrix()

    val horizontalOffset = pathSize.left - strokeWidthPx / 2
    val verticalOffset = pathSize.top - strokeWidthPx / 2
    val scaleWidth = maxWidth / (pathSize.width + strokeWidthPx)
    val scaleHeight = maxHeight / (pathSize.height + strokeWidthPx)
    val scale = min(scaleHeight, scaleWidth)

    matrix.scale(scale, scale)
    matrix.translate(-horizontalOffset, -verticalOffset)

    println(
        "pathSize: $pathSize, " +
                "horizontalOffset: $horizontalOffset, " +
                "verticalOffset: $verticalOffset, " +
                "scaleWidth: $scaleWidth, " +
                "scaleHeight: $scaleHeight, " +
                "scale: $scale"
    )

    transform(matrix)
}

@Composable
fun StringPathAnimation() {
    Box(modifier = Modifier.fillMaxSize()) {

        val path = "M140 20C73 20 20 74 20 140c0 135 136 170 228 303 88-132 229-173" +
                " 229-303 0-66-54-120-120-120-48 0-90 28-109 69-19-41-60-69-108-69z"

        AnimatedPath(
            pathStr = path,
            color = Color.Red,
            strokeWidth = 10.dp,
            easing = EaseInOutSine,
            duration = 10000,
            modifier = Modifier.padding(20.dp)
        )
    }
}