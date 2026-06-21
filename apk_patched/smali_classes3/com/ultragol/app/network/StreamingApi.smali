.class public Lcom/ultragol/app/network/StreamingApi;
.super Ljava/lang/Object;
.source "StreamingApi.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/ultragol/app/network/StreamingApi$Server;,
        Lcom/ultragol/app/network/StreamingApi$ServerData;
    }
.end annotation


# static fields
.field private static final BASE:Ljava/lang/String; = "https://ultragol-api-3--maricarmen43549.replit.app"


# direct methods
.method public constructor <init>()V
    .locals 0

    .line 13
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method private static cap(Ljava/lang/String;)Ljava/lang/String;
    .locals 2
    .param p0, "s"    # Ljava/lang/String;

    .line 67
    if-eqz p0, :cond_1

    invoke-virtual {p0}, Ljava/lang/String;->isEmpty()Z

    move-result v0

    if-eqz v0, :cond_0

    goto :goto_0

    :cond_0
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const/4 v1, 0x0

    invoke-virtual {p0, v1}, Ljava/lang/String;->charAt(I)C

    move-result v1

    invoke-static {v1}, Ljava/lang/Character;->toUpperCase(C)C

    move-result v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    move-result-object v0

    const/4 v1, 0x1

    invoke-virtual {p0, v1}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    goto :goto_1

    :cond_1
    :goto_0
    move-object v0, p0

    :goto_1
    return-object v0
.end method

