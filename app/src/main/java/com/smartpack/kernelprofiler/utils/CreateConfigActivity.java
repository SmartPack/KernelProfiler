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
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 22, 2020
 */

public class CreateConfigActivity extends AppCompatActivity {

    AppCompatEditText mProfileTitleHint;
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
        mProfileTitleHint = findViewById(R.id.config_title_hint);
        mDescriptionHint = findViewById(R.id.config_description_hint);
        mDefaultHint = findViewById(R.id.default_profile_hint);
        mDeveloperHint = findViewById(R.id.developer_hint);
        mSupportHint = findViewById(R.id.support_hint);
        mDonationsHint = findViewById(R.id.donations_hint);
        mTitle = findViewById(R.id.title);
        mTitle.setText(getString(R.string.create_config));
        mSave.setOnClickListener(v -> {
            if (Utils.checkWriteStoragePermission(this)) {
                if (mProfileTitleHint.getText() != null && !mProfileTitleHint.getText().toString().equals("")) {
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("title", mProfileTitleHint.getText());
                        obj.put("description", mDescriptionHint.getText());
                        obj.put("default", mDefaultHint.getText());
                        obj.put("developer", mDeveloperHint.getText());
                        obj.put("support", mSupportHint.getText());
                        obj.put("donations", mDonationsHint.getText());
                        Utils.create(obj.toString(), Environment.getExternalStorageDirectory().toString() + "/kernelprofiler.json");
                        Utils.snackbarIndenite(mTitle, getString(R.string.configuration_created, Environment.getExternalStorageDirectory().toString()));
                    } catch (JSONException ignored) {
                    }
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
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.title_check))
                    .setNegativeButton(getString(R.string.cancel), (dialog1, id1) -> {
                    })
                    .setPositiveButton(getString(R.string.test), (dialog1, id1) -> {
                        if (mProfileTitleHint.getText() != null && !mProfileTitleHint.getText().toString().equals("") &&
                                RootUtils.runAndGetOutput("uname -a").contains(mProfileTitleHint.getText())) {
                            Utils.snackbar(mTitle, getString(R.string.success));
                        } else {
                            Utils.snackbar(mTitle, getString(R.string.failed));
                        }
                    })
                    .show();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}