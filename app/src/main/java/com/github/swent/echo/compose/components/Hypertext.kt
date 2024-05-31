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

/**
 * A Composable function that creates a clickable hyperlink. This function takes a URL as a string.
 */
@Composable
fun Hypertext(url: String) {
    // Get the current context
    val context = LocalContext.current

    // Create an AnnotatedString with the URL
    // The URL is styled with blue color and underline
    // A string annotation is added with the tag "URL"
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

    // Create a ClickableText with the AnnotatedString
    // When clicked, it opens the URL in a browser
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
