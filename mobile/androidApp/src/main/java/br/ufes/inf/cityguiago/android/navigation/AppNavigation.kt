package br.ufes.inf.cityguiago.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.ufes.inf.cityguiago.android.ui.screens.attractiondetail.AttractionDetailScreen
import br.ufes.inf.cityguiago.android.ui.screens.attractions.AttractionsScreen
import br.ufes.inf.cityguiago.android.ui.screens.auth.LoginScreen
import br.ufes.inf.cityguiago.android.ui.screens.auth.SignupScreen
import br.ufes.inf.cityguiago.android.ui.screens.main.MainScreen
import br.ufes.inf.cityguiago.repository.AuthRepository
import org.koin.compose.koinInject

object Route {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val MAIN = "main"
    const val ATTRACTIONS = "attractions"
    const val ATTRACTION_DETAIL = "attraction/{attractionId}"
    
    fun attractionDetail(attractionId: String) = "attraction/$attractionId"
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    authRepository: AuthRepository = koinInject()
) {
    val isLoggedIn by authRepository.isLoggedIn.collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Route.MAIN else Route.LOGIN
    ) {
        // Auth screens
        composable(Route.LOGIN) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Route.MAIN) },
                onNavigateToSignup = { navController.navigate(Route.SIGNUP) }
            )
        }
        
        composable(Route.SIGNUP) {
            SignupScreen(
                onSignupSuccess = { navController.navigate(Route.MAIN) },
                onNavigateToLogin = { navController.navigate(Route.LOGIN) }
            )
        }
        
        // Main screens
        composable(Route.MAIN) {
            MainScreen(
                onNavigateToAttractionDetail = { attractionId ->
                    navController.navigate(Route.attractionDetail(attractionId))
                }
            )
        }
        
        // Detail screens
        composable(
            route = Route.ATTRACTION_DETAIL,
            arguments = listOf(
                navArgument("attractionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val attractionId = backStackEntry.arguments?.getString("attractionId") ?: ""
            AttractionDetailScreen(
                attractionId = attractionId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
} 