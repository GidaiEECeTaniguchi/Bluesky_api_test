package com.testapp.bluesky_api_test.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.testapp.bluesky_api_test.bluesky.BlueskyPostInfo
import com.testapp.bluesky_api_test.repository.AuthRepository
import com.testapp.bluesky_api_test.repository.AuthorRepository
import com.testapp.bluesky_api_test.repository.PostRepository
import work.socialhub.kbsky.auth.BearerTokenAuthProvider
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.Author
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log // Import Log for android.util.Log

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import work.socialhub.kbsky.ATProtocolException

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authorRepository: AuthorRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    private val _timelinePosts = MutableLiveData<List<BlueskyPostInfo>>()
    val timelinePosts: LiveData<List<BlueskyPostInfo>>
        get() = _timelinePosts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    fun fetchTimeline() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                // 認証情報を取得
                val authProvider = withContext(Dispatchers.IO) {
                    authRepository.getAuthProvider()
                }
                if (authProvider == null) {
                    _errorMessage.postValue("ログインしていません。")
                    return@launch
                }

                // タイムラインを取得
                val posts = withContext(Dispatchers.IO) {
                    postRepository.fetchTimelineFromApi(authProvider)
                }
                _timelinePosts.postValue(posts)

                // 取得したタイムラインをデータベースに保存
                withContext(Dispatchers.IO) {
                    for (postInfo in posts) {
                        // Authorを保存または取得
                        var author = authorRepository.getAuthorByHandleFromDb(postInfo.authorHandle)
                        if (author == null) {
                            author = authorRepository.insertAuthorToDb(Author(postInfo.authorHandle, ""))
                            if (author == null) {
                                Log.e("MainViewModel", "Failed to insert author: ${postInfo.authorHandle}")
                                continue // Skip this post if author cannot be saved
                            }
                        }

                        // BasePostを保存
                        // ここではuser_idを仮に1としています。
                        val basePost = BasePost(postInfo.postUri, postInfo.cid, 1, author.getId(), postInfo.text, postInfo.createdAt)
                        postRepository.insertPostToDb(basePost)
                    }
                }

            } catch (e: Exception) {
                if (e is ATProtocolException) {
                    val atException = e
                    Log.e("MainViewModel", "ATProtocolException in fetchTimeline: ${atException.message}, Status: ${atException.status}, Body: ${atException.body}", atException)

                    if (atException.message != null && atException.message!!.contains("Token has expired")) {
                        Log.d("MainViewModel", "Access token expired, attempting to refresh.")
                        viewModelScope.launch {
                            val refreshed = withContext(Dispatchers.IO) {
                                authRepository.refreshToken()
                            }
                            if (refreshed) {
                                Log.d("MainViewModel", "Token refreshed successfully, retrying fetchTimeline.")
                                fetchTimeline() // トークン更新後、再試行
                            } else {
                                _errorMessage.postValue("セッションの更新に失敗しました。再度ログインしてください。")
                            }
                        }
                    } else {
                        _errorMessage.postValue("Failed to fetch timeline: ${atException.message}")
                    }
                } else {
                    Log.e("MainViewModel", "Exception in fetchTimeline", e)
                    _errorMessage.postValue("Failed to fetch timeline: ${e.toString()}")
                }
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        authorRepository.shutdown() // AuthorRepositoryのExecutorServiceもシャットダウン
    }
}