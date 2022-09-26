package com.example.android.politicalpreparedness.election.elections

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase.Companion.getInstance
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

class ElectionsViewModel(context: Context): ViewModel() {

    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections : LiveData<List<Election>>
        get() = _upcomingElections

    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections : LiveData<List<Election>>
        get() = _savedElections

    private val savedElectionObserver = Observer<List<Election>> {
        _savedElections.value = it
    }

    private lateinit var savedListLiveData: LiveData<List<Election>>

    private val electionDatabase = getInstance(context)

    init {
        upcomingElections()
        getSavedElections()
    }

    private fun upcomingElections() {
        viewModelScope.launch {
            try {
                _upcomingElections.value = CivicsApi.retrofitService.getElections().elections
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getSavedElections() {
        viewModelScope.launch {
            try {
                savedListLiveData = electionDatabase.electionDao.getALl()
                savedListLiveData.observeForever(savedElectionObserver)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        savedListLiveData.removeObserver(savedElectionObserver)
    }

}