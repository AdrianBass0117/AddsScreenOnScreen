package com.aldrinlewys117.addspantallaenpantalla

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.ExperimentalApi
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import com.google.android.libraries.ads.mobile.sdk.pip.PictureInPictureAd
import com.google.android.libraries.ads.mobile.sdk.pip.PictureInPictureAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.pip.PictureInPictureAdOptions
import com.google.android.libraries.ads.mobile.sdk.pip.PictureInPictureAdPosition
import com.google.android.libraries.ads.mobile.sdk.pip.PictureInPictureAdPresentationScope
import com.google.android.libraries.ads.mobile.sdk.pip.PictureInPictureAdRequest

@OptIn(ExperimentalApi::class)
class MainActivity : AppCompatActivity() {

    private var pipAd: PictureInPictureAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<RecyclerView>(R.id.rvRows).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = RowAdapter(MovieData.rows)
        }

        Thread {
            MobileAds.initialize(applicationContext, InitializationConfig.Builder(APP_ID).build()) {
                runOnUiThread {
                    if (!isFinishing && !isDestroyed) loadPipAd()
                }
            }
        }.start()
    }

    private fun loadPipAd() {
        val request = PictureInPictureAdRequest.Builder(AD_UNIT_ID).build()
        PictureInPictureAd.load(request, object : AdLoadCallback<PictureInPictureAd> {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e(TAG, "Error al cargar: $adError")
                runOnUiThread {
                    Toast.makeText(this@MainActivity,
                        "Anuncio no disponible: ${adError.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onAdLoaded(ad: PictureInPictureAd) {
                Log.d(TAG, "Anuncio PiP cargado")
                pipAd = ad
                ad.adEventCallback = object : PictureInPictureAdEventCallback {
                    override fun onAdShown() { Log.d(TAG, "Anuncio PiP mostrado") }
                    override fun onAdHidden() { Log.d(TAG, "Anuncio PiP oculto") }
                }
                runOnUiThread { showPipAd(ad) }
            }
        })
    }

    private fun showPipAd(ad: PictureInPictureAd) {
        if (isFinishing || isDestroyed) return
        val options = PictureInPictureAdOptions.Builder()
            .setPosition(PictureInPictureAdPosition.DEFAULT)
            .setPresentationScope(PictureInPictureAdPresentationScope.SCREEN)
            .build()
        ad.show(this, options)
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val APP_ID = "ca-app-pub-3940256099942544~3347511713"
        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/9657123429"
    }
}
