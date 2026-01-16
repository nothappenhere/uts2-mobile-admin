package com.example.uts2agroorderadmin.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.uts2agroorderadmin.R
import com.example.uts2agroorderadmin.ui.activity.LoginActivity
import com.example.uts2agroorderadmin.util.PreferencesManager

class AboutFragment : Fragment() {

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val view = inflater.inflate(R.layout.fragment_about, container, false)

		val tvAbout = view.findViewById<TextView>(R.id.tvAbout)
		val btnYoutube = view.findViewById<Button>(R.id.btnYoutube)
		val btnLogout = view.findViewById<Button>(R.id.btnLogout)

		// ⚠️ LENGKAPI INFORMASI INI SESUAI TIM ANDA
		tvAbout.text = """
            🌾 AgroOrder Admin App
            
            ═══════════════════════════════════
            📋 DESKRIPSI APLIKASI
            ═══════════════════════════════════
            
            AgroOrder adalah sistem pemesanan hasil tani berbasis client-admin. Aplikasi Admin digunakan oleh petani untuk mengelola pesanan dari restoran/rumah makan.
            
            ✨ Fitur Utama:
            • Approval registrasi client
            • Manajemen order (lihat & update status)
            • Lihat detail perhitungan order (subtotal, pajak, ongkir)
            • Informasi cuaca lokasi petani
            • Dashboard statistik & grafik
            
            ═══════════════════════════════════
            🔌 API PUBLIK YANG DIGUNAKAN
            ═══════════════════════════════════
            
            🌤️ OpenWeatherMap API
            Fungsi: Prakiraan cuaca lokasi petani
            Website: https://openweathermap.org
            Dokumentasi: https://openweathermap.org/api
            
            ═══════════════════════════════════
            💻 TEKNOLOGI
            ═══════════════════════════════════
            
            Frontend:
            • Kotlin
            • Android Studio
            • Material Design 3
            • Retrofit 2 (HTTP Client)
            • Coroutines (Asynchronous)
            
            Backend:
            • Node.js + Express.js
            • PostgreSQL Database
            • JWT Authentication
            • RESTful API
            
            ═══════════════════════════════════
            👥 TIM PENGEMBANG
            ═══════════════════════════════════
            
            152022166 - Muhammad Rizky Akbar
            152022142 - Gumiwang Maysa Nusi
            152022137 - Baraja Barsya P.
            152022169 - Erick Erlangga Putra W.
            152022144 - Luthfiansyah Putra Dean F.
            
            📅 Tahun: 2025
            🏫 Institut Teknologi Nasional Bandung
            📚 Mata Kuliah: Pemrograman Mobile
            
            ═══════════════════════════════════
            
            Terima kasih telah menggunakan AgroOrder! 🙏
        """.trimIndent()

		// Button untuk buka video demo YouTube
		btnYoutube.setOnClickListener {
			val youtubeUrl = "https://youtu.be/rWghOVLkuLM?si=Sy5mTpWje6UmmkkY"
			val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl))
			startActivity(intent)
		}

		// Button logout
		btnLogout.setOnClickListener {
			PreferencesManager(requireContext()).clear()
			startActivity(Intent(requireContext(), LoginActivity::class.java))
			requireActivity().finish()
		}

		return view
	}
}