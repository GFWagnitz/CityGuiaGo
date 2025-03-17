package com.pi.cityguiago

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.pi.cityguiago.designsystem.*
import com.pi.cityguiago.designsystem.components.*
import com.pi.cityguiago.module.home.CategoryAttraction
import com.pi.cityguiago.module.home.ExploreEffect
import com.pi.cityguiago.module.home.ExploreEvent
import com.pi.cityguiago.module.home.ExploreViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExploreView(
    navController: NavHostController,
    viewModel: ExploreViewModel = koinViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }

    val attractions = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<List<CategoryAttraction>>("attractions") ?: emptyList()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ExploreEffect.OpenAttractionView -> {
                    navController.navigate("attraction/${effect.attractionId}")
                }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { TextH3("Atrações") },
                backgroundColor = Background,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(horizontal = Metrics.Margins.large)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            VerticalSpacers.Large()
            SearchBar(
                text = searchQuery,
                placeholder = "Explore a Grande Vitória",
                onTextChanged = { newText -> searchQuery = newText },
                icon = painterResource(id = R.drawable.ic_search)
            )
            VerticalSpacers.Default()
            Attractions(attractions, searchQuery) { attraction ->
                viewModel.onEvent(ExploreEvent.OnAttractionClick(attraction.id))
            }
            VerticalSpacers.Large()
        }
    }
}