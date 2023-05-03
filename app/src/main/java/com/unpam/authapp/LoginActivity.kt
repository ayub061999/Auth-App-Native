package com.unpam.authapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unpam.authapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.signInBtn.setOnClickListener {
            val email = binding.emailSignIn.text.toString().trim()
            val pass = binding.passSignIn.text.toString().trim()

            if (email.isEmpty()){
                binding.emailSignIn.apply {
                    error = "Email harus diisi"
                    requestFocus()
                }
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.emailSignIn.apply {
                    error = "Email tidak valid"
                    requestFocus()
                }
                return@setOnClickListener
            }

            if (pass.isEmpty() || pass.length < 8){
                binding.passSignIn.apply {
                    error = "Password harus minimal 8 karakter"
                    requestFocus()
                }
                return@setOnClickListener
            }

            if (!isValidPassword(pass)){
                binding.passSignIn.apply {
                    error = "Password harus terdiri dari huruf besar, huruf kecil, dan angka"
                    requestFocus()
                }
                return@setOnClickListener
            }

            loginUser(email, pass)
        }

        binding.registerBtn.setOnClickListener {
            Intent(this, RegisterActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.resetPass.setOnClickListener {
            Intent(this,ResetPassActivity::class.java).also {
                startActivity(it)
            }
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

    private fun loginUser(email: String, pass: String){
        auth.signInWithEmailAndPassword(email,pass)
            .addOnCompleteListener(this){ authResultTask ->
                if (authResultTask.isSuccessful){
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
                } else {
                    Toast.makeText(this, "${authResultTask.exception?.message}", Toast.LENGTH_SHORT).show()
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