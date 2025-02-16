package com.gultekinahmetabdullah.trainvoc.classes.enums

import androidx.compose.ui.graphics.Color

enum class WordLevel(
    val level: String,
    val color: Color
) {
    A1("Beginner", Color(0xFF4CAF50)),
    A2("Elementary", Color(0xFF2196F3)),
    B1("Intermediate", Color(0xFF9C27B0)),
    B2("Upper Intermediate", Color(0xFFE91E63)),
    C1("Advanced", Color(0xFFFF9800)),
    C2("Proficiency", Color(0xFF795548))
}