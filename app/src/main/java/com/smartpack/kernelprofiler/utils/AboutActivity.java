package com.smartpack.kernelprofiler.utils;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.smartpack.kernelprofiler.BuildConfig;
import com.smartpack.kernelprofiler.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static com.smartpack.kernelprofiler.utils.Utils.readAssetFile;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */

public class AboutActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        AppCompatImageButton mBack = findViewById(R.id.back);
        AppCompatImageView mDeveloper = findViewById(R.id.developer);
        AppCompatTextView mAppTitle = findViewById(R.id.app_title);
        AppCompatTextView mChangeLog = findViewById(R.id.change_log);
        AppCompatTextView mCancel = findViewById(R.id.cancel_button);
        mAppTitle.setText(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);
        mBack.setOnClickListener(v -> onBackPressed());
        mDeveloper.setOnClickListener(v -> {
            Utils.launchUrl("https://github.com/sunilpaulmathew", mDeveloper, this);
        });
        String change_log;
        try {
            change_log = new JSONObject(Objects.requireNonNull(readAssetFile(
                    this, "changelogs.json"))).getString("releaseNotes");
            mChangeLog.setText(change_log);
        } catch (JSONException ignored) {
        }
        mCancel.setOnClickListener(v -> super.onBackPressed());
    }

}