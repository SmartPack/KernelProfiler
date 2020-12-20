package com.smartpack.kernelprofiler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.smartpack.kernelprofiler.utils.AboutActivity;
import com.smartpack.kernelprofiler.utils.CreateConfigActivity;
import com.smartpack.kernelprofiler.utils.CreateProfileActivity;
import com.smartpack.kernelprofiler.utils.EditConfigActivity;
import com.smartpack.kernelprofiler.utils.KP;
import com.smartpack.kernelprofiler.utils.NoRootActivity;
import com.smartpack.kernelprofiler.utils.RecycleViewAdapter;
import com.smartpack.kernelprofiler.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 22, 2020
 */

public class MainActivity extends AppCompatActivity {

    private AppCompatImageButton mSettings;
    private MaterialTextView mProgressMessage;
    private boolean mExit;
    private Handler mHandler = new Handler();
    private LinearLayout mProgressLayout;
    private RecyclerView mRecyclerView;
    private RecycleViewAdapter mRecycleViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize App Theme
        Utils.initializeAppTheme(this);
        super.onCreate(savedInstanceState);
        // Set App Language
        Utils.setLanguage(this);
        setContentView(R.layout.activity_main);

        mProgressLayout = findViewById(R.id.progress_layout);
        mProgressMessage = findViewById(R.id.progress_message);
        mSettings = findViewById(R.id.settings_menu);
        mRecyclerView = findViewById(R.id.recycler_view);
        MaterialTextView textView = findViewById(R.id.unsupported_Text);
        AppCompatImageView helpIcon = findViewById(R.id.help_Image);
        MaterialTextView copyRightText = findViewById(R.id.copyright_Text);
        MaterialTextView customTitle = findViewById(R.id.customTitle);
        MaterialTextView customDescription = findViewById(R.id.customDescription);
        customTitle.setText(KP.supported() && KP.getCustomTitle() != null ? KP.getCustomTitle() : getString(R.string.app_name));
        customDescription.setText(KP.supported() && KP.getCustomDescription() != null ? KP.getCustomDescription() : getString(R.string.app_name_summary));
        mSettings.setOnClickListener(v -> {
            settingsMenu(this);
        });

        if (!Utils.rootAccess() || !KP.supported()) {
            unsupported();
            if (!Utils.rootAccess()) finish();
            textView.setText(getString(R.string.unsupported));
            helpIcon.setImageDrawable(Utils.getColoredIcon(R.drawable.ic_help, this));
            helpIcon.setOnClickListener(v -> {
                unsupported();
            });
            return;
        }

        if (KP.supported() && KP.getDeveloper() != null) {
            String copyright = KP.getDeveloper();
            if (!copyright.startsWith("©")) {
                copyright = "©" + copyright;
            }
            copyRightText.setText(copyright);
        } else {
            copyRightText.setText(R.string.copyright);
        }

