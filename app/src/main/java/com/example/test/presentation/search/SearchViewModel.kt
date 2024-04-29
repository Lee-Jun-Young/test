package com.example.test.presentation.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.data.dto.UserInfo
import com.example.test.domain.GithubRepository
import com.example.test.domain.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: GithubRepository,
    private val localRepository: LocalRepository
) : ViewModel() {

    private val _searchList = MutableStateFlow<List<UserInfo>?>(null)
    val searchList: StateFlow<List<UserInfo>?> = _searchList.asStateFlow()

    fun searchUser(query: String) {
        viewModelScope.launch {
            repository.getSearchUser(query).collectLatest {
                _searchList.value = it.items
            }
        }
    }

    fun postFavorite(isChecked: Boolean, data: UserInfo) {
        viewModelScope.launch {
            if (isChecked) {
                localRepository.postFavorite(data)
            } else {
                localRepository.deleteFavorite(data)
            }
        }
    }
}