package com.example.uts2agroorderadmin.model

data class LoginResponse(
	val token: String,
	val role: String,
	val userId: String
)