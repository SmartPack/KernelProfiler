package com.smartpack.kernelprofiler.utils;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.smartpack.kernelprofiler.R;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on September 16, 2020
 */

public class NoRootActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noroot);

        AppCompatImageButton mBack = findViewById(R.id.back_button);
        AppCompatTextView mCancel = findViewById(R.id.cancel_button);
        mBack.setOnClickListener(v -> onBackPressed());
        mCancel.setOnClickListener(v -> super.onBackPressed());
    }

}