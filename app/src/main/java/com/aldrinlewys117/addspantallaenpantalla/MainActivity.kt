package com.aldrinlewys117.addspantallaenpantalla

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

class MainActivity : AppCompatActivity() {

    private var nativeAd: NativeAd? = null

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

        // El anuncio se carga al abrir la app y se muestra sobre el catÃ¡logo en cuanto estÃ© listo
        MobileAds.initialize(this) { loadAd() }
    }

    // El ID de "Pantalla en pantalla" (Picture-in-picture) es de formato nativo con video
    private fun loadAd() {
        probeFormats()
        val adLoader = AdLoader.Builder(this, AD_UNIT_ID)
            .forNativeAd { ad ->
                if (isDestroyed || isFinishing) {
                    ad.destroy()
                    return@forNativeAd
                }
                nativeAd?.destroy()
                nativeAd = ad
                showPipAd(ad)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(TAG, "Error al cargar: ${error.code} ${error.message}")
                    Toast.makeText(this@MainActivity,
                        "Anuncio no disponible: ${error.message}", Toast.LENGTH_LONG).show()
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    // Sonda temporal: registra qué formatos acepta el ID (filtrar Logcat por "AdProbe")
    private fun probeFormats() {
        val req = AdRequest.Builder().build()
        fun err(n: String, e: LoadAdError) = Log.e("AdProbe", "$n -> FALLA ${e.code} ${e.message}")
        com.google.android.gms.ads.appopen.AppOpenAd.load(this, AD_UNIT_ID, req,
            object : com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: com.google.android.gms.ads.appopen.AppOpenAd) { Log.i("AdProbe", "AppOpen -> OK") }
                override fun onAdFailedToLoad(e: LoadAdError) = err("AppOpen", e)
            })
        com.google.android.gms.ads.rewarded.RewardedAd.load(this, AD_UNIT_ID, req,
            object : com.google.android.gms.ads.rewarded.RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: com.google.android.gms.ads.rewarded.RewardedAd) { Log.i("AdProbe", "Rewarded -> OK") }
                override fun onAdFailedToLoad(e: LoadAdError) = err("Rewarded", e)
            })
        com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd.load(this, AD_UNIT_ID, req,
            object : com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd) { Log.i("AdProbe", "RewardedInterstitial -> OK") }
                override fun onAdFailedToLoad(e: LoadAdError) = err("RewardedInterstitial", e)
            })
        val banner = com.google.android.gms.ads.AdView(this).apply {
            setAdSize(com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE)
            adUnitId = AD_UNIT_ID
            adListener = object : AdListener() {
                override fun onAdLoaded() { Log.i("AdProbe", "Banner -> OK") }
                override fun onAdFailedToLoad(e: LoadAdError) = err("Banner", e)
            }
        }
        banner.loadAd(req)
    }

    private fun showPipAd(ad: NativeAd) {
        val adView = findViewById<NativeAdView>(R.id.pipAdView)
        val media = adView.findViewById<MediaView>(R.id.pipMedia)
        val headline = adView.findViewById<TextView>(R.id.pipHeadline)

        adView.mediaView = media
        adView.headlineView = headline
        headline.text = ad.headline
        ad.mediaContent?.let { media.mediaContent = it }
        adView.findViewById<View>(R.id.pipClose).setOnClickListener { adView.visibility = View.GONE }
        adView.setNativeAd(ad)
        adView.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        nativeAd?.destroy()
        super.onDestroy()
    }
    companion object {
        private const val TAG = "MainActivity"
        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/9657123429"
    }
}
