package br.ufes.inf.cityguiago.android.ui.screens.attractiondetail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.ufes.inf.cityguiago.android.R
import br.ufes.inf.cityguiago.android.ui.components.CityButton
import br.ufes.inf.cityguiago.android.ui.components.RatingBar
import br.ufes.inf.cityguiago.model.Avaliacao
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttractionDetailScreen(
    attractionId: String,
    onNavigateBack: () -> Unit,
    viewModel: AttractionDetailViewModel = koinViewModel()
) {
    val attraction by viewModel.attraction.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    var reviewRating by remember { mutableStateOf(0) }
    var reviewComment by remember { mutableStateOf("") }
    var isSubmittingReview by remember { mutableStateOf(false) }
    
    LaunchedEffect(attractionId) {
        viewModel.loadAttractionDetails(attractionId)
    }
    
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is AttractionDetailEvent.ReviewSubmitted -> {
                    isSubmittingReview = false
                    reviewRating = 0
                    reviewComment = ""
                    scope.launch {
                        snackbarHostState.showSnackbar("Review submitted successfully")
                    }
                }
                is AttractionDetailEvent.Error -> {
                    isSubmittingReview = false
                    scope.launch {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
                is AttractionDetailEvent.Loading -> {
                    isSubmittingReview = true
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.attraction_details)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (isLoading && attraction == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (attraction != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Header with image
                item {
                    if (attraction!!.imagens.isNotEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(attraction!!.imagens.first().caminho)
                                .crossfade(true)
                                .build(),
                            contentDescription = attraction!!.nome,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = attraction!!.nome.first().toString(),
                                style = MaterialTheme.typography.displayLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                
                // Attraction details
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = attraction!!.nome,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = attraction!!.categoria.descricao,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = attraction!!.descricao,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Price
                        if (attraction!!.preco_medio != null) {
                            Text(
                                text = stringResource(R.string.average_price, "R$ ${attraction!!.preco_medio}"),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        
                        // Business hours
                        if (!attraction!!.horario_funcionamento.isNullOrEmpty()) {
                            Text(
                                text = stringResource(R.string.business_hours, attraction!!.horario_funcionamento!!),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        
                        // Address
                        val address = buildString {
                            attraction!!.endereco_logradouro?.let { append(it) }
                            attraction!!.endereco_numero?.let { append(", $it") }
                            attraction!!.endereco_bairro?.let { append(", $it") }
                            attraction!!.endereco_cidade?.let { append(", $it") }
                            attraction!!.endereco_estado?.let { append(" - $it") }
                        }
                        
                        if (address.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                
                                Spacer(modifier = Modifier.width(4.dp))
                                
                                Text(
                                    text = stringResource(R.string.address, address),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                
                // Divider
                item {
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                }
                
                // Review section
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.reviews),
                            style = MaterialTheme.typography.titleLarge
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Add review
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.rate_attraction),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                RatingBar(
                                    rating = reviewRating.toFloat(),
                                    isIndicator = false,
                                    onRatingChanged = { reviewRating = it }
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                TextField(
                                    value = reviewComment,
                                    onValueChange = { reviewComment = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text(stringResource(R.string.review_optional)) },
                                    maxLines = 3
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                CityButton(
                                    text = stringResource(R.string.submit_review),
                                    onClick = {
                                        viewModel.submitReview(
                                            attractionId = attractionId,
                                            rating = reviewRating,
                                            comment = reviewComment.takeIf { it.isNotEmpty() }
                                        )
                                    },
                                    isLoading = isSubmittingReview,
                                    enabled = reviewRating > 0
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                
                // Reviews list
                if (reviews.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_reviews),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(reviews) { review ->
                        ReviewItem(review = review)
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewItem(review: Avaliacao) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(8.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Column {
                    Text(
                        text = stringResource(R.string.posted_by, review.user),
                        style = MaterialTheme.typography.labelMedium
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    RatingBar(
                        rating = review.nota.toFloat(),
                        starSize = 16
                    )
                }
            }
            
            if (!review.comentario.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = review.comentario,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
} 