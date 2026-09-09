package com.pinu.ai_integration_demo_project.ui.navigation

import androidx.compose.material3.Text
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

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object ChatList : Screen("chat_list")
    object Chat : Screen("chat/{chatId}") {
        fun createRoute(chatId: String) = "chat/$chatId"
    }
}

@Composable
fun ChatNavigation(viewModel: ChatViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToChats = { navController.navigate(Screen.ChatList.route) }
            ) { modifier ->
                // Dashboard content could be a summary or welcome screen
                Text("Welcome to Personal Assistants!", modifier = modifier)
            }
        }
        composable(Screen.ChatList.route) {
            ChatListScreen(
                viewModel = viewModel,
                onChatSelected = { chatId ->
                    navController.navigate(Screen.Chat.createRoute(chatId))
                }
            )
        }
        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable
            ChatScreen(
                chatId = chatId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
