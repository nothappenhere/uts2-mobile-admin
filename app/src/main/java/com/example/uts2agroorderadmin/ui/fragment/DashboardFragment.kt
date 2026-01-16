package com.example.uts2agroorderadmin.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.uts2agroorderadmin.R
import com.example.uts2agroorderadmin.api.RetrofitClient
import com.example.uts2agroorderadmin.util.PreferencesManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

	private lateinit var tvTotalOrders: TextView
	private lateinit var tvTotalRevenue: TextView
	private lateinit var tvTotalClients: TextView
	private lateinit var tvPendingApprovals: TextView
	private lateinit var pieChart: PieChart
	private lateinit var barChart: BarChart
	private lateinit var lineChart: LineChart
	private lateinit var progressBar: ProgressBar

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

		// Initialize views
		tvTotalOrders = view.findViewById(R.id.tvTotalOrders)
		tvTotalRevenue = view.findViewById(R.id.tvTotalRevenue)
		tvTotalClients = view.findViewById(R.id.tvTotalClients)
		tvPendingApprovals = view.findViewById(R.id.tvPendingApprovals)
		pieChart = view.findViewById(R.id.pieChart)
		barChart = view.findViewById(R.id.barChart)
		lineChart = view.findViewById(R.id.lineChart)
		progressBar = view.findViewById(R.id.progressBar)

		loadDashboardData()
		return view
	}

	private fun loadDashboardData() {
		val token = PreferencesManager(requireContext()).getToken() ?: return
		progressBar.visibility = View.VISIBLE

		lifecycleScope.launch {
			try {
				// Load Summary
				val summaryResponse = RetrofitClient.apiService.getDashboardSummary(token)
				if (summaryResponse.isSuccessful) {
					val summary = summaryResponse.body()
					tvTotalOrders.text = summary?.totalOrders?.toString() ?: "0"
					tvTotalRevenue.text = "Rp ${String.format("%,.0f", summary?.totalRevenue ?: 0.0)}"
					tvTotalClients.text = summary?.totalClients?.toString() ?: "0"
					tvPendingApprovals.text = summary?.pendingApprovals?.toString() ?: "0"
				}

				// Load Order Status Chart (Pie)
				val statusResponse = RetrofitClient.apiService.getOrdersByStatus(token)
				if (statusResponse.isSuccessful) {
					setupPieChart(statusResponse.body() ?: emptyList())
				}

				// Load Top Products (Bar)
				val productsResponse = RetrofitClient.apiService.getTopProducts(token)
				if (productsResponse.isSuccessful) {
					setupBarChart(productsResponse.body() ?: emptyList())
				}

				// Load Monthly Revenue (Line)
				val revenueResponse = RetrofitClient.apiService.getMonthlyRevenue(token)
				if (revenueResponse.isSuccessful) {
					setupLineChart(revenueResponse.body() ?: emptyList())
				}

				progressBar.visibility = View.GONE

			} catch (e: Exception) {
				progressBar.visibility = View.GONE
				Toast.makeText(
					requireContext(),
					"Error loading dashboard: ${e.message}",
					Toast.LENGTH_SHORT
				).show()
			}
		}
	}

	private fun setupPieChart(data: List<com.example.uts2agroorderadmin.model.OrderStatusStat>) {
		val entries = data.map { PieEntry(it.count.toFloat(), it.status) }

		val colors = listOf(
			Color.parseColor("#FF9800"), // PENDING - Orange
			Color.parseColor("#2196F3"), // APPROVED - Blue
			Color.parseColor("#9C27B0"), // SHIPPED - Purple
			Color.parseColor("#4CAF50"), // DELIVERED - Green
			Color.parseColor("#F44336")  // REJECTED - Red
		)

		val dataSet = PieDataSet(entries, "")
		dataSet.colors = colors
		dataSet.valueTextSize = 12f
		dataSet.valueTextColor = Color.WHITE

		val pieData = PieData(dataSet)
		pieData.setValueFormatter(object : ValueFormatter() {
			override fun getFormattedValue(value: Float): String {
				return value.toInt().toString()
			}
		})

		pieChart.data = pieData
		pieChart.description.isEnabled = false
		pieChart.centerText = "Order\nStatus"
		pieChart.setCenterTextSize(14f)
		pieChart.setEntryLabelColor(Color.BLACK)
		pieChart.animateY(1000)
		pieChart.invalidate()
	}

	private fun setupBarChart(data: List<com.example.uts2agroorderadmin.model.TopProduct>) {
		val entries = data.mapIndexed { index, product ->
			BarEntry(index.toFloat(), product.total_quantity.toFloat())
		}

		val dataSet = BarDataSet(entries, "Total Terjual (kg)")
		dataSet.color = Color.parseColor("#2196F3")
		dataSet.valueTextSize = 12f

		val barData = BarData(dataSet)
		barChart.data = barData

		// X-Axis (Product names)
		val xAxis = barChart.xAxis
		xAxis.valueFormatter = IndexAxisValueFormatter(data.map { it.product_name })
		xAxis.position = XAxis.XAxisPosition.BOTTOM
		xAxis.granularity = 1f
		xAxis.setDrawGridLines(false)
		xAxis.labelRotationAngle = 0f

		barChart.description.text = ""
		barChart.description.textSize = 12f
		barChart.axisRight.isEnabled = false
		barChart.animateY(1000)
		barChart.invalidate()
	}

	private fun setupLineChart(data: List<com.example.uts2agroorderadmin.model.MonthlyRevenue>) {
		val entries = data.reversed().mapIndexed { index, revenue ->
			Entry(index.toFloat(), revenue.total_revenue.toFloat())
		}

		val dataSet = LineDataSet(entries, "Revenue (Rp)")
		dataSet.color = Color.parseColor("#4CAF50")
		dataSet.lineWidth = 2.5f
		dataSet.setCircleColor(Color.parseColor("#4CAF50"))
		dataSet.circleRadius = 5f
		dataSet.setDrawValues(true)
		dataSet.valueTextSize = 12f
		dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

		val lineData = LineData(dataSet)
		lineData.setValueFormatter(object : ValueFormatter() {
			override fun getFormattedValue(value: Float): String {
				return String.format("%.0fK", value / 1000)
			}
		})

		lineChart.data = lineData

		// X-Axis (Months)
		val xAxis = lineChart.xAxis
		xAxis.valueFormatter = IndexAxisValueFormatter(data.reversed().map { it.month })
		xAxis.position = XAxis.XAxisPosition.BOTTOM
		xAxis.granularity = 1f
		xAxis.setDrawGridLines(false)

		lineChart.description.text = ""
		lineChart.description.textSize = 12f
		lineChart.axisRight.isEnabled = false
		lineChart.animateX(1000)
		lineChart.invalidate()
	}
}