package com.example.uts2agroorderadmin.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.uts2agroorderadmin.R
import com.example.uts2agroorderadmin.api.RetrofitClient
import com.example.uts2agroorderadmin.util.PreferencesManager
import kotlinx.coroutines.launch

class WeatherFragment : Fragment() {

	private lateinit var tvWeatherInfo: TextView
	private lateinit var tvAboutApi: TextView

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val view = inflater.inflate(R.layout.fragment_weather, container, false)

		tvWeatherInfo = view.findViewById(R.id.tvWeatherInfo)
		tvAboutApi = view.findViewById(R.id.tvAboutApi)

		// Tampilkan info API (poin 3 tugas)
		tvAboutApi.text = """
            API yang Digunakan:
            • Backend: http://your-ip:3000/api
            • Cuaca: OpenWeatherMap API
              (via endpoint /api/weather)
            
            Aplikasi: AgroOrder Admin
            Dikembangkan oleh:
						• 152022166 Muhammad Rizky Akbar
						• 152022142 Gumiwang Maysa Nusi
						• 152022137 Baraja Barsya Pinandhita
						• 152022169 Erick Erlangga P. W.
						• 152022144 Luthfiansyah Putra Dean F.
        """.trimIndent()

		loadWeather()
		return view
	}

	private fun loadWeather() {
		val token = PreferencesManager(requireContext()).getToken()

		lifecycleScope.launch {
			try {
				val response = RetrofitClient.apiService.getWeather(token, "Bandung")
				if (response.isSuccessful) {
					val weather = response.body()
					tvWeatherInfo.text = """
                        Lokasi Petani: ${weather?.city ?: "Bandung"}
                        Suhu: ${weather?.temperature ?: "-"}°C
                        Kondisi: ${weather?.weather ?: "-"}
                        
                        Cuaca hari ini: ${if (weather?.weather?.contains("rain", ignoreCase = true) == true) "Hujan — hasil panen terbatas" else "Cerah — panen optimal"}
                    """.trimIndent()
				} else {
					tvWeatherInfo.text = "Gagal memuat data cuaca"
				}
			} catch (e: Exception) {
				Toast.makeText(requireContext(), "Error cuaca: ${e.message}", Toast.LENGTH_SHORT).show()
				tvWeatherInfo.text = "Tidak dapat memuat cuaca"
			}
		}
	}
}