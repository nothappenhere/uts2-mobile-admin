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
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
	private lateinit var prefs: PreferencesManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)
		prefs = PreferencesManager(this)

		// Check if already logged in
		if (prefs.getToken() != null) {
			startActivity(Intent(this, MainActivity::class.java))
			finish()
			return
		}

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

			btnLogin.isEnabled = false
			btnLogin.text = "Loading..."

			lifecycleScope.launch {
				try {
					val response = RetrofitClient.apiService.login(
						mapOf("email" to email, "password" to password)
					)

					if (response.isSuccessful) {
						val body = response.body()

						// Cek role
						if (body?.role == "ADMIN") {
							prefs.saveToken("Bearer ${body.token}")
							Toast.makeText(this@LoginActivity, "Login berhasil!", Toast.LENGTH_SHORT).show()
							startActivity(Intent(this@LoginActivity, MainActivity::class.java))
							finish()
						} else {
							Toast.makeText(
								this@LoginActivity,
								"Hanya admin yang boleh login di app ini",
								Toast.LENGTH_SHORT
							).show()
						}
					} else {
						// ⚠️ IMPROVED: Parse error message dari backend
						val errorBody = response.errorBody()?.string()
						val errorMessage = try {
							val json = JSONObject(errorBody ?: "{}")
							json.getString("message")
						} catch (e: Exception) {
							"Login gagal"
						}

						if (errorMessage.contains("invalid credentials", ignoreCase = true)) {
							Toast.makeText(
								this@LoginActivity,
								"❌ Email atau password salah",
								Toast.LENGTH_SHORT
							).show()
						} else {
							Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
						}
					}
				} catch (e: Exception) {
					Toast.makeText(
						this@LoginActivity,
						"Error koneksi: ${e.message}",
						Toast.LENGTH_LONG
					).show()
				} finally {
					btnLogin.isEnabled = true
					btnLogin.text = "Login"
				}
			}
		}
	}
}