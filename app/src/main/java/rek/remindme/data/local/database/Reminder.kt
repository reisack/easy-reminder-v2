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
    @Query("""
        SELECT * FROM
        (
           SELECT *
           FROM Reminder 
           WHERE unixTimestamp >= strftime('%s','now') * 1000
           ORDER BY unixTimestamp ASC, Title ASC
        ) AS T1
        UNION ALL
        SELECT * FROM
        (
           SELECT *
           FROM Reminder 
           WHERE unixTimestamp < strftime('%s','now') * 1000
           ORDER BY unixTimestamp DESC, Title ASC
        ) AS T2
              """)
    fun getReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminder WHERE uid = :id")
    suspend fun getById(id: Int): Reminder?

    @Upsert
    suspend fun upsert(item: Reminder)

    @Query("DELETE FROM reminder WHERE uid = :id")
    suspend fun deleteById(id: Int)

    // TODO : For later : @Query("DELETE FROM reminder WHERE notified = 1")
    @Query("DELETE FROM reminder WHERE unixTimestamp < strftime('%s','now') * 1000")
    suspend fun deleteNotified()
}
