package com.smartpack.kernelprofiler.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 22, 2020
 */

public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            if (Utils.rootAccess()) {
                Utils.startService(context, new Intent(context, BootService.class));
            }
        }
    }
}
