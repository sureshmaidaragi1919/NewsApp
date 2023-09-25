package com.example.newsapp.usecase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.newsapp.network.NetworkResult
import com.example.newsapp.repo.NewsListRepository
import com.example.newsapp.ui.dashboard.mockNewsItemModel1
import com.example.newsapp.ui.dashboard.mockNewsItemModel2
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class FetchNewsListUseCaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var repository: NewsListRepository

    private lateinit var useCase: FetchNewsListUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = false)
        useCase = FetchNewsListUseCase(repository)
    }

    @Test
    fun `getNewsList returns success`() = runTest {
        val mockDataList = listOf(mockNewsItemModel1, mockNewsItemModel2)
        coEvery { repository.getNewsList() } returns flow {
            emit(NetworkResult.Success(mockDataList))
        }

        val result = useCase.getNewsList().toList()

        assert(result.size == 1)
        assert(result[0] is NetworkResult.Success)
        val successResult = result[0] as NetworkResult.Success
        assert(successResult.data == mockDataList)
    }

    @Test
    fun `getNewsList returns error`() = runBlocking {
        val errorMessage = "An error occurred"
        coEvery { repository.getNewsList() } returns flow {
            emit(NetworkResult.Error(errorMessage))
        }

        val result = useCase.getNewsList().toList()

        assert(result.size == 1)
        assert(result[0] is NetworkResult.Error)
        val errorResult = result[0] as NetworkResult.Error
        assert(errorResult.message == errorMessage)
    }
}
