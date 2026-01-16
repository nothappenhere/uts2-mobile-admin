package com.example.uts2agroorderadmin.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.uts2agroorderadmin.R
import com.example.uts2agroorderadmin.model.Order
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(private val onStatusChange: (Order, String) -> Unit) :
	ListAdapter<Order, OrderAdapter.ViewHolder>(DiffCallback()) {

	class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		val tvClient: TextView = view.findViewById(R.id.tvClient)
		val tvProduct: TextView = view.findViewById(R.id.tvProduct)
		val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
		val tvTotal: TextView = view.findViewById(R.id.tvTotal)
		val tvDate: TextView = view.findViewById(R.id.tvDate)
		val tvCurrentStatus: TextView = view.findViewById(R.id.tvCurrentStatus)
		val spinnerStatus: Spinner = view.findViewById(R.id.spinnerStatus)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context)
			.inflate(R.layout.item_order_admin, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val order = getItem(position)
		val context = holder.itemView.context

		holder.tvClient.text = "Client: ${order.client_name ?: "Unknown"}"
		holder.tvProduct.text = "Produk: ${order.product_name ?: "Unknown"}"
		holder.tvQuantity.text = "Jumlah: ${order.quantity} kg"
		holder.tvTotal.text = "Rp ${String.format("%,.0f", order.total_price)}"

		// Format tanggal
		val date = try {
			val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
			val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
			val parsedDate = inputFormat.parse(order.created_at ?: "")
			outputFormat.format(parsedDate ?: Date())
		} catch (e: Exception) {
			"N/A"
		}
		holder.tvDate.text = date

		// SET STATUS COLOR BERDASARKAN STATUS ORDER
		holder.tvCurrentStatus.text = order.status.uppercase()
		val statusColor = when (order.status.uppercase()) {
			"PENDING" -> ContextCompat.getColor(context, R.color.status_pending)
			"APPROVED" -> ContextCompat.getColor(context, R.color.status_approved)
			"SHIPPED" -> ContextCompat.getColor(context, R.color.status_shipped)
			"DELIVERED" -> ContextCompat.getColor(context, R.color.status_delivered)
			"REJECTED" -> ContextCompat.getColor(context, R.color.status_rejected)
			else -> ContextCompat.getColor(context, R.color.gray_medium)
		}
		holder.tvCurrentStatus.setBackgroundColor(statusColor)
		holder.tvCurrentStatus.setTextColor(Color.WHITE)

		// Spinner untuk update status
		val statuses = arrayOf("PENDING", "APPROVED", "SHIPPED", "DELIVERED", "REJECTED")
		val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, statuses)
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		holder.spinnerStatus.adapter = adapter

		val currentPos = statuses.indexOf(order.status.uppercase())
		holder.spinnerStatus.setSelection(if (currentPos >= 0) currentPos else 0)

		// Prevent triggering on initial bind
		holder.spinnerStatus.onItemSelectedListener = null
		holder.spinnerStatus.tag = order.status.uppercase()

		holder.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
				val newStatus = statuses[pos]
				val oldStatus = holder.spinnerStatus.tag as? String

				if (newStatus != oldStatus) {
					holder.spinnerStatus.tag = newStatus
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