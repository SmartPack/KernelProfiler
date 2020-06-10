package com.smartpack.kernelprofiler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.smartpack.kernelprofiler.fragments.KPFragment;
import com.smartpack.kernelprofiler.utils.CreateConfigActivity;
import com.smartpack.kernelprofiler.utils.CreateProfileActivity;
import com.smartpack.kernelprofiler.utils.EditConfigActivity;
import com.smartpack.kernelprofiler.utils.KP;
import com.smartpack.kernelprofiler.utils.PagerAdapter;
import com.smartpack.kernelprofiler.utils.Prefs;
import com.smartpack.kernelprofiler.utils.Utils;
import com.smartpack.kernelprofiler.utils.root.RootUtils;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 22, 2020
 */

public class MainActivity extends AppCompatActivity {

    private AppCompatImageButton mSettings;
    private boolean mDeveloperMode = false;
    private boolean mExit;
    private Handler mHandler = new Handler();
    private ViewPager mViewPager;

    private AppCompatImageButton mBack;
    private AppCompatImageView mAppIcon;
    private AppCompatTextView mCardTitle;
    private AppCompatTextView mAppName;
    private AppCompatTextView mAboutApp;
    private AppCompatTextView mHowTo;
    private AppCompatTextView mHowToSummary;
    private AppCompatTextView mChangeLog;
    private AppCompatTextView mCreditsTitle;
    private AppCompatTextView mCredits;
    private AppCompatTextView mCancel;
    private CardView mForegroundCard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Initialize App Theme & Google Ads
        Utils.initializeAppTheme(this);
        Utils.initializeGoogleAds(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mForegroundCard = findViewById(R.id.foreground_card);
        mBack = findViewById(R.id.back);
        mAppIcon = findViewById(R.id.app_image);
        mCardTitle = findViewById(R.id.card_title);
        mAppName = findViewById(R.id.app_title);
        mAboutApp = findViewById(R.id.about_app);
        mHowTo = findViewById(R.id.how_to);
        mHowToSummary = findViewById(R.id.how_to_summary);
        mCreditsTitle = findViewById(R.id.credits_title);
        mCredits = findViewById(R.id.credits);
        mChangeLog = findViewById(R.id.change_log);
        mCancel = findViewById(R.id.cancel_button);
        mBack.setOnClickListener(v -> {
            closeForeground();
        });
        mCancel.setOnClickListener(v -> {
            closeForeground();
        });

        if (Utils.isNotDonated(this)) {
            AdView mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mAdView.loadAd(adRequest);
        }

        mSettings = findViewById(R.id.settings_menu);
        mViewPager = findViewById(R.id.viewPagerID);
        AppCompatTextView textView = findViewById(R.id.unsupported_Text);
        AppCompatImageView helpIcon = findViewById(R.id.help_Image);
        AppCompatTextView copyRightText = findViewById(R.id.copyright_Text);
        AppCompatTextView customTitle = findViewById(R.id.customTitle);
        AppCompatTextView customDescription = findViewById(R.id.customDescription);
        customTitle.setText(KP.supported() && KP.getCustomTitle() != null ? KP.getCustomTitle() : getString(R.string.app_name));
        customDescription.setText(KP.supported() && KP.getCustomDescription() != null ? KP.getCustomDescription() : getString(R.string.app_name_summary));
        mSettings.setOnClickListener(v -> {
            if (Utils.mForegroundActive) return;
            settingsMenu();
        });

        if (!RootUtils.rootAccess()) {
            textView.setText(getString(R.string.no_root));
            helpIcon.setImageDrawable(Utils.getColoredIcon(R.drawable.ic_help, this));
            Utils.snackbar(mViewPager, getString(R.string.no_root_message));
            helpIcon.setOnClickListener(v -> {
                launchURL("https://www.google.com/search?site=&source=hp&q=android+rooting+magisk");
            });
            return;
        }

        if (!KP.supported()) {
            textView.setText(getString(R.string.unsupported));
            helpIcon.setImageDrawable(Utils.getColoredIcon(R.drawable.ic_help, this));
            Utils.snackbarIndenite(mViewPager, getString(R.string.unsupported_message));
            helpIcon.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(getString(R.string.unsupported))
                        .setMessage(getString(R.string.unsupported_summary) + " " + getString(R.string.unsupported_message) +
                                "\n\n" + getString(R.string.unsupported_help_message))
                        .setPositiveButton(getString(R.string.cancel), (dialog1, id1) -> {
                        })
                        .show();
            });
            return;
        }

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new KPFragment(), getString(R.string.app_name));

        if (KP.supported() && KP.getDeveloper() != null) {
            String copyright = KP.getDeveloper();
            if (!copyright.startsWith("©")) {
                copyright = "©" + copyright;
            }
            copyRightText.setText(copyright);
        } else {
            copyRightText.setText(R.string.copyright);
        }

        mViewPager.setAdapter(adapter);
    }

    private void settingsMenu() {
        PopupMenu popupMenu = new PopupMenu(this, mSettings);
        Menu menu = popupMenu.getMenu();
        menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.dark_theme)).setCheckable(true).setChecked(
                Prefs.getBoolean("dark_theme", true, this));
        if (KP.supported() && KP.isCustomSettingsAvailable()) {
            SubMenu kernel = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.kernel_about));
            if (KP.getSupport() != null && !KP.getSupport().isEmpty()) {
                kernel.add(Menu.NONE, 2, Menu.NONE, getString(R.string.support));
            }
            if (KP.getDonation() != null && !KP.getDonation().isEmpty()) {
                kernel.add(Menu.NONE, 3, Menu.NONE, getString(R.string.donations));
            }
        }
        if (mDeveloperMode) {
            SubMenu tools = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.tools_developer));
            tools.add(Menu.NONE, 7, Menu.NONE, getString(R.string.create_profile));
            tools.add(Menu.NONE, 8, Menu.NONE, getString(R.string.create_config));
            if (KP.supported()) {
                tools.add(Menu.NONE, 12, Menu.NONE, getString(R.string.edit_config));
            }
        } else {
            menu.add(Menu.NONE, 13, Menu.NONE, getString(R.string.developer_mode)).setCheckable(true)
                    .setChecked(mDeveloperMode);
        }
        SubMenu app = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.app_about));
        app.add(Menu.NONE, 15, Menu.NONE, getString(R.string.share));
        app.add(Menu.NONE, 4, Menu.NONE, getString(R.string.support));
        app.add(Menu.NONE, 9, Menu.NONE, getString(R.string.source_code));
        app.add(Menu.NONE, 16, Menu.NONE, getString(R.string.documentation));
        app.add(Menu.NONE, 14, Menu.NONE, getString(R.string.more_apps));
        app.add(Menu.NONE, 10, Menu.NONE, getString(R.string.report_issue));
        app.add(Menu.NONE, 5, Menu.NONE, getString(R.string.change_logs));
        if (Utils.isNotDonated(this)) {
            app.add(Menu.NONE, 11, Menu.NONE, getString(R.string.donations));
        }
        app.add(Menu.NONE, 6, Menu.NONE, getString(R.string.about));
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 0:
                    break;
                case 1:
                    if (Prefs.getBoolean("dark_theme", true, this)) {
                        Prefs.saveBoolean("dark_theme", false, this);
                    } else {
                        Prefs.saveBoolean("dark_theme", true, this);
                    }
                    restartApp();
                    break;
                case 2:
                    launchURL(KP.getSupport());
                    break;
                case 3:
                    launchURL(KP.getDonation());
                    break;
                case 4:
                    launchURL("https://t.me/smartpack_kmanager");
                    break;
                case 5:
                    changeLogDialogue(this);
                    break;
                case 6:
                    aboutDialogue(this);
                    break;
                case 7:
                    Intent createProfile = new Intent(this, CreateProfileActivity.class);
                    startActivity(createProfile);
                    break;
                case 8:
                    Intent createConfig = new Intent(this, CreateConfigActivity.class);
                    startActivity(createConfig);
                    break;
                case 9:
                    launchURL("https://github.com/SmartPack/KernelProfiler/");
                    break;
                case 10:
                    launchURL("https://github.com/SmartPack/KernelProfiler/issues/new");
                    break;
                case 11:
                    new AlertDialog.Builder(this)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(getString(R.string.support_developer))
                            .setMessage(getString(R.string.support_developer_message))
                            .setNeutralButton(getString(R.string.cancel), (dialog1, id1) -> {
                            })
                            .setPositiveButton(getString(R.string.donation_app), (dialogInterface, i) -> {
                                Utils.launchUrl("https://play.google.com/store/apps/details?id=com.smartpack.donate", this);
                            })
                            .show();
                    break;
                case 12:
                    Intent editConfig = new Intent(this, EditConfigActivity.class);
                    startActivity(editConfig);
                    break;
                case 13:
                    mDeveloperMode = true;
                    Utils.snackbar(mViewPager, getString(R.string.developer_mode_message));
                    break;
                case 14:
                    Intent ps = new Intent(Intent.ACTION_VIEW);
                    ps.setData(Uri.parse("https://play.google.com/store/apps/dev?id=5836199813143882901"));
                    startActivity(ps);
                    break;
                case 15:
                    Intent share = new Intent();
                    share.setAction(Intent.ACTION_SEND);
                    share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);
                    share.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message));
                    share.setType("text/plain");
                    Intent shareIntent = Intent.createChooser(share, null);
                    startActivity(shareIntent);
                    break;
                case 16:
                    launchURL("https://github.com/SmartPack/KernelProfiler/wiki");
                    break;
            }
            return false;
        });
        popupMenu.show();
    }

    private void launchURL(String url) {
        if (Utils.isNetworkUnavailable(this)) {
            Utils.snackbar(mViewPager, getString(R.string.no_internet));
        } else {
            Utils.launchUrl(url, this);
        }
    }

    private void restartApp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    private void aboutDialogue(Context context) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        mCardTitle.setText(R.string.about);
        mAppName.setText(context.getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);
        mCredits.setText(context.getString(R.string.credits_summary));
        mCardTitle.setVisibility(View.VISIBLE);
        mBack.setVisibility(View.VISIBLE);
        mAppIcon.setVisibility(View.VISIBLE);
        mAppName.setVisibility(View.VISIBLE);
        mAboutApp.setVisibility(View.VISIBLE);
        mHowTo.setVisibility(View.VISIBLE);
        mHowToSummary.setVisibility(View.VISIBLE);
        mCreditsTitle.setVisibility(View.VISIBLE);
        mCredits.setVisibility(View.VISIBLE);
        mCancel.setVisibility(View.VISIBLE);
        Utils.mForegroundActive = true;
        mForegroundCard.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    private void changeLogDialogue(Context context) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        mCardTitle.setText(R.string.change_logs);
        mAppName.setText(context.getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);
        mChangeLog.setText(Utils.getChangeLogs(this));
        mCardTitle.setVisibility(View.VISIBLE);
        mBack.setVisibility(View.VISIBLE);
        mAppIcon.setVisibility(View.VISIBLE);
        mAppName.setVisibility(View.VISIBLE);
        mChangeLog.setVisibility(View.VISIBLE);
        mCancel.setVisibility(View.VISIBLE);
        Utils.mForegroundActive = true;
        mForegroundCard.setVisibility(View.VISIBLE);
    }

    private void closeForeground() {
        mCardTitle.setVisibility(View.GONE);
        mBack.setVisibility(View.GONE);
        mAppIcon.setVisibility(View.GONE);
        mAppName.setVisibility(View.GONE);
        mAboutApp.setVisibility(View.GONE);
        mHowTo.setVisibility(View.GONE);
        mHowToSummary.setVisibility(View.GONE);
        mCreditsTitle.setVisibility(View.GONE);
        mCredits.setVisibility(View.GONE);
        mChangeLog.setVisibility(View.GONE);
        mCancel.setVisibility(View.GONE);
        mForegroundCard.setVisibility(View.GONE);
        Utils.mForegroundActive = false;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void onBackPressed() {
        if (Utils.mForegroundActive) {
            closeForeground();
        } else if (RootUtils.rootAccess()) {
            if (mExit) {
                mExit = false;
                super.onBackPressed();
            } else {
                Utils.snackbar(mViewPager, getString(R.string.press_back));
                mExit = true;
                mHandler.postDelayed(() -> mExit = false, 2000);
            }
        } else {
            super.onBackPressed();
        }
    }

}