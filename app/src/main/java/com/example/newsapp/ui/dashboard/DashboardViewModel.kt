package com.example.newsapp.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.NewsItemModel
import com.example.newsapp.network.NetworkResult
import com.example.newsapp.usecase.FetchNewsListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DashboardViewModel @Inject constructor(private val useCase: FetchNewsListUseCase) :
    ViewModel() {

    private val _dataLoadStateFlow =
        MutableStateFlow<NetworkResult<List<NewsItemModel>>>(NetworkResult.Loading())
    val dataLoadStateFlow: StateFlow<NetworkResult<List<NewsItemModel>>> get() = _dataLoadStateFlow

    init {
        fetchData()
    }

    fun refresh() {
        fetchData()
    }


    private fun fetchData() {
        viewModelScope.launch(Dispatchers.Default) {
            useCase.getNewsList().onStart { _dataLoadStateFlow.value = NetworkResult.Loading() }
                .catch { /*Handle any exception*/ }.collect {
                    _dataLoadStateFlow.value = it
                }
        }
    }

    /*Can be done in view itself in order to separation of concerns */
    fun sortDataList(sortBy: SortBy, dataList: List<NewsItemModel>): List<NewsItemModel> {
        return when (sortBy) {
            is SortBy.Recent -> {
                dataList.sortedByDescending { it.timeCreated }
            }

            is SortBy.Popular -> {
                dataList.sortedByDescending { it.rank }
            }
        }

    }

}