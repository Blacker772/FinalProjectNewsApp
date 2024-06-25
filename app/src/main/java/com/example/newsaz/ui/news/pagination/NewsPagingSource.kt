package com.example.newsaz.ui.news.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsaz.data.model.newsmodel.NewsListModel
import com.example.newsaz.repository.Repository
import retrofit2.HttpException

class NewsPagingSource(
    private val repository: Repository,
    private val category: Int?
) : PagingSource<Int, NewsListModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsListModel> {
        return try {
            val currentPage = params.key ?: 1
            val response = repository.getNewsRepo(currentPage, 10, category)
            val data = response.body()
            val responseData = mutableListOf<NewsListModel>()
            data?.let { responseData.addAll(it) }

            LoadResult.Page(
                data = responseData,
                prevKey = if (currentPage == 1) null else -1,
                nextKey = currentPage.plus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        } catch (http: HttpException) {
            LoadResult.Error(http)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, NewsListModel>): Int? {
        return null
    }
}