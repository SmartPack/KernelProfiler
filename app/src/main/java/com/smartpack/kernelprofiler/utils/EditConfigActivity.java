package com.smartpack.kernelprofiler.utils;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.smartpack.kernelprofiler.R;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 28, 2020
 */

public class EditConfigActivity extends AppCompatActivity {

    private AppCompatEditText mConfigTitleHint;
    private AppCompatEditText mDescriptionHint;
    private AppCompatEditText mDefaultHint;
    private AppCompatEditText mDeveloperHint;
    private AppCompatEditText mSupportHint;
    private AppCompatEditText mDonationsHint;
    private AppCompatTextView mTitle;

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
            if (mConfigTitleHint.getText() != null && !mConfigTitleHint.getText().toString().equals("")) {
                new MaterialAlertDialogBuilder(this)
                        .setMessage(getString(R.string.edit_config_message))
                        .setNegativeButton(getString(R.string.cancel), (dialog1, id1) -> {
                        })
                        .setPositiveButton(getString(R.string.ok), (dialog1, id1) -> createConfig())
                        .show();
            } else {
                Utils.snackbar(mTitle, getString(R.string.title_empty_message));
            }
        });
        mCheck.setOnClickListener(v -> {
            if (mConfigTitleHint.getText() == null || mConfigTitleHint.getText().toString().equals("")) {
                Utils.snackbar(mTitle, getString(R.string.title_empty_message));
                return;
            }
            if (Utils.runAndGetOutput("uname -a").contains(mConfigTitleHint.getText())) {
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
            new MaterialAlertDialogBuilder(this)
                    .setMessage(getString(R.string.edit_config_saved))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.cancel), (dialog1, id1) -> super.onBackPressed())
                    .show();
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
                    .setPositiveButton(getString(R.string.yes), (dialog1, id1) -> super.onBackPressed())
                    .show();
        } else {
            super.onBackPressed();
        }
    }

}