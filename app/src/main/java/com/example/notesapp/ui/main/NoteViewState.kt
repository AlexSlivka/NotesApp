package com.example.notesapp.ui.main

import com.example.notesapp.data.model.Note
import com.example.notesapp.ui.base.BaseViewState

class NoteViewState(note: Note? = null, error: Throwable? = null) :
    BaseViewState<Note?>(note, error)