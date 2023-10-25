/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rek.remindme.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

    val prefixListReminderRoute = "list?messageRes"
    val listReminderRoute = "$prefixListReminderRoute={${Consts.MESSAGE_RES_NAV_ARG}}"

    val addReminderRoute = "add"

    val prefixEditReminderRoute = "edit"
    val editReminderRoute = "$prefixEditReminderRoute/{${Consts.REMINDER_ID_NAV_ARG}}"

    NavHost(navController = navController, startDestination = listReminderRoute) {
        composable(route = listReminderRoute, arguments = listOf(
            navArgument(name = Consts.MESSAGE_RES_NAV_ARG) {
                type = NavType.IntType
                defaultValue = 0
            }
        )) {entry ->
            ReminderListScreen(
                modifier = Modifier.padding(16.dp),
                snackbarMessageRes = entry.arguments?.getInt(Consts.MESSAGE_RES_NAV_ARG)!!,
                onNewReminder = {
                    navController.navigate(addReminderRoute)
                },
                onReminderClick = { reminderId ->
                    navController.navigate("$prefixEditReminderRoute/$reminderId")
                },
                onBackButtonPressed = {
                    navController.navigate("$prefixListReminderRoute=0")
                }
            )
        }

        composable(route = addReminderRoute) {
            ReminderUpsertScreen(
                modifier = Modifier.padding(16.dp),
                onReminderSaved = { snackbarMessageRes ->
                    navController.navigate("$prefixListReminderRoute=$snackbarMessageRes")
                },
                onReminderDeleted = { snackbarMessageRes ->
                    navController.navigate("$prefixListReminderRoute=$snackbarMessageRes")
                },
                onBack = {
                    navController.navigate("$prefixListReminderRoute=0")
                },
                onBackButtonPressed = {
                    navController.navigate("$prefixListReminderRoute=0")
                }
            )
        }

        composable(route = editReminderRoute, arguments = listOf(
            navArgument(name = Consts.REMINDER_ID_NAV_ARG) {
                type = NavType.IntType
                nullable = false
            }
        )) {
            ReminderUpsertScreen(
                modifier = Modifier.padding(16.dp),
                onReminderSaved = { snackbarMessageRes ->
                    navController.navigate("$prefixListReminderRoute=$snackbarMessageRes")
                },
                onReminderDeleted = { snackbarMessageRes ->
                    navController.navigate("$prefixListReminderRoute=$snackbarMessageRes")
                },
                onBack = {
                    navController.navigate("$prefixListReminderRoute=0")
                },
                onBackButtonPressed = {
                    navController.navigate("$prefixListReminderRoute=0")
                }
            )
        }
    }
}
