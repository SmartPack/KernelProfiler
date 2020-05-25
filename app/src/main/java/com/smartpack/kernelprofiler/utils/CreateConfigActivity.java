package com.smartpack.kernelprofiler.utils;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;

import com.smartpack.kernelprofiler.R;

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
    AppCompatTextView mProfileTitle;
    AppCompatTextView mDescription;
    AppCompatTextView mDefault;
    AppCompatTextView mDeveloper;
    AppCompatTextView mSupport;
    AppCompatTextView mDonation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createconfig);

        AppCompatImageButton mBack = findViewById(R.id.back_button);
        mBack.setOnClickListener(v -> onBackPressed());
        AppCompatImageButton mSave = findViewById(R.id.save_button);
        mProfileTitleHint = findViewById(R.id.config_title_hint);
        mDescriptionHint = findViewById(R.id.config_description_hint);
        mDefaultHint = findViewById(R.id.default_profile_hint);
        mDeveloperHint = findViewById(R.id.developer_hint);
        mSupportHint = findViewById(R.id.support_hint);
        mDonationsHint = findViewById(R.id.donations_hint);
        mTitle = findViewById(R.id.title);
        mProfileTitle = findViewById(R.id.config_title);
        mDescription = findViewById(R.id.config_description);
        mDefault = findViewById(R.id.default_profile);
        mDeveloper = findViewById(R.id.developer);
        mSupport = findViewById(R.id.support);
        mDonation = findViewById(R.id.donations);
        mTitle.setText(getString(R.string.create_config));
        mProfileTitleHint.setVisibility(View.VISIBLE);
        mDescriptionHint.setVisibility(View.VISIBLE);
        mDefaultHint.setVisibility(View.VISIBLE);
        mDeveloperHint.setVisibility(View.VISIBLE);
        mSupportHint.setVisibility(View.VISIBLE);
        mDonationsHint.setVisibility(View.VISIBLE);
        mTitle.setVisibility(View.VISIBLE);
        mProfileTitle.setVisibility(View.VISIBLE);
        mDescription.setVisibility(View.VISIBLE);
        mDefault.setVisibility(View.VISIBLE);
        mDeveloper.setVisibility(View.VISIBLE);
        mSupport.setVisibility(View.VISIBLE);
        mDonation.setVisibility(View.VISIBLE);
        mSave.setOnClickListener(v -> {
            if (Utils.checkWriteStoragePermission(this)) {
                try {
                    JSONObject obj= new JSONObject();
                    obj.put("title", mDescription.getText());
                    obj.put("description", mDescriptionHint.getText());
                    obj.put("default", mDefaultHint.getText());
                    obj.put("developer", mDeveloperHint.getText());
                    obj.put("support", mSupportHint.getText());
                    obj.put("donations", mDescriptionHint.getText());
                    Utils.create(obj.toString(),Environment.getExternalStorageDirectory().toString() + "/kernelprofiler.json");
                    Utils.snackbarIndenite(mTitle, getString(R.string.configuration_created, Environment.getExternalStorageDirectory().toString()));
                } catch (JSONException ignored) {
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                Utils.snackbar(mTitle, getString(R.string.storage_access_denied) + " " +
                        Environment.getExternalStorageDirectory().toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}