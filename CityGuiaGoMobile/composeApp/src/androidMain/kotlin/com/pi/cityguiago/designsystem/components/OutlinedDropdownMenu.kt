package com.pi.cityguiago.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.pi.cityguiago.designsystem.*
import com.pi.cityguiago.R

@Composable
fun OutlinedDropdownMenu(
    options: List<String>,
    selectedOption: String?,
    placeholder: String = "Select an option",
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(selectedOption) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
    ) {
        OutlinedTextField(
            value = selected ?: "",
            onValueChange = {},
            shape = RoundedCornerShape(Metrics.RoundCorners.default),
            readOnly = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholder) },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_down),
                    contentDescription = "Dropdown Icon",
                    modifier = Modifier.clickable { expanded = true }
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = White,
                focusedBorderColor = Blue,
                unfocusedBorderColor = Gray
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        selected = option
                        onOptionSelected(option)
                        expanded = false
                    }
                ) {
                    Text(option)
                }
            }
        }
    }
}
