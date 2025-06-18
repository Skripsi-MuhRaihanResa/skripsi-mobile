package com.trototrackapp.trototrack.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.trototrackapp.trototrack.data.ResultState
import com.trototrackapp.trototrack.data.local.UserPreference
import com.trototrackapp.trototrack.databinding.ActivityLoginBinding
import com.trototrackapp.trototrack.ui.add.SendOtpActivity
import com.trototrackapp.trototrack.ui.home.MainActivity
import com.trototrackapp.trototrack.ui.viewmodel.AuthViewModel
import com.trototrackapp.trototrack.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(this)

        binding.LoginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            authViewModel.login(email, password).observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> {
                        binding.progressIndicator.visibility = View.VISIBLE
                    }
                    is ResultState.Success -> {
                        binding.progressIndicator.visibility = View.GONE
                        val token = result.data.data?.token
                        if (token != null) {
                            lifecycleScope.launch {
                                userPreference.saveToken(token)
                            }
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finishAffinity()
                        } else {
                            Toast.makeText(this, "Error: Token is null", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is ResultState.Error -> {
                        binding.progressIndicator.visibility = View.GONE
                        val errorMessage = result.message.let {
                            try {
                                val json = JSONObject(it)
                                json.getString("message")
                            } catch (e: JSONException) {
                                it
                            }
                        } ?: "An error occurred"
                        val dialog = AlertDialog.Builder(this)
                             .setMessage(errorMessage)
                             .setPositiveButton("OK", null)
                             .create()
                        dialog.show()
                    }
                }
            }
        }

        binding.forgetPasswordButton.setOnClickListener {
            val intent = Intent(this, SendOtpActivity::class.java)
            startActivity(intent)
        }
    }
}

