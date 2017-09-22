/*
 * Copyright 2016 The Android Open Source Project
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

import android.net.Uri;
import android.util.Log;

import com.dbiapps.hdhr.hdhrimporter.guideconversion.JsonHdhrTvParser;
import com.dbiapps.hdhr.hdhrimporter.rich.RichFeedUtil;
import com.google.android.media.tv.companionlibrary.model.Channel;
import com.google.android.media.tv.companionlibrary.model.Program;
import com.google.android.media.tv.companionlibrary.EpgSyncJobService;

import java.util.ArrayList;
import java.util.List;

/**
 * EpgSyncJobService that periodically runs to update channels and programs.
 */
public class SampleJobService extends EpgSyncJobService {

    @Override
    public List<Channel> getChannels() {
        Log.d(this.getClass().getName(), "getChannels");
        // Add channels through an JSONEPG file
        JsonHdhrTvParser.TvListing listings = RichFeedUtil.getRichTvListings(this);
        List<Channel> channelList = new ArrayList<>(listings.getChannels());
        return channelList;
    }

    @Override
    public List<Program> getProgramsForChannel(Uri channelUri, Channel channel, long startMs,
            long endMs) {
            // Is an XMLTV Channel
            JsonHdhrTvParser.TvListing listings = RichFeedUtil.getRichTvListings(getApplicationContext());
            return listings.getPrograms(channel);
    }
}
