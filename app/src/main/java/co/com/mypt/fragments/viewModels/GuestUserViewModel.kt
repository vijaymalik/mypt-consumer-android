package co.com.mypt.fragments.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.com.mypt.fragments.repository.GuestUserRepository
import co.com.mypt.model.GetStoriesList.Data.StoryList
import co.com.mypt.model.HomeContent.Data.Content
import co.com.mypt.model.TrainerListModelX.Data.Trainer
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

    val userState = _userState.asStateFlow()

    private val _trainerListState =
        MutableStateFlow<UiState<List<Trainer?>?>>(UiState.Loading)
    val trainerListState = _trainerListState.asStateFlow()
    private val _contentState =
        MutableStateFlow<UiState< List<Content?>?>>(UiState.Loading)
    val contentState = _contentState.asStateFlow()


    fun getStories(token: String) {

        viewModelScope.launch(Dispatchers.IO) {

            _userState.value = UiState.Loading

            try {

                val response = repository.getStories(token)

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

    fun getTrainerList(token: String,params: Map<String, String>){

        viewModelScope.launch(Dispatchers.IO) {

            _trainerListState.value = UiState.Loading

            try {

                val response = repository.getTrainerList(token,params)

                if (response.isSuccessful) {

                    val list = response.body()?.data?.trainers
                    if (list != null)
                        _trainerListState.value = UiState.Success(list)

                } else {

                    _trainerListState.value =
                        UiState.Error("Error: ${response.code()}")
                }

            } catch (e: Exception) {

                _trainerListState.value =
                    UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }

    }

    fun getContent(token: String){

        viewModelScope.launch(Dispatchers.IO) {
            _contentState.value = UiState.Loading
            try {
                val response = repository.getContent(token)
                if (response.isSuccessful) {
                    val list = response.body()?.data
                    if (list != null)
                        _contentState.value = UiState.Success(list)

                } else {
                    _contentState.value =
                        UiState.Error("Error: ${response.code()}")
                }

            } catch (e: Exception) {
          e.printStackTrace()
                _contentState.value =
                    UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }

    }
}
