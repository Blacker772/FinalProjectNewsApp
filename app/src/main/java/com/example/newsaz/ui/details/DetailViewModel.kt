package com.example.newsaz.ui.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsaz.data.model.newsmodel.NewsDetailsModel
import com.example.newsaz.data.model.newsmodel.NewsListModel
import com.example.newsaz.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val state = MutableLiveData<NewsDetailsModel>()

    val liveData = MutableLiveData<List<NewsListModel>>()


    suspend fun getNews(category: Int?) {
        val result = repository.getNews(2,10, category)
        if (result.isSuccessful){
            liveData.postValue(result.body())
        }
    }

    suspend fun getNewsById(id: Int?) {
        val result = repository.getNewsByIdRepo(id)
        if (result.isSuccessful){
            state.postValue(result.body())
        }
    }
}