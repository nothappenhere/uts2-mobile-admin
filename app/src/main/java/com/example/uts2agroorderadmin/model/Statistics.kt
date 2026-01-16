package com.example.uts2agroorderadmin.model

// ============== STATISTICS MODELS ==============

data class OrderStatusStat(
	val status: String,
	val count: Int
)

data class TopProduct(
	val product_name: String,
	val total_quantity: Int,
	val order_count: Int,
	val total_revenue: Double
)

data class MonthlyRevenue(
	val month: String,
	val order_count: Int,
	val total_revenue: Double
)

data class DashboardSummary(
	val totalOrders: Int,
	val totalClients: Int,
	val pendingApprovals: Int,
	val totalRevenue: Double
)