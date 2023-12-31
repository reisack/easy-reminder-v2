package rek.remindme.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import rek.remindme.common.Consts
import rek.remindme.ui.reminder.ReminderListScreen
import rek.remindme.ui.reminder.ReminderUpsertScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    val listReminderRoute = "${Consts.Route.PREFIX_LIST_REMINDER_ROUTE}={${Consts.Route.MESSAGE_RES_NAV_ARG}}"
    val editReminderRoute = "${Consts.Route.PREFIX_EDIT_REMINDER_ROUTE}/{${Consts.Route.REMINDER_ID_NAV_ARG}}"

    NavHost(navController = navController, startDestination = listReminderRoute) {
        composable(route = listReminderRoute, arguments = listOf(
            navArgument(name = Consts.Route.MESSAGE_RES_NAV_ARG) {
                type = NavType.IntType
                defaultValue = 0
            }
        )) {entry ->
            GetReminderListScreen(
                navController = navController,
                entry = entry
            )
        }

        composable(route = Consts.Route.ADD_REMINDER_ROUTE) {
            GetReminderUpsertScreen(navController = navController)
        }

        composable(route = editReminderRoute, arguments = listOf(
            navArgument(name = Consts.Route.REMINDER_ID_NAV_ARG) {
                type = NavType.IntType
                nullable = false
            }
        )) {
            GetReminderUpsertScreen(navController = navController)
        }
    }
}

@Composable
private fun GetReminderListScreen(navController: NavHostController, entry: NavBackStackEntry) {
    ReminderListScreen(
        modifier = Modifier.padding(8.dp),
        snackbarMessageResOnLoad = entry.arguments?.getInt(Consts.Route.MESSAGE_RES_NAV_ARG)!!,
        onNewReminder = {
            navController.navigate(Consts.Route.ADD_REMINDER_ROUTE)
        },
        onReminderClick = { reminderId ->
            navController.navigate("${Consts.Route.PREFIX_EDIT_REMINDER_ROUTE}/$reminderId")
        },
        onBackButtonPressed = {
            navController.navigate("${Consts.Route.PREFIX_LIST_REMINDER_ROUTE}=0")
        }
    )
}

@Composable
private fun GetReminderUpsertScreen(navController: NavHostController) {
    ReminderUpsertScreen(
        modifier = Modifier.padding(8.dp),
        onReminderSaved = { snackbarMessageRes ->
            navController.navigate("${Consts.Route.PREFIX_LIST_REMINDER_ROUTE}=$snackbarMessageRes")
        },
        onReminderDeleted = { snackbarMessageRes ->
            navController.navigate("${Consts.Route.PREFIX_LIST_REMINDER_ROUTE}=$snackbarMessageRes")
        },
        onBack = {
            navController.navigate("${Consts.Route.PREFIX_LIST_REMINDER_ROUTE}=0")
        },
        onBackButtonPressed = {
            navController.navigate("${Consts.Route.PREFIX_LIST_REMINDER_ROUTE}=0")
        }
    )
}