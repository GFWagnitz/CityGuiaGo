package com.pi.cityguiago

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*
import com.pi.cityguiago.designsystem.Background
import com.pi.cityguiago.network.PrefCacheManager
import com.pi.cityguiago.network.createDataStore
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.pi.cityguiago.model.Complaint
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val store = remember(context) { PrefCacheManager(createDataStore(context)) }

            MaterialTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Background),
                    color = Background
                ) {
                    NavigationGraph(store)
                }
            }
        }
    }
}

@Composable
fun NavigationGraph(store: PrefCacheManager) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginView(navController, store)
        }
        composable("register") {
            RegisterView(navController, store)
        }
        composable("home") {
            HomeView(navController, store)
        }
        composable("explore") {
            ExploreView(navController)
        }
        composable("itineraries") {
            ItinerariesView(navController)
        }
        composable(
            "itinerary_details/{itineraryId}",
            arguments = listOf(
                navArgument("itineraryId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val itineraryId = backStackEntry.arguments?.getString("itineraryId") ?: ""
            ItineraryDetailsView(navController, itineraryId)
        }
        composable(
            "attraction/{attractionId}",
            arguments = listOf(
                navArgument("attractionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val attractionId = backStackEntry.arguments?.getString("attractionId") ?: ""
            AttractionView(navController, attractionId)
        }
        composable(
            "complaint/{complaintData}",
            arguments = listOf(
                navArgument("complaintData") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val complaintJson = backStackEntry.arguments?.getString("complaintData") ?: ""
            val complaint = Json.decodeFromString<Complaint>(complaintJson)

            ComplaintView(navController, complaint)
        }
    }
}