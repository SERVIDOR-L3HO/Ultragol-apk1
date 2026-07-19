package com.ultragol.app.models;

import java.io.Serializable;

public class TvChannel implements Serializable {

    public static final String CAT_TODOS           = "Todos";
    public static final String CAT_NOTICIAS        = "Noticias";
    public static final String CAT_DEPORTES        = "Deportes";
    public static final String CAT_ENTRETENIMIENTO = "Entretenimiento";
    public static final String CAT_MUSICA          = "Música";
    public static final String CAT_DOCUMENTALES    = "Documentales";
    public static final String CAT_INFANTIL        = "Infantil";
    public static final String CAT_NEGOCIOS        = "Negocios";
    public static final String CAT_CINE            = "Cine";
    public static final String CAT_CIENCIA         = "Ciencia";

    public String name;
    public String url;
    public String logo;
    public String category;
    public String country;
    public boolean isAlive = true; // optimistic default; set false if check fails

    public TvChannel(String name, String url, String logo, String category, String country) {
        this.name     = name;
        this.url      = url;
        this.logo     = logo;
        this.category = category;
        this.country  = country;
    }

    /** Maps raw M3U group-title to a localized category. */
    public static String mapGroup(String raw) {
        if (raw == null) return CAT_ENTRETENIMIENTO;
        switch (raw.toLowerCase().trim()) {
            case "news":          return CAT_NOTICIAS;
            case "noticias":      return CAT_NOTICIAS;
            case "sports":        return CAT_DEPORTES;
            case "deportes":      return CAT_DEPORTES;
            case "entertainment": return CAT_ENTRETENIMIENTO;
            case "entretenimiento": return CAT_ENTRETENIMIENTO;
            case "music":         return CAT_MUSICA;
            case "música":        return CAT_MUSICA;
            case "kids":          return CAT_INFANTIL;
            case "infantil":      return CAT_INFANTIL;
            case "documentary":   return CAT_DOCUMENTALES;
            case "documentales":  return CAT_DOCUMENTALES;
            case "business":      return CAT_NEGOCIOS;
            case "negocios":      return CAT_NEGOCIOS;
            case "movies":        return CAT_CINE;
            case "cine":          return CAT_CINE;
            case "science":       return CAT_CIENCIA;
            case "ciencia":       return CAT_CIENCIA;
            default:              return CAT_ENTRETENIMIENTO;
        }
    }

    /** Returns a flag emoji for common ISO-3166 country codes. */
    public static String flagOf(String code) {
        if (code == null || code.length() < 2) return "🌐";
        code = code.toUpperCase().trim();
        int first  = 0x1F1E6 + (code.charAt(0) - 'A');
        int second = 0x1F1E6 + (code.charAt(1) - 'A');
        return new String(Character.toChars(first)) + new String(Character.toChars(second));
    }
}
