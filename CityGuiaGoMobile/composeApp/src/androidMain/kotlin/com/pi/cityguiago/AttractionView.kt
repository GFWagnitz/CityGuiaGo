package com.pi.cityguiago

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.pi.cityguiago.model.Category
import com.pi.cityguiago.model.Image
import kotlinx.serialization.json.Json

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        AsyncImage(
            model = attraction.imagens.firstOrNull()?.caminho ?: "",
            contentDescription = attraction.nome,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(bottomStart = Metrics.RoundCorners.large, bottomEnd = Metrics.RoundCorners.large))
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Metrics.Margins.large),
            horizontalAlignment = Alignment.Start
        ) {
            VerticalSpacers.Large()

            // Title + Star Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextH1(attraction?.nome ?: "Atração")
                Icon(imageVector = Icons.Filled.Star, contentDescription = "Favorite")
            }

            VerticalSpacers.Small()

            // Description
            TextBody1(attraction?.descricao ?: "")

            VerticalSpacers.Default()

            // Rating & Address
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Star, contentDescription = "Star Icon")
                HorizontalSpacers.Small()
                TextBody2("${attraction?.precoMedio ?: 0.0} ⭐")

                HorizontalSpacers.Default()

                TextBody2("(${attraction?.enderecoCidade ?: "Sem localização"})")
            }

            VerticalSpacers.Default()

            // Tabs (Detalhes & Avaliações)
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
        }
    }
}
