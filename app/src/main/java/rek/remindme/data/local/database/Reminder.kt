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

package rek.remindme.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "unixTimestamp") val unixTimestamp: Long,
    @ColumnInfo(name = "notified") val notified: Boolean
)

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminder ORDER BY uid DESC LIMIT 10")
    fun getReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminder WHERE uid = :id")
    suspend fun getById(id: Int): Reminder?

    @Upsert
    suspend fun upsert(item: Reminder)
}
