package com.example.assignment.apiService

import com.example.assignment.model.ArticleData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {

    @GET
    suspend fun getArticles(
        @Url url: String
    ): Response<ArticleData>
}