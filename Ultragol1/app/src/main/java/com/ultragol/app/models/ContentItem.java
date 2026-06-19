package com.ultragol.app.models;

import java.io.Serializable;

public class ContentItem implements Serializable {
    public static final int TYPE_MOVIE  = 0;
    public static final int TYPE_SERIES = 1;
    public static final int TYPE_ANIME  = 2;
    public static final int TYPE_DORAMA = 3;
    public static final int TYPE_SPORT  = 4;
    public static final int TYPE_TV     = 5;

    private String title, genre, year, rating, posterUrl, backdropUrl, overview;
    private int contentType, tmdbId;
    private boolean isNew, isLive;
    private String badge, streamUrl;

    public ContentItem(String title, String genre, String year, String rating,
                       String posterUrl, String overview, int contentType,
                       boolean isNew, boolean isLive) {
        this.title = title; this.genre = genre; this.year = year;
        this.rating = rating; this.posterUrl = posterUrl != null ? posterUrl : "";
        this.overview = overview != null ? overview : "";
        this.contentType = contentType; this.isNew = isNew; this.isLive = isLive;
        this.backdropUrl = ""; this.streamUrl = ""; this.tmdbId = 0;
        if (isLive) badge = "EN VIVO";
        else if (isNew) badge = "NUEVO";
        else badge = "HD";
    }

    public void setTmdbId(int v)      { tmdbId = v; }
    public int  getTmdbId()           { return tmdbId; }
    public void setBackdropUrl(String v) { backdropUrl = v != null ? v : ""; }
    public String getBackdropUrl()    { return backdropUrl; }
    public void setStreamUrl(String v)  { streamUrl = v != null ? v : ""; }

    public String getStreamUrl() {
        if (!streamUrl.isEmpty()) return streamUrl;
        if (tmdbId == 0) return "https://unlimplay.com/";
        String p = "?sub=es&lang=es&audio=es&autoplay=1";
        return contentType == TYPE_MOVIE
            ? "https://unlimplay.com/play/embed/movie/" + tmdbId + p
            : "https://unlimplay.com/play/embed/tv/" + tmdbId + "/1/1" + p;
    }

    public String getTitle()        { return title; }
    public String getGenre()        { return genre; }
    public String getYear()         { return year; }
    public String getRating()       { return rating; }
    public String getPosterUrl()    { return posterUrl; }
    public String getOverview()     { return overview; }
    public int    getContentType()  { return contentType; }
    public boolean isNew()          { return isNew; }
    public boolean isLive()         { return isLive; }
    public String getBadge()        { return badge; }
    public String getGenreYear()    { return genre + " \u2022 " + year; }
    public String getRatingDisplay(){ return "\u2605 " + rating; }
}
