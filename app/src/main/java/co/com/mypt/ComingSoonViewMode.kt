package co.com.mypt

import androidx.annotation.IntDef
import co.com.mypt.ComingSoonViewMode.Companion.FREE_ASSESSMENT
import co.com.mypt.ComingSoonViewMode.Companion.RENEW_PLAN
import co.com.mypt.ComingSoonViewMode.Companion.TOPU_UP
import co.com.mypt.ComingSoonViewMode.Companion.UPGRADE_PLAN

@Retention(AnnotationRetention.SOURCE)
@IntDef(
    FREE_ASSESSMENT, TOPU_UP, RENEW_PLAN, UPGRADE_PLAN
)
annotation class ComingSoonViewMode {
    companion object {
        const val FREE_ASSESSMENT = 1
        const val TOPU_UP = 2
        const val RENEW_PLAN = 3
        const val UPGRADE_PLAN = 4
    }
}
