package com.kashmir.thefilamentary.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kashmir.thefilamentary.ui.screen.*

object AppDestinations {
    const val FILAMENT_LIST = "filament_list"
    const val FILAMENT_DETAIL = "filament_detail/{filamentId}"
    const val FILAMENT_ADD = "filament_add"
    const val FILAMENT_EDIT = "filament_edit/{filamentId}"
    const val PRINT_LOG_ADD = "print_log_add/{filamentId}"
    const val PRINT_LOG_EDIT = "print_log_edit/{filamentId}/{printLogId}"
    const val SETTINGS = "settings"
    
    // Helper functions for navigation with arguments
    fun filamentDetail(filamentId: Long) = "filament_detail/$filamentId"
    fun filamentEdit(filamentId: Long) = "filament_edit/$filamentId"
    fun printLogAdd(filamentId: Long) = "print_log_add/$filamentId"
    fun printLogEdit(filamentId: Long, printLogId: Long) = "print_log_edit/$filamentId/$printLogId"
}

@Composable
fun AppNavigation(navController: NavHostController = androidx.navigation.compose.rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.FILAMENT_LIST
    ) {
        composable(AppDestinations.FILAMENT_LIST) {
            FilamentListScreen(
                onFilamentClick = { filamentId ->
                    navController.navigate(AppDestinations.filamentDetail(filamentId))
                },
                onAddFilamentClick = {
                    navController.navigate(AppDestinations.FILAMENT_ADD)
                },
                onSettingsClick = {
                    navController.navigate(AppDestinations.SETTINGS)
                }
            )
        }
        
        composable(
            route = AppDestinations.FILAMENT_DETAIL,
            arguments = listOf(
                navArgument("filamentId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val filamentId = backStackEntry.arguments?.getLong("filamentId") ?: 0
            FilamentDetailScreen(
                filamentId = filamentId,
                onBackClick = {
                    navController.popBackStack()
                },
                onEditClick = {
                    navController.navigate(AppDestinations.filamentEdit(filamentId))
                },
                onAddPrintLogClick = {
                    navController.navigate(AppDestinations.printLogAdd(filamentId))
                },
                onPrintLogClick = { printLogId ->
                    navController.navigate(AppDestinations.printLogEdit(filamentId, printLogId))
                }
            )
        }
        
        composable(AppDestinations.FILAMENT_ADD) {
            FilamentEditScreen(
                filamentId = null,
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveComplete = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = AppDestinations.FILAMENT_EDIT,
            arguments = listOf(
                navArgument("filamentId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val filamentId = backStackEntry.arguments?.getLong("filamentId") ?: 0
            FilamentEditScreen(
                filamentId = filamentId,
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveComplete = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = AppDestinations.PRINT_LOG_ADD,
            arguments = listOf(
                navArgument("filamentId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val filamentId = backStackEntry.arguments?.getLong("filamentId") ?: 0
            PrintLogEditScreen(
                filamentId = filamentId,
                printLogId = null,
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveComplete = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = AppDestinations.PRINT_LOG_EDIT,
            arguments = listOf(
                navArgument("filamentId") { type = NavType.LongType },
                navArgument("printLogId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val filamentId = backStackEntry.arguments?.getLong("filamentId") ?: 0
            val printLogId = backStackEntry.arguments?.getLong("printLogId") ?: 0
            PrintLogEditScreen(
                filamentId = filamentId,
                printLogId = printLogId,
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveComplete = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(AppDestinations.SETTINGS) {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}