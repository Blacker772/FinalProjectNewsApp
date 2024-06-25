package com.example.newsaz.ui.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsaz.data.model.categorymodel.NewsCategoryModel
import com.example.newsaz.data.model.newsmodel.NewsListModel
import com.example.newsaz.repository.Repository
import com.example.newsaz.ui.news.pagination.NewsPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    //Создаю переменную для хранения состояния
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

    //Метод получения первоначального списка нвостей
    fun getNews(category: Int?): Flow<PagingData<NewsListModel>> {
        return Pager(
            PagingConfig(1, prefetchDistance = 10, enablePlaceholders = false)
        ) {
            NewsPagingSource(repository, category)
        }
            .flow
            .cachedIn(viewModelScope)
    }

    //LiveData для хранения списка категорий
    private val _liveData = MutableLiveData<List<NewsCategoryModel>>()
    val liveData: LiveData<List<NewsCategoryModel>> = _liveData

    //Метод получения списка категорий
    suspend fun getCategory(lang: String) {
        val result = repository.getCategoryRepo(lang)
        if (result.isSuccessful) {
            _liveData.postValue(result.body())
        }
    }
}