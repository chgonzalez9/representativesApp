package com.example.android.politicalpreparedness.election.elections

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import com.example.android.politicalpreparedness.database.ElectionDatabase.Companion.getInstance
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

    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo: LiveData<Election>
        get() = _navigateToVoterInfo

    private val electionsRepository = ElectionsRepository(getInstance(context))

    init {
        getElections()
    }

    private fun getElections() {
        viewModelScope.launch {
            try {
                electionsRepository.getElections()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun navigateToVoterInfo(election: Election) {
        _navigateToVoterInfo.value = election
    }

}