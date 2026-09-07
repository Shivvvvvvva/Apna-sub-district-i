package com.example.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.localization.AppLanguage
import com.example.localization.LanguageManager
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HelpSettingsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.SaffronContainer
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.WarmSurface
import com.example.ui.theme.appBackgroundColor
import com.example.ui.theme.appSurfaceColor
import com.example.ui.viewmodel.BusTimetableViewModel

sealed class Screen(val route: String, val icon: ImageVector) {
    data object Home : Screen("home", Icons.Default.DirectionsBus)
    data object Favorites : Screen("favorites", Icons.Default.Bookmark)
    data object Help : Screen("help", Icons.Default.Call)
    data object Admin : Screen("admin", Icons.Default.DirectionsBus)

    fun getTitle(): String = when (this) {
        Home -> when (LanguageManager.currentLanguage) {
            AppLanguage.MARATHI -> "बस वेळापत्रक"
            AppLanguage.HINDI -> "बस समय"
            AppLanguage.ENGLISH -> "Buses"
        }
        Favorites -> when (LanguageManager.currentLanguage) {
            AppLanguage.MARATHI -> "आवडते"
            AppLanguage.HINDI -> "पसंदीदा"
            AppLanguage.ENGLISH -> "Saved"
        }
        Help -> when (LanguageManager.currentLanguage) {
            AppLanguage.MARATHI -> "मदत व संपर्क"
            AppLanguage.HINDI -> "मदद व संपर्क"
            AppLanguage.ENGLISH -> "Help & Call"
        }
        Admin -> "Admin"
    }
}

@Composable
fun MainApp(
    viewModel: BusTimetableViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    // Simplified to 3 essential bottom tabs for rural, effortless navigation
    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Favorites,
        Screen.Help
    )

    Scaffold(
        containerColor = appBackgroundColor(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = appSurfaceColor(),
                tonalElevation = 8.dp
            ) {
                bottomNavItems.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.getTitle()
                            )
                        },
                        label = {
                            Text(
                                text = screen.getTitle(),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SaffronPrimary,
                            selectedTextColor = SaffronPrimary,
                            indicatorColor = SaffronContainer,
                            unselectedIconColor = Color(0xFF6B7280),
                            unselectedTextColor = Color(0xFF6B7280)
                        ),
                        modifier = Modifier.testTag("nav_item_${screen.route}")
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel
                )
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    viewModel = viewModel,
                    onRouteSelected = { from, to ->
                        viewModel.setFromStand(from)
                        viewModel.setToDestination(to)
                        navController.navigate(Screen.Home.route)
                    }
                )
            }

            composable(Screen.Help.route) {
                HelpSettingsScreen(
                    onNavigateToAdmin = {
                        navController.navigate(Screen.Admin.route)
                    }
                )
            }

            composable(Screen.Admin.route) {
                AdminScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
