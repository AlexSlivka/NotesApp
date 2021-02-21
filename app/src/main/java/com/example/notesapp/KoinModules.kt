package com.example.notesapp

import com.example.notesapp.data.Repository
import com.example.notesapp.data.model.FireStoreProvider
import com.example.notesapp.data.model.RemoteDataProvider
import com.example.notesapp.viewmodel.MainViewModel
import com.example.notesapp.viewmodel.NoteViewModel
import com.example.notesapp.viewmodel.SplashViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FireStoreProvider(get(), get()) } bind RemoteDataProvider::class
    single { Repository(get()) }
}

val splashModule = module {
    viewModel { SplashViewModel(get()) }
}

val mainModule = module {
    viewModel { MainViewModel(get()) }
}

val noteModule = module {
    viewModel { NoteViewModel(get()) }
}
