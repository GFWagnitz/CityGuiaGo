package br.ufes.inf.cityguiago.android.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Attractions
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import br.ufes.inf.cityguiago.android.R
import br.ufes.inf.cityguiago.android.ui.screens.attractions.AttractionsScreen
import br.ufes.inf.cityguiago.android.ui.screens.profile.ProfileScreen

enum class MainTab {
    HOME, ATTRACTIONS, PROFILE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToAttractionDetail: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(MainTab.ATTRACTIONS) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (selectedTab) {
                            MainTab.HOME -> stringResource(R.string.home)
                            MainTab.ATTRACTIONS -> stringResource(R.string.attractions)
                            MainTab.PROFILE -> stringResource(R.string.profile)
                        }
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text(stringResource(R.string.home)) },
                    selected = selectedTab == MainTab.HOME,
                    onClick = { selectedTab = MainTab.HOME }
                )
                
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Attractions, contentDescription = null) },
                    label = { Text(stringResource(R.string.attractions)) },
                    selected = selectedTab == MainTab.ATTRACTIONS,
                    onClick = { selectedTab = MainTab.ATTRACTIONS }
                )
                
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                    label = { Text(stringResource(R.string.profile)) },
                    selected = selectedTab == MainTab.PROFILE,
                    onClick = { selectedTab = MainTab.PROFILE }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                MainTab.HOME -> {
                    // For now, just show attractions screen on home too
                    AttractionsScreen(onAttractionClick = onNavigateToAttractionDetail)
                }
                MainTab.ATTRACTIONS -> {
                    AttractionsScreen(onAttractionClick = onNavigateToAttractionDetail)
                }
                MainTab.PROFILE -> {
                    ProfileScreen()
                }
            }
        }
    }
} 