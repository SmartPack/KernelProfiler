package com.smartpack.kernelprofiler.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 22, 2020
 */

public class KP {

    private static final String KP = "/data/kernel_profiler";
    private static final String KP_CONFIG = KP + "/kernelprofiler.json";

    public static File KPFile() {
        return new File(KP);
    }

    public static String getKPConfig() {
        return KP_CONFIG;
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
        List<String> list = new ArrayList<>();
        String files = Utils.runAndGetOutput("ls '" + KPFile().toString() + "/'");
        if (!files.isEmpty()) {
            for (String file : files.split("\\r?\\n")) {
                if (file != null && !file.isEmpty() && Utils.existFile(KPFile().toString() + "/" + file)) {
                    list.add(file);
                }
            }
        }
        return list;
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
                Utils.runAndGetOutput("uname -a").contains(getCustomTitle());
    }

}