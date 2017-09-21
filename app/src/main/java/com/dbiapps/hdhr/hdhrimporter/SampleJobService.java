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

import android.media.tv.TvContract;
import android.net.Uri;

import com.dbiapps.hdhr.hdhrimporter.rich.RichFeedUtil;
import com.google.android.exoplayer.util.Util;
import com.google.android.media.tv.companionlibrary.model.Channel;
import com.google.android.media.tv.companionlibrary.model.InternalProviderData;
import com.google.android.media.tv.companionlibrary.model.Program;
import com.google.android.media.tv.companionlibrary.EpgSyncJobService;
import com.google.android.media.tv.companionlibrary.XmlTvParser;

import java.util.ArrayList;
import java.util.List;

/**
 * EpgSyncJobService that periodically runs to update channels and programs.
 */
public class SampleJobService extends EpgSyncJobService {
    private String MPEG_DASH_CHANNEL_NAME = "MPEG_DASH";
    private String MPEG_DASH_CHANNEL_NUMBER = "3";
    private String MPEG_DASH_CHANNEL_LOGO
            = "https://storage.googleapis.com/android-tv/images/mpeg_dash.png";
    private int MPEG_DASH_ORIGINAL_NETWORK_ID = 101;
    private String TEARS_OF_STEEL_TITLE = "Tears of Steel";
    private String TEARS_OF_STEEL_DESCRIPTION = "Monsters invade a small town in this sci-fi flick";
    private String TEARS_OF_STEEL_ART
            = "https://storage.googleapis.com/gtv-videos-bucket/sample/images/tears.jpg";
    private String TEARS_OF_STEEL_SOURCE
            = "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd";
    private static final long TEARS_OF_STEEL_START_TIME_MS = 0;
    private static final long TEARS_OF_STEEL_DURATION_MS = 734 * 1000;
    @Override
    public List<Channel> getChannels() {
        // Add channels through an XMLTV file
        XmlTvParser.TvListing listings = RichFeedUtil.getRichTvListings(this);
        List<Channel> channelList = new ArrayList<>(listings.getChannels());

        // Add a channel programmatically
        InternalProviderData internalProviderData = new InternalProviderData();
        internalProviderData.setRepeatable(true);
        Channel channelTears = new Channel.Builder()
                .setDisplayName(MPEG_DASH_CHANNEL_NAME)
                .setDisplayNumber(MPEG_DASH_CHANNEL_NUMBER)
                .setChannelLogo(MPEG_DASH_CHANNEL_LOGO)
                .setOriginalNetworkId(MPEG_DASH_ORIGINAL_NETWORK_ID)
                .setInternalProviderData(internalProviderData)
                .build();
        channelList.add(channelTears);
        return channelList;
    }

    @Override
    public List<Program> getProgramsForChannel(Uri channelUri, Channel channel, long startMs,
            long endMs) {
        if (!channel.getDisplayName().equals(MPEG_DASH_CHANNEL_NAME)) {
            // Is an XMLTV Channel
            XmlTvParser.TvListing listings = RichFeedUtil.getRichTvListings(getApplicationContext());
            return listings.getPrograms(channel);
        } else {

            // Programatically add channel
            List<Program> programsTears = new ArrayList<>();
            InternalProviderData internalProviderData = new InternalProviderData();
            internalProviderData.setVideoType(Util.TYPE_DASH);
            internalProviderData.setVideoUrl(TEARS_OF_STEEL_SOURCE);
            programsTears.add(new Program.Builder()
                    .setTitle(TEARS_OF_STEEL_TITLE)
                    .setStartTimeUtcMillis(TEARS_OF_STEEL_START_TIME_MS)
                    .setEndTimeUtcMillis(TEARS_OF_STEEL_START_TIME_MS + TEARS_OF_STEEL_DURATION_MS)
                    .setDescription(TEARS_OF_STEEL_DESCRIPTION)
                    .setCanonicalGenres(new String[] {TvContract.Programs.Genres.TECH_SCIENCE,
                            TvContract.Programs.Genres.MOVIES})
                    .setPosterArtUri(TEARS_OF_STEEL_ART)
                    .setThumbnailUri(TEARS_OF_STEEL_ART)
                    .setInternalProviderData(internalProviderData)
                    .build());
            return programsTears;
        }
    }
}
