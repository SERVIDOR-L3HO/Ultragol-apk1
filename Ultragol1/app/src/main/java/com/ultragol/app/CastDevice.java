package com.ultragol.app;

import androidx.mediarouter.media.MediaRouter;

/**
 * Represents a castable device: Chromecast, DLNA Smart TV, or AirPlay device.
 */
public class CastDevice {

    public enum Type { CHROMECAST, DLNA, AIRPLAY }

    private String id;
    private String name;
    private Type   type;
    private String ipAddress;
    private int    port;
    private String dlnaControlUrl; // AVTransport SOAP endpoint for DLNA devices
    private boolean inUse;

    // Only populated for Chromecast routes
    private MediaRouter.RouteInfo routeInfo;

    public CastDevice() {}

    public CastDevice(String id, String name, Type type) {
        this.id   = id;
        this.name = name;
        this.type = type;
    }

    // ── Getters / setters ────────────────────────────────────────────────────

    public String getId()                              { return id; }
    public void   setId(String id)                    { this.id = id; }

    public String getName()                            { return name; }
    public void   setName(String name)                 { this.name = name; }

    public Type   getType()                            { return type; }
    public void   setType(Type type)                   { this.type = type; }

    public String getIpAddress()                       { return ipAddress; }
    public void   setIpAddress(String ip)              { this.ipAddress = ip; }

    public int    getPort()                            { return port; }
    public void   setPort(int port)                    { this.port = port; }

    public String getDlnaControlUrl()                  { return dlnaControlUrl; }
    public void   setDlnaControlUrl(String url)        { this.dlnaControlUrl = url; }

    public boolean isInUse()                           { return inUse; }
    public void    setInUse(boolean inUse)             { this.inUse = inUse; }

    public MediaRouter.RouteInfo getRouteInfo()        { return routeInfo; }
    public void setRouteInfo(MediaRouter.RouteInfo r)  { this.routeInfo = r; }

    public String getTypeLabel() {
        switch (type) {
            case CHROMECAST: return "Chromecast";
            case DLNA:       return "DLNA";
            case AIRPLAY:    return "AirPlay";
            default:         return "";
        }
    }
}
