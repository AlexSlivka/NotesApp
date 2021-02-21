package com.example.notesapp.data.model

import kotlinx.coroutines.channels.ReceiveChannel

interface RemoteDataProvider {

    suspend fun subscribeToAllNotes(): ReceiveChannel<NoteResult>

    suspend fun getNoteById(id: String): Note

    suspend fun saveNote(note: Note): Note

    suspend fun getCurrentUser(): User?

    suspend fun deleteNote(noteId: String): Note?
}