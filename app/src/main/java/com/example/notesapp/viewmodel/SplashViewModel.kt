package com.example.notesapp.viewmodel

import com.example.notesapp.data.Repository
import com.example.notesapp.data.errors.NoAuthException
import com.example.notesapp.ui.main.SplashViewState

class SplashViewModel(private val repository: Repository) :
    BaseViewModel<Boolean?, SplashViewState>() {

    fun requestUser() {
        repository.getCurrentUser().observeForever { user ->
            viewStateLiveData.value = user?.let {
                SplashViewState(isAuth = true)
            } ?: SplashViewState(error = NoAuthException())
        }
    }
}