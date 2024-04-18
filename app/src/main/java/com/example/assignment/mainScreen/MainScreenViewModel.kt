package com.example.assignment.mainScreen

import android.util.Log
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.model.ArticleDataItem
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainScreenViewModel(private val repository: MainScreenRepository) : ViewModel() {

    val loader = ObservableField(false)
    val retry = ObservableField(false)

    val articles = MutableLiveData<ArrayList<ArticleDataItem>?>()


    fun getArticles() {
        loader.set(true)
        retry.set(false)


        viewModelScope.launch(Dispatchers.IO) {
            Log.d("ARTICLE_DATA", "1")

            try {
                Log.d("ARTICLE_DATA", "2")
                val response = repository.getArticles()

                if (response.code() == 200) {

                    val data = response.body()

                    if (data != null) {

                        articles.postValue(data)

                    }

                } else {
                    Log.d("ARTICLE_DATA", "4")
                    loader.set(false)
                    retry.set(true)
                }

            } catch (e: Exception) {
                Log.d("ARTICLE_DATA", e.message!!)
                retry.set(true)
                loader.set(false)
            }
        }
    }

}