package com.pi.cityguiago

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pi.cityguiago.designsystem.*
import com.pi.cityguiago.designsystem.components.*
import com.pi.cityguiago.model.Attraction
import com.pi.cityguiago.module.Login.LoginViewModel
import com.pi.cityguiago.module.home.HomeState
import com.pi.cityguiago.module.home.HomeViewModel
import com.pi.cityguiago.network.PrefCacheManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeView(
    navController: NavHostController,
    store: PrefCacheManager,
    viewModel: HomeViewModel = koinViewModel()
) {
    val homeState by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
    ) {
        when (homeState) {
            is ComponentState.Idle, ComponentState.Loading -> {
                Header(scope, store)
            }
            is ComponentState.Error -> {
                Header(scope, store)
            }
            is ComponentState.Loaded<*> -> {
                ((homeState as ComponentState.Loaded<*>).data as HomeState).also { state ->
                    Header(scope, store)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = Metrics.Margins.large),
                        horizontalAlignment = Alignment.Start
                    ) {
                        VerticalSpacers.Large()
                        SearchSection(navController)
                        VerticalSpacers.Large()
                        topAttractions(state)
                        VerticalSpacers.Large()
                        Attractions(state)
                        VerticalSpacers.Large()
                        Itineraries()
                        VerticalSpacers.Large()
                    }
                }
            }
        }
    }
}

@Composable
fun Header(
    scope: CoroutineScope,
    store: PrefCacheManager
) {
    var savedValue by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val user = store.getUser()
        savedValue = user?.user?.nome ?: ""
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Blue)
            .padding(Metrics.Margins.large)
    ) {
        Column {
            TextH1("Olá, $savedValue 👋", colorMode = ColorMode.Secondary)
            TextBody1("Vamos explorar a Grande Vitória juntos!", colorMode = ColorMode.Secondary)
            VerticalSpacers.Massive()
        }
    }
}

@Composable
fun SearchSection(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchBar(
            text = "",
            placeholder = "Explore a Grande Vitória",
            onTextChanged = { navController.navigate("explore") },
            icon = Icons.Filled.Info
        )

        VerticalSpacers.Default()

        Box(
            modifier = Modifier
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(Metrics.RoundCorners.default)
                )
                .fillMaxWidth()
                .clip(RoundedCornerShape(Metrics.RoundCorners.default))
                .background(White)
                .padding(Metrics.Margins.default)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Metrics.Margins.small)) {
                SecondaryButton(text = "Seus Favoritos", onClick = {})
                SecondaryButton(text = "Seus Roteiros", onClick = {})
            }
        }
    }
}

@Composable
fun topAttractions(state: HomeState) {
    if (state.firstAttraction == null) return

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextH2("Melhores atrações")
        VerticalSpacers.Default()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BestAttractionCard(
                title = state.firstAttraction?.nome,
                number = 1,
                modifier = Modifier
                    .weight(1f)
                    .height(200.dp)
            )

            HorizontalSpacers.Default()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(200.dp),
                verticalArrangement = Arrangement.spacedBy(Metrics.Margins.default),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BestAttractionCard(
                    title = state.secondAttraction?.nome,
                    number = 2,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                BestAttractionCard(
                    title = state.thirdAttraction?.nome,
                    number = 3,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun BestAttractionCard(
    title: String?,
    number: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(Metrics.RoundCorners.default),
        backgroundColor = Gray,
        elevation = Metrics.Margins.nano
    ) {
        Box(
            contentAlignment = Alignment.BottomStart
        ) {
            Column(modifier = Modifier.padding(Metrics.Margins.default))
             {
                 title?.let {
                     Row {
                             Icon(
                                 imageVector = Icons.Filled.Star,
                                 contentDescription = "Rating Star",
                                 modifier = Modifier.size(Metrics.Margins.default)
                             )
                             HorizontalSpacers.Micro()
                             TextH6("No $number", colorMode = ColorMode.Secondary)

                     }
                     VerticalSpacers.Small()
                     TextH5(it, colorMode = ColorMode.Secondary)
                 }
            }
        }
    }
}

@Composable
fun Attractions(state: HomeState) {
    val tabTitles = listOf("Restaurantes", "Parques", "Praias", "Hoteis", "Passeios")
    val tabContents = listOf(
        state.attractions.filter { it.categoria.id == "0" },
        state.attractions.filter { it.categoria.id == "1" },
        state.attractions.filter { it.categoria.id == "2" },
        state.attractions.filter { it.categoria.id == "3" },
        state.attractions.filter { it.categoria.id == "4" },
    )

    var selectedTabIndex by remember { mutableStateOf(0) }

    val items = tabContents[selectedTabIndex]
    val rows = (items.size + 1) / 2
    val gridHeight = 184.dp * rows + Metrics.Margins.nano + if (rows > 1) Metrics.Margins.default * (rows - 1) else Metrics.Margins.zero

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Metrics.Margins.default)
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            backgroundColor = Color.Transparent,
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = Blue
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTabIndex == index) Blue else Gray,
                            style = MaterialTheme.typography.body2
                        )
                    }
                )
            }
        }

        VerticalSpacers.Default()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(gridHeight)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(Metrics.Margins.zero),
                horizontalArrangement = Arrangement.spacedBy(Metrics.Margins.default),
                verticalArrangement = Arrangement.spacedBy(Metrics.Margins.default),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items) { item ->
                    AttractionCard(
                        title = item.nome,
                        description = item.descricao,
                        rating = item.precoMedio ?: 0.0,
                        location = item.enderecoCidade ?: "",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun AttractionCard(
    title: String,
    description: String,
    rating: Double,
    location: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(184.dp),
        shape = RoundedCornerShape(Metrics.RoundCorners.default),
        elevation = Metrics.Margins.nano
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Gray)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f)
                    .background(White)
                    .padding(8.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    TextH5(title, maxLines = 1)
                    TextBody2(description, maxLines = 1)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(Metrics.Margins.micro)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rating Star",
                            modifier = Modifier.size(Metrics.Margins.default)
                        )
                        HorizontalSpacers.Micro()
                        TextBody2(String.format("%.1f", rating))
                        HorizontalSpacers.Micro()
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Location Star",
                            modifier = Modifier.size(Metrics.Margins.default)
                        )
                        HorizontalSpacers.Micro()
                        TextBody2(location, maxLines = 1)
                    }
                }
            }
        }
    }
}

data class Itinerary(
    val name: String,
    val duration: String
)

@Composable
fun Itineraries() {
    val itineraries = listOf(
        Itinerary("Roteiro A", "2h 30m"),
        Itinerary("Roteiro B", "3h 15m"),
        Itinerary("Roteiro C", "1h 45m")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        TextH2("Roteiros Prontos")
        VerticalSpacers.Default()

        Column(
            verticalArrangement = Arrangement.spacedBy(Metrics.Margins.default)
        ) {
            itineraries.forEach { itinerary ->
                ItineraryCard(itinerary = itinerary)
            }
        }
    }
}

@Composable
fun ItineraryCard(itinerary: Itinerary, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = White,
        shape = RoundedCornerShape(Metrics.RoundCorners.default),
        elevation = Metrics.Margins.nano
    ) {
        Row(
            modifier = Modifier.padding(Metrics.Margins.default),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(Metrics.RoundCorners.default))
                    .background(Gray)
            )
            HorizontalSpacers.Small()
            Column {
                TextH5(text = itinerary.name)
                VerticalSpacers.Small()
                TextBody2(text = itinerary.duration)
            }
        }
    }
}

