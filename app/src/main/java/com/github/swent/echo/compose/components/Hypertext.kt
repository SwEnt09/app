package com.github.swent.echo.compose.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun Hypertext(url: String) {
    val context = LocalContext.current
    val annotatedString =
        AnnotatedString.Builder(url)
            .apply {
                addStyle(
                    SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline),
                    0,
                    url.length
                )
                addStringAnnotation(tag = "URL", annotation = url, start = 0, end = url.length)
            }
            .toAnnotatedString()

    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            annotatedString
                .getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()
                ?.let { annotation ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                    context.startActivity(intent)
                }
        }
    )
}
