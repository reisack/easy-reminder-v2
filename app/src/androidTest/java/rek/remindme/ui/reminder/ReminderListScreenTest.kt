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

package rek.remindme.ui.reminder

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import rek.remindme.data.local.database.Reminder

/**
 * UI tests for [ReminderListScreen].
 */
@RunWith(AndroidJUnit4::class)
class ReminderListScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {
        composeTestRule.setContent {
            ReminderListScreenContent(items = fakeData)
        }
    }

    @Test
    fun firstItem_exists() {
        composeTestRule.onNodeWithText(fakeData.first().title, substring = true).assertExists().performClick()
    }
}

private val fakeData = listOf(
    Reminder(title = "Title 1", description = "Hello 1", unixTimestamp = 1697808658, alreadyNotified = false),
    Reminder(title = "Title 2", description = "Hello 2", unixTimestamp = 1697808658, alreadyNotified = false),
    Reminder(title = "Title 3", description = "Hello 3", unixTimestamp = 1697808658, alreadyNotified = false)
)
