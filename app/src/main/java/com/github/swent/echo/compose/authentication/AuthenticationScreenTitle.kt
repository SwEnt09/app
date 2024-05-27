package com.github.swent.echo.compose.authentication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R
import java.util.Locale

/**
 * A title for the authentication screen. Displays the app name and a subtitle.
 *
 * @param subtitle The subtitle to be displayed below the title.
 */
@Composable
fun AuthenticationScreenTitle(subtitle: String) {
    Column(
        modifier = Modifier.padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.app_name).replaceFirstChar { it.titlecase(Locale.ROOT) },
            style = MaterialTheme.typography.displayLarge,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}
