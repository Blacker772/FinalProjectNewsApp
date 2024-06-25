package com.example.newsaz.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsaz.data.model.newsmodel.NewsListModel
import com.example.newsaz.repository.Repository
import com.example.newsaz.ui.news.UiState
import com.example.newsaz.ui.news.pagination.NewsPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private fun getNews(category: Int?): Flow<PagingData<NewsListModel>> {
        return Pager(
            PagingConfig(1)
        ) {
            NewsPagingSource(repository, category)
        }
            .flow
            .cachedIn(viewModelScope)
    }

    private val result: Flow<PagingData<NewsListModel>> = getNews(null)
    val uiState: StateFlow<UiState> = result
        .map { data ->
            UiState.Data(data, false)
        }
        .catch { error ->
            UiState.Error(error.message)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading(true)
        )

    fun getSearchNews(search: String?): Flow<PagingData<NewsListModel>> {
        return Pager(
            PagingConfig(1)
        ) {
            SearchPagingSource(repository, search)
        }
            .flow
            .cachedIn(viewModelScope)
    }
}