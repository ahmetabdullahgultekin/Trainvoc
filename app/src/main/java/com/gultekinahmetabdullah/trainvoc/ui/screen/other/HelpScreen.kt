package com.gultekinahmetabdullah.trainvoc.ui.screen.other

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Support
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.ui.components.cards.SettingSectionCard
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val faqList = listOf(
        stringResource(id = R.string.faq_how_to_use) to stringResource(id = R.string.faq_how_to_use_desc),
        stringResource(id = R.string.faq_how_to_reset) to stringResource(id = R.string.faq_how_to_reset_desc),
        stringResource(id = R.string.faq_how_to_theme) to stringResource(id = R.string.faq_how_to_theme_desc),
        stringResource(id = R.string.faq_how_to_contact) to stringResource(id = R.string.faq_how_to_contact_desc),
        stringResource(id = R.string.faq_how_to_report_bug) to stringResource(id = R.string.faq_how_to_report_bug_desc),
        stringResource(id = R.string.faq_how_to_suggest_feature) to stringResource(id = R.string.faq_how_to_suggest_feature_desc),
        stringResource(id = R.string.faq_how_to_backup) to stringResource(id = R.string.faq_how_to_backup_desc)
    )

    Scaffold(
        topAppBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.help_support)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            item { Spacer(modifier = Modifier.height(Spacing.small)) }

            // Welcome Message
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(Spacing.medium)) {
                        Text(
                            text = stringResource(id = R.string.help_welcome),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // FAQs Section
            item {
                SettingSectionCard(
                    icon = Icons.Default.QuestionAnswer,
                    title = stringResource(id = R.string.faq_title)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)) {
                        faqList.forEach { (question, answer) ->
                            ImprovedFAQItem(
                                question = question,
                                answer = answer
                            )
                        }
                    }
                }
            }

            // Contact Support Section
            item {
                SettingSectionCard(
                    icon = Icons.Default.Support,
                    title = stringResource(id = R.string.contact_support)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)) {
                        ImprovedContactItem(
                            icon = Icons.Default.Email,
                            title = stringResource(id = R.string.email_support),
                            detail = "support@trainvoc.com",
                            onClick = {
                                val emailIntent = Intent(
                                    Intent.ACTION_SENDTO,
                                    "mailto:support@trainvoc.com".toUri()
                                )
                                context.startActivity(emailIntent)
                            }
                        )

                        ImprovedContactItem(
                            icon = Icons.Default.Phone,
                            title = stringResource(id = R.string.call_support),
                            detail = "+1 234 567 890",
                            onClick = {
                                val phoneIntent = Intent(
                                    Intent.ACTION_DIAL,
                                    "tel:+1234567890".toUri()
                                )
                                context.startActivity(phoneIntent)
                            }
                        )

                        ImprovedContactItem(
                            icon = Icons.Default.Web,
                            title = stringResource(id = R.string.visit_website),
                            detail = "www.trainvoc.com",
                            onClick = {
                                val webIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    "https://www.trainvoc.com".toUri()
                                )
                                context.startActivity(webIntent)
                            }
                        )
                    }
                }
            }

            // Feedback Section
            item {
                SettingSectionCard(
                    icon = Icons.Default.Feedback,
                    title = stringResource(id = R.string.give_feedback)
                ) {
                    val redirectingFeedback = stringResource(id = R.string.redirecting_feedback)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = redirectingFeedback,
                                    duration = SnackbarDuration.Short
                                )
                            }
                            val feedbackIntent = Intent(
                                Intent.ACTION_VIEW,
                                "https://www.trainvoc.com/feedback".toUri()
                            )
                            context.startActivity(feedbackIntent)
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.submit_feedback),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.medium),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            // Bottom spacing
            item { Spacer(modifier = Modifier.height(Spacing.medium)) }
        }
    }
}

/**
 * Improved FAQ Item with Card wrapper and chevron rotation animation
 */
@Composable
fun ImprovedFAQItem(
    question: String,
    answer: String
) {
    var expanded by remember { mutableStateOf(false) }

    // Chevron rotation animation
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "chevronRotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        onClick = { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = if (expanded)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(Spacing.medium)) {
            // Question with chevron
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.QuestionAnswer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = question,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = if (expanded)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = if (expanded)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.rotate(rotationAngle)
                )
            }

            // Answer (expandable)
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = answer,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

/**
 * Improved Contact Item with Card wrapper and circular icon background
 */
@Composable
fun ImprovedContactItem(
    icon: ImageVector,
    title: String,
    detail: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular icon background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
