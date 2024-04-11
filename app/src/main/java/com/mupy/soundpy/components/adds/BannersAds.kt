package com.mupy.soundpy.components.adds

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.mupy.soundpy.R
import com.mupy.soundpy.ui.theme.ColorWhite

@Composable
fun BannersAds() {
    // on below line creating a variable for location.
    // on below line creating a column for our maps.
    Column(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // on below line we are adding a spacer.
        Spacer(modifier = Modifier.height(20.dp))
        // on below line we are adding a text
        Text(
            // on below line specifying text for heading.
            text = "Google Admob Banner Ads in Android",
            // adding text alignment,
            textAlign = TextAlign.Center,
            // on below line adding text color.
            color = ColorWhite,
            // on below line adding font weight.
            fontWeight = FontWeight.Bold,
            // on below line adding padding from all sides.
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        )

        // on below line adding a spacer.
        Spacer(modifier = Modifier.height(30.dp))

        // on below line adding admob banner ads.
        AndroidView(
            // on below line specifying width for ads.
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                // on below line specifying ad view.
                AdView(context).apply {
                    // on below line specifying ad size
                    setAdSize(AdSize.BANNER)
                    // on below line specifying ad unit id
                    // currently added a test ad unit id.
                    adUnitId = context.getString(R.string.banner_add)
                    // calling load ad to load our ad.
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}