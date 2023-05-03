package com.unpam.authapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.unpam.authapp.databinding.ActivityResetPassBinding

class ResetPassActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityResetPassBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()

        binding.resetBtn.setOnClickListener {
            val email = binding.emailFill.text.toString().trim()

            if (email.isEmpty()){
                binding.emailFill.apply {
                    error = "Email harus diisi"
                    requestFocus()
                }
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.emailFill.apply {
                    error = "Email tidak valid"
                    requestFocus()
                }
                return@setOnClickListener
            }

            resetPassword(email)
        }

        binding.backToLogin.setOnClickListener {
            onBackPressed()
        }
    }

    private fun resetPassword(email: String){
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(this, "Silahkan cek email untuk melakukan reset password", Toast.LENGTH_SHORT).show()
                Intent(this, LoginActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            } else {
                Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}