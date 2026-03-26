package com.github.redborsch.browserpicker

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import com.github.redborsch.browserpicker.chooser.AbstractChooserActivity
import com.github.redborsch.browserpicker.chooser.BottomSheetChooserActivity
import com.github.redborsch.browserpicker.chooser.MultiWindowChooserActivity
import com.github.redborsch.browserpicker.common.Settings
import com.github.redborsch.browserpicker.common.activityManager
import com.github.redborsch.browserpicker.shared.repository.BrowserListSettings
import com.github.redborsch.log.getLogger

class ChooserActivity : FragmentActivity() {

    private val log = getLogger()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cleanupOldTasks()
        processBrowserIntent(intent)
    }

    private fun processBrowserIntent(intent: Intent) {
        val isExternalIntent = !intent.consumeForceInApp()
        val activityClass = if (isExternalIntent && isInMultiWindowMode) {
            MultiWindowChooserActivity::class
        } else {
            BottomSheetChooserActivity::class
        }
        val intent = AbstractChooserActivity.createIntent(this, activityClass.java, intent)
        if (isExternalIntent) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        finish()
    }

    private fun cleanupOldTasks() {
        activityManager.appTasks
            .asSequence()
            .ignoreCurrentTask()
            .forEachIndexed { index, task ->
                log.v { "Task $index ${task.taskInfo}" }
                if (AbstractChooserActivity.isChooserIntent(task.taskInfo.baseIntent)) {
                    log.d { "Closing task $index" }
                    task.finishAndRemoveTask()
                }
            }
    }

    /**
     * Don't pass the internal flag to the actual picker Activity.
     */
    private fun Intent.consumeForceInApp(): Boolean {
        val result = getBooleanExtra(EXTRA_FORCE_IN_APP, false)
        removeExtra(EXTRA_FORCE_IN_APP)
        return result
    }

    private fun Sequence<ActivityManager.AppTask>.ignoreCurrentTask() = run {
        val currentTaskId = taskId
        log.d { "Filtering out current task: $currentTaskId" }
        filter {
            log.v { "Filtering out task: ${it.taskInfo}" }
            it.taskInfo.taskIdCompat != taskId
        }
    }

    companion object {

        private val PREFIX get() = ChooserActivity::class.qualifiedName

        private val EXTRA_FORCE_IN_APP get() = "$PREFIX.force-in-app"

        fun createIntent(
            context: Context,
            url: String = Settings.getInstance(context).testUrl,
            customSettings: BrowserListSettings? = null,
        ) = Intent(context, ChooserActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = url.toUri()
            if (customSettings != null) {
                AbstractChooserActivity.putCustomSettings(this, customSettings)
            }
            putExtra(EXTRA_FORCE_IN_APP, true)
        }

        private val ActivityManager.RecentTaskInfo.taskIdCompat: Int
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                taskId
            } else {
                @Suppress("DEPRECATION")
                id
            }
    }
}
