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
        composable("attraction") {
            AttractionView(navController)
        }
    }
}