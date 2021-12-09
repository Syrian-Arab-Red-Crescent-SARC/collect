package org.odk.collect.android.application.initialization.upgrade

import org.odk.collect.android.BuildConfig
import org.odk.collect.android.utilities.InstallDetector
import org.odk.collect.android.utilities.LaunchState
import org.odk.collect.android.utilities.VersionCodeLaunchState
import org.odk.collect.shared.Settings

class AppUpgrader(
    private val metaSettings: Settings,
    private val launchState: LaunchState,
    private val upgrades: List<Upgrade>
) {

    constructor(metaSettings: Settings, installDetector: InstallDetector, upgrades: List<Upgrade>) : this(
        metaSettings,
        VersionCodeLaunchState(metaSettings, BuildConfig.VERSION_CODE, installDetector),
        upgrades
    )

    fun upgradeIfNeeded() {
        if (launchState.isUpgradedFirstLaunch()) {
            upgrades.forEach {
                val key = it.key()

                if (key == null) {
                    it.run()
                } else if (!metaSettings.getBoolean(key)) {
                    it.run()
                    metaSettings.save(key, true)
                }
            }
        } else {
            upgrades.forEach {
                it.key()?.let { key ->
                    metaSettings.save(key, true)
                }
            }
        }

        launchState.appLaunched()
    }
}

interface Upgrade {

    fun key(): String?

    fun run()
}
