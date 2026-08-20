package com.ultragol.app.models;

/** A single highlight/replay video (goles, resúmenes, mejores momentos). */
public class SportsHighlight {
    public String title     = "";
    public String thumbnail = "";
    public String url       = "";
    public String category  = ""; // "Mejores momentos" | "Resumen" | "Gol"

    public SportsHighlight(String title, String thumbnail, String url, String category) {
        this.title     = title;
        this.thumbnail = thumbnail;
        this.url       = url;
        this.category  = category;
    }
}
