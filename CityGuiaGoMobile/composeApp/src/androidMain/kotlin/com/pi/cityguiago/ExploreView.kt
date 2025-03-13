package com.pi.cityguiago

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.pi.cityguiago.designsystem.*
import com.pi.cityguiago.designsystem.components.*

@Composable
fun ExploreView(navController: NavHostController) {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Metrics.Margins.large)
            .background(Background)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
        ) {
        VerticalSpacers.Large()
        SearchBar(
            text = text,
            placeholder = "Explore a Grande Vitória",
            onTextChanged = { newText -> text = newText },
            icon = Icons.Filled.Info
        )
        VerticalSpacers.Default()
//        Attractions()
        VerticalSpacers.Large()
    }
}