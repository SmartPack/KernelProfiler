package com.smartpack.kernelprofiler.utils;

import android.Manifest;
import android.annotation.SuppressLint;
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
import androidx.core.widget.NestedScrollView;

import com.smartpack.kernelprofiler.R;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 22, 2020
 */

public class CreateProfileActivity extends AppCompatActivity {

    private AppCompatEditText mProfileDescriptionHint;
    private AppCompatEditText mProfileDetailsHint;
    private AppCompatTextView mTitle;
    private AppCompatTextView mTestOutput;
    private NestedScrollView mScrollView;

    private List<String> mOutput = null;

    private boolean mTestingProfile = false;

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
        AppCompatTextView mTestButton = findViewById(R.id.test_button);
        mTestOutput = findViewById(R.id.test_output);
        mScrollView = findViewById(R.id.scroll_view);

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
                testCommands();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            } else {
                Utils.snackbar(mTitle, getString(R.string.profile_details_empty));
            }
        });
        mTestOutput = findViewById(R.id.test_output);
        refreshStatus();
    }

    private void createProfile() {
        Utils.dialogEditText("",
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
                    Utils.create("#!/system/bin/sh\n\n# Description=" + mProfileDescriptionHint.getText() + "\n\n" +
                            mProfileDetailsHint.getText(), Environment.getExternalStorageDirectory().toString() + "/" + text);
                    new AlertDialog.Builder(this)
                            .setMessage(getString(R.string.create_profile_message, text) + " '" +
                                    Environment.getExternalStorageDirectory().toString() + "'")
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.cancel), (dialog1, id1) -> {
                                super.onBackPressed();
                            })
                            .show();
                }, this).setOnDismissListener(dialogInterface -> {
        }).show();
    }

    private void createTestScript() {
        Utils.create("#!/system/bin/sh\n\n" + Objects.requireNonNull(mProfileDetailsHint.getText()).toString(), getCacheDir().getPath() + "/sm");
    }

    @SuppressLint("StaticFieldLeak")
    private void testCommands() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mTestingProfile = true;
                mOutput = new ArrayList<>();
                Utils.delete(getCacheDir().getPath() + "/sm");
            }
            @SuppressLint("WrongThread")
            @Override
            protected Void doInBackground(Void... voids) {
                createTestScript();
                Utils.runAndGetLiveOutput("sh " + getCacheDir().getPath() + "/sm", mOutput);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Utils.delete(getCacheDir().getPath() + "/sm");
                mTestingProfile = false;
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
                            if (mTestingProfile) {
                                mScrollView.fullScroll(NestedScrollView.FOCUS_DOWN);
                            }
                            try {
                                if (!Utils.getOutput(mOutput).isEmpty()) {
                                    mTestOutput.setVisibility(View.VISIBLE);
                                    mTestOutput.setText(Utils.getOutput(mOutput));
                                } else {
                                    mTestOutput.setText(getString(R.string.testing_success));
                                }
                            } catch (ConcurrentModificationException | NullPointerException ignored) {}
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
    public void onBackPressed() {
        if (mTestingProfile) {
            Utils.snackbar(findViewById(android.R.id.content), getString(R.string.testing));
            return;
        }
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