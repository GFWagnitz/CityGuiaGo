package com.pi.cityguiago.designsystem.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import com.pi.cityguiago.designsystem.*

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Metrics.RoundCorners.default)),
        colors = ButtonDefaults.buttonColors(backgroundColor = LightBlue)
    ) {
        Row(
            modifier = Modifier.padding(Metrics.Margins.small),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(painter = icon, contentDescription = text, tint = Blue)
                Spacer(modifier = Modifier.width(Metrics.Margins.small))
            }
            Text(text = text, color = Blue)
        }
    }
}