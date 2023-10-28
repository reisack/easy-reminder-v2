package rek.remindme.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Reminder::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
}
