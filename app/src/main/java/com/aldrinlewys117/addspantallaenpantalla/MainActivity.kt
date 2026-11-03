package com.aldrinlewys117.addspantallaenpantalla

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class MainActivity : AppCompatActivity() {

    private var ad: InterstitialAd? = null
    private lateinit var btnShowAd: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnShowAd = findViewById(R.id.btnShowAd)
        btnShowAd.setOnClickListener { ad?.show(this) }

        MobileAds.initialize(this) { loadAd() }
    }

    private fun loadAd() {
        InterstitialAd.load(this, AD_UNIT_ID, AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(loaded: InterstitialAd) {
                    ad = loaded
                    ad?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            ad = null
                            btnShowAd.isEnabled = false
                            loadAd()
                        }

                        override fun onAdFailedToShowFullScreenContent(error: AdError) {
                            Log.e(TAG, "Error al mostrar: ${error.message}")
                            ad = null
                        }
                    }
                    btnShowAd.isEnabled = true
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(TAG, "Error al cargar: ${error.message}")
                    ad = null
                }
            })
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/9657123429"
    }
}
