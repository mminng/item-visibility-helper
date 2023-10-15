package com.simple.itemvisibility.flex

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Surface
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.github.mminng.itemvisibility.ItemVisibilityHelper
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.simple.itemvisibility.R
import com.simple.itemvisibility.databinding.ActivityFlexListBinding
import com.simple.itemvisibility.renderer.TextureRenderView

class FlexListActivity : AppCompatActivity(), SurfaceTextureListener, OnGlobalLayoutListener {

    private val player = MediaPlayer()
    private val helper = ItemVisibilityHelper()
    private lateinit var _binding: ActivityFlexListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFlexListBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        val data: List<ListModel> = arrayListOf(
            ListModel(
                "https://vfx.mtime.cn/Video/2023/03/16/mp4/230316090518494157.mp4",
                "TitleTitle"
            ),
            ListModel(
                "https://vfx.mtime.cn/Video/2023/04/20/mp4/230420002252951119.mp4",
                "TitleTitleTitle"
            ),
            ListModel(
                "https://vfx.mtime.cn/Video/2023/04/11/mp4/230411091926610168.mp4",
                "TitleTitle"
            ),
            ListModel(
                "https://vfx.mtime.cn/Video/2022/09/29/mp4/220929091956826121.mp4",
                "TitleTitleTitleTitle"
            ),
            ListModel(
                "https://vfx.mtime.cn/Video/2023/04/10/mp4/230410121450786149.mp4",
                "TitleTitleTitleTitleTile"
            ),
            ListModel(
                "https://vfx.mtime.cn/Video/2018/12/03/mp4/181203164204289930.mp4",
                "TitleTitleTitle"
            ),
            ListModel(
                "https://vfx.mtime.cn/Video/2023/03/16/mp4/230316090518494157.mp4",
                "TitleTitleTitle"
            ),
            ListModel(
                "https://vfx.mtime.cn/Video/2019/12/20/mp4/191220092951535445.mp4",
                "TitleTitleTitleTitleTileTileTitle"
            ),
            ListModel(
                "https://vfx.mtime.cn/Video/2020/08/21/mp4/200821152204529139.mp4",
                "TitleTitleTitle"
            ),
            ListModel(
                "https://vfx.mtime.cn/Video/2014/03/06/mp4/140306102651231568.mp4",
                "TitleTitleTitleTitleTile"
            ),
            ListModel(
                "https://vfx.mtime.cn/Video/2023/01/11/mp4/230111074714264116.mp4",
                "TitleTitleTitleTitleTileTileTitle"
            ),
            ListModel(
                "https://vfx.mtime.cn/Video/2023/03/16/mp4/230316090518494157.mp4",
                "TitleTitleTitle"
            ),
            ListModel(
                "https://vfx.mtime.cn/Video/2023/04/20/mp4/230420002252951119.mp4",
                "TitleTitleTitleTitleTileTileTitle"
            ),
            ListModel(
                "https://vfx.mtime.cn/Video/2023/04/11/mp4/230411091926610168.mp4",
                "TitleTitleTitleTitleTile"
            ),
            ListModel(
                "https://vfx.mtime.cn/Video/2022/09/29/mp4/220929091956826121.mp4",
                "TitleTitleTitle"
            ),
            ListModel(
                "https://vfx.mtime.cn/Video/2023/04/10/mp4/230410121450786149.mp4",
                "TitleTitleTitleTitleTileTileTitle"
            ),
            ListModel(
                "https://vfx.mtime.cn/Video/2018/12/03/mp4/181203164204289930.mp4",
                "TitleTitleTitle"
            ),
            ListModel(
                "https://vfx.mtime.cn/Video/2019/12/20/mp4/191220092951535445.mp4",
                "TitleTitleTitleTitleTile"
            ),
            ListModel(
                "https://vfx.mtime.cn/Video/2020/08/21/mp4/200821152204529139.mp4",
                "TitleTitleTitleTitleTileTileTitle"
            ),
            ListModel(
                "https://vfx.mtime.cn/Video/2014/03/06/mp4/140306102651231568.mp4",
                "TitleTitleTitleTitleTileTileTitleTitleTile"
            ),
            ListModel(
                "https://vfx.mtime.cn/Video/2023/01/11/mp4/230111074714264116.mp4",
                "TitleTitleTitleTitleTile"
            ),
        )
        val adapter = FlexListAdapter(data)
        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START

        _binding.fListview.layoutManager = layoutManager
        _binding.fListview.adapter = adapter
        _binding.fListview.viewTreeObserver.addOnGlobalLayoutListener(this)

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
        helper.attachToRecyclerView(_binding.fListview, R.id.item_f_player_view) {
            activateItem { view, position ->
                val renderer: TextureRenderView = view.findViewById(R.id.item_f_renderer)
                val cover: View = view.findViewById(R.id.item_f_cover)
                renderView = renderer
                cover.isVisible = false
                player.reset()
                player.isLooping = true
                player.setDataSource(data[position].url)
                if (renderer.surfaceTexture != null) {
                    player.setSurface(Surface(renderer.surfaceTexture))
                    player.prepareAsync()
                } else {
                    renderer.surfaceTextureListener = this@FlexListActivity
                }
            }
            deactivateItem { view, _ ->
                val cover: View = view.findViewById(R.id.item_f_cover)
                val renderer: TextureRenderView = view.findViewById(R.id.item_f_renderer)
                cover.isVisible = true
                renderer.surfaceTextureListener = null
                player.stop()
            }
        }
    }

    override fun onGlobalLayout() {
        _binding.fListview.viewTreeObserver.removeOnGlobalLayoutListener(this)
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