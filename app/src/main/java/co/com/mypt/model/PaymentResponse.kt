package co.com.mypt.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentResponse(
    val status: Boolean,
    val data: PaymentData,
    val msg: String
) : Parcelable
@Parcelize
data class PaymentData(
    val payment_id: Int,
    val order_ref: String?,
    val transaction_id: String?,
    val gateway: String?,
    val amount: String?,
    val currency: String?,
    val status: String?,
    val is_success: Boolean?,
    val payment_for: String?,
    val status_message: String?,
    val subscription: Subscription? = null,   // Only in success
    val plan_details: PlanDetails?
) : Parcelable
@Parcelize
data class Subscription(
    val id: Int,
    val type: String?,
    val sessions: Int?,
    val remaining_sessions: Int?,
    val sessions_display: String?,
    val validity_display: String?,
    val status: String?,
    val start_date: String?,
    val end_date: String?
) : Parcelable
@Parcelize
data class PlanDetails(
    val plan_name: String?,
    val sessions: Int?,
    val sessions_display: String?,
    val validity: String?,
    val validity_display: String?,
    val primary_trainer: String?,
    val workout_type: String?,
    val training_mode: String?,
    val activation_note: String?,
    val start_date: String?=null,
    val end_date: String?=null,
    val failure_reason: String? = null,   // Only in failure
    val failure_note: String? = null      // Only in failure
) : Parcelable
