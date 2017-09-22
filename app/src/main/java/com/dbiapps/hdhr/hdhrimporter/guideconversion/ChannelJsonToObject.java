package com.dbiapps.hdhr.hdhrimporter.guideconversion;

import android.media.tv.TvContract;

import com.google.android.media.tv.companionlibrary.model.Channel;
import com.google.android.media.tv.companionlibrary.model.Program;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by garett on 2017-09-20.
 */

public class ChannelJsonToObject {

    static String CHANNEL_NUMBER = "GuideNumber";
    static String CHANNEL_NAME = "GuideName";
    static String AFFILIATE_NAME = "Affiliate";
    static String CHANNEL_ICON_URL = "ImageURL";
    static String PROGRAM_ARRAY = "Guide";

    static Channel convertFromJson(JSONObject channelJsonObject) throws JSONException {

        String channelNumber = channelJsonObject.getString(CHANNEL_NUMBER);
        String channelName = channelJsonObject.getString(CHANNEL_NAME);
        String affiliateName = channelJsonObject.optString(AFFILIATE_NAME);
        String channelIconUrl = channelJsonObject.optString(CHANNEL_ICON_URL);

        Channel channel = new Channel.Builder()
                .setDisplayNumber(channelNumber)
                .setDisplayName(channelName)
                .setNetworkAffiliation(affiliateName)
                .setChannelLogo(channelIconUrl)
                .setServiceType(TvContract.Channels.SERVICE_TYPE_AUDIO_VIDEO)
                .build();
        return channel;
    }

    static JSONArray getAssociatedPrograms(JSONObject channelJsonObject){
        return channelJsonObject.optJSONArray(PROGRAM_ARRAY);
    }
}
