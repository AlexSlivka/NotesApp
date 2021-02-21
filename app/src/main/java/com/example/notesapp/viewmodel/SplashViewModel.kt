package com.example.notesapp.viewmodel

import com.example.notesapp.data.Repository
import com.example.notesapp.data.errors.NoAuthException
import com.example.notesapp.ui.main.SplashViewState
import kotlinx.coroutines.launch

class SplashViewModel(private val repository: Repository) :
    BaseViewModel<Boolean>() {

    fun requestUser() {
        launch {
            repository.getCurrentUser()?.let {
                setData(true)
            } ?: setError(NoAuthException())
        }
    }
}