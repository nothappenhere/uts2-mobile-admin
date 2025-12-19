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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.uts2agroorderadmin.R
import com.example.uts2agroorderadmin.adapter.OrderAdapter
import com.example.uts2agroorderadmin.api.RetrofitClient
import com.example.uts2agroorderadmin.util.PreferencesManager
import kotlinx.coroutines.launch

class OrdersFragment : Fragment() {
	private lateinit var adapter: OrderAdapter
	private lateinit var swipeRefresh: SwipeRefreshLayout

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_orders, container, false)

		swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
		swipeRefresh.setOnRefreshListener {
			loadOrders()
		}

		adapter = OrderAdapter { order, newStatus ->
			updateStatus(order.id, newStatus)
		}
		view.findViewById<RecyclerView>(R.id.rvOrders).apply {
			layoutManager = LinearLayoutManager(requireContext())
			this.adapter = this@OrdersFragment.adapter
		}

		loadOrders()
		return view
	}

	private fun loadOrders() {
		swipeRefresh.isRefreshing = true
		val token = PreferencesManager(requireContext()).getToken() ?: return

		lifecycleScope.launch {
			try {
				val response = RetrofitClient.apiService.getOrders(token)
				if (response.isSuccessful) {
					adapter.submitList(response.body())
				} else {
					Toast.makeText(requireContext(), "Gagal load orders", Toast.LENGTH_SHORT).show()
				}
			} catch (e: Exception) {
				Toast.makeText(requireContext(), "Gagal load orders", Toast.LENGTH_SHORT).show()
			} finally {
				swipeRefresh.isRefreshing = false
			}
		}
	}

	private fun updateStatus(orderId: String, status: String) {
		val token = PreferencesManager(requireContext()).getToken() ?: return

		lifecycleScope.launch {
			try {
				val response = RetrofitClient.apiService.updateOrderStatus(token, orderId, mapOf("status" to status))
				if (response.isSuccessful) {
					Toast.makeText(requireContext(), "Status updated", Toast.LENGTH_SHORT).show()
					loadOrders()  // Refresh otomatis setelah update
				} else {
					Toast.makeText(requireContext(), "Gagal update", Toast.LENGTH_SHORT).show()
				}
			} catch (e: Exception) {
				Toast.makeText(requireContext(), "Gagal update", Toast.LENGTH_SHORT).show()
			}
		}
	}
}