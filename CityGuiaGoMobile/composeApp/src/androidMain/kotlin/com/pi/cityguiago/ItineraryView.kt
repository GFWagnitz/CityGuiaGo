package com.pi.cityguiago

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.pi.cityguiago.designsystem.Background
import com.pi.cityguiago.designsystem.Metrics
import com.pi.cityguiago.designsystem.components.SearchBar
import com.pi.cityguiago.designsystem.components.TextH2
import com.pi.cityguiago.designsystem.components.TextH3
import com.pi.cityguiago.designsystem.components.VerticalSpacers
import com.pi.cityguiago.model.Itinerary
import com.pi.cityguiago.module.Itinerary.ItineraryEffect
import com.pi.cityguiago.module.Itinerary.ItineraryViewModel
import com.pi.cityguiago.module.home.HomeEvent
import com.pi.cityguiago.module.home.HomeState
import org.koin.androidx.compose.koinViewModel

@Composable
fun ItinerariesView(
    navController: NavHostController,
    viewModel: ItineraryViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val itineraryState by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ItineraryEffect.ShowErrorMessage -> {
                    android.widget.Toast.makeText(context, effect.errorMessage, android.widget.Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { TextH3("Roteiros") },
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
            when (itineraryState) {
                is ComponentState.Idle, ComponentState.Loading -> {}
                is ComponentState.Error -> {}
                is ComponentState.Loaded<*> -> {
                    ((itineraryState as ComponentState.Loaded<*>).data as List<Itinerary>).also { state ->
                        VerticalSpacers.Large()

                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(Metrics.Margins.default)
                        ) {
                            state.forEach { itinerary ->
                                ItineraryCard(
                                    itinerary = itinerary,
                                    Modifier.clickable { })
                            }
                        }
                        VerticalSpacers.Large()
                    }
                }
            }
        }
    }
}