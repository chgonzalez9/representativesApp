package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.util.SingleLiveEvent
import kotlinx.coroutines.launch

class RepresentativeViewModel: ViewModel() {

    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives : LiveData<List<Representative>>
        get() = _representatives

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    init {
//        _address.value = Address(
//            "",
//            "",
//            "",
//            "Alabama",
//            ""
//        )
    }


    fun getRepresentatives(address: String) {
        viewModelScope.launch {
            val (offices, officials) = CivicsApi.retrofitService.getRepresentatives(address)
            _representatives.value = offices.flatMap { office ->
                office.getRepresentatives(officials) }
        }
    }

    fun getAddressFromLocation(location: Address){
        _address.value = location
    }

    fun getAddressFromUser() {
        _address.value = Address(
            "",
            "",
            "",
            "Alabama",
            ""
        )
    }

}
