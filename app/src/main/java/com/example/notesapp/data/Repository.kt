package com.example.notesapp.data

import com.example.notesapp.data.model.Note
import com.example.notesapp.data.model.RemoteDataProvider

class Repository(private val remoteProvider: RemoteDataProvider) {

    suspend fun getNotes() = remoteProvider.subscribeToAllNotes()

    suspend fun saveNote(note: Note) = remoteProvider.saveNote(note)

    suspend fun getNoteById(id: String) = remoteProvider.getNoteById(id)

    suspend fun getCurrentUser() = remoteProvider.getCurrentUser()

    suspend fun deleteNote(noteId: String) = remoteProvider.deleteNote(noteId)
}