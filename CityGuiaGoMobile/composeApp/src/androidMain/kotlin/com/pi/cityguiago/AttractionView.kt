package com.pi.cityguiago

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pi.cityguiago.designsystem.*
import com.pi.cityguiago.designsystem.components.*
import com.pi.cityguiago.model.Attraction
import androidx.compose.material.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.pi.cityguiago.model.Itinerary
import com.pi.cityguiago.model.Offer
import com.pi.cityguiago.model.Review
import com.pi.cityguiago.module.Attraction.AttractionState
import com.pi.cityguiago.module.home.AttractionEffect
import com.pi.cityguiago.module.home.AttractionEvent
import com.pi.cityguiago.module.home.AttractionViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel

@Composable
fun AttractionView(
    navController: NavHostController,
    attractionId: String,
    viewModel: AttractionViewModel = koinViewModel(key = "AttractionViewModel-${attractionId}")
) {
    val context = LocalContext.current

    val attractionState by viewModel.state.collectAsState()
    var attractionName by remember { mutableStateOf("") }

    LaunchedEffect(attractionId) {
        viewModel.onEvent(AttractionEvent.LoadData(attractionId))
    }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is AttractionEffect.ShowErrorMessage -> {
                    Toast.makeText(context, effect.errorMessage, Toast.LENGTH_LONG).show()
                }
                is AttractionEffect.OpenComplaintView -> {
                    val complaintJson = Json.encodeToString(effect.complaint)
                    navController.navigate("complaint/$complaintJson")
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { TextH3(attractionName) },
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            when (attractionState) {
                is ComponentState.Idle, ComponentState.Loading -> {}
                is ComponentState.Error -> {}
                is ComponentState.Loaded<*> -> {
                    ((attractionState as ComponentState.Loaded<*>).data as AttractionState).also { state ->
                        attractionName = state.attraction.nome
                        ImageHeader(state.attraction.imagens.firstOrNull()?.imageUrl ?: "")
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = Metrics.Margins.large),
                            horizontalAlignment = Alignment.Start
                        ) {
                            VerticalSpacers.Large()

                            Header(state.attraction, state.rating, state.reviewsCount, viewModel::onEvent)

                            VerticalSpacers.Default()

                            TabSection(state, viewModel::onEvent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImageHeader(imageUrl: String) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "Attraction Name",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(
                RoundedCornerShape(
                    bottomStart = Metrics.RoundCorners.large,
                    bottomEnd = Metrics.RoundCorners.large
                )
            )
            .background(Gray)
    )
}

@Composable
fun Header(
    attraction: Attraction,
    rating: Double,
    reviewsCount: Int,
    onEvent: (AttractionEvent) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
    TextH1(attraction?.nome ?: "Atração")
        IconButton(onClick = { onEvent(AttractionEvent.OnAttractionComplaintClick) }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_alert),
                contentDescription = "Favorite",
                tint = Color.Unspecified
            )
        }
    }

    VerticalSpacers.Small()

    TextBody1(attraction?.descricao ?: "")

    VerticalSpacers.Default()

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painter = painterResource(id = R.drawable.ic_star),
            contentDescription = "Star Icon",
            tint = Color.Unspecified)
        HorizontalSpacers.Small()
        TextBody2((String.format("%.1f", rating) + " (${reviewsCount} avaliações)"))

        HorizontalSpacers.Default()

        Icon(painter = painterResource(id = R.drawable.ic_location),
            contentDescription = "Star Icon",
            tint = Color.Unspecified)
        HorizontalSpacers.Small()
        TextBody2("${attraction?.enderecoCidade ?: "Sem localização"}")
    }
}

@Composable
fun TabSection(
    state: AttractionState,
    onEvent: (AttractionEvent) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Detalhes", "Avaliações")

    ScrollableTabRow(
        selectedTabIndex = selectedTab,
        backgroundColor = Color.Transparent,
        edgePadding = 0.dp,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                color = Blue
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { selectedTab = index },
                text = {
                    Text(
                        text = title,
                        color = if (selectedTab == index) Blue else Gray,
                        style = MaterialTheme.typography.body2
                    )
                }
            )
        }
    }

    VerticalSpacers.Large()

    when (selectedTab) {
        0 -> DetailTabSection(state, onEvent)
        1 -> ReviewTabSection(state.reviews, state.rating, state.reviewsCount, state.attraction.nome, onEvent)
    }
}

@Composable
fun DetailTabSection(
    state: AttractionState,
    onEvent: (AttractionEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        ItinerarySection(state.itineraries, onEvent)

        VerticalSpacers.Large()

        AboutAttraction(state.attraction)

        VerticalSpacers.Large()

        Offers(state.offers, onEvent)

        VerticalSpacers.Large()
    }
}

