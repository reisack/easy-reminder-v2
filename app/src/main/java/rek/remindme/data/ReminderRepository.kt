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

package rek.remindme.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import rek.remindme.data.local.database.Reminder
import rek.remindme.data.local.database.ReminderDao
import javax.inject.Inject

interface ReminderRepository {
    val reminders: Flow<List<Reminder>>

    suspend fun add(title: String, description: String, unixTimestamp: Long, notified: Boolean)
    suspend fun update(id: Int, title: String, description: String, unixTimestamp: Long, notified: Boolean)
    suspend fun getById(id: Int): Reminder?
}

class DefaultReminderRepository @Inject constructor(
    private val reminderDao: ReminderDao
) : ReminderRepository {

    override val reminders: Flow<List<Reminder>> =
        reminderDao.getReminders().map {
            items -> items.map {
                Reminder(
                    uid = it.uid,
                    title = it.title,
                    description = it.description,
                    unixTimestamp = it.unixTimestamp,
                    notified = it.notified
                )
            }
        }

    override suspend fun add(title: String, description: String, unixTimestamp: Long, notified: Boolean) {
        reminderDao.upsert(Reminder
            (
                title = title,
                description = description,
                unixTimestamp = unixTimestamp,
                notified = notified
            )
        )
    }

    override suspend fun update(
        id: Int,
        title: String,
        description: String,
        unixTimestamp: Long,
        notified: Boolean
    ) {
        val reminder = getById(id)

        // In case id doesn't exist in database
        if (reminder == null) {
            add(title, description, unixTimestamp, notified)
        }

        reminderDao.upsert(Reminder(
            uid = id,
            title = title,
            description = description,
            unixTimestamp = unixTimestamp,
            notified = notified
        ))
    }

    override suspend fun getById(id: Int): Reminder? {
        return reminderDao.getById(id)
    }
}
