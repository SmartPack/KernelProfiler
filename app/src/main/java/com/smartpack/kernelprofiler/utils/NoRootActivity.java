package com.smartpack.kernelprofiler.utils;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.smartpack.kernelprofiler.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on September 16, 2020
 */

public class NoRootActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noroot);

        AppCompatImageButton mBack = findViewById(R.id.back_button);
        AppCompatTextView mainTitle = findViewById(R.id.main_title);
        AppCompatTextView mainText = findViewById(R.id.main_text);
        AppCompatTextView mCancel = findViewById(R.id.cancel_button);
        if (!Utils.rootAccess()) {
            mainTitle.setText(R.string.no_root);
            mainText.setText(R.string.no_root_message);
        } else {
            mainTitle.setText(R.string.unsupported);
            mainText.setText(getString(R.string.unsupported_reason) + "\n\n" + getString(R.string.unsupported_summary) +
                    "\n\n\n" + getString(R.string.unsupported_solution) + "\n\n" + getString(R.string.unsupported_message) +
                    "\n\n\n" + getString(R.string.kernel_support) + "\n\n" + getString(R.string.kernel_support_summary) +
                    "\n\n\n" + getString(R.string.unsupported_help_message));
        }
        mBack.setOnClickListener(v -> onBackPressed());
        mCancel.setOnClickListener(v -> super.onBackPressed());
    }

}