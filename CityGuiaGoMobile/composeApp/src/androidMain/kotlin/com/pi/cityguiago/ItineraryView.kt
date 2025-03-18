package com.pi.cityguiago

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pi.cityguiago.designsystem.Background
import com.pi.cityguiago.designsystem.Colors
import com.pi.cityguiago.designsystem.Metrics
import com.pi.cityguiago.designsystem.components.Loading
import com.pi.cityguiago.designsystem.components.TextH2
import com.pi.cityguiago.designsystem.components.TextH3
import com.pi.cityguiago.designsystem.components.TextBody
import com.pi.cityguiago.designsystem.components.TextCaption
import com.pi.cityguiago.designsystem.components.VerticalSpacer
import com.pi.cityguiago.model.Itinerary
import com.pi.cityguiago.model.ItineraryAttraction
import com.pi.cityguiago.module.Itinerary.ItineraryAction
import com.pi.cityguiago.module.Itinerary.ItineraryEffect
import com.pi.cityguiago.module.Itinerary.ItineraryState
import com.pi.cityguiago.module.Itinerary.ItineraryViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel


@Composable @Preview
fun ItinerariesView(
    navController: NavHostController,
    viewModel: ItineraryViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val itineraryState by viewModel.state.collectAsState()
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Públicos", "Favoritos")

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ItineraryEffect.ShowErrorMessage -> {
                    android.widget.Toast.makeText(context, effect.errorMessage, android.widget.Toast.LENGTH_LONG).show()
                }
                is ItineraryEffect.FavoriteAdded -> {
                    android.widget.Toast.makeText(context, "Roteiro adicionado aos favoritos", android.widget.Toast.LENGTH_SHORT).show()
                }
                is ItineraryEffect.FavoriteRemoved -> {
                    android.widget.Toast.makeText(context, "Roteiro removido dos favoritos", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Load the appropriate data based on tab selection
    LaunchedEffect(tabIndex) {
        when (tabIndex) {
            0 -> viewModel.handleAction(ItineraryAction.LoadPublicItineraries)
            1 -> viewModel.handleAction(ItineraryAction.LoadFavoriteItineraries)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { TextH3("Roteiros") },
                backgroundColor = Background,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = tabIndex,
                backgroundColor = Background,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.padding(horizontal = 40.dp),
                        height = 2.dp,
                        color = Colors.Orange500
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { TextH3(title) },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index }
                    )
                }
            }

            when (itineraryState) {
                is ItineraryState.Idle, ItineraryState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Background),
                        contentAlignment = Alignment.Center
                    ) {
                        Loading()
                    }
                }

                is ItineraryState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Background)
                            .padding(Metrics.Margins.large),
                        contentAlignment = Alignment.Center
                    ) {
                        TextBody((itineraryState as ItineraryState.Error).message)
                    }
                }

                is ItineraryState.PublicItinerariesLoaded -> {
                    val itineraries = (itineraryState as ItineraryState.PublicItinerariesLoaded).itineraries
                    ItineraryList(
                        itineraries = itineraries,
                        onItineraryClick = { 
                            viewModel.handleAction(ItineraryAction.LoadItineraryDetails(it.id))
                            navController.navigate("itinerary_details/${it.id}")
                        },
                        onFavoriteClick = { itinerary, isFavorite ->
                            viewModel.handleAction(ItineraryAction.ToggleFavorite(itinerary, isFavorite))
                        }
                    )
                }

                is ItineraryState.FavoriteItinerariesLoaded -> {
                    val itineraries = (itineraryState as ItineraryState.FavoriteItinerariesLoaded).itineraries
                    if (itineraries.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Background)
                                .padding(Metrics.Margins.large),
                            contentAlignment = Alignment.Center
                        ) {
                            TextBody("Você não adicionou nenhum roteiro aos favoritos")
                        }
                    } else {
                        ItineraryList(
                            itineraries = itineraries,
                            onItineraryClick = { 
                                viewModel.handleAction(ItineraryAction.LoadItineraryDetails(it.id))
                                navController.navigate("itinerary_details/${it.id}")
                            },
                            onFavoriteClick = { itinerary, isFavorite ->
                                viewModel.handleAction(ItineraryAction.ToggleFavorite(itinerary, isFavorite))
                            }
                        )
                    }
                }
                
                else -> {}
            }
        }
    }
}

@Composable
fun ItineraryList(
    itineraries: List<Itinerary>,
    onItineraryClick: (Itinerary) -> Unit,
    onFavoriteClick: (Itinerary, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Metrics.Margins.large),
        verticalArrangement = Arrangement.spacedBy(Metrics.Margins.medium)
    ) {
        item { VerticalSpacer(Metrics.Margins.medium) }
        
        items(itineraries) { itinerary ->
            ItineraryCard(
                itinerary = itinerary,
                modifier = Modifier.clickable { onItineraryClick(itinerary) },
                onFavoriteClick = { isFavorite -> onFavoriteClick(itinerary, isFavorite) }
            )
        }
        
        item { VerticalSpacer(Metrics.Margins.large) }
    }
}

