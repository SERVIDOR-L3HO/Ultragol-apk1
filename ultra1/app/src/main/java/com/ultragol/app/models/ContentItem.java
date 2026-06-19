package com.ultragol.app.models;

public class ContentItem {
    public static final int TYPE_MOVIE  = 0;
    public static final int TYPE_SERIES = 1;
    public static final int TYPE_ANIME  = 2;
    public static final int TYPE_DORAMA = 3;
    public static final int TYPE_SPORT  = 4;
    public static final int TYPE_TV     = 5;

    private String title;
    private String genre;
    private String year;
    private String rating;
    private String posterUrl;
    private String overview;
    private int    contentType;
    private boolean isNew;
    private boolean isLive;
    private String badge;
    private int    tmdbId;
    private String streamUrl;

    public ContentItem(String title, String genre, String year, String rating,
                       String posterUrl, String overview,
                       int contentType, boolean isNew, boolean isLive) {
        this.title       = title;
        this.genre       = genre;
        this.year        = year;
        this.rating      = rating;
        this.posterUrl   = posterUrl != null ? posterUrl : "";
        this.overview    = overview  != null ? overview  : "";
        this.contentType = contentType;
        this.isNew       = isNew;
        this.isLive      = isLive;
        this.tmdbId      = 0;
        this.streamUrl   = "";
        if (isLive)     badge = "EN VIVO";
        else if (isNew) badge = "NUEVO";
        else            badge = "HD";
    }

    public void setTmdbId(int tmdbId)       { this.tmdbId    = tmdbId; }
    public int  getTmdbId()                 { return tmdbId; }

    public void setStreamUrl(String url)    { this.streamUrl = url != null ? url : ""; }

    public String getStreamUrl() {
        if (streamUrl != null && !streamUrl.isEmpty()) return streamUrl;
        if (tmdbId == 0) return "";
        String p = "?sub=es&lang=es&audio=es&muted=0&autoplay=1";
        if (contentType == TYPE_MOVIE)
            return "https://unlimplay.com/play/embed/movie/" + tmdbId + p;
        return "https://unlimplay.com/play/embed/tv/" + tmdbId + "/1/1" + p;
    }

    public String getTitle()         { return title; }
    public String getGenre()         { return genre; }
    public String getYear()          { return year; }
    public String getRating()        { return rating; }
    public String getPosterUrl()     { return posterUrl; }
    public String getOverview()      { return overview; }
    public int    getContentType()   { return contentType; }
    public boolean isNew()           { return isNew; }
    public boolean isLive()          { return isLive; }
    public String getBadge()         { return badge; }
    public String getGenreYear()     { return genre + " \u2022 " + year; }
    public String getRatingDisplay() { return "\u2605 " + rating; }
    public String getEmoji()         { return ""; }
}
