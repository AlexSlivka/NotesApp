package com.example.notesapp.ui.main

import com.example.notesapp.data.model.Note
import com.example.notesapp.ui.base.BaseViewState

class NoteViewState(
    data: Data = Data(),
    error: Throwable? = null
) :
    BaseViewState<NoteViewState.Data>(data, error) {

    data class Data(val isDeleted: Boolean = false, val note: Note? = null)
}