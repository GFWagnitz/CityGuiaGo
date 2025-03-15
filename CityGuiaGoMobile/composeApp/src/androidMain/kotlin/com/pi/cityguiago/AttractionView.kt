package com.pi.cityguiago

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
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.pi.cityguiago.model.Category
import com.pi.cityguiago.model.Image

@Composable
fun AttractionView(navController: NavHostController) {

    val attraction = Attraction(
        id = "1",
        nome = "Mana Poke",
        descricao = "Restaurante de temática e culinária havaiana",
        categoria = Category(id = "0", descricao = "Restaurante"),
        horarioFuncionamento = "10:00 - 22:00",
        precoMedio = 50.0,
        enderecoLogradouro = "Av. Vitória",
        enderecoNumero = "1234",
        enderecoComplemento = "Loja 5",
        enderecoBairro = "Centro",
        enderecoCidade = "Vitória",
        enderecoEstado = "ES",
        enderecoCep = "29000-000",
        enderecoCoordenadas = "-20.3155, -40.3128",
        imagens = listOf(
                Image(id = "1", caminho = "https://static.vecteezy.com/ti/fotos-gratis/t2/41436456-ai-gerado-cinematografico-imagem-do-uma-leao-dentro-uma-natureza-panorama-foto.jpg")
    )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { TextH3(attraction.categoria.descricao) },
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
            ImageHeader(attraction.imagens.firstOrNull()?.caminho ?: "")
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Metrics.Margins.large),
                horizontalAlignment = Alignment.Start
            ) {
                VerticalSpacers.Large()

                Header(attraction)

                VerticalSpacers.Default()

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
                    0 -> DetailTabSection()
                    1 -> ReviewTabSection()
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
    )
}

@Composable
fun Header(attraction: Attraction) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
    TextH1(attraction?.nome ?: "Atração")
    Icon(painter = painterResource(id = R.drawable.ic_alert),
        contentDescription = "Favorite",
        tint = Color.Unspecified)
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
        TextBody2("${1.0} (135 avaliações)")

        HorizontalSpacers.Default()

        Icon(painter = painterResource(id = R.drawable.ic_location),
            contentDescription = "Star Icon",
            tint = Color.Unspecified)
        HorizontalSpacers.Small()
        TextBody2("${attraction?.enderecoCidade ?: "Sem localização"}")
    }
}

@Composable
fun DetailTabSection() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        ItinerarySection()

        VerticalSpacers.Large()

        AboutAttraction()

        VerticalSpacers.Large()

        Offers()

        VerticalSpacers.Large()
    }
}

@Composable
fun ItinerarySection() {
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
            SearchBar(
                text = "",
                placeholder = "Escolha um roteiro",
                onTextChanged = {},
                icon = painterResource(id = R.drawable.ic_search)
            )
            VerticalSpacers.Small()
            PrimaryButton(text = "Adicionar", onClick = {})
        }
    }
}

@Composable
fun AboutAttraction() {
    TextH2("Sobre o Mana Poke")
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
        TextBody1("Conheça o Mana Poke No MANA POKE, poke não é apenas um prato típico havaiano, mas é, além disso, sinônimo de alimentação nutritiva e agradável.")
    }
}

@Composable
fun Offers() {
    TextH2("Ofertas")

    VerticalSpacers.Default()

    val offers = listOf(
        Offer("Poke Viagem", "R\$65", "Disponível até 12/03", "https://static.vecteezy.com/ti/fotos-gratis/t2/41436456-ai-gerado-cinematografico-imagem-do-uma-leao-dentro-uma-natureza-panorama-foto.jpg"),
    )


    Column(
        verticalArrangement = Arrangement.spacedBy(Metrics.Margins.large)
    ) {
        offers.forEach {
            OfferCard(it)
        }
    }

}

data class Offer(
    val title: String,
    val price: String,
    val disponibility: String,
    val imageUrl: String
)

@Composable
fun OfferCard(offer: Offer) {
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
                model = offer.imageUrl,
                contentDescription = offer.title,
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
                        TextH5(offer.title)
                        Icon(
                            painter = painterResource(id = R.drawable.ic_alert),
                            contentDescription = "Favorite",
                            tint = Color.Unspecified
                        )
                    }

                    VerticalSpacers.Small()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextBody2(offer.price)
                        TextBody2(offer.disponibility)
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewTabSection() {
    Column {
        AttractionRating()

        VerticalSpacers.Large()

        PublicReviews()

        VerticalSpacers.Large()

        LeaveReview()

        VerticalSpacers.Large()
    }
}

@Composable
fun AttractionRating() {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextH1("5.0")
            HorizontalSpacers.Micro()
            TextBody2("(135 avaliações)")
        }

        VerticalSpacers.Small()

        RatingBar(5.0.toFloat())
    }
}

data class Review(
    val name: String,
    val date: String,
    val rating: Float,
    val description: String,
    val imageUrl: String
)

@Composable
fun PublicReviews() {
    val reviews = listOf(
        Review("Gabriel Wagnitz", "21 Jan", 5.0.toFloat(), "Gostei muito da comida, ingredientes muito frescos", "https://static.vecteezy.com/ti/fotos-gratis/t2/41436456-ai-gerado-cinematografico-imagem-do-uma-leao-dentro-uma-natureza-panorama-foto.jpg"),
        Review("Gabriel Wagnitz", "21 Jan", 5.0.toFloat(), "Gostei muito da comida, ingredientes muito frescos", "https://static.vecteezy.com/ti/fotos-gratis/t2/41436456-ai-gerado-cinematografico-imagem-do-uma-leao-dentro-uma-natureza-panorama-foto.jpg")
    )


    Column(
        verticalArrangement = Arrangement.spacedBy(Metrics.Margins.large)
    ) {
        reviews.forEach {
            ReviewCard(it)
        }
    }
}

@Composable
fun ReviewCard(review: Review) {
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
                model = review.imageUrl,
                contentDescription = review.name,
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
                    TextH4(review.name)
                    TextBody2(review.date)
                }
                Icon(
                    painter = painterResource(id = R.drawable.ic_alert),
                    contentDescription = "Report Review",
                    tint = Color.Unspecified
                )
            }
        }

        VerticalSpacers.Small()

        RatingBar(review.rating.toFloat(), starSize = 24)

        VerticalSpacers.Small()

        TextBody1(review.description)
    }
}

@Composable
fun LeaveReview() {
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




