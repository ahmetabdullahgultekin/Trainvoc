package com.gultekinahmetabdullah.trainvoc.ui.screen.other

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gultekinahmetabdullah.trainvoc.R

@Composable
fun HelpScreen() {
    val context = LocalContext.current
    val faqList = listOf(
        "How to use the app?" to "You can navigate through different sections using the bottom navigation bar.",
        "How to reset my progress?" to "Go to Settings > Reset Progress to clear your data.",
        "How to change app theme?" to "Go to Settings > Theme and select your preferred theme.",
        "How to contact support?" to "You can email us or call our support team from the contact section below."
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.bg_6),
                contentScale = ContentScale.FillBounds
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Help & Support", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            // FAQs Section
            Text("Frequently Asked Questions", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            faqList.forEach { (question, answer) ->
                FAQItem(question, answer)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Contact Support Section
            Text("Contact Support", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            ContactItem("Email Support", Icons.Default.Email, "support@trainvoc.com") {
                val emailIntent =
                    Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@trainvoc.com"))
                context.startActivity(emailIntent)
            }
            ContactItem("Call Support", Icons.Default.Phone, "+1 234 567 890") {
                val phoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+1234567890"))
                context.startActivity(phoneIntent)
            }
            ContactItem("Visit Website", Icons.Default.Home, "www.trainvoc.com") {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.trainvoc.com"))
                context.startActivity(webIntent)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Feedback Section
            Text("Give Us Feedback", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Button(
                onClick = {
                    Toast.makeText(context, "Redirecting to feedback form...", Toast.LENGTH_SHORT)
                        .show()
                    val feedbackIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://www.trainvoc.com/feedback"))
                    context.startActivity(feedbackIntent)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Submit Feedback")
            }
        }
    }
}

// FAQ Item (Expandable Question & Answer)
@Composable
fun FAQItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "FAQ Icon",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = question, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        if (expanded) {
            Text(
                text = answer,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 32.dp, bottom = 8.dp)
            )
        }
        HorizontalDivider()
    }
}

// Contact Item (Clickable Email, Phone, Website)
@Composable
fun ContactItem(title: String, icon: ImageVector, contactDetail: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = contactDetail, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}
