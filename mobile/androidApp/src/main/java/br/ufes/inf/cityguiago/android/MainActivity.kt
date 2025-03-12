package br.ufes.inf.cityguiago.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import br.ufes.inf.cityguiago.android.navigation.AppNavigation
import br.ufes.inf.cityguiago.android.ui.theme.CityGuiaGoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            CityGuiaGoApp()
        }
    }
}

@Composable
fun CityGuiaGoApp() {
    CityGuiaGoTheme {
        val navController = rememberNavController()
        AppNavigation(navController = navController)
    }
}