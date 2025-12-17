package com.example.uts2agroorderadmin.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.uts2agroorderadmin.R
import com.example.uts2agroorderadmin.ui.fragment.OrdersFragment
import com.example.uts2agroorderadmin.ui.fragments.UsersFragment
import com.example.uts2agroorderadmin.ui.fragments.WeatherFragment
import com.example.uts2agroorderadmin.util.PreferencesManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

class MainActivity : AppCompatActivity() {
	private lateinit var prefs: PreferencesManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		prefs = PreferencesManager(this)

		val token = prefs.getToken()
		if (token == null) {
			startActivity(Intent(this, LoginActivity::class.java))
			finish()
			return
		}

		val fragments = listOf(
			UsersFragment(),
			OrdersFragment(),
			WeatherFragment()
		)
		val titles = listOf("Users", "Orders", "Weather")

		val viewPager = findViewById<ViewPager2>(R.id.viewPager)
		val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

		val adapter = ViewPagerAdapter(this, fragments)
		viewPager.adapter = adapter

		TabLayoutMediator(tabLayout, viewPager) { tab, position ->
			tab.text = titles[position]
		}.attach()
	}
}

class ViewPagerAdapter(
	fragmentActivity: FragmentActivity,
	private val fragments: List<Fragment>
) : FragmentStateAdapter(fragmentActivity) {
	override fun getItemCount(): Int = fragments.size
	override fun createFragment(position: Int): Fragment = fragments[position]
}