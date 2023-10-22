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

package rek.remindme.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import rek.remindme.data.ReminderRepository
import rek.remindme.data.DefaultReminderRepository
import rek.remindme.data.local.database.Reminder
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsReminderRepository(
        reminderRepository: DefaultReminderRepository
    ): ReminderRepository
}

class FakeReminderRepository @Inject constructor() : ReminderRepository {
    override val reminders: Flow<List<Reminder>> = flowOf(fakeReminders)

    override suspend fun upsert(
        id: Int?,
        title: String,
        description: String,
        unixTimestamp: Long,
        notified: Boolean
    ) {
        throw NotImplementedError()
    }

    override suspend fun getById(id: Int): Reminder? {
        throw NotImplementedError()
    }
}

val fakeReminders = listOf(
    Reminder(title = "Title 1", description = "Hello 1", unixTimestamp = 1697808658, notified = false),
    Reminder(title = "Title 2", description = "Hello 2", unixTimestamp = 1697808658, notified = false),
    Reminder(title = "Title 3", description = "Hello 3", unixTimestamp = 1697808658, notified = false))
