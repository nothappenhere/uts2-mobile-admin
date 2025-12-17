package com.example.uts2agroorderadmin.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.uts2agroorderadmin.R
import com.example.uts2agroorderadmin.api.RetrofitClient
import com.example.uts2agroorderadmin.util.PreferencesManager
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
	private lateinit var prefs: PreferencesManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)
		prefs = PreferencesManager(this)

		if (prefs.getToken() != null) {
			startActivity(Intent(this, MainActivity::class.java))
			finish()
			return
		}

		// Sekarang ID sudah benar merujuk ke TextInputEditText
		val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
		val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
		val btnLogin = findViewById<Button>(R.id.btnLogin)

		btnLogin.setOnClickListener {
			val email = etEmail.text.toString().trim()
			val password = etPassword.text.toString()

			if (email.isEmpty() || password.isEmpty()) {
				Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show()
				return@setOnClickListener
			}

			lifecycleScope.launch {
				try {
					val response = RetrofitClient.apiService.login(
						mapOf("email" to email, "password" to password)
					)
					if (response.isSuccessful && response.body()?.role == "ADMIN") {
						val token = "Bearer ${response.body()!!.token}"
						prefs.saveToken(token)
						startActivity(Intent(this@LoginActivity, MainActivity::class.java))
						finish()
					} else {
						Toast.makeText(this@LoginActivity, "Hanya admin yang boleh login", Toast.LENGTH_SHORT).show()
					}
				} catch (e: Exception) {
					Toast.makeText(this@LoginActivity, "Login gagal: ${e.message}", Toast.LENGTH_LONG).show()
				}
			}
		}
	}
}