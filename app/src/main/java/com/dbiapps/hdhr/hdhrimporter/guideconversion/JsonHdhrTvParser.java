package com.dbiapps.hdhr.hdhrimporter.guideconversion;
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

import android.support.annotation.NonNull;

import com.google.android.media.tv.companionlibrary.model.Channel;
import com.google.android.media.tv.companionlibrary.model.Program;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.google.android.media.tv.companionlibrary.XmlTvParser.*;

/**
 * JSONTV document parser which conforms to http://wiki.jsontv.org/index.php/Main_Page
 * </p>
 * Please note that jsontv.dtd are extended to be align with Android TV Input Framework and
 * contain static video contents:
 * </p>
 * <!ELEMENT channel ([elements in jsontv.dtd], display-number, app-link) > <!ATTLIST channel
 * [attributes in jsontv.dtd] repeat-programs CDATA #IMPLIED > <!ATTLIST programme [attributes in
 * jsontv.dtd] video-src CDATA #IMPLIED video-type CDATA #IMPLIED > <!ELEMENT app-link (icon) >
 * <!ATTLIST app-link text CDATA #IMPLIED color CDATA #IMPLIED poster-uri CDATA #IMPLIED intent-uri
 * CDATA #IMPLIED > <!ELEMENT advertisement > <!ATTLIST start stop type >
 * </p>
 * display-number : The channel number that is displayed to the user.
 * </p>
 * repeat-programs : If "true", the programs in the json document are scheduled sequentially in a
 * loop. Program and advertisement start and end times will be shifted as necessary for looping
 * content. This is introduced to simulate a live channel in this sample.
 * </p>
 * video-src : The video URL for the given program. This can be omitted if the json will be used only
 * for the program guide update.
 * </p>
 * video-type : The video type. Should be one of "HTTP_PROGRESSIVE", "HLS", or "MPEG-DASH". This can
 * be omitted if the json will be used only for the program guide update.
 * </p>
 * app-link : The app-link allows channel input sources to provide activity links from their live
 * channel programming to another activity. This enables content providers to increase user
 * engagement by offering the viewer other content or actions.
 * </p>
 * &emsp;text : The text of the app link template for this channel.
 * </p>
 * &emsp;color : The accent color of the app link template for this channel. This is primarily
 * used for the background color of the text box in the template.
 * </p>
 * &emsp;poster-uri : The URI for the poster art used as the background of the app link template
 * for this channel.
 * </p>
 * &emsp;intent-uri : The intent URI of the app link for this channel. It should be created using
 * Intent.toUri(int) with Intent.URI_INTENT_SCHEME. (see https://developer.android.com/reference/android/media/tv/TvContract.Channels.html#COLUMN_APP_LINK_INTENT_URI)
 * The intent is launched when the user clicks the corresponding app link for the current channel.
 * </p>
 * advertisement : Representing an advertisement that can play on a channel or during a program.
 * </p>
 * &emsp;type : The type of advertisement. Requires "VAST".
 * </p>
 * &emsp;start : The start time of the advertisement.
 * </p>
 * &emsp;stop : The stop time of the advertisement.
 * </p>
 * &emsp;request-url : This element should contain the URL for the advertisement.
 * </p>
 */
public class JsonHdhrTvParser {

    private JsonHdhrTvParser() {
    }

    /**
     * Reads an InputStream and parses the data to identify channels and programs
     *
     * @param epgJsonArray The JSON Array formatted EPG returned from your HDHR
     * @return A TvListing containing your channels and programs
     */

    public static TvListing parse(@NonNull InputStream inputStream) throws JsonTvParseException {
        StringBuilder total;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            total = new StringBuilder(inputStream.available());
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                total.append(line).append('\n');
            }
        } catch (IOException e) {
            return null;
        }

        JSONArray parsedEpgFromInputStream;
        try {
            parsedEpgFromInputStream = new JSONArray(total);
        } catch (JSONException e) {
            throw new JsonTvParseException("Failed to parse EPG: " + e.getMessage());
        }

        return parse(parsedEpgFromInputStream);
    }

    /**
     * Reads an InputStream and parses the data to identify channels and programs
     *
     * @param epgJsonArray The JSON Array formatted EPG returned from your HDHR
     * @return A TvListing containing your channels and programs
     */

    public static TvListing parse(@NonNull JSONArray epgJsonArray) throws JsonTvParseException {
        EpgJsonToObjects epgJsonToObjects = new EpgJsonToObjects();
        try {
            epgJsonToObjects.parseEpg(epgJsonArray);
        } catch (JSONException e) {
            throw new JsonTvParseException("Failed to parse EPG: " + e.getMessage());
        }

        return new TvListing(epgJsonToObjects.getChannels(), epgJsonToObjects.getPrograms());
    }

    /**
     * An exception that indicates the provided JSONTV file is invalid or improperly formatted.
     */
    public static class JsonTvParseException extends Exception {
        JsonTvParseException(String msg) {
            super(msg);
        }
    }

    /**
     * Contains a list of channels and corresponding programs that have been generated from parsing
     * an XML TV file.
     */
    public static class TvListing {
        private List<Channel> mChannels;
        private List<Program> mPrograms;
        private HashMap<Integer, List<Program>> mProgramMap;

        private TvListing(List<Channel> channels, List<Program> programs) {
            this.mChannels = channels;
            this.mPrograms = new ArrayList<>(programs);
            // Place programs into the epg map
            mProgramMap = new HashMap<>();
            for (Channel channel : channels) {
                List<Program> programsForChannel = new ArrayList<>();
                Iterator<Program> programIterator = programs.iterator();
                while (programIterator.hasNext()) {
                    Program program = programIterator.next();
                    if (program.getChannelId() == channel.getOriginalNetworkId()) {
                        programsForChannel.add(new Program.Builder(program)
                                .setChannelId(channel.getId())
                                .build());
                        programIterator.remove();
                    }
                }
                mProgramMap.put(channel.getOriginalNetworkId(), programsForChannel);
            }
        }

        /**
         * @return All channels found by the XmlTvParser.
         */
        public List<Channel> getChannels() {
            return mChannels;
        }

        /**
         * @return All programs found by the XmlTvParser.
         */
        public List<Program> getAllPrograms() {
            return mPrograms;
        }

        /**
         * Returns a list of programs found by the XmlTvParser for a given channel.
         *
         * @param channel The channel to obtain programs for.
         * @return A list of programs that belong to that channel.
         */
        public List<Program> getPrograms(Channel channel) {
            return mProgramMap.get(channel.getOriginalNetworkId());
        }
    }


}
