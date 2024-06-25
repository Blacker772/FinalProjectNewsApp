package com.example.newsaz.ui

import androidx.paging.PagingData
import com.example.newsaz.data.model.newsmodel.NewsListModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

sealed class UiState {
    data class Error(val message: String?) : UiState()
    data class Loading(var isLoading: Boolean) : UiState()
    data class Data(val data: PagingData<NewsListModel>, var isLoading: Boolean) : UiState()
//    data object None: UiState()
}
