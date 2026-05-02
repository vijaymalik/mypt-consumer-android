package co.com.mypt.fragments.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.com.mypt.fragments.repository.GuestUserRepository
import co.com.mypt.model.BannerItem
import co.com.mypt.model.GetStoriesList.Data.StoryList
import co.com.mypt.model.HomeContent.Data.Content
import co.com.mypt.model.TrainerListModelX
import co.com.mypt.model.TrainerListModelX.Data.Trainer
import co.com.mypt.model.TrainerStudiosResponse
import co.com.mypt.retrofitApi.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val _allGymTrainerListState =
        MutableStateFlow<UiState<List<Trainer?>?>>(UiState.Loading)
    val allGymTrainerListState = _allGymTrainerListState.asStateFlow()

    private val _trainerStudiosState =
        MutableStateFlow<UiState<TrainerStudiosResponse?>?>(null)
    val trainerStudiosState = _trainerStudiosState.asStateFlow()
    private val _contentState =
        MutableStateFlow<UiState<List<Content?>?>>(UiState.Loading)
    val contentState = _contentState.asStateFlow()

    private val _bannerState =
        MutableStateFlow<UiState<List<BannerItem?>?>>(UiState.Loading)
    val bannerState = _bannerState.asStateFlow()

    private val _exerciseList = MutableLiveData<List<TrainerListModelX.Data.Tag?>>()
    val exerciseList = _exerciseList

    var isTagsAlreadyFetched = false

    var slotId=0
    var trainerId=0
    var workType=""
    var studioId=0


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

    fun fetchHomeAndGymTrainers(
        token: String,
        latitude: Double?,
        longitude: Double?,
        tagId: Int?=null
    ) {
        viewModelScope.launch {
            coroutineScope {

                val homeDeferred = async(Dispatchers.IO) {
                    getTrainerListInternal(token, latitude, longitude, tagId)
                }

                val gymDeferred = async(Dispatchers.IO) {
                    getAllGymTrainerListInternal(token, latitude, longitude)
                }

                // Run in parallel
                homeDeferred.await()
                gymDeferred.await()
            }
        }
    }

    private suspend fun getTrainerListInternal(
        token: String,
        latitude: Double?,
        longitude: Double?,
        tagId: Int?=null
    ) {
        _trainerListState.emit( UiState.Loading)

        try {
            val param = mutableMapOf<String, String>()
            param["type"] = "home"
            param["lat"] = "" + latitude
            param["long"] = "" + longitude
            if (tagId != null)
                param["tag_id"] = "" + tagId
            val response = repository.getTrainerList(token, param)

            if (response.isSuccessful) {
                val list = response.body()?.data?.trainers

                _trainerListState.emit(
                    if (list.isNullOrEmpty()) UiState.Success(emptyList())
                    else UiState.Success(list))


                if(!isTagsAlreadyFetched) {
                    response.body()?.data?.tags?.let { tags ->
                        if (tags.isNotEmpty()) {
                            val updatedTags = tags.toMutableList()
                            val allWorkoutTag = TrainerListModelX.Data.Tag(
                                description = null,
                                icon = null,
                                id = null,
                                image = null,
                                name = "All Workouts"
                            )

                            updatedTags.add(0, allWorkoutTag)
                            _exerciseList.postValue(updatedTags)
                            isTagsAlreadyFetched = true
                        }
                    }
                }
            } else {
                _trainerListState.emit(UiState.Error("Error: ${response.code()}"))
            }

        } catch (e: Exception) {
            _trainerListState.emit(
                UiState.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    private suspend fun getAllGymTrainerListInternal(
        token: String,
        latitude: Double?,
        longitude: Double?
    ) {
        _allGymTrainerListState.emit(UiState.Loading)

        try {
            val param = mutableMapOf<String, String>()
            param["lat"] = "$latitude"
            param["long"] = "$longitude"

            val response = repository.getAllGymTrainerList(token, param)

            if (response.isSuccessful) {
                val list = response.body()?.data?.trainers

                _allGymTrainerListState.emit(if (list.isNullOrEmpty()) UiState.Success(emptyList()) else UiState.Success(list))

            } else {
                _allGymTrainerListState.emit(UiState.Error("Error: ${response.code()}"))
            }

        } catch (e: Exception) {
            _allGymTrainerListState.emit( UiState.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    fun getTrainerList(token: String,latitude: Double?,longitude: Double?,tagId: Int?=null){

        viewModelScope.launch(Dispatchers.IO) {

            _trainerListState.value = UiState.Loading

            try {
                val param: MutableMap<String, String> = HashMap()

                param["type"] = "home"
                param["lat"] = "" + latitude
                param["long"] = "" + longitude
                if (tagId != null)
                    param["tag_id"] = "" + tagId

                val response = repository.getTrainerList(token,param)

                if (response.isSuccessful) {

                    val list = response.body()?.data?.trainers
                    if (list != null) {
                        _trainerListState.value = UiState.Success(list)

                        if(!isTagsAlreadyFetched) {
                            response.body()?.data?.tags?.let { tags ->
                                if (tags.isNotEmpty()) {
                                    val updatedTags = tags.toMutableList()
                                    val allWorkoutTag = TrainerListModelX.Data.Tag(
                                        description = null,
                                        icon = null,
                                        id = null,
                                        image = null,
                                        name = "All Workouts"
                                    )

                                    updatedTags.add(0, allWorkoutTag)
                                    _exerciseList.postValue(updatedTags)
                                    isTagsAlreadyFetched = true
                                }
                            }
                        }
                    }

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

    fun getTrainerStudios(token: String,latitude: Double?,longitude: Double?,trainerId:String){

        viewModelScope.launch(Dispatchers.IO) {

            _trainerStudiosState.value = UiState.Loading

            try {
                val param: MutableMap<String, String> = HashMap()
                param["lat"] = "" + latitude
                param["long"] = "" + longitude
                param["trainer_id"] = "" + trainerId
                val response = repository.getTrainersStudios(token,param)

                if (response.isSuccessful) {

                    val response = response.body()
                    if (response != null) {
                        _trainerStudiosState.value = UiState.Success(response)
                    }

                } else {

                    _trainerStudiosState.value =
                        UiState.Error("Error: ${response.code()}")
                }

            } catch (e: Exception) {

                _trainerStudiosState.value =
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

    fun getBanners(token: String){
        viewModelScope.launch(Dispatchers.IO) {
            _bannerState.value = UiState.Loading
            try {
                val response = repository.getBanners(token)
                if (response.isSuccessful) {
                    val list = response.body()?.data
                    if (list != null)
                        _bannerState.value = UiState.Success(list)

                } else {
                    _bannerState.value =
                        UiState.Error("Error: ${response.code()}")
                }

            } catch (e: Exception) {
                _bannerState.value =
                    UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }

    }

    fun resetTrainerStudioState() {
        _trainerStudiosState.value = null
    }
}
