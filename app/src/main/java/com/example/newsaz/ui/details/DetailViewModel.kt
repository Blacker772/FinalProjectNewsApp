package com.example.newsaz.ui.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsaz.data.model.newsmodel.NewsDetailsModel
import com.example.newsaz.data.model.newsmodel.NewsListModel
import com.example.newsaz.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {


    private val _data = MutableStateFlow<UiStateDetails>(UiStateDetails.None)
    val data: StateFlow<UiStateDetails> = _data

    suspend fun getNews(category: Int?) {
        _data.value = UiStateDetails.Loading(true)
        val result = repository.getNewsRepo(2,10, category)
        if (result.isSuccessful){
            _data.value = UiStateDetails.Data(result.body(), false)
        }else{
            _data.value = UiStateDetails.Error(result.message())
        }
    }

    val state = MutableLiveData<NewsDetailsModel>()
    suspend fun getNewsById(id: Int?) {
        val result = repository.getNewsByIdRepo(id)
        if (result.isSuccessful){
            state.postValue(result.body())
        }
    }
}