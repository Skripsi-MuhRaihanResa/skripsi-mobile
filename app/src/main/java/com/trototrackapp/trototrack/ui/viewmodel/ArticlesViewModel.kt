package com.trototrackapp.trototrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.trototrackapp.trototrack.data.repository.ArticleRepository

class ArticlesViewModel(private val articleRepository: ArticleRepository) : ViewModel() {

    fun getArticle() =
        articleRepository.getArticle()
}