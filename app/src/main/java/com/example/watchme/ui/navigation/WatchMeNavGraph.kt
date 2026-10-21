package com.example.watchme.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.watchme.data.repository.WatchMeRepository
import com.example.watchme.ui.auth.ForgotPasswordScreen
import com.example.watchme.ui.auth.LoginScreen
import com.example.watchme.ui.auth.RegisterScreen
import com.example.watchme.ui.auth.WelcomeScreen
import com.example.watchme.ui.home.EmergencyNumbersScreen
import com.example.watchme.ui.home.HomeScreen

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Map : Screen("map")
    object Community : Screen("community")
    object Circle : Screen("circle")
    object Emergency : Screen("emergency")
    object ReportIncident : Screen("report_incident")
    object IncidentDetails : Screen("incident_details/{incidentId}") {
        fun createRoute(incidentId: String) = "incident_details/$incidentId"
    }
}

@Composable
fun WatchMeNavGraph(
    darkTheme: Boolean = false,
    onToggleDarkTheme: () -> Unit = {}
) {
    val navController = rememberNavController()
    val repository = remember { WatchMeRepository() }

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onGetStartedClick = { navController.navigate(Screen.Register.route) },
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onContinueAsGuestClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onForgotPasswordClick = { navController.navigate(Screen.ForgotPassword.route) },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onBackClick = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onLoginClick = { navController.navigate(Screen.Login.route) }
            )
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackClick = { navController.popBackStack() },
                onSendResetLinkClick = { }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                repository = repository,
                selectedTab = 0,
                onTabSelected = { tabIndex ->
                    when (tabIndex) {
                        0 -> {}
                        1 -> navController.navigate(Screen.Map.route)
                        2 -> navController.navigate(Screen.Community.route)
                        3 -> navController.navigate(Screen.Circle.route)
                    }
                },
                onViewMapClick = { navController.navigate(Screen.Map.route) },
                onSeeAllClick = { navController.navigate(Screen.Community.route) },
                onIncidentClick = { incidentId ->
                    navController.navigate(Screen.IncidentDetails.createRoute(incidentId))
                },
                onSosClick = { navController.navigate(Screen.Emergency.route) },
                onNotificationClick = { }
            )
        }
        composable(Screen.Map.route) {
            com.example.watchme.ui.map.MapScreen(
                repository = repository,
                selectedTab = 1,
                onTabSelected = { tabIndex ->
                    when (tabIndex) {
                        0 -> navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } }
                        1 -> {}
                        2 -> navController.navigate(Screen.Community.route)
                        3 -> navController.navigate(Screen.Circle.route)
                    }
                },
                onIncidentDetailsClick = { incidentId ->
                    navController.navigate(Screen.IncidentDetails.createRoute(incidentId))
                },
                onReportClick = { navController.navigate(Screen.ReportIncident.route) },
                onSosClick = { navController.navigate(Screen.Emergency.route) },
                onNotificationClick = { }
            )
        }
        composable(Screen.Community.route) {
            com.example.watchme.ui.community.CommunityScreen(
                repository = repository,
                selectedTab = 2,
                onTabSelected = { tabIndex ->
                    when (tabIndex) {
                        0 -> navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } }
                        1 -> navController.navigate(Screen.Map.route)
                        2 -> {}
                        3 -> navController.navigate(Screen.Circle.route)
                    }
                },
                onReportClick = { navController.navigate(Screen.ReportIncident.route) },
                onIncidentClick = { incidentId ->
                    navController.navigate(Screen.IncidentDetails.createRoute(incidentId))
                },
                onNotificationClick = { }
            )
        }
        composable(Screen.ReportIncident.route) {
            com.example.watchme.ui.report.ReportIncidentScreen(
                repository = repository,
                onBackClick = { navController.popBackStack() },
                onSubmitSuccess = {
                    navController.navigate(Screen.Community.route) {
                        popUpTo(Screen.Community.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Circle.route) {
            com.example.watchme.ui.circle.CircleScreen(
                repository = repository,
                selectedTab = 3,
                onTabSelected = { tabIndex ->
                    when (tabIndex) {
                        0 -> navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } }
                        1 -> navController.navigate(Screen.Map.route)
                        2 -> navController.navigate(Screen.Community.route)
                        3 -> {}
                    }
                },
                onNotificationClick = { }
            )
        }
        composable(Screen.Emergency.route) {
            EmergencyNumbersScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.IncidentDetails.route,
            arguments = listOf(navArgument("incidentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val incidentId = backStackEntry.arguments?.getString("incidentId") ?: "WM-101"
            com.example.watchme.ui.incident.IncidentDetailsScreen(
                repository = repository,
                incidentId = incidentId,
                onBackClick = { navController.popBackStack() },
                selectedTab = 0,
                onTabSelected = { tabIndex ->
                    when (tabIndex) {
                        0 -> navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } }
                        1 -> navController.navigate(Screen.Map.route)
                        2 -> navController.navigate(Screen.Community.route)
                        3 -> navController.navigate(Screen.Circle.route)
                    }
                }
            )
        }
    }
}
