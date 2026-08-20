package com.ultragol.app.models;

import java.util.ArrayList;
import java.util.List;

/** A live sports IPTV channel returned by /canales/deportes. */
public class SportsChannel {
    public String id = "";
    public String name = "";
    public String country = "";
    public String countryCode = "";
    public String flag = "";
    public String logo = "";
    public final List<String> streams = new ArrayList<>();

    public String primaryStream() {
        return streams.isEmpty() ? "" : streams.get(0);
    }
}