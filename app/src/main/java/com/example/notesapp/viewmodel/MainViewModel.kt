package com.example.notesapp.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.Observer
import com.example.notesapp.data.Repository
import com.example.notesapp.data.model.Note
import com.example.notesapp.data.model.NoteResult
import com.example.notesapp.ui.main.MainViewState
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainViewModel(val repository: Repository) :
    BaseViewModel<List<Note>?>() {

    private val notesChannel by lazy { runBlocking { repository.getNotes() } }

    init {
        launch {
            notesChannel.consumeEach { result ->
                when (result) {
                    is NoteResult.Success<*> -> setData(result.data as? List<Note>)
                    is NoteResult.Error -> setError(result.error)
                }
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onCleared() {
        notesChannel.cancel()
        super.onCleared()
    }
}