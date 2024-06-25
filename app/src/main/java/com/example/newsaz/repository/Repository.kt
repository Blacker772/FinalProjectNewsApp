package com.example.newsaz.repository

import com.example.newsaz.data.model.newsmodel.NewsListModel
import com.example.newsaz.data.response.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class Repository @Inject constructor(
    private val apiService: ApiService
) {
    //Метод для получения новостей и новостей по категории
    suspend fun getNewsRepo(page: Int, items: Int, categoryId: Int?) = apiService.getNews(page, items, categoryId)

    //Метод для получения новостей по id
    suspend fun getNewsByIdRepo(id: Int?) = apiService.getNewsById(id)

    //Метод для получения категорий
    suspend fun getCategoryRepo(lang: String) = apiService.getCategory(lang)

    //Метод для получения новостей по поиску
    suspend fun getSearchNewsRepo(page: Int, items: Int, search: String?) = apiService.getSearchNews(page, items, search)
}





