package com.pi.cityguiago

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.pi.cityguiago.designsystem.*
import com.pi.cityguiago.designsystem.components.*
import com.pi.cityguiago.model.Attraction
import com.pi.cityguiago.module.Login.LoginEffect
import com.pi.cityguiago.module.home.HomeEffect
import com.pi.cityguiago.module.home.HomeEvent
import com.pi.cityguiago.module.home.HomeState
import com.pi.cityguiago.module.home.HomeViewModel
import com.pi.cityguiago.network.PrefCacheManager
import io.ktor.http.ContentType.Application.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeView(
    navController: NavHostController,
    store: PrefCacheManager,
    viewModel: HomeViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val homeState by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is HomeEffect.OpenAttractionView -> {
                    navController.navigate("attraction")
                }
                is HomeEffect.OpenExploreView -> {
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        key = "attractions",
                        value = effect.attractions
                    )
                    navController.navigate("explore")
                }
                is HomeEffect.ShowErrorMessage -> {
                    Toast.makeText(context, effect.errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

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
                        SearchSection(navController, state, viewModel::onEvent)
                        VerticalSpacers.Large()
                        topAttractions(state)
                        VerticalSpacers.Large()
                        Attractions(navController, state.attractions) {
                            viewModel.onEvent(HomeEvent.OpenAttractionView)
                        }
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
fun SearchSection(
    navController: NavHostController,
    state: HomeState,
    onEvent: (HomeEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchBar(
            text = "",
            placeholder = "Explore a Grande Vitória",
            onTextChanged = {
                onEvent(HomeEvent.OpenExploreView(state.attractions))
            },
            icon = painterResource(id = R.drawable.ic_search)
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
                SecondaryButton(text = "Seus Favoritos", onClick = {}, icon = painterResource(id = R.drawable.ic_option))
                SecondaryButton(text = "Seus Roteiros", onClick = {}, icon = painterResource(id = R.drawable.ic_option))
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
            topAttractionCard(
                title = state.firstAttraction?.nome,
                imageUrl = state.firstAttraction?.imagens?.first()?.caminho,
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
                topAttractionCard(
                    title = state.secondAttraction?.nome,
                    imageUrl = state.secondAttraction?.imagens?.first()?.caminho,
                    number = 2,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                topAttractionCard(
                    title = state.thirdAttraction?.nome,
                    imageUrl = state.thirdAttraction?.imagens?.first()?.caminho,
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
fun topAttractionCard(
    title: String?,
    imageUrl: String?,
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
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomStart
        ) {
            AsyncImage(
                model = imageUrl ?: "",
                contentDescription = title ?: "Attraction Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Metrics.Margins.default)
            ) {
                title?.let {
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_crown),
                            contentDescription = "Rating Star",
                            modifier = Modifier.size(Metrics.Margins.default),
                            tint = Color.Unspecified
                        )
                        HorizontalSpacers.Micro()
                        TextH6("No $number", colorMode = ColorMode.Secondary)
                    }
                    VerticalSpacers.Small()
                    TextH5(it, colorMode = ColorMode.Secondary, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
fun Attractions(
    navController: NavHostController,
    attractions: List<Attraction>,
    onClick: (attractionId: Attraction) -> Unit
) {
    val tabTitles = listOf("Restaurantes", "Parques", "Praias", "Hoteis", "Passeios")
    val tabContents = listOf(
        attractions.filter { it.categoria.id == "0" },
        attractions.filter { it.categoria.id == "1" },
        attractions.filter { it.categoria.id == "2" },
        attractions.filter { it.categoria.id == "3" },
        attractions.filter { it.categoria.id == "4" },
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
                        item,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onClick(item)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AttractionCard(
    attraction: Attraction,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(184.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(Metrics.RoundCorners.default),
        elevation = Metrics.Margins.nano
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Gray)
            ) {
                AsyncImage(
                    model = attraction.imagens.firstOrNull()?.caminho ?: "",
                    contentDescription = attraction?.nome,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
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
                    TextH5(attraction.nome, maxLines = 1)
                    TextBody2(attraction.descricao, maxLines = 1)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(Metrics.Margins.micro)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_star),
                            contentDescription = "Rating Star",
                            modifier = Modifier.size(Metrics.Margins.default),
                            tint = Color.Unspecified
                        )
                        HorizontalSpacers.Micro()
                        TextBody2(String.format("%.1f", 1.0))
                        HorizontalSpacers.Micro()
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location),
                            contentDescription = "Location Star",
                            modifier = Modifier.size(Metrics.Margins.default),
                            tint = Color.Unspecified
                        )
                        HorizontalSpacers.Micro()
                        TextBody2(attraction.enderecoCidade ?: "", maxLines = 1)
                    }
                }
            }
        }
    }
}

data class Itinerary(
    val name: String,
    val duration: String,
    val imageUrl: String
)

@Composable
fun Itineraries() {
    val itineraries = listOf(
        Itinerary("Roteiro A", "2h 30m", "https://static.vecteezy.com/ti/fotos-gratis/t2/41436456-ai-gerado-cinematografico-imagem-do-uma-leao-dentro-uma-natureza-panorama-foto.jpg"),
        Itinerary("Roteiro B", "3h 15m", "https://static.vecteezy.com/ti/fotos-gratis/t2/41436456-ai-gerado-cinematografico-imagem-do-uma-leao-dentro-uma-natureza-panorama-foto.jpg"),
        Itinerary("Roteiro C", "1h 45m", "https://static.vecteezy.com/ti/fotos-gratis/t2/41436456-ai-gerado-cinematografico-imagem-do-uma-leao-dentro-uma-natureza-panorama-foto.jpg")
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
        modifier = modifier
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, Gray),
                RoundedCornerShape(Metrics.RoundCorners.default)
            ),
        backgroundColor = White,
        shape = RoundedCornerShape(Metrics.RoundCorners.default),
        elevation = Metrics.Margins.nano
    ) {
        Row(
            modifier = Modifier.padding(Metrics.Margins.default),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = itinerary.imageUrl,
                contentDescription = itinerary.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(Metrics.RoundCorners.default))
                    .background(Gray)
                    .fillMaxWidth()
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

