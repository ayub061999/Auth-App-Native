package com.unpam.authapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unpam.authapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.registerBtn.setOnClickListener {
            val email = binding.emailSignUp.text.toString().trim()
            val pass = binding.passSignUp.text.toString().trim()
            val confPass = binding.confPassSignUp.text.toString().trim()


            if (email.isEmpty()){
                binding.emailSignUp.apply {
                    error = "Email harus diisi"
                    requestFocus()
                }
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.emailSignUp.apply {
                    error = "Email tidak valid"
                    requestFocus()
                }
                return@setOnClickListener
            }

            if (pass.isEmpty() || pass.length < 8){
                binding.passSignUp.apply {
                    error = "Password harus minimal 8 karakter"
                    requestFocus()
                }
                return@setOnClickListener
            }

            if (!isValidPassword(pass)){
                binding.passSignUp.apply {
                    error = "Password harus terdiri dari huruf besar, huruf kecil, dan angka"
                    requestFocus()
                }
                return@setOnClickListener
            }

            if (confPass.isEmpty() || confPass != pass){
                binding.confPassSignUp.apply {
                    error = "Konfirmasi password harus sesuai dengan password"
                    requestFocus()
                }
                return@setOnClickListener
            }

            registerUser(email, pass)

        }

        binding.backToLogin.setOnClickListener {
            onBackPressed()
        }

    }

    private fun isValidPassword(pass: String): Boolean{
        var hasUpper = false
        var hasLower = false
        var hasNumber = false

        for (cha in pass){
            if (Character.isLowerCase(cha)){
                hasLower = true
            }

            if (Character.isUpperCase(cha)){
                hasUpper = true
            }

            if (Character.isDigit(cha)){
                hasNumber = true
            }
        }
        return hasLower && hasUpper && hasNumber
    }

    private fun registerUser(email: String, pass: String){
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this){ resultTask ->
                if (resultTask.isSuccessful){
                    Intent(this, FillNameActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                } else {
                    Toast.makeText(this, resultTask.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            db.collection("users").document(auth.uid.toString()).get()
                .addOnCompleteListener {
                    if (it.result.exists()){
                        Intent(this, MainActivity::class.java).also { intent ->
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    } else if (!it.result.exists()) {
                        val i = Intent(this, FillNameActivity::class.java)
                        startActivity(i)
                    }
                }
        }
    }
}

