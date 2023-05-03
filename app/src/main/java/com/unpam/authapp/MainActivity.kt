package com.unpam.authapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unpam.authapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val user = auth.currentUser

        if (user != null){
            val docref = db.collection("users").document(user.uid)
            docref.addSnapshotListener { value, error ->
                binding.homeName.text = "Halo ${value?.getString("name")}"
            }
            binding.homeEmail.text = user.email

            if (user.isEmailVerified){
                binding.btnVerified.visibility = View.VISIBLE
                binding.txtVerified.visibility = View.VISIBLE
            } else {
                binding.btnUnverified.visibility = View.VISIBLE
                binding.txtUnverified.visibility = View.VISIBLE

                binding.btnUnverified.setOnClickListener{
                    user.sendEmailVerification().addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(this, "Silahkan cek email untuk melakukan verifikasi akun", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        binding.logoutBtn.setOnClickListener {
            val alertDialog = this.let { AlertDialog.Builder(it) }
            alertDialog.setTitle("Apakah anda ingin keluar?")
                ?.setPositiveButton("Ya") { _, _ ->
                    auth.signOut()
                    Intent(this, LoginActivity::class.java).also {
                        it.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                }
                ?.setNegativeButton("Tidak", null)
            val alert = alertDialog.create()
            alert.show()
        }
    }
}