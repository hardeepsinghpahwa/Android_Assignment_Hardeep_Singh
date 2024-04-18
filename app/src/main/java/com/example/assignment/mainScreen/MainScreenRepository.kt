package com.example.assignment.mainScreen

import com.example.assignment.apiService.ApiService
import com.example.assignment.model.ArticleData
import retrofit2.Response
import javax.inject.Inject

class MainScreenRepository @Inject constructor(private val apiService: ApiService){


    suspend fun getArticles(): Response<ArticleData> {
        return apiService.getArticles("https://acharyaprashant.org/api/v2/content/misc/media-coverages?limit=100")
    }
}