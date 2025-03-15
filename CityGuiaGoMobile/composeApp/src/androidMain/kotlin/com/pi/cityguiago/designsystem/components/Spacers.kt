package com.pi.cityguiago.designsystem.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pi.cityguiago.designsystem.Metrics

object VerticalSpacers {
    @Composable fun Zero() = Spacer(modifier = Modifier.height(Metrics.Margins.zero))
    @Composable fun Nano() = Spacer(modifier = Modifier.height(Metrics.Margins.nano))
    @Composable fun Micro() = Spacer(modifier = Modifier.height(Metrics.Margins.micro))
    @Composable fun Small() = Spacer(modifier = Modifier.height(Metrics.Margins.small))
    @Composable fun Default() = Spacer(modifier = Modifier.height(Metrics.Margins.default))
    @Composable fun Large() = Spacer(modifier = Modifier.height(Metrics.Margins.large))
    @Composable fun Huge() = Spacer(modifier = Modifier.height(Metrics.Margins.huge))
    @Composable fun Giant() = Spacer(modifier = Modifier.height(Metrics.Margins.giant))
    @Composable fun Massive() = Spacer(modifier = Modifier.height(Metrics.Margins.massive))
}

object HorizontalSpacers {
    @Composable fun Zero() = Spacer(modifier = Modifier.width(Metrics.Margins.zero))
    @Composable fun Nano() = Spacer(modifier = Modifier.width(Metrics.Margins.nano))
    @Composable fun Micro() = Spacer(modifier = Modifier.width(Metrics.Margins.micro))
    @Composable fun Small() = Spacer(modifier = Modifier.width(Metrics.Margins.small))
    @Composable fun Default() = Spacer(modifier = Modifier.width(Metrics.Margins.default))
    @Composable fun Large() = Spacer(modifier = Modifier.width(Metrics.Margins.large))
    @Composable fun Huge() = Spacer(modifier = Modifier.width(Metrics.Margins.huge))
    @Composable fun Giant() = Spacer(modifier = Modifier.width(Metrics.Margins.giant))
    @Composable fun Massive() = Spacer(modifier = Modifier.width(Metrics.Margins.massive))
}