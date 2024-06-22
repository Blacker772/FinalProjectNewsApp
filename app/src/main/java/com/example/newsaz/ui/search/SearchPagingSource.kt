package com.example.newsaz.ui.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsaz.data.model.newsmodel.NewsListModel
import com.example.newsaz.repository.Repository
import retrofit2.HttpException

class SearchPagingSource(
    private val repository: Repository,
    private val search: String
): PagingSource<Int, NewsListModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsListModel> {
        return try {
            val currentPage = params.key ?: 1
            val response = repository.getSearchNewsRepo(currentPage,10, search)
            val data = response.body()!!
            val responseData = mutableListOf<NewsListModel>()
            responseData.addAll(data)

            LoadResult.Page(
                data = responseData,
                prevKey = if (currentPage == 1) null else -1,
                nextKey = currentPage.plus(1)
            )

        }catch (e:Exception){
            LoadResult.Error(e)

        }catch (http: HttpException){
            LoadResult.Error(http)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, NewsListModel>): Int? {
        return null
    }
}