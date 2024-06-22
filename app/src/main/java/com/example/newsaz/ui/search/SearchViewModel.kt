package com.example.newsaz.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsaz.data.model.newsmodel.NewsListModel
import com.example.newsaz.repository.Repository
import com.example.newsaz.ui.news.pagination.NewsPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: Repository
):ViewModel() {

    fun getSearchNews(search: String): Flow<PagingData<NewsListModel>> {
        return Pager(
            PagingConfig(1)
        ) {
            SearchPagingSource(repository, search)
        }
            .flow
            .cachedIn(viewModelScope)
    }

    fun getNews(category: Int?): Flow<PagingData<NewsListModel>> {
        return Pager(
            PagingConfig(1)
        ) {
            NewsPagingSource(repository, category)
        }
            .flow
            .cachedIn(viewModelScope)
    }
}