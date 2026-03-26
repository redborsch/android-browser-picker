package com.github.redborsch.browserpicker.common

import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commitNow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.github.redborsch.log.dumpForLog
import com.github.redborsch.log.getLogger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

class ActivityDismisser(
    activity: FragmentActivity,
) {

    private val fragmentTag = "${ActivityDismisser::class.qualifiedName}.FragmentTag"

    private val fragment = activity.getOrAddFragment()

    private val context = activity.applicationContext

    val callback: IntentSender
        get() = PendingIntent.getBroadcast(
            context,
            0,
            fragment.broadcastIntent,
            PendingIntent.FLAG_IMMUTABLE + PendingIntent.FLAG_ONE_SHOT,
        ).intentSender

    private fun FragmentActivity.getOrAddFragment(): DismisserFragment {
        val fm = supportFragmentManager
        val tag = fragmentTag

        var fragment = fm.findFragmentByTag(tag) as? DismisserFragment
        if (fragment == null) {
            fragment = DismisserFragment()
            fm.commitNow(allowStateLoss = true) {
                add(fragment, tag)
            }
        }
        return fragment
    }
}

class DismisserFragment : Fragment() {

    private val log = getLogger()

    private val viewModel: DismisserViewModel by activityViewModels()

    val broadcastIntent: Intent get() = viewModel.receiverIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        log.v { "onCreate" }
        viewModel.replaceCurrentActivity(requireActivity())
    }

    override fun onDestroy() {
        super.onDestroy()

        log.v { "onDestroy" }
        viewModel.clearCurrentActivity(requireActivity())
    }
}

class DismisserViewModel(
    application: Application,
    private val state: SavedStateHandle,
) : AndroidViewModel(application) {

    private val log = getLogger()

    private val currentActivityRef = AtomicReference<FragmentActivity>(null)

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            log.v { "Receiver onReceive: ${intent.dumpForLog()}" }

            currentActivityRef.get()?.finish()
        }
    }

    private val id: Long = state[STATE_ID] as? Long ?: counter.incrementAndGet().also {
        state[STATE_ID] = it
    }

    private val intentAction get() = "$ACTION_PREFIX.dismiss$id"

    val receiverIntent get() = Intent(intentAction)

    init {
        log.v { "init id = $id" }

        ContextCompat.registerReceiver(
            application,
            receiver,
            IntentFilter(intentAction),
            ContextCompat.RECEIVER_EXPORTED,
        )

        log.v { "Receiver registered" }
    }

    fun replaceCurrentActivity(activity: FragmentActivity) {
        log.v { "replaceCurrentActivity $activity" }

        currentActivityRef.set(activity)
    }

    fun clearCurrentActivity(expected: FragmentActivity) {
        val cleared = currentActivityRef.compareAndSet(expected, null)
        log.v { "clearCurrentActivity, expected = $expected, cleared? $cleared" }
    }

    override fun onCleared() {
        getApplication<Application>().unregisterReceiver(receiver)

        log.v { "Receiver unregistered" }
    }

    companion object {
        private val counter = AtomicLong()

        private const val STATE_ID = "DismisserID"

        private val ACTION_PREFIX get() = ActivityDismisser::class.qualifiedName
    }
}
