package com.example.android.politicalpreparedness.election.elections

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import com.example.android.politicalpreparedness.database.ElectionDatabase.Companion.getInstance
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import kotlinx.coroutines.launch

class ElectionsViewModel(context: Context): ViewModel() {

    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections : LiveData<List<Election>>
        get() = _upcomingElections

    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections : LiveData<List<Election>>
        get() = _savedElections

    private val electionsObserver = Observer<List<Election>> {
        _upcomingElections.value = it
    }

    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo: LiveData<Election>
        get() = _navigateToVoterInfo

//    private var electionsListLiveData: LiveData<List<Election>>

    private val electionsRepository = ElectionsRepository(getInstance(context))

    init {
        upcomingElections()
        getElections()
//        electionsListLiveData.observeForever(electionsObserver)
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

    private fun getElections() {
        viewModelScope.launch {
            try {
                electionsRepository.refreshElections()
//                electionsListLiveData = electionsRepository.getElections()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun navigateToVoterInfo(election: Election) {
        _navigateToVoterInfo.value = election
    }

}