package com.ultragol.app.models;

public class Channel {
    private String id, name, country, flag, logo, playerUrl, category;

    public Channel(String id, String name, String country, String flag,
                   String logo, String playerUrl, String category) {
        this.id = id; this.name = name; this.country = country;
        this.flag = flag; this.logo = logo != null ? logo : "";
        this.playerUrl = playerUrl; this.category = category;
    }

    public String getId()        { return id; }
    public String getName()      { return name; }
    public String getCountry()   { return country; }
    public String getFlag()      { return flag; }
    public String getLogo()      { return logo; }
    public String getPlayerUrl() { return playerUrl; }
    public String getCategory()  { return category; }
    public String getDisplayName() { return flag + " " + name; }
}
