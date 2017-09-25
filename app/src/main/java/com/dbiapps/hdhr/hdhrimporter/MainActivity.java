/*
 * Copyright 2015 The Android Open Source Project
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

package com.dbiapps.hdhr.hdhrimporter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.dbiapps.hdhr.hdhrimporter.R;
import com.dbiapps.hdhr.hdhrimporter.guideconversion.JsonHdhrTvParser;
import com.dbiapps.hdhr.hdhrimporter.rich.RichFeedUtil;

import java.util.Date;

/**
 * MainActivity class that loads {@link MainFragment}.
 */
public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.d("JSON Thing", "" + hello());
        JsonHdhrTvParser.TvListing tvListing = RichFeedUtil.getRichTvListings(getApplicationContext());
        if (tvListing != null) {
            Log.d("Channels", tvListing.getChannels().size() + "");
            Log.d("Programs", tvListing.getAllPrograms().size() + "");
        }
        Log.d("CURRENT_TIME", new Date().getTime() + "");
    }

    public native String hello();

    static {
        System.loadLibrary("hdhomerun");
    }
}
