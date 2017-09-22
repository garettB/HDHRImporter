package com.dbiapps.hdhr.hdhrimporter.guideconversion;

import com.google.android.media.tv.companionlibrary.model.Channel;
import com.google.android.media.tv.companionlibrary.model.Program;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by garett on 2017-09-20.
 */

public class EpgJsonToObjects {

    private List<Channel> channels = new ArrayList<>();
    private List<Program> programs = new ArrayList<>();

    void parseEpg(JSONArray epgJsonArray) throws JSONException {
        channels.clear();
        programs.clear();
        for (int i = 0; i < epgJsonArray.length(); ++i) {
            JSONObject channelJsonObject = epgJsonArray.getJSONObject(i);
            Channel newChannel = ChannelJsonToObject.convertFromJson(channelJsonObject);
            channels.add(newChannel);

            JSONArray programJsonArray = ChannelJsonToObject.getAssociatedPrograms(channelJsonObject);
            for (int p = 0 ; p < programJsonArray.length() ; ++p){
                Program newProgram = ProgramJsonToObject.convertFromJson(newChannel, programJsonArray.getJSONObject(p), "");
                programs.add(newProgram);
            }

        }
    }

    public List<Channel> getChannels(){
        return channels;
    }

    public List<Program> getPrograms(){
        return programs;
    }
}
