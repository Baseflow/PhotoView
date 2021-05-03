package com.github.chrisbanes.photoview.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.api.load
import com.github.chrisbanes.photoview.PhotoView

class CoilSampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple)

        val photoView = findViewById<PhotoView>(R.id.iv_photo)
        photoView.load("https://images.unsplash.com/photo-1577643816920-65b43ba99fba?ixlib=rb-1.2.1&auto=format&fit=crop&w=3300&q=80") {
            crossfade(true)
        }
    }
}
