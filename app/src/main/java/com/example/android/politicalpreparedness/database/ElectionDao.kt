package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    //TODO: Add insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg elections: Election)

    //TODO: Add select all election query
    @Query("SELECT * FROM election_table")
    fun getALl() : LiveData<List<Election>>

    //TODO: Add select single election query
    @Query("SELECT * FROM election_table WHERE id = :id")
    suspend fun get(id: Int): LiveData<List<Election>>

    //TODO: Add delete query
    @Query("DELETE FROM election_table WHERE id = :id")
    suspend fun deleteElection(id: Int)

    //TODO: Add clear query
    @Delete
    suspend fun clearAll()
}