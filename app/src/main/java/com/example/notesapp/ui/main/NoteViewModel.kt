package com.example.notesapp.ui.main

import androidx.lifecycle.ViewModel
import com.example.notesapp.data.Repository
import com.example.notesapp.data.model.Note

class NoteViewModel(private val repository: Repository = Repository) :
    ViewModel() {
    private var pendingNote: Note? = null

    fun saveChanges(note: Note) {
        pendingNote = note
    }

    override fun onCleared() {
        if (pendingNote != null) {
            repository.saveNote(pendingNote!!)
        }
    }
}