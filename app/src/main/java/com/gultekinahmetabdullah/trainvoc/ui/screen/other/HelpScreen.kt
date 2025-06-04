package com.gultekinahmetabdullah.trainvoc.ui.screen.other

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
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

@Composable
fun HelpScreen() {
    val context = LocalContext.current
    val faqList = listOf(
        stringResource(id = R.string.faq_how_to_use) to stringResource(id = R.string.faq_how_to_use_desc),
        stringResource(id = R.string.faq_how_to_reset) to stringResource(id = R.string.faq_how_to_reset_desc),
        stringResource(id = R.string.faq_how_to_theme) to stringResource(id = R.string.faq_how_to_theme_desc),
        stringResource(id = R.string.faq_how_to_contact) to stringResource(id = R.string.faq_how_to_contact_desc)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                stringResource(id = R.string.help_support),
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0D47A1),
                letterSpacing = 1.2.sp
            )

            // FAQs Section
            Text(
                stringResource(id = R.string.faq_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0),
                letterSpacing = 0.5.sp
            )
            faqList.forEach { (question, answer) ->
                FAQItem(
                    question = question,
                    answer = answer,
                    questionFontSize = 18,
                    answerFontSize = 16
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Contact Support Section
            Text(
                stringResource(id = R.string.contact_support),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0),
                letterSpacing = 0.5.sp
            )
            ContactItem(
                stringResource(id = R.string.email_support),
                Icons.Default.Email,
                "support@trainvoc.com"
            ) {
                val emailIntent =
                    Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@trainvoc.com"))
                context.startActivity(emailIntent)
            }
            ContactItem(
                stringResource(id = R.string.call_support),
                Icons.Default.Phone,
                "+1 234 567 890"
            ) {
                val phoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+1234567890"))
                context.startActivity(phoneIntent)
            }
            ContactItem(
                stringResource(id = R.string.visit_website),
                Icons.Default.Home,
                "www.trainvoc.com"
            ) {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.trainvoc.com"))
                context.startActivity(webIntent)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Feedback Section
            Text(
                stringResource(id = R.string.give_feedback),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0),
                letterSpacing = 0.5.sp
            )
            val redirectingFeedback = stringResource(id = R.string.redirecting_feedback)
            Button(
                onClick = {
                    Toast.makeText(
                        context,
                        redirectingFeedback,
                        Toast.LENGTH_SHORT
                    ).show()
                    val feedbackIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://www.trainvoc.com/feedback"))
                    context.startActivity(feedbackIntent)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    stringResource(id = R.string.submit_feedback),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.2.sp
                )
            }
        }
    }
}

// FAQ Item (Expandable Question & Answer)
@Composable
fun FAQItem(question: String, answer: String, questionFontSize: Int, answerFontSize: Int) {
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
            Text(text = question, fontSize = questionFontSize.sp, fontWeight = FontWeight.Medium)
        }
        if (expanded) {
            Text(
                text = answer,
                fontSize = answerFontSize.sp,
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
