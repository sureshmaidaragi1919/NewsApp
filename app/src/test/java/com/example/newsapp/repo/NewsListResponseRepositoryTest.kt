package com.example.newsapp.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.newsapp.data.NewsItemModel
import com.example.newsapp.data.NewsListResponse
import com.example.newsapp.db.NewsItemEntity
import com.example.newsapp.db.NewsListDao
import com.example.newsapp.network.ApiService
import com.example.newsapp.network.NetworkResult
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class NewsListResponseRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var apiService: ApiService

    @MockK
    private lateinit var newsListDao: NewsListDao

    private lateinit var repository: NewsListRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        repository = NewsListRepository(apiService, newsListDao)
    }

    @Test
    fun `getNewsList success with fresh data`() = runTest {


        coEvery { apiService.getNewsListsApiResponse() } returns newsResponseList()
        coEvery { newsListDao.nukeTable() } coAnswers { 1 }
        coEvery { newsListDao.insertAll(any()) } just Runs

        val result = mutableListOf<NetworkResult<List<NewsItemModel>>>()
        withContext(UnconfinedTestDispatcher(testScheduler)) {
            repository.getNewsList().toList(result)

        }

        assert(result.size == 1)
        assert(result[0] is NetworkResult.Success)

        coVerify { apiService.getNewsListsApiResponse() }
        coVerify { newsListDao.nukeTable() }
        coVerify { newsListDao.insertAll(any()) }
        coVerify(exactly = 0) { newsListDao.getNewsList() }
    }


    @Test
    fun `getNewsList success with cached data`() = runTest {

        coEvery { apiService.getNewsListsApiResponse() } returns newsResponseList()
        coEvery { newsListDao.nukeTable() } coAnswers { 1 }
        coEvery { newsListDao.insertAll(any()) } just Runs
        coEvery { newsListDao.getNewsList() } returns getMockNewsItemEntity()

        val result = mutableListOf<NetworkResult<List<NewsItemModel>>>()
        withContext(UnconfinedTestDispatcher(testScheduler)) {
            repository.getNewsList().toList(result)

        }

        assert(result.size == 1)
        assert(result[0] is NetworkResult.Success)

        coVerify { apiService.getNewsListsApiResponse() }
        coVerify { newsListDao.nukeTable() }
        coVerify { newsListDao.insertAll(any()) }
    }


    @Test
    fun `getNewsList success with no API data and cached data`() = runTest {
        coEvery { apiService.getNewsListsApiResponse() } returns newsResponseList()
        coEvery { newsListDao.nukeTable() } coAnswers { 1 }
        coEvery { newsListDao.getNewsList() } returns getMockNewsItemEntity()
        coEvery { newsListDao.insertAll(any()) } just Runs

        val result = mutableListOf<NetworkResult<List<NewsItemModel>>>()
        withContext(UnconfinedTestDispatcher(testScheduler)) {
            repository.getNewsList().toList(result)

        }

        assert(result.size == 1)
        assert(result[0] is NetworkResult.Success)
        coVerify { apiService.getNewsListsApiResponse() }
        coVerify { newsListDao.nukeTable() }
    }

    @Test
    fun `getNewsList error with no API data and cached data`() = runTest {

        coEvery { apiService.getNewsListsApiResponse() } returns newsResponseList()
        coEvery { newsListDao.getNewsList() } returns getMockNewsItemEntity()
        coEvery { newsListDao.nukeTable() } coAnswers { 1 }
        coEvery { newsListDao.insertAll(any()) } just Runs

        val result = mutableListOf<NetworkResult<List<NewsItemModel>>>()
        withContext(UnconfinedTestDispatcher(testScheduler)) {
            repository.getNewsList().toList(result)

        }


        val resultList = result.toList()
        assert(resultList.size == 1)
        assert(resultList[0] is NetworkResult.Success)
        coVerify { apiService.getNewsListsApiResponse() }
    }
}

private fun newsResponseList(): Response<NewsListResponse> {
    return Response.success(
        NewsListResponse()
    )
}


private fun getMockNewsItemEntityList(): List<NewsItemEntity> {
    return listOf(
        NewsItemEntity(
            id = "1",
            bannerUrl = "url",
            timeCreated = 1L,
            rank = 100,
            description = "desc",
            title = "title",
            displayTime = "displayTime"
        )
    )
}

private fun getMockNewsItemEntity(): Flow<List<NewsItemEntity>> {

    return flow {
        getMockNewsItemEntityList()
    }
}
