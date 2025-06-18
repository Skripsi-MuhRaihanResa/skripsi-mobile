package com.trototrackapp.trototrack.ui.welcome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.trototrackapp.trototrack.R
import com.trototrackapp.trototrack.databinding.ActivityWelcomeBinding
import com.trototrackapp.trototrack.ui.auth.LoginActivity
import com.trototrackapp.trototrack.ui.auth.SignInActivity

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val animText1 = AnimationUtils.loadAnimation(this, R.anim.slide_right)
        binding.Text1.startAnimation(animText1)
        binding.View1.startAnimation(animText1)

        val animText2 = AnimationUtils.loadAnimation(this, R.anim.slide_left)
        binding.Text2.startAnimation(animText2)
        binding.View2.startAnimation(animText2)

        val animAppLogo = AnimationUtils.loadAnimation(this, R.anim.pop_in)
        binding.AppLogo.startAnimation(animAppLogo)

        val animAppName = AnimationUtils.loadAnimation(this, R.anim.pop_in)
        binding.AppName.startAnimation(animAppName)

        binding.LoginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.SignInButton.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}