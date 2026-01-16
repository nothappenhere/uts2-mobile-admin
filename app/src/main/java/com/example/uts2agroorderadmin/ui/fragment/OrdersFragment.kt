package com.example.uts2agroorderadmin.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uts2agroorderadmin.R
import com.example.uts2agroorderadmin.adapter.OrderAdapter
import com.example.uts2agroorderadmin.api.RetrofitClient
import com.example.uts2agroorderadmin.util.PreferencesManager
import kotlinx.coroutines.launch

class OrdersFragment : Fragment() {
	private lateinit var adapter: OrderAdapter
	private lateinit var rvOrders: RecyclerView
	private lateinit var emptyStateLayout: View
	private lateinit var progressBar: ProgressBar

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val view = inflater.inflate(R.layout.fragment_orders, container, false)

		adapter = OrderAdapter { order, newStatus ->
			updateStatus(order.id, newStatus)
		}

		rvOrders = view.findViewById(R.id.rvOrders)
		emptyStateLayout = view.findViewById(R.id.emptyStateLayout)
		progressBar = view.findViewById(R.id.progressBar)

		rvOrders.apply {
			layoutManager = LinearLayoutManager(requireContext())
			adapter = this@OrdersFragment.adapter
		}

		loadOrders()
		return view
	}

	override fun onResume() {
		super.onResume()
		// Refresh data ketika kembali ke fragment ini
		loadOrders()
	}

	private fun loadOrders() {
		val token = PreferencesManager(requireContext()).getToken() ?: return

		// Show loading
		progressBar.visibility = View.VISIBLE
		rvOrders.visibility = View.GONE
		emptyStateLayout.visibility = View.GONE

		lifecycleScope.launch {
			try {
				val response = RetrofitClient.apiService.getOrders(token)

				progressBar.visibility = View.GONE

				if (response.isSuccessful) {
					val orders = response.body() ?: emptyList()

					if (orders.isEmpty()) {
						// Show empty state
						emptyStateLayout.visibility = View.VISIBLE
						rvOrders.visibility = View.GONE
					} else {
						// Show list
						adapter.submitList(orders)
						emptyStateLayout.visibility = View.GONE
						rvOrders.visibility = View.VISIBLE
					}
				} else {
					Toast.makeText(
						requireContext(),
						"Gagal memuat pesanan: ${response.message()}",
						Toast.LENGTH_SHORT
					).show()
					emptyStateLayout.visibility = View.VISIBLE
				}
			} catch (e: Exception) {
				progressBar.visibility = View.GONE
				Toast.makeText(
					requireContext(),
					"Error koneksi: ${e.message}",
					Toast.LENGTH_SHORT
				).show()
				emptyStateLayout.visibility = View.VISIBLE
			}
		}
	}

	private fun updateStatus(orderId: String, status: String) {
		val token = PreferencesManager(requireContext()).getToken() ?: return

		lifecycleScope.launch {
			try {
				val response = RetrofitClient.apiService.updateOrderStatus(
					token, orderId, mapOf("status" to status)
				)
				if (response.isSuccessful) {
					Toast.makeText(
						requireContext(),
						"✅ Status diperbarui ke $status",
						Toast.LENGTH_SHORT
					).show()
					loadOrders() // Refresh list
				} else {
					Toast.makeText(
						requireContext(),
						"❌ Gagal update status",
						Toast.LENGTH_SHORT
					).show()
				}
			} catch (e: Exception) {
				Toast.makeText(
					requireContext(),
					"Error: ${e.message}",
					Toast.LENGTH_SHORT
				).show()
			}
		}
	}
}