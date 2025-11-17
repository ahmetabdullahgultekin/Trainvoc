package com.gultekinahmetabdullah.trainvoc.ui.screen.other

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.ui.theme.IconSize
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing

@Composable
fun AboutScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.mediumLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Icon
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(id = R.string.app_icon_desc),
            modifier = Modifier
                .size(120.dp)
                .background(Color.Gray, CircleShape)
                .padding(Spacing.small),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(Spacing.medium))

        // App Name & Version
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(id = R.string.version, "1.0.0"),
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(Spacing.mediumLarge))

        // App Description
        Text(
            text = stringResource(id = R.string.about_app_desc),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = Spacing.small)
        )

        Spacer(modifier = Modifier.height(Spacing.mediumLarge))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(Spacing.medium))

        // Developer Section
        Text(
            text = stringResource(id = R.string.about_developer),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(Spacing.medium))

        Image(
            painter = painterResource(id = R.drawable.baseline_generating_tokens_24),
            contentDescription = stringResource(id = R.string.developer_picture),
            modifier = Modifier
                .size(100.dp)
                .background(Color.Gray, CircleShape)
                .padding(Spacing.small),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(Spacing.small))

        Text(
            text = stringResource(id = R.string.developer_name),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(id = R.string.developer_title),
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(Spacing.medium))

        // Social Links
        SocialLink(
            stringResource(id = R.string.github),
            Icons.Default.Add,
            "https://github.com/gultekinahmetabdullah"
        ) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/gultekinahmetabdullah")
                )
            )
        }
        SocialLink(
            stringResource(id = R.string.linkedin),
            Icons.Default.Person,
            "https://www.linkedin.com/in/gultekinahmetabdullah/"
        ) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.linkedin.com/in/gultekinahmetabdullah/")
                )
            )
        }
        SocialLink(
            stringResource(id = R.string.website),
            Icons.Default.Favorite,
            "https://ahmetabdullahgultekin.com"
        ) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://ahmetabdullahgultekin.com")
                )
            )
        }

        Spacer(modifier = Modifier.height(Spacing.mediumLarge))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(Spacing.medium))

        // License & Credits
        Text(
            text = stringResource(id = R.string.credits_licenses),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(Spacing.small))

        Text(
            text = stringResource(id = R.string.credits_desc),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = Spacing.small)
        )

        Spacer(modifier = Modifier.height(Spacing.medium))

        Button(
            onClick = {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://opensource.org/licenses/MIT")
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(stringResource(id = R.string.view_license))
        }

        Spacer(modifier = Modifier.height(Spacing.mediumLarge))
    }
}

// Social Link Composable
@Composable
fun SocialLink(title: String, icon: ImageVector, link: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(Spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(IconSize.medium)
        )
        Spacer(modifier = Modifier.width(Spacing.small))
        Text(text = link, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
    }
}
