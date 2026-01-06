package co.com.mypt.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SuperSetExerciseModel(
   var workout_exercise_id: String = " ",
   var sets_position: String = " ",
   var position: String = " ",
   var note: String = " ",
   var time_type: String = " ",
   var duration: String = " ",
   var reps: String = " ",
   var sets: String = " ",
   var type: String = " ",
   var calories: String = " ",
   var image: String = " ",
   var name: String = " ",
   var category: String = " ",
   var id: String = " ",
   var rest_duration: String = " "
) : Parcelable
