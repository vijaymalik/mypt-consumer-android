package co.com.mypt.fragments.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.com.mypt.fragments.repository.GuestUserRepository
import co.com.mypt.model.GetStoriesList.Data.StoryList
import co.com.mypt.retrofitApi.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class GuestUserViewModel(

) : ViewModel() {
    private val repository = GuestUserRepository()
    private val _userState =
        MutableStateFlow<UiState<List<StoryList?>?>>(UiState.Loading)

    val userState: StateFlow<UiState<List<StoryList?>?>> =
        _userState.asStateFlow()


    fun fetchUsers(token: String) {

        viewModelScope.launch(Dispatchers.IO) {

            _userState.value = UiState.Loading

            try {

                val response = repository.getUsers(token)

                if (response.isSuccessful) {

                    val list = response.body()?.data?.data
                    if (list != null)
                    _userState.value = UiState.Success(list)

                } else {

                    _userState.value =
                        UiState.Error("Error: ${response.code()}")
                }

            } catch (e: Exception) {

                _userState.value =
                    UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}
