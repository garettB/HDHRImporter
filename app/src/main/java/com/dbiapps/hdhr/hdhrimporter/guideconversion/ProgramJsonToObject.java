package com.dbiapps.hdhr.hdhrimporter.guideconversion;

import android.util.Pair;

import com.google.android.media.tv.companionlibrary.model.Channel;
import com.google.android.media.tv.companionlibrary.model.InternalProviderData;
import com.google.android.media.tv.companionlibrary.model.Program;
import com.google.android.media.tv.companionlibrary.utils.TvContractUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * "StartTime": 1505863800,
 * "EndTime": 1505865600,
 * "Title": "Daily Blast Live",
 * "OriginalAirdate": 1505779200,
 * "ImageURL": "http://img.hdhomerun.com/tmsimg/assets/p14400309_b_h3_aa.jpg",
 * "SeriesID": "C14400309ENHRMM",
 * "Filter": [
 * "News"
 * ]
 * <p>
 * "EpisodeNumber": "S12E23",
 * <p>
 * "EpisodeTitle": "Live Show Finale",
 * "Synopsis": "The top 10 acts compete one final time.",
 */


public class ProgramJsonToObject {

    public static String START_TIME = "StartTime";
    public static String END_TIME = "EndTime";
    public static String TITLE = "Title";
    public static String ORIGINAL_AIR_DATE = "OriginalAirdate";
    public static String IMAGE_URL = "ImageURL";
    public static String SERIES_ID = "SeriesID";
    public static String GENRES_ARRAY = "Filter";
    public static String EPISODE_NUMBER = "EpisodeNumber";
    public static String EPISODE_TITLE = "EpisodeTitle";
    public static String SYNOPSIS = "Synopsis";


    static Program convertFromJson(Channel channel, JSONObject programJsonObject, String tunerUrl) throws JSONException {
        long startTime = programJsonObject.getLong(START_TIME);
        long endTime = programJsonObject.getLong(END_TIME);
        String title = programJsonObject.getString(TITLE);
        String imageUri = programJsonObject.getString(IMAGE_URL);

        List<String> genres = new ArrayList<>();
        JSONArray genresArray = programJsonObject.getJSONArray(GENRES_ARRAY);
        if (genresArray != null) {
            for (int i = 0; i < genresArray.length(); ++i) {
                genres.add(genresArray.getString(i));
            }
        }

        int seasonNumber = -1;
        int episodeNumber = -1;
        String episode = programJsonObject.optString(EPISODE_NUMBER);
        if (!episode.isEmpty()){
            Pair<String, String> episodeString = parseEpisodeAndSeason(episode);
            seasonNumber = Integer.parseInt(episodeString.first);
            episodeNumber = Integer.parseInt(episodeString.second);
        }

        String episodeTitle = programJsonObject.optString(EPISODE_TITLE);

        String episodeSynopsis = programJsonObject.optString(SYNOPSIS);


        String videoUrl = tunerUrl + ":5004/" + channel.getId();

        InternalProviderData internalProviderData = new InternalProviderData();
        internalProviderData.setVideoType(TvContractUtils.SOURCE_TYPE_HLS);
        internalProviderData.setVideoUrl(videoUrl);

        Program program = new Program.Builder(channel)
                .setStartTimeUtcMillis(startTime)
                .setEndTimeUtcMillis(endTime)
                .setTitle(title)
                .setThumbnailUri(imageUri)
                .setPosterArtUri(imageUri)
                .setBroadcastGenres(genres.toArray(new String[genres.size()]))
                .setEpisodeNumber(episodeNumber)
                .setSeasonNumber(seasonNumber)
                .setEpisodeTitle(episodeTitle)
                .setDescription(episodeSynopsis.substring(0, Math.min(episodeSynopsis.length(), 255)))
                .setLongDescription(episodeSynopsis)
                .setInternalProviderData(internalProviderData)
                .setSearchable(true)
                .setRecordingProhibited(false)
                .build();

        return program;
    }

    private static Pair<String, String> parseEpisodeAndSeason(String episode) {
        int indexOfE = episode.indexOf('E');

        String seasonNumber = episode.substring(1, indexOfE);
        String episodeNumber = episode.substring(indexOfE + 1);

        return new Pair<>(seasonNumber, episodeNumber);

    }

}
