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
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class MainActivity : AppCompatActivity() {

    private var adShown = false

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

        // El anuncio se carga al abrir la app y se muestra sobre el catálogo en cuanto esté listo
        MobileAds.initialize(this) { loadAd() }
    }

    private fun loadAd() {
        InterstitialAd.load(this, AD_UNIT_ID, AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    if (adShown || isFinishing || isDestroyed) return
                    adShown = true
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdFailedToShowFullScreenContent(error: AdError) {
                            Log.e(TAG, "Error al mostrar: ${error.code} ${error.message}")
                        }
                    }
                    ad.show(this@MainActivity)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(TAG, "Error al cargar: ${error.code} ${error.message}")
                    Toast.makeText(this@MainActivity,
                        "Anuncio no disponible: ${error.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/9657123429"
    }
}
