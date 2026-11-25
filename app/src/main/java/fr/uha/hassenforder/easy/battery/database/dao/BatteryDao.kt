package fr.uha.hassenforder.easy.battery.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import fr.uha.hassenforder.easy.battery.model.Battery
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

@Dao
interface BatteryDao {

    @Query("""SELECT B.* 
            FROM batteries AS B 
            ORDER BY B.timestamp ASC"""
    )
    fun getAll(): Flow<List<Battery>>

    @Query("""SELECT B.* 
            FROM batteries AS B 
            WHERE B.timestamp > :after
            ORDER BY B.timestamp ASC"""
    )
    fun getAllAfter(after : Instant): Flow<List<Battery>>

    @Transaction
    @Query("""SELECT B.* 
            FROM batteries AS B 
            WHERE B.bid = :id
            """
    )
    fun getById(id: Long?): Flow<Battery?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun create(battery: Battery): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun create(battery: List<Battery>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(battery: Battery): Long

    @Delete
    suspend fun delete(battery: Battery)

}