.method private static fetch(Ljava/lang/String;)Ljava/lang/String;
    .locals 7
    .param p0, "path"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .line 31
    new-instance v0, Ljava/net/URL;

    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "https://ultragol-api-3--maricarmen43549.replit.app"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-direct {v0, v1}, Ljava/net/URL;-><init>(Ljava/lang/String;)V

    .line 32
    .local v0, "u":Ljava/net/URL;
    invoke-virtual {v0}, Ljava/net/URL;->openConnection()Ljava/net/URLConnection;

    move-result-object v1

    check-cast v1, Ljava/net/HttpURLConnection;

    .line 33
    .local v1, "c":Ljava/net/HttpURLConnection;
    const-string v2, "GET"

    invoke-virtual {v1, v2}, Ljava/net/HttpURLConnection;->setRequestMethod(Ljava/lang/String;)V

    .line 34
    const-string v2, "Accept"

    const-string v3, "application/json"

    invoke-virtual {v1, v2, v3}, Ljava/net/HttpURLConnection;->setRequestProperty(Ljava/lang/String;Ljava/lang/String;)V

    .line 35
    const/16 v2, 0x3a98

    invoke-virtual {v1, v2}, Ljava/net/HttpURLConnection;->setConnectTimeout(I)V

    const/16 v2, 0x4e20

    invoke-virtual {v1, v2}, Ljava/net/HttpURLConnection;->setReadTimeout(I)V

    .line 36
    invoke-virtual {v1}, Ljava/net/HttpURLConnection;->getResponseCode()I

    move-result v2

    .line 37
    .local v2, "code":I
    const/16 v3, 0xc8

    if-ne v2, v3, :cond_1

    .line 38
    new-instance v3, Ljava/io/BufferedReader;

    new-instance v4, Ljava/io/InputStreamReader;

    invoke-virtual {v1}, Ljava/net/HttpURLConnection;->getInputStream()Ljava/io/InputStream;

    move-result-object v5

    const-string v6, "UTF-8"

    invoke-direct {v4, v5, v6}, Ljava/io/InputStreamReader;-><init>(Ljava/io/InputStream;Ljava/lang/String;)V

    invoke-direct {v3, v4}, Ljava/io/BufferedReader;-><init>(Ljava/io/Reader;)V

    .line 39
    .local v3, "br":Ljava/io/BufferedReader;
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    .line 40
    .local v4, "sb":Ljava/lang/StringBuilder;
    :goto_0
    invoke-virtual {v3}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;

    move-result-object v5

    move-object v6, v5

    .local v6, "line":Ljava/lang/String;
    if-eqz v5, :cond_0

    invoke-virtual {v4, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    goto :goto_0

    .line 41
    :cond_0
    invoke-virtual {v3}, Ljava/io/BufferedReader;->close()V

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v5

    return-object v5

    .line 37
    .end local v3    # "br":Ljava/io/BufferedReader;
    .end local v4    # "sb":Ljava/lang/StringBuilder;
    .end local v6    # "line":Ljava/lang/String;
    :cond_1
    new-instance v3, Ljava/lang/Exception;

    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "HTTP "

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v4

    invoke-virtual {v4, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v4

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-direct {v3, v4}, Ljava/lang/Exception;-><init>(Ljava/lang/String;)V

    throw v3
.end method

.method public static fetchAllChannels(Ljava/lang/String;)Ljava/util/List;
    .locals 2
    .param p0, "categoria"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/String;",
            ")",
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/Channel;",
            ">;"
        }
    .end annotation

    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .line 106
    if-eqz p0, :cond_0

    invoke-virtual {p0}, Ljava/lang/String;->isEmpty()Z

    move-result v0

    if-nez v0, :cond_0

    .line 107
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "/canales?categoria="

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v1, "&limit=80"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    goto :goto_0

    .line 108
    :cond_0
    const-string v0, "/canales?limit=80"

    :goto_0
    nop

    .line 109
    .local v0, "path":Ljava/lang/String;
    invoke-static {v0}, Lcom/ultragol/app/network/StreamingApi;->fetch(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    invoke-static {v1}, Lcom/ultragol/app/network/StreamingApi;->parseChannels(Ljava/lang/String;)Ljava/util/List;

    move-result-object v1

    return-object v1
.end method

.method public static fetchMovieServers(I)Lcom/ultragol/app/network/StreamingApi$ServerData;
    .locals 2
    .param p0, "tmdbId"    # I
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .line 71
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "/api/unlimplay/m3u8/"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Lcom/ultragol/app/network/StreamingApi;->fetch(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Lcom/ultragol/app/network/StreamingApi;->parseServerData(Ljava/lang/String;)Lcom/ultragol/app/network/StreamingApi$ServerData;

    move-result-object v0

    return-object v0
.end method

.method public static fetchSeriesServers(III)Lcom/ultragol/app/network/StreamingApi$ServerData;
    .locals 2
    .param p0, "tmdbId"    # I
    .param p1, "season"    # I
    .param p2, "ep"    # I
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .line 75
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "/api/unlimplay/m3u8/tv/"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v1, "/"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0, p2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Lcom/ultragol/app/network/StreamingApi;->fetch(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Lcom/ultragol/app/network/StreamingApi;->parseServerData(Ljava/lang/String;)Lcom/ultragol/app/network/StreamingApi$ServerData;

    move-result-object v0

    return-object v0
.end method

.method public static fetchSportsChannels()Ljava/util/List;
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/Channel;",
            ">;"
        }
    .end annotation

    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .line 101
    const-string v0, "/canales?categoria=sports&limit=100"

    invoke-static {v0}, Lcom/ultragol/app/network/StreamingApi;->fetch(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 102
    .local v0, "json":Ljava/lang/String;
    invoke-static {v0}, Lcom/ultragol/app/network/StreamingApi;->parseChannels(Ljava/lang/String;)Ljava/util/List;

    move-result-object v1

    return-object v1
.end method

.method private static parseChannels(Ljava/lang/String;)Ljava/util/List;
    .locals 17
    .param p0, "json"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/String;",
            ")",
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/Channel;",
            ">;"
        }
    .end annotation

    .line 113
    move-object/from16 v1, p0

    const-string v2, ""

    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    move-object v3, v0

    .line 115
    .local v3, "list":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/Channel;>;"
    :try_start_0
    new-instance v0, Lorg/json/JSONObject;

    invoke-direct {v0, v1}, Lorg/json/JSONObject;-><init>(Ljava/lang/String;)V

    move-object v4, v0

    .line 116
    .local v4, "root":Lorg/json/JSONObject;
    const-string v0, "canales"

    invoke-virtual {v4, v0}, Lorg/json/JSONObject;->optJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v0

    .line 117
    .local v0, "arr":Lorg/json/JSONArray;
    if-nez v0, :cond_0

    new-instance v5, Lorg/json/JSONArray;

    invoke-direct {v5, v1}, Lorg/json/JSONArray;-><init>(Ljava/lang/String;)V

    move-object v0, v5

    goto :goto_0

    :cond_0
    move-object v5, v0

    .line 118
    .end local v0    # "arr":Lorg/json/JSONArray;
    .local v5, "arr":Lorg/json/JSONArray;
    :goto_0
    const/4 v0, 0x0

    move v6, v0

    .local v6, "i":I
    :goto_1
    invoke-virtual {v5}, Lorg/json/JSONArray;->length()I

    move-result v0
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_1

    if-ge v6, v0, :cond_3

    .line 120
    :try_start_1
    invoke-virtual {v5, v6}, Lorg/json/JSONArray;->getJSONObject(I)Lorg/json/JSONObject;

    move-result-object v0

    .line 121
    .local v0, "o":Lorg/json/JSONObject;
    const-string v7, "id"

    invoke-static {v6}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v8

    invoke-virtual {v0, v7, v8}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v10

    .line 122
    .local v10, "id":Ljava/lang/String;
    const-string v7, "nombre"

    const-string v8, "name"

    new-instance v9, Ljava/lang/StringBuilder;

    invoke-direct {v9}, Ljava/lang/StringBuilder;-><init>()V

    const-string v11, "Canal "

    invoke-virtual {v9, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v9

    add-int/lit8 v11, v6, 0x1

    invoke-virtual {v9, v11}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v9

    invoke-virtual {v9}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v9

    invoke-virtual {v0, v8, v9}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v8

    invoke-virtual {v0, v7, v8}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v11

    .line 123
    .local v11, "name":Ljava/lang/String;
    const-string v7, "pais"

    invoke-virtual {v0, v7, v2}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v12

    .line 124
    .local v12, "country":Ljava/lang/String;
    const-string v7, "bandera"

    const-string v8, "\ud83d\udcfa"

    invoke-virtual {v0, v7, v8}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v13

    .line 125
    .local v13, "flag":Ljava/lang/String;
    const-string v7, "logo"

    invoke-virtual {v0, v7, v2}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v14

    .line 126
    .local v14, "logo":Ljava/lang/String;
    const-string v7, "player_url"

    invoke-virtual {v0, v7, v2}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v15

    .line 127
    .local v15, "playerUrl":Ljava/lang/String;
    const-string v7, "categorias"

    invoke-virtual {v0, v7}, Lorg/json/JSONObject;->optJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v7
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0

    .line 128
    .local v7, "cats":Lorg/json/JSONArray;
    const-string v8, "TV"

    if-eqz v7, :cond_1

    :try_start_2
    invoke-virtual {v7}, Lorg/json/JSONArray;->length()I

    move-result v9

    if-lez v9, :cond_1

    const/4 v9, 0x0

    invoke-virtual {v7, v9, v8}, Lorg/json/JSONArray;->optString(ILjava/lang/String;)Ljava/lang/String;

    move-result-object v8

    :cond_1
    move-object/from16 v16, v8

    .line 129
    .local v16, "cat":Ljava/lang/String;
    invoke-virtual {v15}, Ljava/lang/String;->isEmpty()Z

    move-result v8

    if-nez v8, :cond_2

    new-instance v8, Lcom/ultragol/app/models/Channel;

    move-object v9, v8

    invoke-direct/range {v9 .. v16}, Lcom/ultragol/app/models/Channel;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V

    invoke-interface {v3, v8}, Ljava/util/List;->add(Ljava/lang/Object;)Z
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_0

    goto :goto_2

    .line 130
    .end local v0    # "o":Lorg/json/JSONObject;
    .end local v7    # "cats":Lorg/json/JSONArray;
    .end local v10    # "id":Ljava/lang/String;
    .end local v11    # "name":Ljava/lang/String;
    .end local v12    # "country":Ljava/lang/String;
    .end local v13    # "flag":Ljava/lang/String;
    .end local v14    # "logo":Ljava/lang/String;
    .end local v15    # "playerUrl":Ljava/lang/String;
    .end local v16    # "cat":Ljava/lang/String;
    :catch_0
    move-exception v0

    :cond_2
    :goto_2
    nop

    .line 118
    add-int/lit8 v6, v6, 0x1

    goto :goto_1

    .end local v4    # "root":Lorg/json/JSONObject;
    .end local v5    # "arr":Lorg/json/JSONArray;
    .end local v6    # "i":I
    :cond_3
    goto :goto_3

    .line 132
    :catch_1
    move-exception v0

    :goto_3
    nop

    .line 133
    return-object v3
.end method

.method public static parseServerData(Ljava/lang/String;)Lcom/ultragol/app/network/StreamingApi$ServerData;
    .locals 10
    .param p0, "json"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .line 79
    new-instance v0, Lcom/ultragol/app/network/StreamingApi$ServerData;

    invoke-direct {v0}, Lcom/ultragol/app/network/StreamingApi$ServerData;-><init>()V

    .line 80
    .local v0, "data":Lcom/ultragol/app/network/StreamingApi$ServerData;
    new-instance v1, Lorg/json/JSONObject;

    invoke-direct {v1, p0}, Lorg/json/JSONObject;-><init>(Ljava/lang/String;)V

    .line 81
    .local v1, "root":Lorg/json/JSONObject;
    const-string v2, "embed_url"

    const-string v3, ""

    invoke-virtual {v1, v2, v3}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    iput-object v2, v0, Lcom/ultragol/app/network/StreamingApi$ServerData;->embedUrl:Ljava/lang/String;

    .line 82
    const-string v2, "idiomas"

    invoke-virtual {v1, v2}, Lorg/json/JSONObject;->optJSONObject(Ljava/lang/String;)Lorg/json/JSONObject;

    move-result-object v2

    .line 83
    .local v2, "idiomas":Lorg/json/JSONObject;
    const-string v3, "embed"

    const-string v4, "Principal"

    if-nez v2, :cond_1

    .line 84
    iget-object v5, v0, Lcom/ultragol/app/network/StreamingApi$ServerData;->embedUrl:Ljava/lang/String;

    invoke-virtual {v5}, Ljava/lang/String;->isEmpty()Z

    move-result v5

    if-nez v5, :cond_0

    iget-object v5, v0, Lcom/ultragol/app/network/StreamingApi$ServerData;->latino:Ljava/util/List;

    new-instance v6, Lcom/ultragol/app/network/StreamingApi$Server;

    iget-object v7, v0, Lcom/ultragol/app/network/StreamingApi$ServerData;->embedUrl:Ljava/lang/String;

    invoke-direct {v6, v4, v7, v3}, Lcom/ultragol/app/network/StreamingApi$Server;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V

    invoke-interface {v5, v6}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    .line 85
    :cond_0
    return-object v0

    .line 87
    :cond_1
    iget-object v5, v0, Lcom/ultragol/app/network/StreamingApi$ServerData;->latino:Ljava/util/List;

    const-string v6, "latino"

    invoke-virtual {v2, v6}, Lorg/json/JSONObject;->optJSONObject(Ljava/lang/String;)Lorg/json/JSONObject;

    move-result-object v6

    invoke-static {v6}, Lcom/ultragol/app/network/StreamingApi;->parseServers(Lorg/json/JSONObject;)Ljava/util/List;

    move-result-object v6

    invoke-interface {v5, v6}, Ljava/util/List;->addAll(Ljava/util/Collection;)Z

    .line 88
    const-string v5, "espa\u00f1ol"

    invoke-virtual {v2, v5}, Lorg/json/JSONObject;->optJSONObject(Ljava/lang/String;)Lorg/json/JSONObject;

    move-result-object v5

    .line 89
    .local v5, "esp":Lorg/json/JSONObject;
    if-nez v5, :cond_2

    const-string v6, "espanol"

    invoke-virtual {v2, v6}, Lorg/json/JSONObject;->optJSONObject(Ljava/lang/String;)Lorg/json/JSONObject;

    move-result-object v5

    .line 90
    :cond_2
    iget-object v6, v0, Lcom/ultragol/app/network/StreamingApi$ServerData;->espanol:Ljava/util/List;

    invoke-static {v5}, Lcom/ultragol/app/network/StreamingApi;->parseServers(Lorg/json/JSONObject;)Ljava/util/List;

    move-result-object v7

    invoke-interface {v6, v7}, Ljava/util/List;->addAll(Ljava/util/Collection;)Z

    .line 91
    const-string v6, "subtitulado"

    invoke-virtual {v2, v6}, Lorg/json/JSONObject;->optJSONObject(Ljava/lang/String;)Lorg/json/JSONObject;

    move-result-object v6

    .line 92
    .local v6, "sub":Lorg/json/JSONObject;
    if-nez v6, :cond_3

    const-string v7, "subtitles"

    invoke-virtual {v2, v7}, Lorg/json/JSONObject;->optJSONObject(Ljava/lang/String;)Lorg/json/JSONObject;

    move-result-object v6

    .line 93
    :cond_3
    iget-object v7, v0, Lcom/ultragol/app/network/StreamingApi$ServerData;->subtitulado:Ljava/util/List;

    invoke-static {v6}, Lcom/ultragol/app/network/StreamingApi;->parseServers(Lorg/json/JSONObject;)Ljava/util/List;

    move-result-object v8

    invoke-interface {v7, v8}, Ljava/util/List;->addAll(Ljava/util/Collection;)Z

    .line 94
    iget-object v7, v0, Lcom/ultragol/app/network/StreamingApi$ServerData;->latino:Ljava/util/List;

    invoke-interface {v7}, Ljava/util/List;->isEmpty()Z

    move-result v7

    if-eqz v7, :cond_4

    iget-object v7, v0, Lcom/ultragol/app/network/StreamingApi$ServerData;->espanol:Ljava/util/List;

    invoke-interface {v7}, Ljava/util/List;->isEmpty()Z

    move-result v7

    if-eqz v7, :cond_4

    iget-object v7, v0, Lcom/ultragol/app/network/StreamingApi$ServerData;->subtitulado:Ljava/util/List;

    invoke-interface {v7}, Ljava/util/List;->isEmpty()Z

    move-result v7

    if-eqz v7, :cond_4

    iget-object v7, v0, Lcom/ultragol/app/network/StreamingApi$ServerData;->embedUrl:Ljava/lang/String;

    .line 95
    invoke-virtual {v7}, Ljava/lang/String;->isEmpty()Z

    move-result v7

    if-nez v7, :cond_4

    .line 96
    iget-object v7, v0, Lcom/ultragol/app/network/StreamingApi$ServerData;->latino:Ljava/util/List;

    new-instance v8, Lcom/ultragol/app/network/StreamingApi$Server;

    iget-object v9, v0, Lcom/ultragol/app/network/StreamingApi$ServerData;->embedUrl:Ljava/lang/String;

    invoke-direct {v8, v4, v9, v3}, Lcom/ultragol/app/network/StreamingApi$Server;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V

    invoke-interface {v7, v8}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    .line 97
    :cond_4
    return-object v0
.end method

.method private static parseServers(Lorg/json/JSONObject;)Ljava/util/List;
    .locals 11
    .param p0, "langObj"    # Lorg/json/JSONObject;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Lorg/json/JSONObject;",
            ")",
            "Ljava/util/List<",
            "Lcom/ultragol/app/network/StreamingApi$Server;",
            ">;"
        }
    .end annotation

    .line 45
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    .line 46
    .local v0, "list":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/network/StreamingApi$Server;>;"
    if-nez p0, :cond_0

    return-object v0

    .line 47
    :cond_0
    const-string v1, "servidores"

    invoke-virtual {p0, v1}, Lorg/json/JSONObject;->optJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v1

    .line 48
    .local v1, "arr":Lorg/json/JSONArray;
    const-string v2, "embed"

    const-string v3, ""

    if-eqz v1, :cond_2

    .line 49
    const/4 v4, 0x0

    .local v4, "i":I
    :goto_0
    invoke-virtual {v1}, Lorg/json/JSONArray;->length()I

    move-result v5

    if-ge v4, v5, :cond_2

    .line 51
    :try_start_0
    invoke-virtual {v1, v4}, Lorg/json/JSONArray;->getJSONObject(I)Lorg/json/JSONObject;

    move-result-object v5

    .line 52
    .local v5, "s":Lorg/json/JSONObject;
    const-string v6, "url"

    invoke-virtual {v5, v6, v3}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v6

    .line 53
    .local v6, "url":Ljava/lang/String;
    invoke-virtual {v6}, Ljava/lang/String;->isEmpty()Z

    move-result v7

    if-nez v7, :cond_1

    new-instance v7, Lcom/ultragol/app/network/StreamingApi$Server;

    const-string v8, "nombre"

    new-instance v9, Ljava/lang/StringBuilder;

    invoke-direct {v9}, Ljava/lang/StringBuilder;-><init>()V

    const-string v10, "Srv "

    invoke-virtual {v9, v10}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v9

    add-int/lit8 v10, v4, 0x1

    invoke-virtual {v9, v10}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v9

    invoke-virtual {v9}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v9

    .line 54
    invoke-virtual {v5, v8, v9}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v8

    invoke-static {v8}, Lcom/ultragol/app/network/StreamingApi;->cap(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v8

    const-string v9, "tipo"

    .line 55
    invoke-virtual {v5, v9, v2}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v9

    invoke-direct {v7, v8, v6, v9}, Lcom/ultragol/app/network/StreamingApi$Server;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V

    .line 53
    invoke-interface {v0, v7}, Ljava/util/List;->add(Ljava/lang/Object;)Z
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_1

    .line 56
    .end local v5    # "s":Lorg/json/JSONObject;
    .end local v6    # "url":Ljava/lang/String;
    :catch_0
    move-exception v5

    :cond_1
    :goto_1
    nop

    .line 49
    add-int/lit8 v4, v4, 0x1

    goto :goto_0

    .line 59
    .end local v4    # "i":I
    :cond_2
    invoke-interface {v0}, Ljava/util/List;->isEmpty()Z

    move-result v4

    if-eqz v4, :cond_3

    .line 60
    const-string v4, "embed_url"

    invoke-virtual {p0, v4, v3}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    .line 61
    .local v3, "emb":Ljava/lang/String;
    invoke-virtual {v3}, Ljava/lang/String;->isEmpty()Z

    move-result v4

    if-nez v4, :cond_3

    new-instance v4, Lcom/ultragol/app/network/StreamingApi$Server;

    const-string v5, "Principal"

    invoke-direct {v4, v5, v3, v2}, Lcom/ultragol/app/network/StreamingApi$Server;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V

    invoke-interface {v0, v4}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    .line 63
    .end local v3    # "emb":Ljava/lang/String;
    :cond_3
    return-object v0
.end method
