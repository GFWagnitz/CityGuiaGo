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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.Color
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
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    val color = when (colorMode) {
        ColorMode.Primary -> PrimaryTitle
        ColorMode.Secondary -> SecondaryTitle
    }
    Text(
        text = text,
        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 30.sp, fontWeight = FontWeight.Bold),
        color = color,
        textAlign = textAlign,
        modifier = modifier,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun TextH2(
    text: String,
    modifier: Modifier = Modifier,
    colorMode: ColorMode = ColorMode.Primary,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    val color = when (colorMode) {
        ColorMode.Primary -> PrimaryTitle
        ColorMode.Secondary -> SecondaryTitle
    }
    Text(
        text = text,
        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 24.sp, fontWeight = FontWeight.Bold),
        color = color,
        textAlign = textAlign,
        modifier = modifier,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun TextH3(
    text: String,
    modifier: Modifier = Modifier,
    colorMode: ColorMode = ColorMode.Primary,
    colorValue: Color? = null,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    val color = colorValue ?: when (colorMode) {
        ColorMode.Primary -> PrimaryTitle
        ColorMode.Secondary -> SecondaryTitle
    }
    Text(
        text = text,
        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 20.sp, fontWeight = FontWeight.Bold),
        color = color,
        textAlign = textAlign,
        modifier = modifier,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun TextH4(
    text: String,
    modifier: Modifier = Modifier,
    colorMode: ColorMode = ColorMode.Primary,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    val color = when (colorMode) {
        ColorMode.Primary -> PrimaryTitle
        ColorMode.Secondary -> SecondaryTitle
    }
    Text(
        text = text,
        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 18.sp, fontWeight = FontWeight.Bold),
        color = color,
        textAlign = textAlign,
        modifier = modifier,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun TextH5(
    text: String,
    modifier: Modifier = Modifier,
    colorMode: ColorMode = ColorMode.Primary,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    val color = when (colorMode) {
        ColorMode.Primary -> PrimaryTitle
        ColorMode.Secondary -> SecondaryTitle
    }
    Text(
        text = text,
        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, fontWeight = FontWeight.Bold),
        color = color,
        textAlign = textAlign,
        modifier = modifier,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun TextH6(
    text: String,
    modifier: Modifier = Modifier,
    colorMode: ColorMode = ColorMode.Primary,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    val color = when (colorMode) {
        ColorMode.Primary -> PrimaryTitle
        ColorMode.Secondary -> SecondaryTitle
    }
    Text(
        text = text,
        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, fontWeight = FontWeight.Bold),
        color = color,
        textAlign = textAlign,
        modifier = modifier,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun TextBody1(
    text: String,
    modifier: Modifier = Modifier,
    colorMode: ColorMode = ColorMode.Primary,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    val color = when (colorMode) {
        ColorMode.Primary -> PrimaryBody
        ColorMode.Secondary -> SecondaryBody
    }
    Text(
        text = text,
        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, fontWeight = FontWeight.Normal),
        color = color,
        textAlign = textAlign,
        modifier = modifier,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun TextBody2(
    text: String,
    modifier: Modifier = Modifier,
    colorMode: ColorMode = ColorMode.Primary,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    val color = when (colorMode) {
        ColorMode.Primary -> PrimaryBody
        ColorMode.Secondary -> SecondaryBody
    }
    Text(
        text = text,
        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, fontWeight = FontWeight.Normal),
        color = color,
        textAlign = textAlign,
        modifier = modifier,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun LinkText(
    text: String,
    modifier: Modifier = Modifier,
    colorMode: ColorMode = ColorMode.Primary,
    onClick: () -> Unit,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis
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
        modifier = modifier.clickable(onClick = onClick),
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun TextBody(
    text: String,
    modifier: Modifier = Modifier,
    colorMode: ColorMode = ColorMode.Primary,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    val color = when (colorMode) {
        ColorMode.Primary -> PrimaryBody
        ColorMode.Secondary -> SecondaryBody
    }
    Text(
        text = text,
        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, fontWeight = FontWeight.Normal),
        color = color,
        textAlign = textAlign,
        modifier = modifier,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun TextCaption(
    text: String,
    modifier: Modifier = Modifier,
    colorMode: ColorMode = ColorMode.Primary,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    val color = when (colorMode) {
        ColorMode.Primary -> PrimaryBody
        ColorMode.Secondary -> SecondaryBody
    }
    Text(
        text = text,
        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, fontWeight = FontWeight.Normal),
        color = color,
        textAlign = textAlign,
        modifier = modifier,
        maxLines = maxLines,
        overflow = overflow
    )
}
