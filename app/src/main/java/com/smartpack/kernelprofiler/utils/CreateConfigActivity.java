package com.smartpack.kernelprofiler.utils;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.smartpack.kernelprofiler.R;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 22, 2020
 */

public class CreateConfigActivity extends AppCompatActivity {

    private AppCompatEditText mConfigTitleHint;
    private AppCompatEditText mDescriptionHint;
    private AppCompatEditText mDefaultHint;
    private AppCompatEditText mDeveloperHint;
    private AppCompatEditText mSupportHint;
    private AppCompatEditText mDonationsHint;
    private MaterialTextView mTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createconfig);

        AppCompatImageButton mBack = findViewById(R.id.back_button);
        mBack.setOnClickListener(v -> onBackPressed());
        AppCompatImageButton mSave = findViewById(R.id.save_button);
        AppCompatImageButton mCheck = findViewById(R.id.check_button);
        mConfigTitleHint = findViewById(R.id.config_title_hint);
        mDescriptionHint = findViewById(R.id.config_description_hint);
        mDefaultHint = findViewById(R.id.default_profile_hint);
        mDeveloperHint = findViewById(R.id.developer_hint);
        mSupportHint = findViewById(R.id.support_hint);
        mDonationsHint = findViewById(R.id.donations_hint);
        mTitle = findViewById(R.id.title);
        mTitle.setText(getString(R.string.create_config));
        mSave.setOnClickListener(v -> {
            if (Utils.checkWriteStoragePermission(this)) {
                if (mConfigTitleHint.getText() != null && !mConfigTitleHint.getText().toString().equals("")) {
                    new MaterialAlertDialogBuilder(this)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(getString(R.string.save_config_title))
                            .setMessage(Utils.existFile(
                                    Environment.getExternalStorageDirectory().toString() + "/kernelprofiler.json") ?
                                    getString(R.string.save_config_warning, Environment.getExternalStorageDirectory()
                                            .toString() + "/kernelprofiler.json") : getString(R.string.save_config_message,
                                    Environment.getExternalStorageDirectory().toString() + "/kernelprofiler.json"))
                            .setNeutralButton(getString(R.string.cancel), (dialog1, id1) -> {})
                            .setPositiveButton(getString(R.string.yes), (dialog1, id1) -> saveConfig())
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
            new MaterialAlertDialogBuilder(this)
                    .setMessage(getString(R.string.title_check))
                    .setNegativeButton(getString(R.string.cancel), (dialog1, id1) -> {
                    })
                    .setPositiveButton(getString(R.string.test), (dialog1, id1) -> {
                        if (Utils.runAndGetOutput("uname -a").contains(mConfigTitleHint.getText())) {
                            Utils.snackbar(mTitle, getString(R.string.success_message, mConfigTitleHint.getText()));
                        } else {
                            Utils.snackbar(mTitle, getString(R.string.failed_message, mConfigTitleHint.getText()));
                        }
                    })
                    .show();
        });
    }

    private void saveConfig() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("title", mConfigTitleHint.getText());
            obj.put("description", mDescriptionHint.getText());
            obj.put("default", mDefaultHint.getText());
            obj.put("developer", mDeveloperHint.getText());
            obj.put("support", mSupportHint.getText());
            obj.put("donations", mDonationsHint.getText());
            Utils.create(obj.toString(), Environment.getExternalStorageDirectory().toString() + "/kernelprofiler.json");
            super.onBackPressed();
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
            new MaterialAlertDialogBuilder(this)
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