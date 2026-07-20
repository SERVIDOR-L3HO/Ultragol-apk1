package com.ultragol.app.models;

import java.io.Serializable;

/** Represents a video item from the RedTube API. */
public class AdultVideoItem implements Serializable {

    private final String videoId;
    private final String title;
    private final String thumbUrl;
    private final String duration;
    private final String views;
    private final String rating;      // e.g. "85" (percentage)
    private final String embedUrl;
    private final String publishDate;
    private final String tags;

    public AdultVideoItem(String videoId, String title, String thumbUrl,
                          String duration, String views, String rating,
                          String embedUrl, String publishDate, String tags) {
        this.videoId     = videoId;
        this.title       = title;
        this.thumbUrl    = thumbUrl;
        this.duration    = duration;
        this.views       = views;
        this.rating      = rating;
        this.embedUrl    = embedUrl;
        this.publishDate = publishDate;
        this.tags        = tags;
    }

    public String getVideoId()     { return videoId; }
    public String getTitle()       { return title; }
    public String getThumbUrl()    { return thumbUrl; }
    public String getDuration()    { return duration; }
    public String getViews()       { return formatViews(views); }
    public String getRating()      { return rating; }
    public String getEmbedUrl()    { return embedUrl; }
    public String getPublishDate() { return publishDate; }
    public String getTags()        { return tags; }

    private static String formatViews(String raw) {
        try {
            long v = Long.parseLong(raw.replaceAll("[^0-9]", ""));
            if (v >= 1_000_000) return String.format("%.1fM vistas", v / 1_000_000.0);
            if (v >= 1_000)     return String.format("%.0fK vistas", v / 1_000.0);
            return v + " vistas";
        } catch (Exception e) {
            return raw + " vistas";
        }
    }
}
