package com.smartpack.kernelprofiler

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar
import com.smartpack.kernelprofiler.fragments.KPFragment
import com.smartpack.kernelprofiler.utils.*
import com.smartpack.kernelprofiler.utils.root.RootUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.rv_foreground_view.*

/**
 * Created by Lennoard on May 29, 2020
 */
class MainActivityKt : AppCompatActivity() {
    private var mDeveloperMode = false
    private var mExit = false
    private val mHandler : Handler by lazy { Handler() }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize App Theme & Google Ads
        Utils.initializeAppTheme(this)
        Utils.initializeGoogleAds(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        back.setOnClickListener {
            closeForeground()
        }
        cancelButton.setOnClickListener {
            closeForeground()
        }

        if (Utils.isNotDonated(this)) {
            adView.loadAd(AdRequest.Builder().build())
        }

        val supported = KP.supported() && KP.geCustomImage(this) != null
        customBanner.setImageDrawable(if (supported) {
            KP.geCustomImage(this)
        } else {
            resources.getDrawable(R.mipmap.ic_launcher_round, null)
        })

        customTitle.text = if (KP.supported() && KP.getCustomTitle() != null) {
            KP.getCustomTitle()
        } else {
            getString(R.string.app_name)
        }

        customDescription.text = if (KP.supported() && KP.getCustomDescription() != null) KP.getCustomDescription() else getString(R.string.app_name_summary)
        settingsMenu.setOnClickListener {
            if (Utils.mForegroundActive) return@setOnClickListener
            settingsMenu()
        }

        if (!RootUtils.rootAccess()) {
            unsupportedText.text = getString(R.string.no_root)
            helpImage.setImageDrawable(getColoredIcon(R.drawable.ic_help))
            viewPager.snackbar(getString(R.string.no_root_message))
            helpImage.setOnClickListener {
                launchURL("https://www.google.com/search?site=&source=hp&q=android+rooting+magisk")
            }
            return
        }

        if (!KP.supported()) {
            unsupportedText.text = getString(R.string.unsupported)
            helpImage.setImageDrawable(getColoredIcon(R.drawable.ic_help))
            viewPager.snackbar(getString(R.string.unsupported_message), Snackbar.LENGTH_INDEFINITE)
            helpImage.setOnClickListener {
                AlertDialog.Builder(this)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(getString(R.string.unsupported))
                        .setMessage("""
                            ${getString(R.string.unsupported_summary)} 
                            ${getString(R.string.unsupported_message)}
                            ${getString(R.string.unsupported_help_message)}""")
                        .setPositiveButton(getString(R.string.cancel)) { _, _ -> }
                        .show()
            }
            return
        }
        val adapter = PagerAdapter(supportFragmentManager)
        adapter.AddFragment(KPFragment(), getString(R.string.app_name))
        if (KP.supported() && KP.getDeveloper() != null) {
            var copyright = KP.getDeveloper()
            if (!copyright.startsWith("©")) {
                copyright = "©$copyright"
            }
            copyrightText.text = copyright
        } else {
            copyrightText.setText(R.string.copyright)
        }

        viewPager.adapter = adapter
    }

