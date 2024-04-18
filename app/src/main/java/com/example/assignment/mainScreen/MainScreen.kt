package com.example.assignment.mainScreen

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.assignment.BR
import com.example.assignment.R
import com.example.assignment.databinding.ActivityMainScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@AndroidEntryPoint
class MainScreen : AppCompatActivity() {

    private lateinit var binding:ActivityMainScreenBinding

    @Inject
    lateinit var viewModelFactory: MainScreenViewModelFactory

    @Inject
    lateinit var articleAdapter: ArticleAdapter

    private val viewModel: MainScreenViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDependencyInjection()

        binding.articleList.layoutManager=GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false)
        binding.articleList.adapter=articleAdapter

        viewModel.articles.observe(this){
            if(!it.isNullOrEmpty()) {
                articleAdapter.setData(applicationContext,it)
            }
        }

        viewModel.getArticles()
    }

    private fun initDependencyInjection() {

        initDataBinding()
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main_screen)
        binding.setVariable(BR.viewModel, viewModel)
        binding.executePendingBindings()
    }
}