@Composable
fun ItineraryCard(
    itinerary: Itinerary,
    modifier: Modifier = Modifier,
    onFavoriteClick: ((Boolean) -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        elevation = 4.dp,
        backgroundColor = Background,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(Metrics.Margins.medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextH2(itinerary.titulo)
                
                if (onFavoriteClick != null) {
                    IconButton(
                        onClick = { onFavoriteClick(!itinerary.isFavorite) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (itinerary.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favoritar",
                            tint = if (itinerary.isFavorite) Colors.Orange500 else Color.Gray
                        )
                    }
                }
            }
            
            TextBody(itinerary.descricao)
            
            VerticalSpacer(Metrics.Margins.small)
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                TextCaption(" ${itinerary.user.username}")
                
                if (itinerary.duracao != null) {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    TextCaption(" ${itinerary.duracao} dias")
                }
            }
        }
    }
}

@Composable
fun ItineraryDetailsView(
    navController: NavHostController,
    itineraryId: String,
    viewModel: ItineraryViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val itineraryState by viewModel.state.collectAsState()

    LaunchedEffect(itineraryId) {
        viewModel.handleAction(ItineraryAction.LoadItineraryDetails(itineraryId))
    }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ItineraryEffect.ShowErrorMessage -> {
                    android.widget.Toast.makeText(context, effect.errorMessage, android.widget.Toast.LENGTH_LONG).show()
                }
                is ItineraryEffect.FavoriteAdded -> {
                    android.widget.Toast.makeText(context, "Roteiro adicionado aos favoritos", android.widget.Toast.LENGTH_SHORT).show()
                }
                is ItineraryEffect.FavoriteRemoved -> {
                    android.widget.Toast.makeText(context, "Roteiro removido dos favoritos", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { TextH3("Detalhes do Roteiro") },
                backgroundColor = Background,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        when (itineraryState) {
            is ItineraryState.Idle, ItineraryState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Background),
                    contentAlignment = Alignment.Center
                ) {
                    Loading()
                }
            }

            is ItineraryState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Background)
                        .padding(Metrics.Margins.large),
                    contentAlignment = Alignment.Center
                ) {
                    TextBody((itineraryState as ItineraryState.Error).message)
                }
            }

            is ItineraryState.ItineraryDetailsLoaded -> {
                val data = itineraryState as ItineraryState.ItineraryDetailsLoaded
                val itinerary = data.itinerary
                val attractionsByDay = data.attractionsByDay
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Background)
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Metrics.Margins.large)
                    ) {
                        VerticalSpacer(Metrics.Margins.medium)
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextH2(itinerary.titulo)
                            
                            IconButton(
                                onClick = { 
                                    viewModel.handleAction(
                                        ItineraryAction.ToggleFavorite(itinerary, !itinerary.isFavorite)
                                    )
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (itinerary.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favoritar",
                                    tint = if (itinerary.isFavorite) Colors.Orange500 else Color.Gray
                                )
                            }
                        }
                        
                        TextBody(itinerary.descricao)
                        
                        VerticalSpacer(Metrics.Margins.small)
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            TextCaption(" ${itinerary.user.username}")
                            
                            if (itinerary.duracao != null) {
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                TextCaption(" ${itinerary.duracao} dias")
                            }
                        }
                        
                        VerticalSpacer(Metrics.Margins.medium)
                        Divider()
                        VerticalSpacer(Metrics.Margins.medium)
                    }
                    
                    // Days with attractions
                    if (attractionsByDay.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(Metrics.Margins.large),
                            contentAlignment = Alignment.Center
                        ) {
                            TextBody("Este roteiro não possui atrações")
                        }
                    } else {
                        val sortedDays = attractionsByDay.keys.sorted()
                        
                        sortedDays.forEach { day ->
                            val dayAttractions = attractionsByDay[day] ?: emptyList()
                            
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = Metrics.Margins.large)
                            ) {
                                TextH3("Dia $day")
                                VerticalSpacer(Metrics.Margins.small)
                                
                                dayAttractions.sortedBy { it.ordem }.forEach { attraction ->
                                    AttractionCard(attraction)
                                    VerticalSpacer(Metrics.Margins.small)
                                }
                                
                                VerticalSpacer(Metrics.Margins.medium)
                                if (day != sortedDays.last()) {
                                    Divider()
                                    VerticalSpacer(Metrics.Margins.medium)
                                }
                            }
                        }
                    }
                    
                    VerticalSpacer(Metrics.Margins.large)
                }
            }
            
            else -> {}
        }
    }
}

@Composable
fun AttractionCard(attraction: ItineraryAttraction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp,
        backgroundColor = Color(0xFF2A2A2A),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Metrics.Margins.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Colors.Orange500),
                contentAlignment = Alignment.Center
            ) {
                TextH3(attraction.ordem.toString())
            }
            
            Column(
                modifier = Modifier
                    .padding(start = Metrics.Margins.medium)
                    .weight(1f)
            ) {
                TextH3(attraction.atracao.nome)
                TextBody(attraction.atracao.descricao)
            }
        }
    }
}