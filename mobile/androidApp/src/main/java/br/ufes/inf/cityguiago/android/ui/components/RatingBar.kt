package br.ufes.inf.cityguiago.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun RatingBar(
    rating: Float,
    modifier: Modifier = Modifier,
    starSize: Int = 24,
    starColor: Color = Color(0xFFFFC107),
    starCount: Int = 5,
    isIndicator: Boolean = true,
    onRatingChanged: ((Int) -> Unit)? = null
) {
    Row(modifier = modifier) {
        for (i in 1..starCount) {
            val starIcon = when {
                i <= floor(rating) -> Icons.Default.Star
                i <= ceil(rating) && rating % 1 != 0f -> Icons.Default.StarHalf
                else -> Icons.Default.StarBorder
            }
            
            Icon(
                imageVector = starIcon,
                contentDescription = "Star $i",
                tint = starColor,
                modifier = Modifier
                    .size(starSize.dp)
                    .padding(end = 4.dp)
                    .then(
                        if (!isIndicator && onRatingChanged != null) {
                            Modifier.clickable { onRatingChanged(i) }
                        } else Modifier
                    )
            )
        }
    }
} 