@Composable
fun ItinerarySection(
    itineraries: List<Itinerary>,
    onEvent: (AttractionEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .shadow(
                elevation = Metrics.Margins.micro,
                shape = RoundedCornerShape(Metrics.RoundCorners.default)
            )
            .fillMaxWidth()
            .clip(RoundedCornerShape(Metrics.RoundCorners.default))
            .background(White)
            .padding(Metrics.Margins.default)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Metrics.Margins.small)) {
            TextH2("Adicionar ao Roteiro")
            VerticalSpacers.Small()

            val optionsList = itineraries.map { it.titulo }
            var selectedOption by remember { mutableStateOf<String?>(null) }

            OutlinedDropdownMenu(
                options = optionsList,
                selectedOption = selectedOption,
                onOptionSelected = { selectedOption = it },
                placeholder = "Choose an option"
            )
            VerticalSpacers.Small()
            PrimaryButton(text = "Adicionar", onClick = {})
        }
    }
}

@Composable
fun AboutAttraction(attraction: Attraction) {
    TextH2("Sobre o ${attraction.nome}")
    VerticalSpacers.Default()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, Gray),
                RoundedCornerShape(Metrics.RoundCorners.default)
            )
            .clip(RoundedCornerShape(Metrics.RoundCorners.default))
            .background(White)
            .padding(Metrics.Margins.default),
    ) {
        TextBody1(attraction.descricao)
    }
}

@Composable
fun Offers(
    offers: List<Offer>,
    onEvent: (AttractionEvent) -> Unit
) {
    if (offers.isEmpty()) return

    TextH2("Ofertas")

    VerticalSpacers.Default()

    Column(
        verticalArrangement = Arrangement.spacedBy(Metrics.Margins.large)
    ) {
        offers.forEach {
            OfferCard(it, onEvent)
        }
    }

}

@Composable
fun OfferCard(
    offer: Offer,
    onEvent: (AttractionEvent) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .size(106.dp),
        backgroundColor = White,
        shape = RoundedCornerShape(Metrics.RoundCorners.default),
        elevation = Metrics.Margins.nano
    ) {
        Row {
            AsyncImage(
                model = "",
                contentDescription = offer.titulo,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(106.dp)
                    .clip(RoundedCornerShape(Metrics.RoundCorners.default))
                    .background(Gray)
                    .fillMaxWidth()
            )
            Row(
                modifier = Modifier.padding(Metrics.Margins.default),
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextH5(offer.titulo)
                        IconButton(
                            onClick = { onEvent(AttractionEvent.OnOfferComplaintClick(offer.id, offer.titulo)) },
                            modifier = Modifier.size(Metrics.Margins.large)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_alert),
                                contentDescription = "Favorite",
                                tint = Color.Unspecified
                            )
                        }
                    }

                    VerticalSpacers.Small()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextBody2(("R$" + String.format("%.2f", offer.preco)))
                        TextBody2(offer.dataFim)
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewTabSection(
    reviews: List<Review>,
    rating: Double,
    reviewsCount: Int,
    attractionName: String,
    onEvent: (AttractionEvent) -> Unit
) {
    Column {
        AttractionRating(rating, reviewsCount)

        VerticalSpacers.Large()

        PublicReviews(reviews, onEvent)

        VerticalSpacers.Large()

        LeaveReview(attractionName, onEvent)

        VerticalSpacers.Large()
    }
}

@Composable
fun AttractionRating(rating: Double, reviewsCount: Int) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextH1(String.format("%.1f", rating))
            HorizontalSpacers.Micro()
            TextBody2("(${reviewsCount} avaliações)")
        }

        VerticalSpacers.Small()

        RatingBar(rating.toFloat())
    }
}

@Composable
fun PublicReviews(
    reviews: List<Review>,
    onEvent: (AttractionEvent) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Metrics.Margins.large)
    ) {
        reviews.forEach {
            ReviewCard(it, onEvent)
        }
    }
}

@Composable
fun ReviewCard(
    review: Review,
    onEvent: (AttractionEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .shadow(
                elevation = Metrics.Margins.micro,
                shape = RoundedCornerShape(Metrics.RoundCorners.default)
            )
            .fillMaxWidth()
            .clip(RoundedCornerShape(Metrics.RoundCorners.default))
            .background(White)
            .padding(Metrics.Margins.default)
    ) {
        Row {

            AsyncImage(
                model = review.user.avatar ?: "",
                contentDescription = review.user.username,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Gray)
            )

            HorizontalSpacers.Small()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    TextH4(review.user.username)
                    TextBody2(review.createdAt)
                }
                IconButton(onClick = { onEvent(AttractionEvent.OnReviewComplaintClick(review.id, review.user.username)) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_alert),
                        contentDescription = "Report Review",
                        tint = Color.Unspecified
                    )
                }
            }
        }

        VerticalSpacers.Small()

        RatingBar(review.nota.toFloat(), starSize = 24)

        VerticalSpacers.Small()

        TextBody1(review.comentario ?: "")
    }
}

@Composable
fun LeaveReview(
    attractionName: String,
    onEvent: (AttractionEvent) -> Unit
) {
    Column {
        TextH2("Avalie Mana Poke")

        VerticalSpacers.Default()

        RatingBar(5.0.toFloat(), starSize = 24)

        VerticalSpacers.Default()

        TextEditor("Comente sobre sua experiência com Mana Poke", "") {}

        VerticalSpacers.Default()

        PrimaryButton( "Enviar", onClick = {})
    }
}




