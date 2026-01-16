package com.example.uts2agroorderadmin.api

import com.example.uts2agroorderadmin.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
	// ============== AUTH ==============
	@POST("auth/login")
	suspend fun login(@Body request: Map<String, String>): Response<LoginResponse>

	// ============== ADMIN - USER MANAGEMENT ==============
	@GET("admin/users")
	suspend fun getUsers(@Header("Authorization") token: String): Response<List<User>>

	@PUT("admin/users/{id}/approve")
	suspend fun approveUser(
		@Header("Authorization") token: String,
		@Path("id") userId: String
	): Response<ApiResponse>

	@DELETE("admin/users/{id}")
	suspend fun deleteUser(
		@Header("Authorization") token: String,
		@Path("id") userId: String
	): Response<ApiResponse>

	// ============== ADMIN - ORDER MANAGEMENT ==============
	@GET("admin/orders")
	suspend fun getOrders(@Header("Authorization") token: String): Response<List<Order>>

	@PUT("orders/{id}/status")
	suspend fun updateOrderStatus(
		@Header("Authorization") token: String,
		@Path("id") orderId: String,
		@Body status: Map<String, String>
	): Response<ApiResponse>

	// ============== WEATHER ==============
	@GET("weather")
	suspend fun getWeather(
		@Header("Authorization") token: String? = null,
		@Query("city") city: String = "Bandung"
	): Response<WeatherResponse>

	// ============== STATISTICS (untuk grafik) ==============
	@GET("statistics/orders-by-status")
	suspend fun getOrdersByStatus(@Header("Authorization") token: String): Response<List<OrderStatusStat>>

	@GET("statistics/top-products")
	suspend fun getTopProducts(@Header("Authorization") token: String): Response<List<TopProduct>>

	@GET("statistics/monthly-revenue")
	suspend fun getMonthlyRevenue(@Header("Authorization") token: String): Response<List<MonthlyRevenue>>

	@GET("statistics/dashboard")
	suspend fun getDashboardSummary(@Header("Authorization") token: String): Response<DashboardSummary>
}