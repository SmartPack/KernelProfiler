package com.smartpack.kernelprofiler.views.recyclerview;

import android.view.View;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;

import com.smartpack.kernelprofiler.R;
import com.smartpack.kernelprofiler.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapted from https://github.com/Grarak/KernelAdiutor by Willi Ye.
 */

public class DescriptionView extends RecyclerViewItem {

    public interface OnCheckBoxListener {
        void onChanged(DescriptionView descriptionView, boolean isChecked);
    }

    private List<OnCheckBoxListener> mOnCheckBoxListeners = new ArrayList<>();

    private View mRootView;
    private AppCompatTextView mTitleView;
    private AppCompatTextView mSummaryView;
    private AppCompatCheckBox mCheckBox;

    private boolean mChecked;
    private CharSequence mTitle;
    private CharSequence mSummary;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_description_view;
    }

    @Override
    public void onCreateView(View view) {
        mRootView = view;
        mTitleView = view.findViewById(R.id.title);
        mSummaryView = view.findViewById(R.id.summary);
        mCheckBox = view.findViewById(R.id.checkbox);
        super.onCreateView(view);

        mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mChecked = isChecked;
            List<OnCheckBoxListener> applied = new ArrayList<>();
            for (OnCheckBoxListener onCheckBoxListener : mOnCheckBoxListeners) {
                if (applied.indexOf(onCheckBoxListener) == -1) {
                    onCheckBoxListener.onChanged(this, isChecked);
                    applied.add(onCheckBoxListener);
                }
            }
        });
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        refresh();
    }
    public void setSummary(CharSequence summary) {
        mSummary = summary;
        refresh();
    }
    public void setOnCheckBoxListener(OnCheckBoxListener OnCheckBoxListener) {
        if (Utils.mForegroundActive) return;
        mOnCheckBoxListeners.add(OnCheckBoxListener);
    }
    public void setChecked(boolean checked) {
        mChecked = checked;
        refresh();
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mTitleView != null) {
            if (mTitle != null) {
                mTitleView.setText(mTitle);
            } else {
                mTitleView.setVisibility(View.GONE);
            }
        }
        if (mSummaryView != null) {
            if (mSummary != null) {
                mSummaryView.setText(mSummary);
            } else {
                mSummaryView.setVisibility(View.GONE);
            }
        }
        if (mCheckBox != null) {
            mCheckBox.setVisibility(View.VISIBLE);
            mCheckBox.setChecked(mChecked);
        }
        if (mRootView != null && getOnItemClickListener() != null && mTitleView != null
                && mSummaryView != null) {
            mRootView.setOnClickListener(v -> {
                if (Utils.mForegroundActive) return;
                if (getOnItemClickListener() != null) {
                    getOnItemClickListener().onClick(DescriptionView.this);
                }
            });
        }
    }

}