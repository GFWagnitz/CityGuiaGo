package com.pi.cityguiago.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import com.pi.cityguiago.designsystem.*

@Composable
fun SearchBar(
    text: String,
    placeholder: String,
    onTextChanged: (String) -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChanged,
        shape = RoundedCornerShape(Metrics.RoundCorners.default),
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(text = placeholder) },
        trailingIcon = {
            Icon(imageVector = icon, contentDescription = "Trailing Icon", tint = Gray)
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = White,
            focusedBorderColor = Blue,
            unfocusedBorderColor = Gray
        )
    )
}