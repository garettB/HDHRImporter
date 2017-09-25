package com.dbiapps.hdhr.hdhrimporter.guideconversion;

import android.media.tv.TvContract;
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


    static Program convertFromJson(Channel channel, JSONObject programJsonObject, String tunerUrl) throws JSONException { //TODO pass tuneUrl from a more global config file
        long startTime = programJsonObject.getLong(START_TIME);
        long endTime = programJsonObject.getLong(END_TIME);
        String title = programJsonObject.getString(TITLE);
        String imageUri = programJsonObject.getString(IMAGE_URL);

        List<String> genres = new ArrayList<>();
        JSONArray genresArray = programJsonObject.optJSONArray(GENRES_ARRAY);
        if (genresArray != null) {
            genres.clear();
            for (int i = 0; i < genresArray.length(); ++i) {
                String convertedGenre = genreMap(genresArray.getString(i));
                genres.add(convertedGenre);
            }
        }

        int seasonNumber = -1;
        int episodeNumber = -1;
        String episode = programJsonObject.optString(EPISODE_NUMBER);
        if (!episode.isEmpty()) {
            Pair<String, String> episodeString = parseEpisodeAndSeason(episode);
            seasonNumber = Integer.parseInt(episodeString.first);
            episodeNumber = Integer.parseInt(episodeString.second);
        }

        String episodeTitle = programJsonObject.optString(EPISODE_TITLE);

        String episodeSynopsis = programJsonObject.optString(SYNOPSIS);


        String videoUrl = "http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8";//tunerUrl + ":5004/auto/v/" + channel.getDisplayNumber();

        InternalProviderData internalProviderData = new InternalProviderData();
        internalProviderData.setVideoType(TvContractUtils.SOURCE_TYPE_HLS);
        internalProviderData.setVideoUrl(videoUrl);
        internalProviderData.setRepeatable(false);

        Program.Builder programBuilder = new Program.Builder(channel);

        programBuilder.setStartTimeUtcMillis((startTime - 1506096000 + 1506349465) * 1000) //TODO Remove test time conversion, just use startTime * 1000
                .setEndTimeUtcMillis((endTime - 1506096000 + 1506349465) * 1000) //TODO Remove test time conversion, just use endTime * 1000
                .setTitle(title)
                .setChannelId(channel.getOriginalNetworkId())
                .setInternalProviderData(internalProviderData)
                .setSearchable(true)
                .setRecordingProhibited(false);

        if (!genres.isEmpty()) {
            programBuilder.setBroadcastGenres(genres.toArray(new String[genres.size()]));
        }

        if (isStringFieldValid(imageUri)) {
            programBuilder.setThumbnailUri(imageUri)
                    .setPosterArtUri(imageUri);
        }
        ;

        if (episodeNumber > 0 && seasonNumber > 0) {
            programBuilder.setEpisodeNumber(episodeNumber)
                    .setSeasonNumber(seasonNumber);
        }

        if (isStringFieldValid(episodeTitle)) {
            programBuilder.setEpisodeTitle(episodeTitle);
        }

        if (isStringFieldValid(episodeSynopsis)) {
            programBuilder.setDescription(episodeSynopsis.substring(0, Math.min(episodeSynopsis.length(), 255)))
                    .setLongDescription(episodeSynopsis);
        }

        return programBuilder.build();
    }

    private static Pair<String, String> parseEpisodeAndSeason(String episode) {
        int indexOfE = episode.indexOf('E');

        String seasonNumber = episode.substring(1, indexOfE);
        String episodeNumber = episode.substring(indexOfE + 1);

        return new Pair<>(seasonNumber, episodeNumber);

    }

    private static boolean isStringFieldValid(String fieldValue) {
        return fieldValue != null && !fieldValue.isEmpty();
    }

    /**
     * Most genres served up by the HDHR map nicely to those defined by Android TV, but a few (e.g.
     * Kids) do not, so this does the conversion to help categorize values properly.
     * @param epgGenre - The Genre defined by the HDHR EPG
     * @return The genre expected by Android TV, or the epgGenre if we don't know the proper map
     */
    private static String genreMap(String epgGenre) {
        String convertedGenre = epgGenre;

        switch (epgGenre) {
            case "News":
                convertedGenre = TvContract.Programs.Genres.NEWS;
                break;
            case "Kids":
                convertedGenre = TvContract.Programs.Genres.FAMILY_KIDS;
                break;
            case "Movies":
                convertedGenre = TvContract.Programs.Genres.MOVIES;
                break;
            case "Comedy":
                convertedGenre = TvContract.Programs.Genres.COMEDY;
                break;
            case "Drama":
                convertedGenre = TvContract.Programs.Genres.DRAMA;
                break;
            case "Sports":
                convertedGenre = TvContract.Programs.Genres.SPORTS;
                break;
            case "Food":
                convertedGenre = TvContract.Programs.Genres.LIFE_STYLE;
                break;
        }

        return convertedGenre;
    }

}
