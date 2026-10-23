package com.jantursky.crease.samples.photos

import androidx.compose.ui.graphics.ImageBitmap

class Photo(
    val title: String,
    scene: Scene,
) {
    val image: ImageBitmap by lazy(LazyThreadSafetyMode.NONE) { renderScene(scene) }
}

val samplePhotos: List<Photo> =
    listOf(
        Photo("Harbour dusk", Scene.Dusk),
        Photo("Alpine lake", Scene.Lake),
        Photo("City lights", Scene.City),
        Photo("Dune light", Scene.Dunes),
    )
