package com.example.myapplication

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivityVm:ViewModel() {
    var permissionFlow = MutableStateFlow(false)
}