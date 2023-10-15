package com.simple.itemvisibility.gird

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Surface
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.github.mminng.itemvisibility.ItemVisibilityHelper
import com.simple.itemvisibility.R
import com.simple.itemvisibility.databinding.ActivityGridListBinding
import com.simple.itemvisibility.renderer.TextureRenderView

class GridListActivity : AppCompatActivity(), SurfaceTextureListener, OnGlobalLayoutListener {

    private val player = MediaPlayer()
    private val helper = ItemVisibilityHelper()
    private lateinit var _binding: ActivityGridListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityGridListBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        val data: List<ListModel> = arrayListOf(
            ListModel("", false),
            ListModel("", false),
            ListModel("https://vfx.mtime.cn/Video/2023/03/16/mp4/230316090518494157.mp4", true),
            ListModel("", false),
            ListModel("", false),
            ListModel("", false),
            ListModel("", false),
            ListModel("https://vfx.mtime.cn/Video/2023/04/20/mp4/230420002252951119.mp4", true),
            ListModel("", false),
            ListModel("", false),
            ListModel("", false),
            ListModel("", false),
            ListModel("", false),
            ListModel("", false),
            ListModel("https://vfx.mtime.cn/Video/2023/04/11/mp4/230411091926610168.mp4", true),
            ListModel("https://vfx.mtime.cn/Video/2022/09/29/mp4/220929091956826121.mp4", true),
            ListModel("", false),
            ListModel("", false),
            ListModel("", false),
            ListModel("https://vfx.mtime.cn/Video/2023/04/10/mp4/230410121450786149.mp4", true),
            ListModel("", false),
            ListModel("", false),
            ListModel("https://vfx.mtime.cn/Video/2018/12/03/mp4/181203164204289930.mp4", true),
            ListModel("", false),
            ListModel("", false),
            ListModel("", false),
            ListModel("", false),
            ListModel("https://vfx.mtime.cn/Video/2020/08/21/mp4/200821152204529139.mp4", true),
            ListModel("", false),
            ListModel("", false),
        )
        val adapter = GridListAdapter(data)
        _binding.gListview.viewTreeObserver.addOnGlobalLayoutListener(this)
        _binding.gListview.adapter = adapter
        val layoutManager = GridLayoutManager(this, 2)
        _binding.gListview.layoutManager = layoutManager
        layoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (data[position].isVideoType) {
                    return layoutManager.spanCount
                }
                return 1
            }
        }
        var renderView: TextureRenderView? = null
        player.setOnPreparedListener {
            player.start()
        }
        player.setOnVideoSizeChangedListener { _, w, h ->
            renderView?.setVideoSize(w, h, true)
        }
        adapter.setOnItemClickListener { _, position ->
            helper.activateItem(position)
        }
        helper.attachToRecyclerView(_binding.gListview, R.id.item_g_player_view) {
            activateItem { view, position ->
                val renderer: TextureRenderView = view.findViewById(R.id.item_g_renderer)
                val cover: View = view.findViewById(R.id.item_g_cover)
                renderView = renderer
                cover.isVisible = false
                player.reset()
                player.isLooping = true
                player.setDataSource(data[position].url)
                if (renderer.surfaceTexture != null) {
                    player.setSurface(Surface(renderer.surfaceTexture))
                    player.prepareAsync()
                } else {
                    renderer.surfaceTextureListener = this@GridListActivity
                }
            }
            deactivateItem { view, _ ->
                val cover: View = view.findViewById(R.id.item_g_cover)
                val renderer: TextureRenderView = view.findViewById(R.id.item_g_renderer)
                cover.isVisible = true
                renderer.surfaceTextureListener = null
                player.stop()
            }
        }
    }

    override fun onGlobalLayout() {
        _binding.gListview.viewTreeObserver.removeOnGlobalLayoutListener(this)
        helper.activateItem()
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        player.setSurface(Surface(surface))
        player.prepareAsync()
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onResume() {
        super.onResume()
        player.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}