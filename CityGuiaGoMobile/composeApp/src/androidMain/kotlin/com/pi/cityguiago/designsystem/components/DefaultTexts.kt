package com.pi.cityguiago.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.pi.cityguiago.designsystem.*

sealed class ColorMode {
    object Primary : ColorMode()
    object Secondary : ColorMode()
}

@Composable
fun TextH1(
    text: String,
    modifier: Modifier = Modifier,
    colorMode: ColorMode = ColorMode.Primary,
    textAlign: TextAlign? = null
) {
    val color = when (colorMode) {
        ColorMode.Primary -> PrimaryTitle
        ColorMode.Secondary -> SecondaryTitle
    }
    Text(text = text, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 30.sp, fontWeight = FontWeight.Bold), color = color, textAlign = textAlign, modifier = modifier)
}

@Composable
fun TextH2(
    text: String,
    modifier: Modifier = Modifier,
    colorMode: ColorMode = ColorMode.Primary,
    textAlign: TextAlign? = null
) {
    val color = when (colorMode) {
        ColorMode.Primary -> PrimaryTitle
        ColorMode.Secondary -> SecondaryTitle
    }
    Text(text = text, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 24.sp, fontWeight = FontWeight.Bold), color = color, textAlign = textAlign, modifier = modifier)
}

@Composable
fun TextH3(
    text: String,
    modifier: Modifier = Modifier,
    colorMode: ColorMode = ColorMode.Primary,
    textAlign: TextAlign? = null
) {
    val color = when (colorMode) {
        ColorMode.Primary -> PrimaryTitle
        ColorMode.Secondary -> SecondaryTitle
    }
    Text(text = text, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 20.sp, fontWeight = FontWeight.Bold), color = color, textAlign = textAlign, modifier = modifier)
}

@Composable
fun TextH4(
    text: String,
    modifier: Modifier = Modifier,
    colorMode: ColorMode = ColorMode.Primary,
    textAlign: TextAlign? = null
) {
    val color = when (colorMode) {
        ColorMode.Primary -> PrimaryTitle
        ColorMode.Secondary -> SecondaryTitle
    }
    Text(text = text, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 18.sp, fontWeight = FontWeight.Bold), color = color, textAlign = textAlign, modifier = modifier)
}

@Composable
fun TextH5(
    text: String,
    modifier: Modifier = Modifier,
    colorMode: ColorMode = ColorMode.Primary,
    textAlign: TextAlign? = null
) {
    val color = when (colorMode) {
        ColorMode.Primary -> PrimaryTitle
        ColorMode.Secondary -> SecondaryTitle
    }
    Text(text = text, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, fontWeight = FontWeight.Bold), color = color, textAlign = textAlign, modifier = modifier)
}

@Composable
fun TextH6(
    text: String,
    modifier: Modifier = Modifier,
    colorMode: ColorMode = ColorMode.Primary,
    textAlign: TextAlign? = null
) {
    val color = when (colorMode) {
        ColorMode.Primary -> PrimaryTitle
        ColorMode.Secondary -> SecondaryTitle
    }
    Text(text = text, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, fontWeight = FontWeight.Bold), color = color, textAlign = textAlign, modifier = modifier)
}

@Composable
fun TextBody1(
    text: String,
    modifier: Modifier = Modifier,
    colorMode: ColorMode = ColorMode.Primary,
    textAlign: TextAlign? = null
) {
    val color = when (colorMode) {
        ColorMode.Primary -> PrimaryBody
        ColorMode.Secondary -> SecondaryBody
    }
    Text(text = text, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, fontWeight = FontWeight.Normal), color = color, textAlign = textAlign, modifier = modifier)
}

@Composable
fun TextBody2(
    text: String,
    modifier: Modifier = Modifier,
    colorMode: ColorMode = ColorMode.Primary,
    textAlign: TextAlign? = null
) {
    val color = when (colorMode) {
        ColorMode.Primary -> PrimaryBody
        ColorMode.Secondary -> SecondaryBody
    }
    Text(text = text, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, fontWeight = FontWeight.Normal), color = color, textAlign = textAlign, modifier = modifier)
}

@Composable
fun LinkText(
    text: String,
    modifier: Modifier = Modifier,
    colorMode: ColorMode = ColorMode.Primary,
    onClick: () -> Unit
) {
    val color = when (colorMode) {
        ColorMode.Primary -> PrimaryTitle
        ColorMode.Secondary -> SecondaryTitle
    }
    Text(
        text = text,
        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, fontWeight = FontWeight.Normal),
        color = color,
        textDecoration = TextDecoration.Underline,
        modifier = modifier.clickable(onClick = onClick)
    )
}