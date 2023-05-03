package com.unpam.authapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unpam.authapp.databinding.ActivityFillNameBinding
import com.unpam.authapp.databinding.ActivityRegisterBinding

class FillNameActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityFillNameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFillNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        binding.saveBtn.setOnClickListener {
            val name = binding.nameFill.text.toString().trim()

            if (name.length < 3 || name.length > 50){
                binding.nameFill.apply {
                    error = "Nama harus diantara 3 sampai 50 karakter"
                    requestFocus()
                }
                return@setOnClickListener
            }

            saveName(name)
        }
    }

    private fun saveName(name: String){
        val uid = auth.currentUser?.uid
        val db = Firebase.firestore

        val user = hashMapOf(
            "name" to name,
        )

        if (uid != null){
            db.collection("users")
                .document(uid)
                .set(user)
                .addOnCompleteListener{
                    Toast.makeText(this, "Nama Tersimpan", Toast.LENGTH_SHORT).show()
                    Intent(this, MainActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Data Gagal Tersimpan", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onBackPressed() {
        openLogoutDialog()
    }

    private fun openLogoutDialog() {
        val alertDialog = AlertDialog.Builder(this)
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