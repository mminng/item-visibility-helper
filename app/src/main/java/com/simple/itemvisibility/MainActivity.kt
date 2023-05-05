package com.simple.itemvisibility

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.simple.itemvisibility.databinding.ActivityMainBinding
import com.simple.itemvisibility.horizontallist.HorizontalListActivity
import com.simple.itemvisibility.similartiktok.SimilarTikTokActivity
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
        binding.tikTok.setOnClickListener {
            startActivity(Intent(this@MainActivity, SimilarTikTokActivity::class.java))
        }
    }
}