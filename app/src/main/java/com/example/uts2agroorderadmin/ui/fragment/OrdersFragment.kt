package com.example.uts2agroorderadmin.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uts2agroorderadmin.R
import com.example.uts2agroorderadmin.adapter.OrderAdapter
import com.example.uts2agroorderadmin.util.PreferencesManager
import com.example.uts2agroorderadmin.api.RetrofitClient

class OrdersFragment : Fragment() {
	private lateinit var adapter: OrderAdapter

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val view = inflater.inflate(R.layout.fragment_orders, container, false)

		adapter = OrderAdapter { order, newStatus ->
			updateStatus(order.id, newStatus)
		}

		view.findViewById<RecyclerView>(R.id.rvOrders).apply {
			layoutManager = LinearLayoutManager(requireContext())
			adapter = this@OrdersFragment.adapter
		}

		loadOrders()
		return view
	}

	private fun loadOrders() {
		val token = PreferencesManager(requireContext()).getToken() ?: return
		lifecycleScope.launchWhenCreated {
			try {
				val response = RetrofitClient.apiService.getOrders(token)
				if (response.isSuccessful) {
					adapter.submitList(response.body())
				} else {
					Toast.makeText(requireContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show()
				}
			} catch (e: Exception) {
				Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
			}
		}
	}

	private fun updateStatus(orderId: String, status: String) {
		val token = PreferencesManager(requireContext()).getToken() ?: return
		lifecycleScope.launchWhenCreated {
			try {
				val response = RetrofitClient.apiService.updateOrderStatus(
					token, orderId, mapOf("status" to status)
				)
				if (response.isSuccessful) {
					Toast.makeText(requireContext(), "Status diperbarui", Toast.LENGTH_SHORT).show()
					loadOrders()
				}
			} catch (e: Exception) {
				Toast.makeText(requireContext(), "Gagal update status", Toast.LENGTH_SHORT).show()
			}
		}
	}
}