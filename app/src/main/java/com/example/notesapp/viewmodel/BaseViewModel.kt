package com.example.notesapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notesapp.ui.base.BaseViewState

open class BaseViewModel<T, VS : BaseViewState<T>> : ViewModel() {

    open val viewStateLiveData = MutableLiveData<VS>()

    open fun getViewState(): LiveData<VS> = viewStateLiveData
}