package com.pi.cityguiago.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pi.cityguiago.R
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun RatingBar(
    rating: Float,
    modifier: Modifier = Modifier,
    starSize: Int = 32,
    starColor: Color = Color.Unspecified,
    starCount: Int = 5,
    isIndicator: Boolean = true,
    onRatingChanged: ((Int) -> Unit)? = null
) {
    Row(modifier = modifier) {
        for (i in 1..starCount) {
            val starIcon = when {
                i <= floor(rating) -> painterResource(id = R.drawable.ic_star)
                i <= ceil(rating) && rating % 1 != 0f -> painterResource(id = R.drawable.ic_star_half)
                else -> painterResource(id = R.drawable.ic_star_empty)
            }
            
            Icon(
                painter = starIcon,
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