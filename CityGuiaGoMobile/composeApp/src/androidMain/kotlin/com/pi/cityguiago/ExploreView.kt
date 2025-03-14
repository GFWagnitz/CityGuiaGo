package com.pi.cityguiago

import android.widget.Toast
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
import com.pi.cityguiago.model.Attraction
import com.pi.cityguiago.module.home.ExploreEffect
import com.pi.cityguiago.module.home.ExploreEvent
import com.pi.cityguiago.module.home.ExploreViewModel
import com.pi.cityguiago.module.home.HomeEffect
import com.pi.cityguiago.module.home.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExploreView(
    navController: NavHostController,
    viewModel: ExploreViewModel = koinViewModel()
) {
    var text by remember { mutableStateOf("") }

    val attractions = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<List<Attraction>>("attractions") ?: emptyList()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ExploreEffect.OpenAttractionView -> {
                    navController.navigate("attraction")
                }
            }
        }
    }

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
        Attractions(navController, attractions) {
            viewModel.onEvent(ExploreEvent.OpenAttractionView)
        }
        VerticalSpacers.Large()
    }
}