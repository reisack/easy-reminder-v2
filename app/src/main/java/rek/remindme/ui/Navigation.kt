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
import rek.remindme.ui.reminder.ReminderListScreen
import rek.remindme.ui.reminder.ReminderUpsertScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    val listReminderRoute = "list"
    val addReminderRoute = "add"

    val prefixEditReminderRoute = "edit"
    val editReminderRoute = "edit/{reminderId}"

    NavHost(navController = navController, startDestination = listReminderRoute) {
        composable(route = listReminderRoute) {
            ReminderListScreen(
                modifier = Modifier.padding(16.dp),
                onNewReminder = {
                    navController.navigate(addReminderRoute)
                },
                onReminderClick = { reminderId -> navController.navigate("$prefixEditReminderRoute/$reminderId") }
            )
        }

        composable(route = addReminderRoute) {
            ReminderUpsertScreen(
                modifier = Modifier.padding(16.dp),
                onReminderSaved = {
                    navController.navigate(listReminderRoute)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = editReminderRoute, arguments = listOf(
            navArgument("reminderId") {
                type = NavType.IntType
                nullable = false
            }
        )) {
            ReminderUpsertScreen(
                modifier = Modifier.padding(16.dp),
                onReminderSaved = {
                    navController.navigate(listReminderRoute)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
