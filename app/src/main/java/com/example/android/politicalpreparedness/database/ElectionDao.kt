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
    suspend fun insertAll(vararg election: Election)

    //TODO: Add select all election query
    @Query("SELECT * FROM election_table")
    fun getALl() : LiveData<List<Election>>

    //TODO: Add select single election query
    @Query("SELECT * FROM election_table")
    suspend fun get(): Election?

    //TODO: Add delete query
    @Delete
    suspend fun deleteElection(election: Election)

    //TODO: Add clear query
    @Query("DELETE FROM election_table")
    suspend fun clearAll()
}