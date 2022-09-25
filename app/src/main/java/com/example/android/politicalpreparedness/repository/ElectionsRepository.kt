package com.example.android.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionsRepository(private val database: ElectionDatabase) {

    suspend fun refreshElections() {
        withContext(Dispatchers.IO) {
            try {
                val elections = CivicsApi.retrofitService.getElections()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getElections(): LiveData<List<Election>> {
        return database.electionDao.getALl()
    }

}