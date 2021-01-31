package com.example.notesapp.ui.main

import com.example.notesapp.data.model.Note
import com.example.notesapp.ui.base.BaseViewState

class MainViewState(val notes: List<Note>? = null, error: Throwable? = null) :
    BaseViewState<List<Note>?>(notes, error)