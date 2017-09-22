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

package com.dbiapps.hdhr.hdhrimporter.rich;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.dbiapps.hdhr.hdhrimporter.guideconversion.JsonHdhrTvParser;
import com.dbiapps.hdhr.hdhrimporter.guideconversion.JsonHdhrTvParser.JsonTvParseException;
import com.dbiapps.hdhr.hdhrimporter.guideconversion.JsonHdhrTvParser.TvListing;

import java.io.IOException;
import java.io.InputStream;

import com.dbiapps.hdhr.hdhrimporter.R;

/**
 * Static helper methods for fetching the channel feed.
 */
public class RichFeedUtil {

    private RichFeedUtil() {
    }

    public static TvListing getRichTvListings(Context context) {

        Log.d(TvListing.class.getName(), "getRichTvListings");

        Uri catalogUri = Uri.parse("android.resource://" + context.getPackageName() + "/"
                + R.raw.sampleepg);

        TvListing tvListing;
        try (InputStream inputStream = context.getContentResolver().openInputStream(catalogUri)) {
            tvListing = JsonHdhrTvParser.parse(inputStream);
        } catch (JsonTvParseException | IOException e) {
            e.printStackTrace();
            return null;
        }
        return tvListing;
    }

}
