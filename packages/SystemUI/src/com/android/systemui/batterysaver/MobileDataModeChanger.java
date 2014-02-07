/*
 * Copyright (C) 2014 The OmniRom Project
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
package com.android.systemui.batterysaver;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.TrafficStats;

import com.android.systemui.R;

public class MobileDataModeChanger extends ModeChanger {

    private ConnectivityManager mCM;
    private long mTrafficBytes;
    private final long TRAFFIC_BYTES_THRESHOLD = 5 * 1024 * 1024; // 5mb

    public MobileDataModeChanger(Context context) {
        super(context);
    }

    public void setServices(ConnectivityManager cm) {
        mCM = cm;
        setWasEnabled(isStateEnabled());
    }

    public void updateTraffic() {
        mTrafficBytes = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();
    }

    @Override
    public boolean isDelayChanges() {
        final long traffic = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();
        return ((traffic - mTrafficBytes) > TRAFFIC_BYTES_THRESHOLD);
    }

    @Override
    public boolean isStateEnabled() {
        if (!isSupported()) return false;
        return (mCM != null) ? mCM.getMobileDataEnabled() : false;
    };

    @Override
    public boolean isSupported() {
        boolean isSupport = (mCM != null) ? mCM.isNetworkSupported(ConnectivityManager.TYPE_MOBILE) : false;
        return isModeEnabled() && isSupport;
    };

    @Override
    public int getMode() {
        return 0;
    }

    @Override
    public void stateNormal() {
        mCM.setMobileDataEnabled(true);
    }

    @Override
    public void stateSaving() {
        mCM.setMobileDataEnabled(false);
    }

    @Override
    public boolean checkModes() {
        if (isDelayChanges()) {
            // download/upload progress detected, delay changing mode
            changeMode(true);
            return false;
        }
        return true;
    };

    @Override
    public void setModes() {
        super.setModes();
    }
}
