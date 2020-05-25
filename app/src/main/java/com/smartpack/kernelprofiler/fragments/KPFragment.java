package com.smartpack.kernelprofiler.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import androidx.appcompat.app.AlertDialog;

import com.smartpack.kernelprofiler.R;
import com.smartpack.kernelprofiler.utils.KP;
import com.smartpack.kernelprofiler.utils.Utils;
import com.smartpack.kernelprofiler.views.recyclerview.DescriptionView;
import com.smartpack.kernelprofiler.views.recyclerview.RecyclerViewItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 22, 2020
 */

public class KPFragment extends RecyclerViewFragment {

    private AsyncTask<Void, Void, List<RecyclerViewItem>> mLoader;

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        reload();
    }

    private void reload() {
        if (mLoader == null) {
            getHandler().postDelayed(new Runnable() {
                @SuppressLint("StaticFieldLeak")
                @Override
                public void run() {
                    mLoader = new AsyncTask<Void, Void, List<RecyclerViewItem>>() {

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            showProgress();
                        }

                        @Override
                        protected List<RecyclerViewItem> doInBackground(Void... voids) {
                            List<RecyclerViewItem> items = new ArrayList<>();
                            load(items);
                            return items;
                        }

                        @Override
                        protected void onPostExecute(List<RecyclerViewItem> recyclerViewItems) {
                            super.onPostExecute(recyclerViewItems);
                            if (isAdded()) {
                                clearItems();
                                for (RecyclerViewItem item : recyclerViewItems) {
                                    addItem(item);
                                }

                                hideProgress();
                                mLoader = null;
                            }
                        }
                    };
                    mLoader.execute();
                }
            }, 250);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void load(List<RecyclerViewItem> items) {
        for (final String kpProfilesItems : KP.profileItems()) {
            File kpProfiles = new File(KP.KPFile() + "/" + kpProfilesItems);
            if (KP.KPFile().length() > 0 && KP.isKPProfile(kpProfiles.toString())) {
                DescriptionView kpProfile = new DescriptionView();
                kpProfile.setTitle(kpProfiles.getName().replace(".sh", ""));
                String description = KP.getProfileDescription(kpProfiles.toString());
                if (description == null) {
                    description = getString(R.string.description_unknown);
                }
                kpProfile.setSummary(description);
                kpProfile.setFullSpan(true);
                kpProfile.setChecked(kpProfiles.getName().equals(KP.getDefaultProfile()));
                kpProfile.setOnCheckBoxListener((descriptionView, isChecked) -> {
                    if (Utils.mForegroundActive) return;
                    if (!kpProfiles.getName().equals(KP.getDefaultProfile())) {
                        Utils.create(Utils.readFile(KP.KP_CONFIG).replaceAll(Objects.requireNonNull(
                                KP.getDefaultProfile()), kpProfiles.getName()), KP.KP_CONFIG);
                        Utils.snackbar(getRootView(), getString(R.string.on_boot_message, kpProfiles.getName()));
                    } else {
                        Utils.snackbar(getRootView(), getString(R.string.on_boot_conformation, kpProfiles.getName()));
                    }
                    reload();
                });
                kpProfile.setOnItemClickListener(item -> new AlertDialog.Builder(requireActivity())
                        .setMessage(getString(R.string.apply_question, kpProfiles.getName().replace(".sh", "")))
                        .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                        })
                        .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                            if (!KP.isKPProfile(kpProfiles.toString())) {
                                Utils.snackbar(getRootView(), getString(R.string.wrong_profile, kpProfiles.getName().replace(".sh", "")));
                                return;
                            }
                            new AsyncTask<Void, Void, Void>() {
                                private ProgressDialog mProgressDialog;
                                @Override
                                protected void onPreExecute() {
                                    super.onPreExecute();
                                    mProgressDialog = new ProgressDialog(getActivity());
                                    mProgressDialog.setMessage(getString(R.string.applying_profile, kpProfiles.getName()));
                                    mProgressDialog.setCancelable(false);
                                    mProgressDialog.show();
                                }
                                @Override
                                protected Void doInBackground(Void... voids) {
                                    KP.applyProfile(kpProfiles.toString());
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void aVoid) {
                                    super.onPostExecute(aVoid);
                                    try {
                                        mProgressDialog.dismiss();
                                    } catch (IllegalArgumentException ignored) {
                                    }
                                    boolean resultEmpty = KP.mOutput != null && KP.mOutput.toString().isEmpty();
                                    new AlertDialog.Builder(requireActivity())
                                            .setMessage(resultEmpty ? getString(R.string.profile_applied_success,
                                                    kpProfiles.getName()) : KP.mOutput.toString())
                                            .setPositiveButton(getString(R.string.cancel), (dialog, id) -> {
                                            })
                                            .show();
                                }
                            }.execute();
                        })
                        .show());

                items.add(kpProfile);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoader != null) {
            mLoader.cancel(true);
        }
    }

}