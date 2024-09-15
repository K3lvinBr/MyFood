package com.example.myfood.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val searchQuery = MutableLiveData<String?>()
}