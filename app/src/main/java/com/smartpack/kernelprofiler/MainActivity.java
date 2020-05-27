package com.smartpack.kernelprofiler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import com.smartpack.kernelprofiler.fragments.KPFragment;
import com.smartpack.kernelprofiler.utils.CreateConfigActivity;
import com.smartpack.kernelprofiler.utils.CreateProfileActivity;
import com.smartpack.kernelprofiler.utils.KP;
import com.smartpack.kernelprofiler.utils.PagerAdapter;
import com.smartpack.kernelprofiler.utils.Prefs;
import com.smartpack.kernelprofiler.utils.Utils;
import com.smartpack.kernelprofiler.utils.ViewUtils;
import com.smartpack.kernelprofiler.utils.root.RootUtils;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 22, 2020
 */

public class MainActivity extends AppCompatActivity {

    private AppCompatImageButton mSettings;
    private boolean mExit;
    private Handler mHandler = new Handler();
    private ViewPager mViewPager;

    private AppCompatImageButton mBack;
    private AppCompatImageView mAppIcon;
    private AppCompatTextView mCardTitle;
    private AppCompatTextView mAppName;
    private AppCompatTextView mAboutApp;
    private AppCompatTextView mCreditsTitle;
    private AppCompatTextView mCredits;
    private AppCompatTextView mCancel;
    private CardView mForegroundCard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Initialize App Theme
        Utils.initializeAppTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mForegroundCard = findViewById(R.id.foreground_card);
        mBack = findViewById(R.id.back);
        mAppIcon = findViewById(R.id.app_image);
        mCardTitle = findViewById(R.id.card_title);
        mAppName = findViewById(R.id.app_title);
        mAboutApp = findViewById(R.id.about_app);
        mCreditsTitle = findViewById(R.id.credits_title);
        mCredits = findViewById(R.id.credits);
        mCancel = findViewById(R.id.cancel_button);
        mBack.setOnClickListener(v -> {
            closeForeground();
        });
        mCancel.setOnClickListener(v -> {
            closeForeground();
        });

        mSettings = findViewById(R.id.settings_menu);
        mViewPager = findViewById(R.id.viewPagerID);
        AppCompatTextView textView = findViewById(R.id.unsupported_Text);
        AppCompatImageView helpIcon = findViewById(R.id.help_Image);
        AppCompatTextView copyRightText = findViewById(R.id.copyright_Text);
        AppCompatImageView customBanner = findViewById(R.id.customBanner);
        AppCompatTextView customTitle = findViewById(R.id.customTitle);
        AppCompatTextView customDescription = findViewById(R.id.customDescription);
        boolean supported = KP.supported() && KP.geCustomImage(this) != null;
        customBanner.setImageDrawable(supported ? KP.geCustomImage(this)
                : getResources().getDrawable(R.mipmap.ic_launcher_round));
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
        if (KP.supported()) {
            SubMenu kernel = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.kernel_about));
            if (KP.getSupport() != null) {
                kernel.add(Menu.NONE, 2, Menu.NONE, getString(R.string.support));
            }
            if (KP.getDonation() != null) {
                kernel.add(Menu.NONE, 3, Menu.NONE, getString(R.string.donations));
            }
        }
        SubMenu tools = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.tools_developer));
        tools.add(Menu.NONE, 7, Menu.NONE, getString(R.string.create_profile));
        tools.add(Menu.NONE, 8, Menu.NONE, getString(R.string.create_config));
        SubMenu app = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.app_about));
        app.add(Menu.NONE, 4, Menu.NONE, getString(R.string.support));
        app.add(Menu.NONE, 9, Menu.NONE, getString(R.string.source_code));
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
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.change_logs))
                            .setMessage(Utils.getChangeLogs(this))
                            .setPositiveButton(getString(R.string.cancel), (dialogInterface, i) -> {
                            })
                            .show();
                    break;
                case 6:
                    aboutDialogue(this);
                    break;
                case 7:
                    ViewUtils.dialogEditText("",
                            (dialogInterface, i) -> {
                            }, text -> {
                                if (text.isEmpty()) {
                                    Utils.snackbar(mViewPager, getString(R.string.name_empty));
                                    return;
                                }
                                if (!text.endsWith(".sh")) {
                                    text += ".sh";
                                }
                                if (text.contains(" ")) {
                                    text = text.replace(" ", "_");
                                }
                                Intent intent = new Intent(this, CreateProfileActivity.class);
                                intent.putExtra(CreateProfileActivity.TITLE, text);
                                startActivity(intent);
                            }, this).setOnDismissListener(dialogInterface -> {
                    }).show();
                    break;
                case 8:
                    Intent intent = new Intent(this, CreateConfigActivity.class);
                    startActivity(intent);
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
        mCardTitle.setText(R.string.about);
        mAppName.setText(context.getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);
        mCredits.setText(context.getString(R.string.credits_summary));
        mCardTitle.setVisibility(View.VISIBLE);
        mBack.setVisibility(View.VISIBLE);
        mAppIcon.setVisibility(View.VISIBLE);
        mAppName.setVisibility(View.VISIBLE);
        mAboutApp.setVisibility(View.VISIBLE);
        mCreditsTitle.setVisibility(View.VISIBLE);
        mCredits.setVisibility(View.VISIBLE);
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
        mCreditsTitle.setVisibility(View.GONE);
        mCredits.setVisibility(View.GONE);
        mCancel.setVisibility(View.GONE);
        mForegroundCard.setVisibility(View.GONE);
        Utils.mForegroundActive = false;
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