package com.example.notesapp.data

import com.example.notesapp.data.model.Note
import com.example.notesapp.data.model.RemoteDataProvider

class Repository(private val remoteProvider: RemoteDataProvider) {

    fun getNotes() = remoteProvider.subscribeToAllNotes()

    fun saveNote(note: Note) = remoteProvider.saveNote(note)

    fun getNoteById(id: String) = remoteProvider.getNoteById(id)

    fun getCurrentUser() = remoteProvider.getCurrentUser()

    fun deleteNote(noteId: String) = remoteProvider.deleteNote(noteId)
}