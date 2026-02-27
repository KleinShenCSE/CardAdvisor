package com.example.cardadvisor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CardPreview(
    name: String,
    lastFour: String,
    network: String,
    color: Long,
    modifier: Modifier = Modifier
) {
    val cardColor = Color(color)
    val contentColor = if (cardColor.luminance() > 0.4f) Color.Black.copy(alpha = 0.8f) else Color.White

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(cardColor, cardColor.copy(alpha = 0.75f))
                )
            )
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = name,
            color = contentColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            modifier = Modifier.align(Alignment.TopStart)
        )
        Text(
            text = "•••• $lastFour",
            color = contentColor,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.BottomStart)
        )
        Text(
            text = network,
            color = contentColor,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}
