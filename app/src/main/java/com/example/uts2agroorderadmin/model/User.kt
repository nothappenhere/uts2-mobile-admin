package com.example.uts2agroorderadmin.model

data class User(
	val id: String,
	val name: String,
	val email: String,
	val role: String,
	val approved: Boolean,
	val city: String?
)