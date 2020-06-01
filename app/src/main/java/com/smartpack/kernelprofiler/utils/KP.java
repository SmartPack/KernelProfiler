package com.smartpack.kernelprofiler.utils;

import com.smartpack.kernelprofiler.utils.root.RootFile;
import com.smartpack.kernelprofiler.utils.root.RootUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 22, 2020
 */

public class KP {

    private static final String KP = "/data/kernel_profiler";
    public static final String KP_CONFIG = KP + "/kernelprofiler.json";
    public static StringBuilder mOutput;

    static boolean mTestingProfile = false;

    public static File KPFile() {
        return new File(KP);
    }

    public static String getCustomTitle() {
        try {
            JSONObject obj = new JSONObject(Utils.readFile(KP_CONFIG));
            return (obj.getString("title"));
        } catch (JSONException e) {
            return null;
        }
    }

    public static String getCustomDescription() {
        try {
            JSONObject obj = new JSONObject(Utils.readFile(KP_CONFIG));
            return (obj.getString("description"));
        } catch (JSONException e) {
            return null;
        }
    }

    public static String getDefaultProfile() {
        try {
            JSONObject obj = new JSONObject(Utils.readFile(KP_CONFIG));
            return (obj.getString("default"));
        } catch (JSONException e) {
            return null;
        }
    }

    public static String getDeveloper() {
        try {
            JSONObject obj = new JSONObject(Utils.readFile(KP_CONFIG));
            return (obj.getString("developer"));
        } catch (JSONException e) {
            return null;
        }
    }

    public static String getSupport() {
        try {
            JSONObject obj = new JSONObject(Utils.readFile(KP_CONFIG));
            return (obj.getString("support"));
        } catch (JSONException e) {
            return null;
        }
    }

    public static String getDonation() {
        try {
            JSONObject obj = new JSONObject(Utils.readFile(KP_CONFIG));
            return (obj.getString("donations"));
        } catch (JSONException e) {
            return null;
        }
    }

    public static String getProfileDescription(String string) {
        try {
            for (String line : Utils.readFile(string).split("\\r?\\n")) {
                if (line.startsWith("# Description=")) {
                    return line.replace("# Description=", "");
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static List<String> profileItems() {
        RootFile file = new RootFile(KPFile().toString());
        if (!file.exists()) {
            file.mkdir();
        }
        return file.list();
    }

    public static void applyProfile(String file) {
        if (mOutput == null) {
            mOutput = new StringBuilder();
        } else {
            mOutput.setLength(0);
        }
        mOutput.append(RootUtils.runAndGetError("sh " + file));;
    }

    private static String readProfile(String file) {
        return Utils.readFile(file);
    }

    public static boolean isKPProfile(String file) {
        return Utils.getExtension(file).equals("sh") && readProfile(file).startsWith("#!/system/bin/sh");
    }

    public static boolean isCustomSettingsAvailable() {
        return getSupport() != null && !getSupport().isEmpty() || getDonation() != null && !getDonation().isEmpty();
    }

    public static boolean supported() {
        return Utils.existFile(KP_CONFIG) && getCustomTitle() != null &&
                RootUtils.runAndGetOutput("uname -a").contains(getCustomTitle());
    }

}