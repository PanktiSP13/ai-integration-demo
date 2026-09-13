package com.pinu.ai_integration_demo_project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pinu.ai_integration_demo_project.ui.ChatViewModel
import com.pinu.ai_integration_demo_project.ui.chat.ChatScreen
import com.pinu.ai_integration_demo_project.ui.chatlist.ChatListScreen
import com.pinu.ai_integration_demo_project.ui.dashboard.DashboardScreen
import com.pinu.ai_integration_demo_project.ui.utils.BankingMockDataLoader

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object ChatList : Screen("chat_list")
    object Chat : Screen("chat/{chatId}/{role}/{name}") {
        fun createRoute(chatId: String, role: String, name: String?="") = "chat/$chatId/$role/$name"

    }
}

@Composable
fun ChatNavigation(viewModel: ChatViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToChats = { navController.navigate(Screen.ChatList.route) },
                onNavigateToBankingSupportChat = {
                    navController.navigate(Screen.Chat.createRoute("banking_support", "Banking Application Support","Bank Support"))
                },
                onNavigateToBankingAppSupportChat = {
                    navController.navigate(Screen.Chat.createRoute("banking_app_support", "Banking Application Support","Bank App Support"))
                }
            )
        }
        composable(Screen.ChatList.route) {
            ChatListScreen(
                viewModel = viewModel,
                onChatSelected = { chatId, role ->
                    navController.navigate(Screen.Chat.createRoute(chatId, role = role))
                }
            )
        }
        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType },
                navArgument("role") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType  ; nullable = true },
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable
            val role = backStackEntry.arguments?.getString("role") ?: return@composable
            val name = backStackEntry.arguments?.getString("name") ?: return@composable

            ChatScreen(
                chatId = chatId,
                role = role,
                name = name,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
