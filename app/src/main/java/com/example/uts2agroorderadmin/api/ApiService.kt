package com.example.uts2agroorderadmin.api

import com.example.uts2agroorderadmin.model.ApiResponse
import com.example.uts2agroorderadmin.model.LoginResponse
import com.example.uts2agroorderadmin.model.Order
import com.example.uts2agroorderadmin.model.User
import com.example.uts2agroorderadmin.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
	@POST("auth/login")
	suspend fun login(@Body request: Map<String, String>): Response<LoginResponse>

	@GET("admin/users")
	suspend fun getUsers(@Header("Authorization") token: String): Response<List<User>>

	@PUT("admin/users/{id}/approve")
	suspend fun approveUser(
		@Header("Authorization") token: String,
		@Path("id") userId: String
	): Response<ApiResponse>

	@GET("admin/orders")
	suspend fun getOrders(@Header("Authorization") token: String): Response<List<Order>>

	@PUT("orders/{id}/status")
	suspend fun updateOrderStatus(
		@Header("Authorization") token: String,
		@Path("id") orderId: String,
		@Body status: Map<String, String>
	): Response<ApiResponse>

	@GET("weather")
	suspend fun getWeather(
		@Header("Authorization") token: String? = null,
		@Query("city") city: String = "Bandung"
	): Response<WeatherResponse>
}