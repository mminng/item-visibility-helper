package com.simple.itemvisibility

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.simple.itemvisibility.databinding.ActivityMainBinding
import com.simple.itemvisibility.gird.GridListActivity
import com.simple.itemvisibility.horizontallist.HorizontalListActivity
import com.simple.itemvisibility.pager.PagerListActivity
import com.simple.itemvisibility.staggered.StaggeredListActivity
import com.simple.itemvisibility.verticallist.VerticalListActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.vertical.setOnClickListener {
            startActivity(Intent(this@MainActivity, VerticalListActivity::class.java))
        }
        binding.horizontal.setOnClickListener {
            startActivity(Intent(this@MainActivity, HorizontalListActivity::class.java))
        }
        binding.grid.setOnClickListener {
            startActivity(Intent(this@MainActivity, GridListActivity::class.java))
        }
        binding.staggered.setOnClickListener {
            startActivity(Intent(this@MainActivity, StaggeredListActivity::class.java))
        }
        binding.pager.setOnClickListener {
            startActivity(Intent(this@MainActivity, PagerListActivity::class.java))
        }
    }
}