        if (KP.supported()) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecycleViewAdapter = new RecycleViewAdapter(getData());
            mRecyclerView.setAdapter(mRecycleViewAdapter);
            mRecycleViewAdapter.setOnItemClickListener((view, position) -> applyProfile(mRecycleViewAdapter.getName(position), this));
        }
    }

    private List<String> getData() {
        List<String> mData = new ArrayList<>();
        for (final String kpProfilesItems : KP.profileItems()) {
            File kpProfiles = new File(KP.KPFile() + "/" + kpProfilesItems);
            if (KP.KPFile().length() > 0 && KP.isKPProfile(kpProfiles.toString())) {
                mData.add(kpProfiles.getName().replace(".sh", ""));
            }
        }
        return mData;
    }

    @SuppressLint("StaticFieldLeak")
    private void applyProfile(String profile, Activity activity) {
        String profileName = profile + ".sh";
        String profilePath = KP.KPFile() + "/" +  profileName;
        new MaterialAlertDialogBuilder(activity)
                .setMessage(getString(R.string.apply_question, profile))
                .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                })
                .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                    if (!KP.isKPProfile(profilePath)) {
                        Utils.snackbar(mRecyclerView, getString(R.string.wrong_profile, profile));
                        return;
                    }
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            mProgressMessage.setText(getString(R.string.applying_profile, profile));
                            mProgressMessage.setVisibility(View.VISIBLE);
                            mProgressLayout.setVisibility(View.VISIBLE);
                        }
                        @Override
                        protected Void doInBackground(Void... voids) {
                            Utils.runCommand("sh " + profilePath);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            mProgressMessage.setVisibility(View.GONE);
                            mProgressLayout.setVisibility(View.GONE);
                            new MaterialAlertDialogBuilder(activity)
                                    .setMessage(getString(R.string.profile_applied_success, profile))
                                    .setPositiveButton(getString(R.string.cancel), (dialog, id) -> {
                                    })
                                    .show();
                        }
                    }.execute();
                })
                .show();
    }

    private void unsupported() {
        Intent noRoot = new Intent(this, NoRootActivity.class);
        startActivity(noRoot);
    }

    private void settingsMenu(Activity activity) {
        PopupMenu popupMenu = new PopupMenu(activity, mSettings);
        Menu menu = popupMenu.getMenu();
        SubMenu appTheme = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.dark_theme));
        appTheme.add(Menu.NONE, 20, Menu.NONE, getString(R.string.dark_theme_auto)).setCheckable(true)
                .setChecked(Utils.getBoolean("theme_auto", true, activity));
        appTheme.add(Menu.NONE, 1, Menu.NONE, getString(R.string.dark_theme_enable)).setCheckable(true)
                .setChecked(Utils.getBoolean("dark_theme", false, activity));
        appTheme.add(Menu.NONE, 19, Menu.NONE, getString(R.string.dark_theme_disable)).setCheckable(true)
                .setChecked(Utils.getBoolean("light_theme", false, activity));
        if (KP.supported() && KP.isCustomSettingsAvailable()) {
            SubMenu kernel = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.kernel_about));
            if (KP.getSupport() != null && !KP.getSupport().isEmpty()) {
                kernel.add(Menu.NONE, 2, Menu.NONE, getString(R.string.support));
            }
            if (KP.getDonation() != null && !KP.getDonation().isEmpty()) {
                kernel.add(Menu.NONE, 3, Menu.NONE, getString(R.string.donations));
            }
        }
        SubMenu tools = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.tools_developer));
        tools.add(Menu.NONE, 4, Menu.NONE, getString(R.string.create_profile));
        tools.add(Menu.NONE, 5, Menu.NONE, getString(R.string.create_config));
        if (KP.supported()) {
            tools.add(Menu.NONE, 6, Menu.NONE, getString(R.string.edit_config));
        }
        SubMenu language = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.language, Utils.getLanguage(activity)));
        language.add(Menu.NONE, 15, Menu.NONE, getString(R.string.language_default)).setCheckable(true)
                .setChecked(Utils.languageDefault(activity));
        language.add(Menu.NONE, 16, Menu.NONE, getString(R.string.language_en)).setCheckable(true)
                .setChecked(Utils.getBoolean("use_en", false, activity));
        language.add(Menu.NONE, 17, Menu.NONE, getString(R.string.language_pt)).setCheckable(true)
                .setChecked(Utils.getBoolean("use_pt", false, activity));
        language.add(Menu.NONE, 18, Menu.NONE, getString(R.string.language_el)).setCheckable(true)
                .setChecked(Utils.getBoolean("use_el", false, activity));
        language.add(Menu.NONE, 21, Menu.NONE, getString(R.string.language_ko)).setCheckable(true)
                .setChecked(Utils.getBoolean("use_ko", false, activity));
        language.add(Menu.NONE, 22, Menu.NONE, getString(R.string.language_in)).setCheckable(true)
                .setChecked(Utils.getBoolean("use_in", false, activity));
        SubMenu app = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.app_about));
        app.add(Menu.NONE, 7, Menu.NONE, getString(R.string.share));
        app.add(Menu.NONE, 8, Menu.NONE, getString(R.string.support));
        app.add(Menu.NONE, 9, Menu.NONE, getString(R.string.source_code));
        app.add(Menu.NONE, 10, Menu.NONE, getString(R.string.documentation));
        app.add(Menu.NONE, 11, Menu.NONE, getString(R.string.more_apps));
        app.add(Menu.NONE, 12, Menu.NONE, getString(R.string.report_issue));
        if (Utils.isNotDonated(activity)) {
            app.add(Menu.NONE, 14, Menu.NONE, getString(R.string.donations));
        }
        app.add(Menu.NONE, 13, Menu.NONE, getString(R.string.about));
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 0:
                    break;
                case 1:
                    if (!Utils.getBoolean("dark_theme", false, activity)) {
                        Utils.saveBoolean("dark_theme", true, activity);
                        Utils.saveBoolean("light_theme", false, activity);
                        Utils.saveBoolean("theme_auto", false, activity);
                        Utils.restartApp(activity);
                    }
                    break;
                case 2:
                    Utils.launchUrl(KP.getSupport(), mRecyclerView, activity);
                    break;
                case 3:
                    Utils.launchUrl(KP.getDonation(), mRecyclerView, activity);
                    break;
                case 4:
                    Intent createProfile = new Intent(activity, CreateProfileActivity.class);
                    startActivity(createProfile);
                    break;
                case 5:
                    Intent createConfig = new Intent(activity, CreateConfigActivity.class);
                    startActivity(createConfig);
                    break;
                case 6:
                    Intent editConfig = new Intent(activity, EditConfigActivity.class);
                    startActivity(editConfig);
                    break;
                case 7:
                    Intent share = new Intent();
                    share.setAction(Intent.ACTION_SEND);
                    share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);
                    share.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message));
                    share.setType("text/plain");
                    Intent shareIntent = Intent.createChooser(share, null);
                    startActivity(shareIntent);
                    break;
                case 8:
                    Utils.launchUrl("https://t.me/smartpack_kmanager", mRecyclerView, activity);
                    break;
                case 9:
                    Utils.launchUrl("https://github.com/SmartPack/KernelProfiler/", mRecyclerView, activity);
                    break;
                case 10:
                    Utils.launchUrl("https://github.com/SmartPack/KernelProfiler/wiki", mRecyclerView, activity);
                    break;
                case 11:
                    Utils.launchUrl("https://play.google.com/store/apps/dev?id=5836199813143882901", mRecyclerView, activity);
                    break;
                case 12:
                    Utils.launchUrl("https://github.com/SmartPack/KernelProfiler/issues/new", mRecyclerView, activity);
                    break;
                case 13:
                    Intent about = new Intent(this, AboutActivity.class);
                    startActivity(about);
                    break;
                case 14:
                    new MaterialAlertDialogBuilder(activity)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(getString(R.string.support_developer))
                            .setMessage(getString(R.string.support_developer_message))
                            .setNeutralButton(getString(R.string.cancel), (dialog1, id1) -> {
                            })
                            .setPositiveButton(getString(R.string.donation_app), (dialogInterface, i) -> {
                                Utils.launchUrl(
                                        "https://play.google.com/store/apps/details?id=com.smartpack.donate", mRecyclerView, activity);
                            })
                            .show();
                    break;
                case 15:
                    if (!Utils.languageDefault(activity)) {
                        Utils.setDefaultLanguage(activity);
                        Utils.restartApp(activity);
                    }
                    break;
                case 16:
                    if (!Utils.getBoolean("use_en", false, activity)) {
                        Utils.setDefaultLanguage(activity);
                        Utils.saveBoolean("use_en", true, activity);
                        Utils.restartApp(activity);
                    }
                    break;
                case 17:
                    if (!Utils.getBoolean("use_pt", false, activity)) {
                        Utils.setDefaultLanguage(activity);
                        Utils.saveBoolean("use_pt", true, activity);
                        Utils.restartApp(activity);
                    }
                    break;
                case 18:
                    if (!Utils.getBoolean("use_el", false, activity)) {
                        Utils.setDefaultLanguage(activity);
                        Utils.saveBoolean("use_el", true, activity);
                        Utils.restartApp(activity);
                    }
                    break;
                case 19:
                    if (!Utils.getBoolean("light_theme", false, activity)) {
                        Utils.saveBoolean("dark_theme", false, activity);
                        Utils.saveBoolean("light_theme", true, activity);
                        Utils.saveBoolean("theme_auto", false, activity);
                        Utils.restartApp(activity);
                    }
                    break;
                case 20:
                    if (!Utils.getBoolean("theme_auto", true, activity)) {
                        Utils.saveBoolean("dark_theme", false, activity);
                        Utils.saveBoolean("light_theme", false, activity);
                        Utils.saveBoolean("theme_auto", true, activity);
                        Utils.restartApp(activity);
                    }
                    break;
                case 21:
                    if (!Utils.getBoolean("use_ko", false, activity)) {
                        Utils.setDefaultLanguage(activity);
                        Utils.saveBoolean("use_ko", true, activity);
                        Utils.restartApp(activity);
                    }
                    break;
                case 22:
                    if (!Utils.getBoolean("use_in", false, activity)) {
                        Utils.setDefaultLanguage(activity);
                        Utils.saveBoolean("use_in", true, activity);
                        Utils.restartApp(activity);
                    }
                    break;
            }
            return false;
        });
        popupMenu.show();
    }

    @Override
    public void onBackPressed() {
        if (Utils.rootAccess()) {
            if (mExit) {
                mExit = false;
                super.onBackPressed();
            } else {
                Utils.snackbar(mRecyclerView, getString(R.string.press_back));
                mExit = true;
                mHandler.postDelayed(() -> mExit = false, 2000);
            }
        } else {
            super.onBackPressed();
        }
    }

}