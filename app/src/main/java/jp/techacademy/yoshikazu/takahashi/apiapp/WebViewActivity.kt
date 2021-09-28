package jp.techacademy.yoshikazu.takahashi.apiapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.android.synthetic.main.fragment_api.*
import jp.techacademy.yoshikazu.takahashi.apiapp.MainActivity.Companion as MainActivity

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        var shop = intent.getSerializableExtra("shop") as Shop
        val urlC = if (shop.couponUrls.sp.isNotEmpty()) shop.couponUrls.sp else shop.couponUrls.pc
        webView.loadUrl(urlC)
        val isFavorite = FavoriteShop.findBy(shop.id) != null
        if (isFavorite) {
            addFavoriteBtn.text = "お気に入りから削除"
        } else {
            addFavoriteBtn.text = "お気に入りに追加"
        }

        addFavoriteBtn.setOnClickListener {
            if (isFavorite) {
                FavoriteShop.delete(shop.id)
                addFavoriteBtn.text = "お気に入りに追加"
            } else {
                FavoriteShop.insert(FavoriteShop().apply {
                    id = shop.id
                    name = shop.name
                    imageUrl = shop.logoImage
                    address = shop.address
                    url = urlC
                })
                addFavoriteBtn.text = "お気に入りから削除"
            }
        }
    }

    companion object {
        private const val KEY_URL="key_url"
        private const val NAME="name"
        private const val ID="id"
        private const val ADDRESS="address"
        private const val IMAGE="image"
        fun start(activity: Activity, shop: Shop) {
            activity.startActivity(
                Intent(activity, WebViewActivity::class.java)
                    .putExtra("shop", shop)

            )
        }

    }
}