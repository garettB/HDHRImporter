package com.dbiapps.hdhr.hdhrimporter.guideconversion;

import com.google.android.media.tv.companionlibrary.model.Channel;
import com.google.android.media.tv.companionlibrary.model.Program;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by garett on 2017-09-20.
 */

public class EpgJsonToObjects {

    List<Channel> channels;
    List<Program> programs;

    static void parseEpg(JSONArray epgJsonArray) {
        for (int i = 0; i < epgJsonArray.length(); ++i) {

        }
    }
}
