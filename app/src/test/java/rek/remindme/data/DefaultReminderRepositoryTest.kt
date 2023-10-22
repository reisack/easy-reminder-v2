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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import rek.remindme.data.local.database.Reminder
import rek.remindme.data.local.database.ReminderDao

/**
 * Unit tests for [DefaultReminderRepository].
 */
class DefaultReminderRepositoryTest {

    @Test
    fun reminders_newItemSaved_itemIsReturned() = runTest {
        val repository = DefaultReminderRepository(FakeReminderDao())

        repository.upsert(0,"title 1", "desc", System.currentTimeMillis(), false)

        assertEquals(repository.reminders.first().size, 1)
    }

}

private class FakeReminderDao : ReminderDao {

    private val data = mutableListOf<Reminder>()
    private var index: Int = 0

    override fun getReminders(): Flow<List<Reminder>> = flow {
        emit(data)
    }

    override suspend fun getById(id: Int): Reminder? {
        return data.find { reminder -> reminder.uid == id }
    }

    override suspend fun upsert(item: Reminder) {
        val reminder = data.find { reminder -> reminder.uid == item.uid }
        if (reminder != null) {
            val index = data.indexOf(reminder)
            data.removeAt(index)
            data.add(index, item)
        }
        else {
            data.add(index++, item)
        }
    }

    override suspend fun deleteById(id: Int) {
        val reminder = data.find { reminder -> reminder.uid == id }
        data.remove(reminder)
    }
}
