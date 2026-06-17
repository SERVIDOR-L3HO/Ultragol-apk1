package com.streamnova.app.models;

public class ContentItem {
    public static final int TYPE_MOVIE = 0;
    public static final int TYPE_SERIES = 1;
    public static final int TYPE_ANIME = 2;
    public static final int TYPE_DORAMA = 3;
    public static final int TYPE_SPORT = 4;
    public static final int TYPE_TV = 5;

    private String title;
    private String genre;
    private String year;
    private String rating;
    private String emoji;
    private int contentType;
    private boolean isNew;
    private boolean isLive;
    private String badge;

    public ContentItem(String title, String genre, String year, String rating,
                       String emoji, int contentType, boolean isNew, boolean isLive) {
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.rating = rating;
        this.emoji = emoji;
        this.contentType = contentType;
        this.isNew = isNew;
        this.isLive = isLive;
        if (isLive) badge = "EN VIVO";
        else if (isNew) badge = "NUEVO";
        else badge = "HD";
    }

    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public String getYear() { return year; }
    public String getRating() { return rating; }
    public String getEmoji() { return emoji; }
    public int getContentType() { return contentType; }
    public boolean isNew() { return isNew; }
    public boolean isLive() { return isLive; }
    public String getBadge() { return badge; }
    public String getGenreYear() { return genre + " • " + year; }
    public String getRatingDisplay() { return "⭐ " + rating; }
}
