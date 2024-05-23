package com.github.swent.echo.compose.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * A pager that allows to swipe between different pages.
 *
 * @param content A list of pairs where the first element is the title of the page and the second
 *   element is the content of the page.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Pager(content: List<Pair<String, @Composable () -> Unit>>, initialPage: Int? = 0) {
    val pagerState = rememberPagerState(initialPage = initialPage!!, pageCount = { content.size })
    val coroutineScope = rememberCoroutineScope()
    val itemsPadding = 2.dp
    val itemsWeight = 1f
    val spaceBetweenTitleAndItem = 8.dp
    val titlePadding = 8.dp
    val underlineHeight = 1.dp
    Column(modifier = Modifier.testTag("pager")) {
        Box {
            Row(
                Modifier.wrapContentHeight().fillMaxWidth().align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.Center
            ) {
                content.forEachIndexed { id, item ->
                    Column(
                        modifier = Modifier.padding(horizontal = itemsPadding).weight(itemsWeight),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    // Call scroll to on pagerState
                                    pagerState.animateScrollToPage(id)
                                }
                            },
                            modifier = Modifier.testTag("page_title_$id"),
                            shape = RectangleShape
                        ) {
                            Text(text = item.first, modifier = Modifier.padding(titlePadding), textAlign = TextAlign.Center)
                        }
                        if (pagerState.currentPage == id) {
                            Box(
                                modifier =
                                    Modifier.height(underlineHeight)
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.primary)
                                        .testTag("underline_$id")
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(spaceBetweenTitleAndItem))
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.Top,
        ) {
            content[it].second()
        }
    }
}
