package com.smartpack.kernelprofiler.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;

import com.smartpack.kernelprofiler.R;
import com.smartpack.kernelprofiler.utils.root.RootUtils;

import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 22, 2020
 */

public class CreateProfileActivity extends AppCompatActivity {

    AppCompatEditText mProfileDescriptionHint;
    AppCompatEditText mProfileDetailsHint;
    AppCompatTextView mTitle;
    AppCompatTextView mTestButton;
    AppCompatTextView mTestOutput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createprofile);

        AppCompatImageButton mBack = findViewById(R.id.back_button);
        mBack.setOnClickListener(v -> onBackPressed());
        AppCompatImageButton mSave = findViewById(R.id.save_button);
        mProfileDescriptionHint = findViewById(R.id.profile_description_hint);
        mProfileDetailsHint = findViewById(R.id.profile_details_hint);
        mTitle = findViewById(R.id.title);
        mTitle.setText(getString(R.string.create_profile));
        mTestButton = findViewById(R.id.test_button);
        mTestOutput = findViewById(R.id.test_output);
        mBack.setOnClickListener(v -> onBackPressed());
        mSave.setOnClickListener(v -> {
            if (Utils.checkWriteStoragePermission(this)) {
                if (mProfileDetailsHint.getText() != null && !mProfileDetailsHint.getText().toString().equals("")) {
                    if (mProfileDescriptionHint.getText() == null || mProfileDescriptionHint.getText().toString().equals("")) {
                        Utils.snackbar(mTitle, getString(R.string.profile_description_empty));
                    }
                    createProfile();
                } else {
                    Utils.snackbar(mTitle, getString(R.string.profile_details_empty));
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                Utils.snackbar(mTitle, getString(R.string.storage_access_denied) + " " +
                        Environment.getExternalStorageDirectory().toString());
            }
        });
        mTestButton.setOnClickListener(v -> {
            if (mProfileDetailsHint.getText() != null && !mProfileDetailsHint.getText().toString().equals("")) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                testCommands(new WeakReference<>(this));
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            } else {
                Utils.snackbar(mTitle, getString(R.string.profile_details_empty));
            }
        });
        mTestOutput = findViewById(R.id.test_output);
        refreshStatus();
    }

    private void createProfile() {
        ViewUtils.dialogEditText("",
                (dialogInterface, i) -> {
                }, text -> {
                    if (text.isEmpty()) {
                        Utils.snackbar(mTitle, getString(R.string.name_empty));
                        return;
                    }
                    if (!text.endsWith(".sh")) {
                        text += ".sh";
                    }
                    if (text.contains(" ")) {
                        text = text.replace(" ", "_");
                    }
                    if (Utils.existFile(Environment.getExternalStorageDirectory().toString() + "/" + text)) {
                        Utils.snackbar(mTitle, getString(R.string.profile_exists, text));
                        return;
                    }
                    Utils.create("#!/system/bin/sh\n\n# Description=" + mProfileDescriptionHint.getText() + "\n\n" + mProfileDetailsHint.getText(),
                            Environment.getExternalStorageDirectory().toString() + "/" + text);
                    Utils.snackbarIndenite(mTitle, getString(R.string.create_profile_message, text) + " " +
                            Environment.getExternalStorageDirectory().toString());
                }, this).setOnDismissListener(dialogInterface -> {
        }).show();
    }

    private void createTestScript() {
        Utils.create("#!/system/bin/sh\n\n" + Objects.requireNonNull(mProfileDetailsHint.getText()).toString(),"/data/local/tmp/sm");
    }

    @SuppressLint("StaticFieldLeak")
    private void testCommands(WeakReference<Activity> activityRef) {
        new AsyncTask<Void, Void, Void>() {
            private ProgressDialog mProgressDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(activityRef.get());
                mProgressDialog.setMessage(activityRef.get().getString(R.string.testing));
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                KP.mTestingProfile = true;
            }
            @SuppressLint("WrongThread")
            @Override
            protected Void doInBackground(Void... voids) {
                Utils.delete("/data/local/tmp/sm");
                createTestScript();
                String output = RootUtils.runAndGetError("sh  /data/local/tmp/sm");
                if (output.isEmpty()) {
                    output = activityRef.get().getString(R.string.testing_success);
                }
                KP.mOutput.append(output);
                Utils.delete("/data/local/tmp/sm");
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    mProgressDialog.dismiss();
                } catch (IllegalArgumentException ignored) {
                }
                KP.mTestingProfile = false;
            }
        }.execute();
    }

    private void refreshStatus() {
        new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(100);
                        runOnUiThread(() -> {
                            if (mTestOutput != null && KP.mOutput != null) {
                                mTestOutput.setVisibility(View.VISIBLE);
                                mTestOutput.setText(KP.mOutput.toString());
                            }
                        });
                    }
                } catch (InterruptedException ignored) {}
            }
        }.start();
    }

    private boolean isTextEntered() {
        return mProfileDetailsHint.getText() != null && !mProfileDetailsHint.getText().toString().equals("")
                || mProfileDescriptionHint.getText() != null && !mProfileDescriptionHint.getText().toString().equals("");
    }

    @Override
    public void onStart() {
        super.onStart();

        if (KP.mOutput == null) {
            KP.mOutput = new StringBuilder();
        } else {
            KP.mOutput.setLength(0);
        }
    }

    @Override
    public void onBackPressed() {
        if (KP.mTestingProfile) return;
        if (isTextEntered()) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.data_lose_warning))
                    .setNegativeButton(getString(R.string.cancel), (dialog1, id1) -> {
                    })
                    .setPositiveButton(getString(R.string.yes), (dialog1, id1) -> {
                        super.onBackPressed();
                    })
                    .show();
        } else {
            super.onBackPressed();
        }
    }

}