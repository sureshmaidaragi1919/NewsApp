package com.example.newsapp.ui.dashboard

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.newsapp.R
import com.example.newsapp.ui.theme.CarousellNewsTheme
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class DashboardListActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarousellNewsTheme {
                Surface {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                    ) {

                        var sortSelection by remember { mutableStateOf<SortBy>(SortBy.Recent) }

                        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
                            rememberTopAppBarState()
                        )
                        //todo Can make it collapsable toolbar by using scaffold
                        TopAppBar(
                            colors = TopAppBarDefaults.mediumTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                titleContentColor = MaterialTheme.colorScheme.surface,
                                actionIconContentColor = MaterialTheme.colorScheme.background
                            ), title = {
                                Text(
                                    modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_28dp)),
                                    text = getString(R.string.app_name),
                                )
                            }, actions = {
                                CreateActionBarAndMenuOptions {
                                    sortSelection = it
                                }
                            }, scrollBehavior = scrollBehavior
                        )

                        var context = LocalContext.current.applicationContext
                        CreateNewsContentLazyColumn(sortBy = sortSelection,
                            onItemClick = {
                                Toast.makeText(
                                    context,
                                    "Clicked Id : ${it.id}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                    }
                }
            }
        }
    }

    @Composable
    fun CreateActionBarAndMenuOptions(onSortClickListener: (sortBy: SortBy) -> Unit) {
        val expanded = rememberSaveable { mutableStateOf(false) }
        IconButton(
            modifier = Modifier.rotate(90f), onClick = { expanded.value = true }) {
            Icon(
                tint = MaterialTheme.colorScheme.surface,
                imageVector = Icons.Default.MoreVert,
                contentDescription = "",
            )

            //todo create custom options with radio button
            DropdownMenu(
                modifier = Modifier.background(MaterialTheme.colorScheme.secondary),
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }) {

                DropdownMenuItem(
                    text = {
                        Text(
                            color = MaterialTheme.colorScheme.surface,
                            text = stringResource(id = R.string.app_sort_by_recent)
                        )
                    },
                    onClick = {
                        expanded.value = false
                        onSortClickListener.invoke(SortBy.Recent)
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text(
                            color = MaterialTheme.colorScheme.surface,
                            text = stringResource(id = R.string.app_sort_by_popular)
                        )
                    },
                    onClick = {
                        expanded.value = false
                        onSortClickListener.invoke(SortBy.Popular)
                    }
                )
            }
        }
    }


}

sealed class SortBy {
    object Popular : SortBy()
    object Recent : SortBy()
}

