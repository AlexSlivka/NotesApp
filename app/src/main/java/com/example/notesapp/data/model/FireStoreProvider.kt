package com.example.notesapp.data.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.notesapp.data.errors.NoAuthException
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val NOTES_COLLECTION = "notes"
private const val USERS_COLLECTION = "users"

class FireStoreProvider(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : RemoteDataProvider {

    companion object {

        private val TAG = "${FireStoreProvider::class.java.simpleName} :"
    }

    private val currentUser
        get() = firebaseAuth.currentUser

    override suspend fun subscribeToAllNotes(): ReceiveChannel<NoteResult> =
        Channel<NoteResult>(Channel.CONFLATED).apply {
            var registration: ListenerRegistration? = null
            try {
                registration =
                    getUserNotesCollection().addSnapshotListener { snapshot,
                                                                   error ->
                        val value = error?.let {
                            NoteResult.Error(it)
                        } ?: snapshot?.let {
                            val notes = it.documents.map { document ->
                                document.toObject(Note::class.java)
                            }
                            NoteResult.Success(notes)
                        }

                        value?.let { offer(it) }
                    }
            } catch (e: Throwable) {
                offer(NoteResult.Error(e))
            }

            invokeOnClose { registration?.remove() }
        }

    override suspend fun getNoteById(id: String): Note = suspendCoroutine { continuation ->
        try {
            getUserNotesCollection().document(id).get()
                .addOnSuccessListener { snapshot ->
                    continuation.resume(snapshot.toObject(Note::class.java)!!)
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        } catch (e: Throwable) {
            continuation.resumeWithException(e)
        }
    }

    override suspend fun saveNote(note: Note): Note = suspendCoroutine { continuation ->
        try {
            getUserNotesCollection().document(note.id)
                .set(note).addOnSuccessListener {
                    Log.d(TAG, "Note $note is saved")
                    continuation.resume(note)
                }.addOnFailureListener {
                    OnFailureListener { exception ->
                        Log.d(
                            TAG, "Error saving note $note,message:${exception.message}"
                        )
                        continuation.resumeWithException(exception)
                    }
                }
        } catch (e: Throwable) {
            continuation.resumeWithException(e)
        }
    }

    private fun getUserNotesCollection() = currentUser?.let { firebaseUser ->
        db.collection(USERS_COLLECTION)
            .document(firebaseUser.uid)
            .collection(NOTES_COLLECTION)
    } ?: throw NoAuthException()

    override suspend fun getCurrentUser(): User? = suspendCoroutine { continuation ->
        currentUser?.let { firebaseUser ->
            continuation.resume(
                User(
                    firebaseUser.displayName ?: "",
                    firebaseUser.email ?: ""
                )
            )
        } ?: continuation.resume(null)
    }

    override suspend fun deleteNote(noteId: String): Note? = suspendCoroutine { continuation ->
        try {
            getUserNotesCollection()
                .document(noteId)
                .delete()
                .addOnSuccessListener {
                    continuation.resume(null)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        } catch (e: Throwable) {
            continuation.resumeWithException(e)
        }
    }

}