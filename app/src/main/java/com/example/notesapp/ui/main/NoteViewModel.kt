package com.example.notesapp.ui.main

import androidx.lifecycle.Observer
import com.example.notesapp.data.Repository
import com.example.notesapp.data.model.Note
import com.example.notesapp.data.model.NoteResult
import com.example.notesapp.ui.base.BaseViewModel

class NoteViewModel(val repository: Repository = Repository) :
    BaseViewModel<Note?, NoteViewState>() {

    private var pendingNote: Note? = null

    fun saveChanges(note: Note) {
        pendingNote = note
    }

    override fun onCleared() {
        if (pendingNote != null) {
            repository.saveNote(pendingNote!!)
        }
    }

    fun loadNote(noteId: String) {
        repository.getNoteById(noteId).observeForever(object :
            Observer<NoteResult> {
            override fun onChanged(t: NoteResult?) {
                if (t == null) return
                when (t) {
                    is NoteResult.Success<*> ->
                        viewStateLiveData.value = NoteViewState(note = t.data as? Note)
                    is NoteResult.Error ->
                        viewStateLiveData.value = NoteViewState(error = t.error)
                }
            }
        })
    }
}