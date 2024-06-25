package com.example.newsaz.ui.details

import com.example.newsaz.data.model.newsmodel.NewsListModel

sealed class UiStateDetails {
    data class Error(val message: String?) : UiStateDetails()
    data class Loading(var isLoading: Boolean) : UiStateDetails()
    data class Data(val data: List<NewsListModel>?, var isLoading: Boolean) : UiStateDetails()
    data object None: UiStateDetails()
}