    private fun settingsMenu() {
        val popupMenu = PopupMenu(this, settingsMenu)
        val menu = popupMenu.menu
        menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.dark_theme)).setCheckable(true).isChecked = Prefs.getBoolean("dark_theme", true, this)
        if (KP.supported() && KP.isCustomSettingsAvailable()) {
            val kernel = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.kernel_about))
            if (KP.getSupport() != null && KP.getSupport().isNotEmpty()) {
                kernel.add(Menu.NONE, 2, Menu.NONE, getString(R.string.support))
            }
            if (KP.getDonation() != null && KP.getDonation().isNotEmpty()) {
                kernel.add(Menu.NONE, 3, Menu.NONE, getString(R.string.donations))
            }
        }
        if (mDeveloperMode) {
            // Tools menu
            menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.tools_developer)).apply {
                add(Menu.NONE, 7, Menu.NONE, getString(R.string.create_profile))
                add(Menu.NONE, 8, Menu.NONE, getString(R.string.create_config))
                if (KP.supported()) {
                    add(Menu.NONE, 12, Menu.NONE, getString(R.string.edit_config))
                }
            }
        } else {
            menu.add(Menu.NONE, 13, Menu.NONE, getString(R.string.developer_mode)).setCheckable(true).isChecked = mDeveloperMode
        }

        // App menu
        menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.app_about)).apply {
            add(Menu.NONE, 4, Menu.NONE, getString(R.string.support))
            add(Menu.NONE, 9, Menu.NONE, getString(R.string.source_code))
            add(Menu.NONE, 10, Menu.NONE, getString(R.string.report_issue))
            add(Menu.NONE, 5, Menu.NONE, getString(R.string.change_logs))
            if (Utils.isNotDonated(this@MainActivityKt)) {
                add(Menu.NONE, 11, Menu.NONE, getString(R.string.donations))
            }
            add(Menu.NONE, 6, Menu.NONE, getString(R.string.about))
        }

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                0 -> { /*todo: ? */ }
                1 -> {
                    if (Prefs.getBoolean("dark_theme", true, this)) {
                        Prefs.saveBoolean("dark_theme", false, this)
                    } else {
                        Prefs.saveBoolean("dark_theme", true, this)
                    }
                    restartApp()
                }
                2 -> launchURL(KP.getSupport())
                3 -> launchURL(KP.getDonation())
                4 -> launchURL("https://t.me/smartpack_kmanager")
                5 -> AlertDialog.Builder(this)
                        .setTitle(getString(R.string.change_logs))
                        .setMessage(Utils.getChangeLogs(this))
                        .setPositiveButton(getString(R.string.cancel)) { _, _ -> }
                        .show()
                6 -> aboutDialogue(this)

                7 -> dialogEditText("") {
                    var text = it
                    if (text.isEmpty()) {
                        viewPager.snackbar(getString(R.string.name_empty))
                        return@dialogEditText
                    }
                    if (!text.endsWith(".sh")) {
                        text += ".sh"
                    }
                    if (text.contains(" ")) {
                        text = text.replace(" ", "_")
                    }

                    Intent(this, CreateProfileActivity::class.java).apply {
                        putExtra(CreateProfileActivity.TITLE, text)
                        startActivity(this)
                    }
                }

                8 -> startActivity(Intent(this, CreateConfigActivity::class.java))
                9 -> launchURL("https://github.com/SmartPack/KernelProfiler/")
                10 -> launchURL("https://github.com/SmartPack/KernelProfiler/issues/new")
                11 -> {
                    AlertDialog.Builder(this)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(getString(R.string.support_developer))
                            .setMessage(getString(R.string.support_developer_message))
                            .setNeutralButton(getString(R.string.cancel)) { _, _ -> }
                            .setPositiveButton(getString(R.string.donation_app)) { _, _ ->
                                Utils.launchUrl("https://play.google.com/store/apps/details?id=com.smartpack.donate", this)
                            }
                            .show()
                }
                12 -> startActivity(Intent(this, EditConfigActivity::class.java))
                13 -> {
                    mDeveloperMode = true
                    viewPager.snackbar(getString(R.string.developer_mode_message))
                }
            }
            false
        }
        popupMenu.show()
    }

    private fun launchURL(url: String) {
        if (Utils.isNetworkUnavailable(this)) {
            viewPager.snackbar(getString(R.string.no_internet))
        } else {
            Utils.launchUrl(url, this)
        }
    }

    private fun restartApp() {
        Intent(this, MainActivityKt::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(this)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun aboutDialogue(context: Context) {
        cardTitle.setText(R.string.about)
        appTitle.text = "${context.getString(R.string.app_name)} ${BuildConfig.VERSION_NAME}"
        credits.text = context.getString(R.string.credits_summary)

        arrayOf (
                cardTitle, back, appImage, appTitle, aboutApp,
                creditsTitle, credits, cancelButton, foregroundCard
        ).forEach {
            it.show()
        }
        Utils.mForegroundActive = true
    }

    private fun closeForeground() {
        arrayOf (
                cardTitle, back, appImage, appTitle, aboutApp,
                creditsTitle, credits, cancelButton, foregroundCard
        ).forEach {
            it.goAway()
        }
        Utils.mForegroundActive = false
    }

    override fun onBackPressed() {
        if (Utils.mForegroundActive) {
            closeForeground()
        } else if (RootUtils.rootAccess()) {
            if (mExit) {
                mExit = false
                super.onBackPressed()
            } else {
                viewPager.snackbar(getString(R.string.press_back))
                mExit = true
                mHandler.postDelayed({ mExit = false }, 2000)
            }
        } else {
            super.onBackPressed()
        }
    }
}