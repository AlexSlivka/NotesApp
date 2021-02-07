package com.example.notesapp.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel<T, VS : BaseViewState<T>> : ViewModel() {

    open val viewStateLiveData = MutableLiveData<VS>()

    open fun getViewState(): LiveData<VS> = viewStateLiveData
}