package com.example.newsapp.ui.dashboard

import android.graphics.drawable.ColorDrawable
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.newsapp.R
import com.example.newsapp.data.NewsItemModel
import com.example.newsapp.network.NetworkResult
import com.example.newsapp.util.isOnline


@Composable
fun CreateNewsContentLazyColumn(
        sortBy: SortBy,
        onItemClick: (NewsItemModel) -> Unit,
        viewModel: DashboardViewModel = viewModel()
) {
    val dataState by viewModel.dataLoadStateFlow.collectAsState(initial = NetworkResult.Loading())
    when (dataState) {
        is NetworkResult.Error -> {
            ErrorView(msg = dataState.message.toString()) {
                viewModel.refresh()
            }
        }

        is NetworkResult.Loading -> {
            LoaderView()
        }

        is NetworkResult.SuccessWithNoResult -> { //do nothing
        }

        is NetworkResult.Success -> {
            dataState.data?.let {
                CreateList(
                    data = viewModel.sortDataList(sortBy = sortBy, dataList = it),
                    onItemClick = onItemClick,
                    onRefresh = { viewModel.refresh() }
                )
            } ?: run {
                ErrorView(msg = dataState.message.toString()) {
                    viewModel.refresh()
                }
            }
        }
    }
}

@Composable
fun LoaderView() {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(dimensionResource(id = R.dimen.padding_50dp)),
            color = MaterialTheme.colorScheme.primary
        )
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CreateList(
        data: List<NewsItemModel>,
        onItemClick: (NewsItemModel) -> Unit,
        onRefresh: () -> Unit
) {


    var refreshing by remember { mutableStateOf(false) }
    val state = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        refreshing = true
        onRefresh.invoke()
        refreshing = false
    })
    Box(
        modifier = Modifier.pullRefresh(state),
    ) {
        LazyColumn(
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentPadding = PaddingValues(dimensionResource(id = R.dimen.padding_8dp)),//margin at start and end
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_8dp)), //space between each items in list
        ) {
            items(data, key = { item -> item.id }) {
                ListItemView(item = it, onItemClick = onItemClick)
            }
        }

        PullRefreshIndicator(
            refreshing = refreshing,
            state = state,
            modifier = Modifier.align(Alignment.TopCenter),
            contentColor = MaterialTheme.colorScheme.primary
        )

    }


}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
private fun ListItemView(item: NewsItemModel, onItemClick: (NewsItemModel) -> Unit) {

    Card(
        elevation = CardDefaults.cardElevation(),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.padding_10dp)),
        onClick = {
            onItemClick(item)
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {

            GlideImage(
                model = item.bannerUrl,
                contentDescription = "Banner image",
                contentScale = ContentScale.Crop,
                loading = placeholder(ColorDrawable(0xff000000.toInt())),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.news_image_height))
            )

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.absolutePadding(
                    left = dimensionResource(id = R.dimen.padding_16dp),
                    right = dimensionResource(id = R.dimen.padding_16dp),
                    top = dimensionResource(id = R.dimen.padding_12dp)
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis

            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.absolutePadding(
                    top = dimensionResource(id = R.dimen.padding_6dp),
                    left = dimensionResource(id = R.dimen.padding_16dp),
                    right = dimensionResource(id = R.dimen.padding_16dp),
                ),
                softWrap = true,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.displayTime,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface,
                modifier = Modifier.absolutePadding(
                    top = dimensionResource(id = R.dimen.padding_10dp),
                    left = dimensionResource(id = R.dimen.padding_16dp),
                    right = dimensionResource(id = R.dimen.padding_16dp),
                    bottom = dimensionResource(id = R.dimen.padding_12dp),
                ),
                softWrap = true,
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun ErrorView(msg: String, onRetryClicked: () -> Unit) {
    val isOnline = LocalContext.current.isOnline()
    val isConnected by remember { mutableStateOf(isOnline) }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_12dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = msg,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 16.sp, //TODO create typography size dimen
                lineHeight = 26.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = { onRetryClicked.invoke() },
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_16dp))
            ) {
                Text(
                    modifier = Modifier.padding(
                        start = dimensionResource(id = R.dimen.padding_16dp),
                        end = dimensionResource(id = R.dimen.padding_16dp),
                        top = dimensionResource(id = R.dimen.padding_14dp)
                    ),
                    text = stringResource(id = R.string.app_retry_btn).uppercase(),
                    textAlign = TextAlign.Center
                )
            }
            if (!isConnected) {
                Toast.makeText(
                    LocalContext.current,
                    stringResource(id = R.string.app_no_internet),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}