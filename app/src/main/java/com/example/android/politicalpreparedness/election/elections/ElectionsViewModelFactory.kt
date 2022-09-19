package com.example.android.politicalpreparedness.election.elections

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

//TODO: Create Factory to generate ElectionViewModel with provided election datasource
class ElectionsViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectionsViewModel::class.java)) {
            return ElectionsViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}