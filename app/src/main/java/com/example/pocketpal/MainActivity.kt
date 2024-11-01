package com.example.pocketpal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.pocketpal.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var dashboard: DashBoard
    private lateinit var income: IncomeFragment
    private lateinit var insigths: InsightsFragment
    private lateinit var binding: ActivityMainBinding
    private lateinit var addBtn: Button

    private lateinit var navBar: BottomNavigationView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d("TestLog", "Current time: " + System.currentTimeMillis());



        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        navBar = findViewById(R.id.navView)

        dashboard = DashBoard()
        income = IncomeFragment()
        insigths = InsightsFragment()
        goToFragment(dashboard)

        addBtn = findViewById(R.id.dash_addBtn)

        addBtn.setOnClickListener{
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }

        binding.navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_history -> {
                    goToFragment(income)
                    addBtn.visibility = Button.VISIBLE
                    true
                }
                R.id.menu_home -> {
                    goToFragment(dashboard)
                    addBtn.visibility = Button.VISIBLE
                    true
                }
                R.id.menu_insights -> {
                    goToFragment(insigths)
                    addBtn.visibility = Button.INVISIBLE
                    true
                }
                else -> false
            }

        }

    }

    private fun goToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }




}