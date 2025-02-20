@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.test.presentation.detail

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.test.R
import com.example.test.data.dto.RepositoryInfo
import com.example.test.data.dto.UserInfo
import com.example.test.presentation.home.RepositoryItem
import com.example.test.presentation.search.testIconToggleButton
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun DetailRoute(
    login: String, onBookmarkClick: (UserInfo) -> Unit,
    viewModel: DetailViewModel = hiltViewModel(),
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {

    val detailUiState by viewModel.detailUiState.collectAsState()
    val detailRepositoriesUiState by viewModel.detailRepositoriesUiState.collectAsState()

    viewModel.getUserById(login)

    DetailScreen(
        detailUiState = detailUiState,
        detailRepoUiState = detailRepositoriesUiState,
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        onFavoriteClick = onBookmarkClick,
    )
}

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    detailUiState: DetailUiState,
    detailRepoUiState: DetailRepoUiState,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onFavoriteClick: (UserInfo) -> Unit
) {
    LazyColumn {
        item {
            when (detailUiState) {
                DetailUiState.Loading -> {}//LoadingState(modifier)
                is DetailUiState.Success -> {
                    DetailContent(
                        user = detailUiState.item,
                        onChangeFavorite = onFavoriteClick,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedContentScope
                    )
                }
            }
        }

        item {
            when (detailRepoUiState) {
                DetailRepoUiState.Loading -> {}
                is DetailRepoUiState.Success -> {
                    RepositoryList(
                        modifier = Modifier.padding(16.dp),
                        repositories = detailRepoUiState.item
                    )
                }
            }
        }
    }
}

@Composable
fun RepositoryList(
    modifier: Modifier = Modifier,
    repositories: List<RepositoryInfo>
) {
    Column(modifier = modifier) {
        Text(text = stringResource(id = R.string.repository_text))
        Spacer(modifier = Modifier.height(8.dp))
        repositories.forEach { repository ->
            RepositoryItem(data = repository)
        }
    }
}

@Composable
fun DetailContent(
    user: UserInfo,
    onChangeFavorite: (UserInfo) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    with(sharedTransitionScope) {
        Box {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                GlideImage(
                    modifier = Modifier
                        .Companion
                        .sharedElement(
                            sharedTransitionScope.rememberSharedContentState(key = "image-${user.avatarUrl}"),
                            animatedVisibilityScope = animatedContentScope
                        )
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                    imageModel = { user.avatarUrl })
                Text(text = user.name ?: "")
                Text(
                    text = user.login, Modifier.Companion
                        .sharedElement(
                            sharedTransitionScope.rememberSharedContentState(key = "text-${user.login}"),
                            animatedVisibilityScope = animatedContentScope
                        )
                )
            }

            var favoriteChecked by rememberSaveable { mutableStateOf(user.isFavorite) }
            testIconToggleButton(
                modifier = Modifier
                    .padding(16.dp)
                    .size(48.dp)
                    .align(Alignment.BottomEnd),
                checked = favoriteChecked,
                onCheckedChange = { checked ->
                    favoriteChecked = checked
                    user.isFavorite = checked
                    onChangeFavorite(user)
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                    )
                },
                checkedIcon = {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                    )
                },
            )
        }
    }
}