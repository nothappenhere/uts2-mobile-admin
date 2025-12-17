package com.example.uts2agroorderadmin.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uts2agroorderadmin.R
import com.example.uts2agroorderadmin.api.RetrofitClient
import com.example.uts2agroorderadmin.model.User
import com.example.uts2agroorderadmin.util.PreferencesManager
import kotlinx.coroutines.launch

class UsersFragment : Fragment() {

	private lateinit var adapter: UserAdapter

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val view = inflater.inflate(R.layout.fragment_users, container, false)

		adapter = UserAdapter { userId ->
			approveUser(userId)
		}

		view.findViewById<RecyclerView>(R.id.rvUsers).apply {
			layoutManager = LinearLayoutManager(requireContext())
			adapter = this@UsersFragment.adapter
		}

		loadUsers()
		return view
	}

	private fun loadUsers() {
		val token = PreferencesManager(requireContext()).getToken() ?: return

		lifecycleScope.launch {
			try {
				val response = RetrofitClient.apiService.getUsers(token)
				if (response.isSuccessful) {
					val users = response.body() ?: emptyList()
					// Filter hanya client
					val clients = users.filter { it.role.uppercase() == "CLIENT" }
					adapter.submitList(clients)
				} else {
					Toast.makeText(requireContext(), "Gagal memuat data user", Toast.LENGTH_SHORT).show()
				}
			} catch (e: Exception) {
				Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
			}
		}
	}

	private fun approveUser(userId: String) {
		val token = PreferencesManager(requireContext()).getToken() ?: return

		lifecycleScope.launch {
			try {
				val response = RetrofitClient.apiService.approveUser(token, userId)
				if (response.isSuccessful) {
					Toast.makeText(requireContext(), "User berhasil di-approve", Toast.LENGTH_SHORT).show()
					loadUsers() // Refresh list
				} else {
					Toast.makeText(requireContext(), "Gagal approve user", Toast.LENGTH_SHORT).show()
				}
			} catch (e: Exception) {
				Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
			}
		}
	}
}

// Adapter untuk Users
class UserAdapter(private val onApproveClick: (String) -> Unit) :
	androidx.recyclerview.widget.ListAdapter<User, UserAdapter.ViewHolder>(UserDiffCallback()) {

	class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		val tvName: TextView = view.findViewById(R.id.tvName)
		val tvEmail: TextView = view.findViewById(R.id.tvEmail)
		val tvStatus: TextView = view.findViewById(R.id.tvStatus)
		val btnApprove: Button = view.findViewById(R.id.btnApprove)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context)
			.inflate(R.layout.item_user_admin, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val user = getItem(position)
		holder.tvName.text = user.name
		holder.tvEmail.text = user.email
		holder.tvStatus.text = if (user.approved) "Approved" else "Pending Approval"
		holder.btnApprove.isEnabled = !user.approved
		holder.btnApprove.text = if (user.approved) "Approved ✓" else "Approve"

		holder.btnApprove.setOnClickListener {
			if (!user.approved) {
				onApproveClick(user.id)
			}
		}
	}

	class UserDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<User>() {
		override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.id == newItem.id
		override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
	}
}