package com.example.uts2agroorderadmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.uts2agroorderadmin.R
import com.example.uts2agroorderadmin.model.Order

class OrderAdapter(private val onStatusChange: (Order, String) -> Unit) :
	ListAdapter<Order, OrderAdapter.ViewHolder>(DiffCallback()) {

	class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		val tvClient: TextView = view.findViewById(R.id.tvClient)
		val tvProduct: TextView = view.findViewById(R.id.tvProduct)
		val tvTotal: TextView = view.findViewById(R.id.tvTotal)
		val spinnerStatus: Spinner = view.findViewById(R.id.spinnerStatus)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context)
			.inflate(R.layout.item_order_admin, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val order = getItem(position)
		holder.tvClient.text = "Client: ${order.client_name ?: "Unknown"}"
		holder.tvProduct.text = "Produk: ${order.product_name ?: "Unknown"} x${order.quantity}"
		holder.tvTotal.text = "Total: Rp ${String.format("%.2f", order.total_price)}"

		val statuses = arrayOf("PENDING", "APPROVED", "SHIPPED", "DELIVERED")
		val adapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, statuses)
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		holder.spinnerStatus.adapter = adapter

		val currentPos = statuses.indexOf(order.status.uppercase())
		holder.spinnerStatus.setSelection(if (currentPos >= 0) currentPos else 0)

		holder.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
				val newStatus = statuses[pos]
				if (newStatus != order.status.uppercase()) {
					onStatusChange(order, newStatus)
				}
			}
			override fun onNothingSelected(parent: AdapterView<*>?) {}
		}
	}

	class DiffCallback : DiffUtil.ItemCallback<Order>() {
		override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean =
			oldItem.id == newItem.id

		override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean =
			oldItem == newItem
	}
}