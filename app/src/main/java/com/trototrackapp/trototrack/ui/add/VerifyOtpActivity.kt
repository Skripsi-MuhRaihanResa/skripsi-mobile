package com.trototrackapp.trototrack.ui.add

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
import com.trototrackapp.trototrack.databinding.ActivityVerifyOtpBinding
import com.trototrackapp.trototrack.ui.viewmodel.ForgetPasswordViewModel
import com.trototrackapp.trototrack.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class VerifyOtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyOtpBinding
    private val forgetPasswordViewModel: ForgetPasswordViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(this)

        val email = intent.getStringExtra("email")

        binding.buttonResendOtp.setOnClickListener {
            if (email != null) {
                forgetPasswordViewModel.sendOtp(email).observe(this) { result ->
                    when (result) {
                        is ResultState.Loading -> {
                            binding.progressIndicator.visibility = View.VISIBLE
                        }

                        is ResultState.Success -> {
                            binding.progressIndicator.visibility = View.GONE
                            val dialog = AlertDialog.Builder(this)
                                .setMessage("OTP has been successfully sent to your email. Please check your email inbox and spam folder and follow the instructions to verify your account")
                                .setPositiveButton("OK") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .create()
                            dialog.show()
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
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        binding.verifyOtpButton.setOnClickListener {
            val otpDigit1 = binding.otpDigit1.text.toString()
            val otpDigit2 = binding.otpDigit2.text.toString()
            val otpDigit3 = binding.otpDigit3.text.toString()
            val otpDigit4 = binding.otpDigit4.text.toString()
            val otpDigit5 = binding.otpDigit5.text.toString()
            val otpDigit6 = binding.otpDigit6.text.toString()

            val otp = otpDigit1 + otpDigit2 + otpDigit3 + otpDigit4 + otpDigit5 + otpDigit6
            if (email != null) {
                forgetPasswordViewModel.verifyOtp(email, otp).observe(this) { result ->
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
                                val dialog = AlertDialog.Builder(this)
                                    .setMessage("OTP verified successfully")
                                    .setPositiveButton("OK") { dialog, _ ->
                                        dialog.dismiss()
                                        val intent = Intent(this, NewPasswordActivity::class.java)
                                        startActivity(intent)
                                    }
                                    .create()
                                dialog.show()
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
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}