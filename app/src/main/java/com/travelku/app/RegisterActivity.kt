package com.travelku.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val tvMasuk = findViewById<TextView>(R.id.tvMasuk)

        val etNama = findViewById<EditText>(R.id.etNama)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        val btnDaftar = findViewById<Button>(R.id.btnDaftar)

        tvMasuk.setOnClickListener {

            val intent = Intent(
                this@RegisterActivity,
                LoginActivity::class.java
            )

            startActivity(intent)
        }

        btnDaftar.setOnClickListener {

            val username = etNama.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            lifecycleScope.launch {

                try {

                    val response = ApiClient.instance.register(
                        RegisterRequest(
                            username = username,
                            email = email,
                            password = password
                        )
                    )

                    Toast.makeText(
                        this@RegisterActivity,
                        response.message,
                        Toast.LENGTH_LONG
                    ).show()

                    startActivity(
                        Intent(
                            this@RegisterActivity,
                            LoginActivity::class.java
                        )
                    )

                    finish()

                } catch (e: Exception) {

                    Toast.makeText(
                        this@RegisterActivity,
                        "Register gagal: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()

                    e.printStackTrace()
                }
            }
        }
    }
}