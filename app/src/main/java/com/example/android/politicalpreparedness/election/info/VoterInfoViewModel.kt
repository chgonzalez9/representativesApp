package com.example.android.politicalpreparedness.election.info

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val id :Int, private val division: Division, private val dataSource: ElectionDao) : ViewModel() {

    //TODO: Add live data to hold voter info
    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    private val stateTest = "alabama"

//    private val address =
//        if (division.state.isEmpty()) {
//            "${division.country}, $stateTest"
//        } else {
//           "${division.country}, ${division.state}"
//        }


    init {
        getVoterInfo()
    }

    //TODO: Add var and methods to populate voter info
    private fun getVoterInfo() {
        viewModelScope.launch {
            try {
                var address = division.state
                address = if (address.isEmpty()) {
                    "california"
                } else {
                    "${division.country}, ${division.state}"
                }
                _voterInfo.value = CivicsApi.retrofitService.getVoterInfo(address,id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status
    fun saveElection(election: Election) {
        viewModelScope.launch {
            try {
                dataSource.insertAll(election)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteElection() {
        viewModelScope.launch {
            try {
                dataSource.delete(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}