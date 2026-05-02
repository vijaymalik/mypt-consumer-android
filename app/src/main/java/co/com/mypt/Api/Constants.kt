package co.com.mypt.Api

object Constants {

    var token = "token"
    val step = "step"
    val name = "name"
    val phone = "phone"
    val isProfileCompleted = "is_completed"
    val email = "email"
    val userId = "userId"
    val userInfo = "userInfo"
    val weight = "weight"
    val height = "height"
    val user_goals = "user_goals"
    val user_prefernce = "user_prefernce"
    val long = "long"
    val lat = "lat"
    val address = "address"
    val profile_image = "profile_image"
    val ISFROMGYMWORKOUT="isFromGymWorkout"
    val PASS_DATA="passData"
    val REVIEW_ADDRESS_ID="review_address_id"
    val BEST_PLAN_ID= "best_plan_id"
    val HAS_HOME = "HAS_HOME"
    val HAS_GYM = "HAS_GYM"
    val IS_GYM_MEMBERSHIP_FLOW = "isGYMMembershipFlow"

    val KEY_STORIES_DATA = "stories_data"

    val delayMillis = 7000L

     val TERMS_REQUEST_KEY = "terms_result"
     val TERMS_BUNDLE_KEY = "accepted"
}
enum class Plans{
    IS_BEST_AVAILABLE,IS_BEST_NOT_AVAILABLE,CUSTOMIZE
}