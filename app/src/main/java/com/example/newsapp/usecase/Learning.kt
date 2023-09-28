package com.example.newsapp.usecase

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Preview
@LocalePreview
@DevicePreview
@FontScalePreview
@Composable
fun Learning() {
    val niaButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFF8F1919),
        contentColor = Color(0xFFFFFFFF)
    )

    Button(onClick = { /*TODO*/ }, colors = niaButtonColors) {
        Text(text = "Button", modifier = Modifier.padding(5.dp))

    }

}


@Preview(locale = "ja", name = "Japanese")
@Preview(locale = "en", name = "English")
@Preview(locale = "ar", name = "Arabic")
annotation class LocalePreview


@Preview(showBackground = true, fontScale = 1.0f, name = "Default (100%)")
@Preview(showBackground = true, fontScale = 0.85f, name = "Small (85%")
annotation class FontScalePreview


@Preview(
    name = "smartphone",
    device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=portrait"
)
@Preview(name = "tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
annotation class DevicePreview