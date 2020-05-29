package com.smartpack.kernelprofiler.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.smartpack.kernelprofiler.utils.root.RootUtils

/**
 * Created by sunilpaulmathew <sunil.kde></sunil.kde>@gmail.com> on May 22, 2020
 */
class OnBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            if (RootUtils.rootAccess()) {
                Utils.startService(context, Intent(context, BootService::class.java))
            }
        }
    }
}