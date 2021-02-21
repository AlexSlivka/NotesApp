package com.example.notesapp.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.notesapp.R
import com.example.notesapp.data.errors.NoAuthException
import com.example.notesapp.databinding.ActivitySplashBinding
import com.example.notesapp.ui.base.BaseActivity
import com.example.notesapp.viewmodel.SplashViewModel
import com.firebase.ui.auth.AuthUI
import org.koin.android.viewmodel.ext.android.viewModel

private const val RC_SIGN_IN = 458
private const val START_DELAY = 1000L

class SplashActivity : BaseActivity<Boolean>() {

    override val viewModel: SplashViewModel by viewModel()
    override val layoutRes: Int = R.layout.activity_splash
    override val ui: ActivitySplashBinding by lazy { ActivitySplashBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper())
            .postDelayed({
                viewModel.requestUser()
            }, START_DELAY)
    }

    override fun renderError(error: Throwable) {
        when (error) {
            is NoAuthException -> startLoginActivity()
            else -> error.message?.let { showError(it) }
        }
    }

    override fun renderData(data: Boolean) {
        if (data) {
            startMainActivity()
        }
    }

    private fun startLoginActivity() {
        val providers = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.drawable.common_google_signin_btn_icon_dark_focused)
                .setTheme(R.style.LoginStyle)
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && resultCode != Activity.RESULT_OK) {
            finish()
        }
    }

    private fun startMainActivity() {
        startActivity(MainActivity.getStartIntent(this))
        finish()
    }
}
