package com.example.newsaz.data.response


import com.example.newsaz.data.model.categorymodel.NewsCategoryModel
import com.example.newsaz.data.model.newsmodel.NewsDetailsModel
import com.example.newsaz.data.model.newsmodel.NewsListModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    //Запрос на получение списка новостей и новостей по выбранной категории
    @GET("api/getNewsList.php")
    suspend fun getNews(
        @Query("page") page: Int,
        @Query("items") item: Int,
        @Query("category_id") categoryId: Int?,
    ): Response<List<NewsListModel>>

    //Запрос на получение новостей через поиск
    @GET("api/getNewsList.php")
    suspend fun getSearchNews(
        @Query("page") page: Int,
        @Query("items") item: Int,
        @Query("search") search: String?
    ): Response<List<NewsListModel>>

    //Запрос на получение деталей новости
    @GET("api/getNews.php")
    suspend fun getNewsById(
        @Query("id") id: Int?
    ): Response<NewsDetailsModel>

    //Запрос на получение списка категорий
    @GET("api/getNavigation.php")
    suspend fun getCategory(
        @Query("lang") lang: String
    ): Response<List<NewsCategoryModel>>
}