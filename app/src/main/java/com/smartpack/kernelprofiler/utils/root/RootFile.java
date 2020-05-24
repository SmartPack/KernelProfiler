package com.smartpack.kernelprofiler.utils.root;

import androidx.annotation.NonNull;

import com.smartpack.kernelprofiler.utils.Utils;
import com.topjohnwu.superuser.Shell;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 22, 2020
 * Based on the original implementation on Kernel Adiutor by
 * Willi Ye <williye97@gmail.com>
 */

public class RootFile {

    private final String mFile;

    public RootFile(String file) {
        mFile = file;
    }

    public String getName() {
        return new File(mFile).getName();
    }

    public void mkdir() {
        Shell.su("mkdir -p '" + mFile + "'").exec();
    }

    public List<String> list() {
        List<String> list = new ArrayList<>();
        String files = RootUtils.runAndGetOutput("ls '" + mFile + "/'");
        if (!files.isEmpty()) {
            // Make sure the files exists
            for (String file : files.split("\\r?\\n")) {
                if (file != null && !file.isEmpty() && Utils.existFile(mFile + "/" + file)) {
                    list.add(file);
                }
            }
        }
        return list;
    }

    public boolean exists() {
        String output = RootUtils.runAndGetOutput("[ -e " + mFile + " ] && echo true");
        return !output.isEmpty() && output.equals("true");
    }

    public String readFile() {
        return RootUtils.runAndGetOutput("cat '" + mFile + "'");
    }

    @NonNull
    @Override
    public String toString() {
        return mFile;
    }

}