package com.example.newsapp.ui.dashboard

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.newsapp.data.NewsItemModel
import com.example.newsapp.network.NetworkResult
import com.example.newsapp.usecase.FetchNewsListUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DashboardViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var fetchNewsListUseCase: FetchNewsListUseCase

    private lateinit var viewModel: DashboardViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = false)
        viewModel = DashboardViewModel(fetchNewsListUseCase)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun fetchData_Success() {
        val mockDataList = listOf(mockNewsItemModel1, mockNewsItemModel2)
        coEvery { (fetchNewsListUseCase.getNewsList()) } returns (flowOf(
            NetworkResult.Success(
                mockDataList
            )
        ))

        viewModel.refresh()

        assertEquals(
            NetworkResult.Success(mockDataList).message, viewModel.dataLoadStateFlow.value.message
        )
    }

    @Test
    fun fetchData_Error() {
        val errorMessage = "An error occurred"
        coEvery { (fetchNewsListUseCase.getNewsList()) } returns (flowOf(
            NetworkResult.Error(
                errorMessage
            )
        ))

        viewModel.refresh()

        assertEquals(
            NetworkResult.Error(errorMessage, null).data, viewModel.dataLoadStateFlow.value.data
        )
    }

    @Test
    fun sortDataList_SortByRecent() {
        val dataList = listOf(
            mockNewsItemModel1, mockNewsItemModel2
        )

        val sortedList = viewModel.sortDataList(SortBy.Recent, dataList)

        assertEquals(2, sortedList.size)
        assertEquals(12345, sortedList[0].timeCreated)
    }

    @Test
    fun sortDataList_SortByPopular() {
        val dataList = listOf(
            mockNewsItemModel1, mockNewsItemModel2
        )

        val sortedList = viewModel.sortDataList(SortBy.Popular, dataList)

        assertEquals(101, sortedList[0].rank)
    }
}

val mockNewsItemModel1 = NewsItemModel("1", "Title", "Desc", "Url", 1234L, 100, "Display time")
val mockNewsItemModel2 = NewsItemModel("2", "Title2", "Desc2", "Url2", 12345L, 101, "Display time2")
