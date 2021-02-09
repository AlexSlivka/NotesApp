package com.example.notesapp.data

import com.example.notesapp.data.model.FireStoreProvider
import com.example.notesapp.data.model.Note
import com.example.notesapp.data.model.RemoteDataProvider

object Repository {
    private val remoteProvider: RemoteDataProvider = FireStoreProvider()

    fun getNotes() = remoteProvider.subscribeToAllNotes()

    fun saveNote(note: Note) = remoteProvider.saveNote(note)

    fun getNoteById(id: String) = remoteProvider.getNoteById(id)

    fun getCurrentUser() = remoteProvider.getCurrentUser()
}