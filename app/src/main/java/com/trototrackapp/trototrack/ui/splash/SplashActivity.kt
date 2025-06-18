package com.trototrackapp.trototrack.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.trototrackapp.trototrack.R
import com.trototrackapp.trototrack.data.local.UserPreference
import com.trototrackapp.trototrack.ui.home.MainActivity
import com.trototrackapp.trototrack.ui.welcome.WelcomeActivity
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val userPreference = UserPreference.getInstance(this)
        lifecycleScope.launch {
            val token = userPreference.tokenFlow.firstOrNull()
            if (token != null && !userPreference.isTokenExpired()) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                userPreference.clear()
                startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
            }
            finish()
        }
    }
}
