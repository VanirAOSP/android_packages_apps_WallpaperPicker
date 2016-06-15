/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cyanogenmod.wallpaperpicker;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities to discover and interact with partner customizations. There can
 * only be one set of customizations on a device, and it must be bundled with
 * the system.
 */
public class Partner {

    static final String TAG = "Launcher.Partner";

    /** Marker action used to discover partner */
    private static final String
            ACTION_PARTNER_CUSTOMIZATION = "com.android.launcher3.action.PARTNER_CUSTOMIZATION";

    public static final String RES_FOLDER = "partner_folder";
    public static final String RES_WALLPAPERS = "partner_wallpapers";

    public static final String RES_DEFAULT_WALLPAPER_HIDDEN = "default_wallpapper_hidden";
    public static final String RES_SYSTEM_WALLPAPER_DIR = "system_wallpaper_directory";

    private static boolean sSearched = false;
    private static List<Partner> sPartners;

    static {
        sPartners = new ArrayList<Partner>();
    }

    /**
     * Find and return first partner details, or {@code null} if none exists.
     */
    public static synchronized Partner get(PackageManager pm) {
        getAllPartners(pm);
        return sPartners.size() > 0 ? sPartners.get(0) : null;
    }

    /**
     * Find and return all partner details, or {@code null} if none exists.
     */
    public static synchronized List<Partner> getAllPartners(PackageManager pm) {
        if (!sSearched) {
            List<Pair<String, Resources>> apkInfos =
                    Utilities.findSystemApks(ACTION_PARTNER_CUSTOMIZATION, pm);
            for (Pair<String, Resources> apkInfo : apkInfos) {
                sPartners.add(new Partner(apkInfo.first, apkInfo.second));
            }
            sSearched = true;
        }
        return sPartners;
    }

    private final String mPackageName;
    private final Resources mResources;

    private Partner(String packageName, Resources res) {
        mPackageName = packageName;
        mResources = res;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public Resources getResources() {
        return mResources;
    }

    public boolean hasFolder() {
        int folder = getResources().getIdentifier(Partner.RES_FOLDER,
                "xml", getPackageName());
        return folder != 0;
    }

    public boolean hideDefaultWallpaper() {
        int resId = getResources().getIdentifier(RES_DEFAULT_WALLPAPER_HIDDEN, "bool",
                getPackageName());
        return resId != 0 && getResources().getBoolean(resId);
    }

    public File getWallpaperDirectory() {
        int resId = getResources().getIdentifier(RES_SYSTEM_WALLPAPER_DIR, "string",
                getPackageName());
        return (resId != 0) ? new File(getResources().getString(resId)) : null;
    }
}
