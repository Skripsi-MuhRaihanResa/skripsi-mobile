package com.trototrackapp.trototrack.ui.add

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.trototrackapp.trototrack.data.ResultState
import com.trototrackapp.trototrack.data.local.UserPreference
import com.trototrackapp.trototrack.databinding.ActivityNewPasswordBinding
import com.trototrackapp.trototrack.ui.auth.LoginActivity
import com.trototrackapp.trototrack.ui.viewmodel.ForgetPasswordViewModel
import com.trototrackapp.trototrack.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class NewPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewPasswordBinding
    private val forgetPasswordViewModel: ForgetPasswordViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userPreference = UserPreference.getInstance(this)

        binding.submitButton.setOnClickListener {
            val password = binding.passwordEditText.text.toString()
            val passwordConfirmation = binding.passwordConfirmationEditText.text.toString()

            forgetPasswordViewModel.newPassword(password, passwordConfirmation).observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> {
                        binding.progressIndicator.visibility = View.VISIBLE
                    }

                    is ResultState.Success -> {
                        binding.progressIndicator.visibility = View.GONE
                        lifecycleScope.launch {
                            userPreference.clear()
                        }
                        val dialog = AlertDialog.Builder(this)
                            .setMessage("New password created successfully")
                            .setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finishAffinity()
                            }
                            .create()
                        dialog.show()
                    }

                    is ResultState.Error -> {
                        Log.d("SubmitButton", "Error: ${result.message}")
                        binding.progressIndicator.visibility = View.GONE
                        val errorMessage = result.message.let {
                            try {
                                val json = JSONObject(it)
                                json.getString("message")
                            } catch (e: JSONException) {
                                it
                            }
                        } ?: "An error occurred"
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}