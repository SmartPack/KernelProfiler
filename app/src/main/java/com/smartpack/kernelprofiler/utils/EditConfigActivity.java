package com.smartpack.kernelprofiler.utils;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;

import com.smartpack.kernelprofiler.R;
import com.smartpack.kernelprofiler.utils.root.RootUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 28, 2020
 */

public class EditConfigActivity extends AppCompatActivity {

    AppCompatEditText mConfigTitleHint;
    AppCompatEditText mDescriptionHint;
    AppCompatEditText mDefaultHint;
    AppCompatEditText mDeveloperHint;
    AppCompatEditText mSupportHint;
    AppCompatEditText mDonationsHint;
    AppCompatTextView mTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createconfig);

        AppCompatImageButton mBack = findViewById(R.id.back_button);
        mBack.setOnClickListener(v -> onBackPressed());
        AppCompatImageButton mSave = findViewById(R.id.save_button);
        AppCompatImageButton mCheck = findViewById(R.id.check_button);
        mConfigTitleHint = findViewById(R.id.config_title_hint);
        if (KP.getCustomTitle() != null) {
            mConfigTitleHint.setText(KP.getCustomTitle());
        }
        mDescriptionHint = findViewById(R.id.config_description_hint);
        if (KP.getCustomDescription() != null) {
            mDescriptionHint.setText(KP.getCustomDescription());
        }
        mDefaultHint = findViewById(R.id.default_profile_hint);
        if (KP.getDefaultProfile() != null) {
            mDefaultHint.setText(KP.getDefaultProfile());
        }
        mDeveloperHint = findViewById(R.id.developer_hint);
        if (KP.getDeveloper() != null) {
            mDeveloperHint.setText(KP.getDeveloper());
        }
        mSupportHint = findViewById(R.id.support_hint);
        if (KP.getSupport() != null) {
            mSupportHint.setText(KP.getSupport());
        }
        mDonationsHint = findViewById(R.id.donations_hint);
        if (KP.getDonation() != null) {
            mDonationsHint.setText(KP.getDonation());
        }
        mTitle = findViewById(R.id.title);
        mTitle.setText(getString(R.string.edit_config));
        mSave.setOnClickListener(v -> {
            if (Utils.checkWriteStoragePermission(this)) {
                if (mConfigTitleHint.getText() != null && !mConfigTitleHint.getText().toString().equals("")) {
                    new AlertDialog.Builder(this)
                            .setMessage(getString(R.string.edit_config_message))
                            .setNegativeButton(getString(R.string.cancel), (dialog1, id1) -> {
                            })
                            .setPositiveButton(getString(R.string.ok), (dialog1, id1) -> {
                                createConfig();
                            })
                            .show();
                } else {
                    Utils.snackbar(mTitle, getString(R.string.title_empty_message));
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                Utils.snackbar(mTitle, getString(R.string.storage_access_denied) + " " +
                        Environment.getExternalStorageDirectory().toString());
            }
        });
        mCheck.setOnClickListener(v -> {
            if (mConfigTitleHint.getText() == null || mConfigTitleHint.getText().toString().equals("")) {
                Utils.snackbar(mTitle, getString(R.string.title_empty_message));
                return;
            }
            if (RootUtils.runAndGetOutput("uname -a").contains(mConfigTitleHint.getText())) {
                Utils.snackbar(mTitle, getString(R.string.success_message, mConfigTitleHint.getText()));
            } else {
                Utils.snackbar(mTitle, getString(R.string.failed_message, mConfigTitleHint.getText()));
            }
        });
    }

    private void createConfig() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("title", mConfigTitleHint.getText());
            obj.put("description", mDescriptionHint.getText());
            obj.put("default", mDefaultHint.getText());
            obj.put("developer", mDeveloperHint.getText());
            obj.put("support", mSupportHint.getText());
            obj.put("donations", mDonationsHint.getText());
            Utils.create(obj.toString(), "/data/kernel_profiler/kernelprofiler.json");
            Utils.snackbarIndenite(mTitle, getString(R.string.edit_config_saved));
        } catch (JSONException ignored) {
        }
    }

    private boolean isTextEntered() {
        return mConfigTitleHint.getText() != null && !mConfigTitleHint.getText().toString().equals("")
                || mDescriptionHint.getText() != null && !mDescriptionHint.getText().toString().equals("")
                || mDefaultHint.getText() != null && !mDefaultHint.getText().toString().equals("")
                || mDeveloperHint.getText() != null && !mDeveloperHint.getText().toString().equals("")
                || mSupportHint.getText() != null && !mSupportHint.getText().toString().equals("")
                || mDonationsHint.getText() != null && !mDonationsHint.getText().toString().equals("");
    }

    @Override
    public void onBackPressed() {
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