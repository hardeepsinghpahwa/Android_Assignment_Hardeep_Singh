package com.example.assignment.mainScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class MainScreenViewModelFactory @Inject constructor(private val repository: MainScreenRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainScreenViewModel(repository) as T
    }
}