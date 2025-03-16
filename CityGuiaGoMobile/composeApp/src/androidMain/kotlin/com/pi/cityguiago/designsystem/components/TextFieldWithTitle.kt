package com.pi.cityguiago.designsystem.components

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
fun TextFieldWithTitle(
    title: String,
    placeholder: String,
    text: String,
    onTextChanged: (String) -> Unit,
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(Metrics.Margins.small))

        OutlinedTextField(
            value = text,
            onValueChange = onTextChanged,
            shape = RoundedCornerShape(Metrics.RoundCorners.default),
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholder) },
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                if (isPassword) {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = if (passwordVisible) painterResource(id = R.drawable.ic_eye_open) else painterResource(id = R.drawable.ic_eye_closed),
                            contentDescription = if (passwordVisible) "Esconder senha" else "Mostrar senha",
                            tint = Gray
                        )
                    }
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = White,
                focusedBorderColor = Blue,
                unfocusedBorderColor = Gray
            )
        )
    }
}
