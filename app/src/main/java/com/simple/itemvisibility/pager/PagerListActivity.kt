package com.simple.itemvisibility.pager

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Surface
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.PagerSnapHelper
import com.github.mminng.itemvisibility.ItemVisibilityHelper
import com.simple.itemvisibility.R
import com.simple.itemvisibility.databinding.ActivityPagerListBinding
import com.simple.itemvisibility.renderer.TextureRenderView

class PagerListActivity : AppCompatActivity(), SurfaceTextureListener, OnGlobalLayoutListener {

    private val player = MediaPlayer()
    private val helper = ItemVisibilityHelper()
    private lateinit var _binding: ActivityPagerListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPagerListBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        val data: List<String> = arrayListOf(
            "https://vfx.mtime.cn/Video/2023/03/16/mp4/230316090518494157.mp4",
            "https://vfx.mtime.cn/Video/2023/04/20/mp4/230420002252951119.mp4",
            "https://vfx.mtime.cn/Video/2023/04/11/mp4/230411091926610168.mp4",
            "https://vfx.mtime.cn/Video/2022/09/29/mp4/220929091956826121.mp4",
            "https://vfx.mtime.cn/Video/2023/04/10/mp4/230410121450786149.mp4",
            "https://vfx.mtime.cn/Video/2018/12/03/mp4/181203164204289930.mp4",
            "https://vfx.mtime.cn/Video/2019/12/20/mp4/191220092951535445.mp4",
            "https://vfx.mtime.cn/Video/2020/08/21/mp4/200821152204529139.mp4",
            "https://vfx.mtime.cn/Video/2014/03/06/mp4/140306102651231568.mp4",
            "https://vfx.mtime.cn/Video/2023/01/11/mp4/230111074714264116.mp4",
            "https://vfx.mtime.cn/Video/2023/03/16/mp4/230316090518494157.mp4",
            "https://vfx.mtime.cn/Video/2023/04/20/mp4/230420002252951119.mp4",
            "https://vfx.mtime.cn/Video/2023/04/11/mp4/230411091926610168.mp4",
            "https://vfx.mtime.cn/Video/2022/09/29/mp4/220929091956826121.mp4",
            "https://vfx.mtime.cn/Video/2023/04/10/mp4/230410121450786149.mp4",
            "https://vfx.mtime.cn/Video/2018/12/03/mp4/181203164204289930.mp4",
            "https://vfx.mtime.cn/Video/2019/12/20/mp4/191220092951535445.mp4",
            "https://vfx.mtime.cn/Video/2020/08/21/mp4/200821152204529139.mp4",
            "https://vfx.mtime.cn/Video/2014/03/06/mp4/140306102651231568.mp4",
            "https://vfx.mtime.cn/Video/2023/01/11/mp4/230111074714264116.mp4",
        )
        val adapter = PagerListAdapter(data)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(_binding.pListview)

        var renderView: TextureRenderView? = null
        player.setOnPreparedListener {
            player.start()
        }
        player.setOnVideoSizeChangedListener { _, w, h ->
            renderView?.setVideoSize(w, h, true)
        }
        _binding.pListview.adapter = adapter
        _binding.pListview.viewTreeObserver.addOnGlobalLayoutListener(this)
        helper.attachToRecyclerView(_binding.pListview) {
            activateItem { view, position ->
                val renderer: TextureRenderView = view.findViewById(R.id.item_p_renderer)
                val cover: View = view.findViewById(R.id.item_p_cover)
                renderView = renderer
                cover.isVisible = false
                player.isLooping = true
                player.reset()
                player.setDataSource(data[position])
                if (renderer.surfaceTexture != null) {
                    player.setSurface(Surface(renderer.surfaceTexture))
                    player.prepareAsync()
                } else {
                    renderer.surfaceTextureListener = this@PagerListActivity
                }
            }
            deactivateItem { view, _ ->
                val renderer: TextureRenderView = view.findViewById(R.id.item_p_renderer)
                val cover: View = view.findViewById(R.id.item_p_cover)
                renderer.surfaceTextureListener = null
                cover.isVisible = true
                player.stop()
            }
        }
    }

    override fun onGlobalLayout() {
        _binding.pListview.viewTreeObserver.removeOnGlobalLayoutListener(this)
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