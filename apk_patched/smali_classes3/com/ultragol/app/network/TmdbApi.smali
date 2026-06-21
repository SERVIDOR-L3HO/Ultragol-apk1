.class public Lcom/ultragol/app/network/TmdbApi;
.super Ljava/lang/Object;
.source "TmdbApi.java"


# static fields
.field private static final BASE:Ljava/lang/String; = "https://api.themoviedb.org/3"

.field private static final BEARER:Ljava/lang/String; = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI4NmQ5YTgzNGQ0NDEzNzAwYjQ5MWNjMjY4OTIxNDdhYSIsIm5iZiI6MTc1MjQ1NjQ4My4zNDUsInN1YiI6IjY4NzQ1ZDIzNjIwNzU1OWUwNDVhZTRjMiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.Mm-GBMnPS_WUAslIwTiewd6khCIFIqR4XDBqTlT9Yx0"

.field public static final IMG_BG:Ljava/lang/String; = "https://image.tmdb.org/t/p/w780"

.field public static final IMG_W:Ljava/lang/String; = "https://image.tmdb.org/t/p/w342"


# direct methods
.method public constructor <init>()V
    .locals 0

    .line 13
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method private static backdrop(Ljava/lang/String;)Ljava/lang/String;
    .locals 2
    .param p0, "p"    # Ljava/lang/String;

    .line 50
    if-eqz p0, :cond_1

    invoke-virtual {p0}, Ljava/lang/String;->isEmpty()Z

    move-result v0

    if-eqz v0, :cond_0

    goto :goto_0

    :cond_0
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "https://image.tmdb.org/t/p/w780"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    goto :goto_1

    :cond_1
    :goto_0
    const-string v0, ""

    :goto_1
    return-object v0
.end method

.method private static fetch(Ljava/lang/String;)Ljava/lang/String;
    .locals 6
    .param p0, "path"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .line 21
    new-instance v0, Ljava/net/URL;

    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "https://api.themoviedb.org/3"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-direct {v0, v1}, Ljava/net/URL;-><init>(Ljava/lang/String;)V

    .line 22
    .local v0, "url":Ljava/net/URL;
    invoke-virtual {v0}, Ljava/net/URL;->openConnection()Ljava/net/URLConnection;

    move-result-object v1

    check-cast v1, Ljava/net/HttpURLConnection;

    .line 23
    .local v1, "c":Ljava/net/HttpURLConnection;
    const-string v2, "GET"

    invoke-virtual {v1, v2}, Ljava/net/HttpURLConnection;->setRequestMethod(Ljava/lang/String;)V

    .line 24
    const-string v2, "Authorization"

    const-string v3, "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI4NmQ5YTgzNGQ0NDEzNzAwYjQ5MWNjMjY4OTIxNDdhYSIsIm5iZiI6MTc1MjQ1NjQ4My4zNDUsInN1YiI6IjY4NzQ1ZDIzNjIwNzU1OWUwNDVhZTRjMiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.Mm-GBMnPS_WUAslIwTiewd6khCIFIqR4XDBqTlT9Yx0"

    invoke-virtual {v1, v2, v3}, Ljava/net/HttpURLConnection;->setRequestProperty(Ljava/lang/String;Ljava/lang/String;)V

    .line 25
    const-string v2, "accept"

    const-string v3, "application/json"

    invoke-virtual {v1, v2, v3}, Ljava/net/HttpURLConnection;->setRequestProperty(Ljava/lang/String;Ljava/lang/String;)V

    .line 26
    const/16 v2, 0x2ee0

    invoke-virtual {v1, v2}, Ljava/net/HttpURLConnection;->setConnectTimeout(I)V

    invoke-virtual {v1, v2}, Ljava/net/HttpURLConnection;->setReadTimeout(I)V

    .line 27
    new-instance v2, Ljava/io/BufferedReader;

    new-instance v3, Ljava/io/InputStreamReader;

    invoke-virtual {v1}, Ljava/net/HttpURLConnection;->getInputStream()Ljava/io/InputStream;

    move-result-object v4

    const-string v5, "UTF-8"

    invoke-direct {v3, v4, v5}, Ljava/io/InputStreamReader;-><init>(Ljava/io/InputStream;Ljava/lang/String;)V

    invoke-direct {v2, v3}, Ljava/io/BufferedReader;-><init>(Ljava/io/Reader;)V

    .line 28
    .local v2, "br":Ljava/io/BufferedReader;
    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    .line 29
    .local v3, "sb":Ljava/lang/StringBuilder;
    :goto_0
    invoke-virtual {v2}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;

    move-result-object v4

    move-object v5, v4

    .local v5, "line":Ljava/lang/String;
    if-eqz v4, :cond_0

    invoke-virtual {v3, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    goto :goto_0

    .line 30
    :cond_0
    invoke-virtual {v2}, Ljava/io/BufferedReader;->close()V

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    return-object v4
.end method

.method public static fetchAnime()Ljava/util/List;
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/ContentItem;",
            ">;"
        }
    .end annotation

    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .line 116
    new-instance v0, Lorg/json/JSONObject;

    const-string v1, "/discover/tv?with_genres=16&with_origin_country=JP&sort_by=popularity.desc&language=es-MX&page=1"

    invoke-static {v1}, Lcom/ultragol/app/network/TmdbApi;->fetch(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    invoke-direct {v0, v1}, Lorg/json/JSONObject;-><init>(Ljava/lang/String;)V

    const-string v1, "results"

    invoke-virtual {v0, v1}, Lorg/json/JSONObject;->getJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v0

    const/4 v1, 0x2

    invoke-static {v0, v1}, Lcom/ultragol/app/network/TmdbApi;->parse(Lorg/json/JSONArray;I)Ljava/util/List;

    move-result-object v0

    return-object v0
.end method

.method public static fetchByProvider(ILjava/lang/String;)Ljava/util/List;
    .locals 4
    .param p0, "providerId"    # I
    .param p1, "mediaType"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(I",
            "Ljava/lang/String;",
            ")",
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/ContentItem;",
            ">;"
        }
    .end annotation

    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .line 126
    const-string v0, "movie"

    invoke-virtual {v0, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    xor-int/lit8 v0, v0, 0x1

    .line 127
    .local v0, "type":I
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "/discover/"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string v2, "?with_watch_providers="

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string v2, "&watch_region=MX&sort_by=popularity.desc&language=es-MX&page=1"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    .line 133
    .local v1, "path":Ljava/lang/String;
    new-instance v2, Lorg/json/JSONObject;

    invoke-static {v1}, Lcom/ultragol/app/network/TmdbApi;->fetch(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    invoke-direct {v2, v3}, Lorg/json/JSONObject;-><init>(Ljava/lang/String;)V

    const-string v3, "results"

    invoke-virtual {v2, v3}, Lorg/json/JSONObject;->getJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v2

    invoke-static {v2, v0}, Lcom/ultragol/app/network/TmdbApi;->parse(Lorg/json/JSONArray;I)Ljava/util/List;

    move-result-object v2

    return-object v2
.end method

.method public static fetchDoramas()Ljava/util/List;
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/ContentItem;",
            ">;"
        }
    .end annotation

    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .line 119
    new-instance v0, Lorg/json/JSONObject;

    const-string v1, "/discover/tv?with_origin_country=KR&sort_by=popularity.desc&language=es-MX&page=1"

    invoke-static {v1}, Lcom/ultragol/app/network/TmdbApi;->fetch(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    invoke-direct {v0, v1}, Lorg/json/JSONObject;-><init>(Ljava/lang/String;)V

    const-string v1, "results"

    invoke-virtual {v0, v1}, Lorg/json/JSONObject;->getJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v0

    const/4 v1, 0x3

    invoke-static {v0, v1}, Lcom/ultragol/app/network/TmdbApi;->parse(Lorg/json/JSONArray;I)Ljava/util/List;

    move-result-object v0

    return-object v0
.end method

.method public static fetchMovies()Ljava/util/List;
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/ContentItem;",
            ">;"
        }
    .end annotation

    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .line 107
    new-instance v0, Lorg/json/JSONObject;

    const-string v1, "/discover/movie?sort_by=popularity.desc&language=es-MX&page=1"

    invoke-static {v1}, Lcom/ultragol/app/network/TmdbApi;->fetch(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    invoke-direct {v0, v1}, Lorg/json/JSONObject;-><init>(Ljava/lang/String;)V

    const-string v1, "results"

    invoke-virtual {v0, v1}, Lorg/json/JSONObject;->getJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v0

    const/4 v1, 0x0

    invoke-static {v0, v1}, Lcom/ultragol/app/network/TmdbApi;->parse(Lorg/json/JSONArray;I)Ljava/util/List;

    move-result-object v0

    return-object v0
.end method

.method public static fetchNewMovies()Ljava/util/List;
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/ContentItem;",
            ">;"
        }
    .end annotation

    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .line 122
    new-instance v0, Lorg/json/JSONObject;

    const-string v1, "/movie/now_playing?language=es-MX&page=1"

    invoke-static {v1}, Lcom/ultragol/app/network/TmdbApi;->fetch(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    invoke-direct {v0, v1}, Lorg/json/JSONObject;-><init>(Ljava/lang/String;)V

    const-string v1, "results"

    invoke-virtual {v0, v1}, Lorg/json/JSONObject;->getJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v0

    const/4 v1, 0x0

    invoke-static {v0, v1}, Lcom/ultragol/app/network/TmdbApi;->parse(Lorg/json/JSONArray;I)Ljava/util/List;

    move-result-object v0

    return-object v0
.end method

.method public static fetchSeries()Ljava/util/List;
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/ContentItem;",
            ">;"
        }
    .end annotation

    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .line 113
    new-instance v0, Lorg/json/JSONObject;

    const-string v1, "/discover/tv?sort_by=popularity.desc&language=es-MX&page=1"

    invoke-static {v1}, Lcom/ultragol/app/network/TmdbApi;->fetch(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    invoke-direct {v0, v1}, Lorg/json/JSONObject;-><init>(Ljava/lang/String;)V

    const-string v1, "results"

    invoke-virtual {v0, v1}, Lorg/json/JSONObject;->getJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v0

    const/4 v1, 0x1

    invoke-static {v0, v1}, Lcom/ultragol/app/network/TmdbApi;->parse(Lorg/json/JSONArray;I)Ljava/util/List;

    move-result-object v0

    return-object v0
.end method

.method public static fetchTopMovies()Ljava/util/List;
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/ContentItem;",
            ">;"
        }
    .end annotation

    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .line 110
    new-instance v0, Lorg/json/JSONObject;

    const-string v1, "/movie/top_rated?language=es-MX&page=1"

    invoke-static {v1}, Lcom/ultragol/app/network/TmdbApi;->fetch(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    invoke-direct {v0, v1}, Lorg/json/JSONObject;-><init>(Ljava/lang/String;)V

    const-string v1, "results"

    invoke-virtual {v0, v1}, Lorg/json/JSONObject;->getJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v0

    const/4 v1, 0x0

    invoke-static {v0, v1}, Lcom/ultragol/app/network/TmdbApi;->parse(Lorg/json/JSONArray;I)Ljava/util/List;

    move-result-object v0

    return-object v0
.end method

.method public static fetchTrending()Ljava/util/List;
    .locals 22
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/ContentItem;",
            ">;"
        }
    .end annotation

    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .line 78
    const-string v1, "movie"

    new-instance v0, Lorg/json/JSONObject;

    const-string v2, "/trending/all/week?language=es-MX"

    invoke-static {v2}, Lcom/ultragol/app/network/TmdbApi;->fetch(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    invoke-direct {v0, v2}, Lorg/json/JSONObject;-><init>(Ljava/lang/String;)V

    const-string v2, "results"

    invoke-virtual {v0, v2}, Lorg/json/JSONObject;->getJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v2

    .line 79
    .local v2, "arr":Lorg/json/JSONArray;
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    move-object v3, v0

    .line 80
    .local v3, "list":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    const/4 v0, 0x0

    move v4, v0

    .local v4, "i":I
    :goto_0
    invoke-virtual {v2}, Lorg/json/JSONArray;->length()I

    move-result v0

    if-ge v4, v0, :cond_7

    invoke-interface {v3}, Ljava/util/List;->size()I

    move-result v0

    const/16 v5, 0xa

    if-ge v0, v5, :cond_7

    .line 82
    :try_start_0
    invoke-virtual {v2, v4}, Lorg/json/JSONArray;->getJSONObject(I)Lorg/json/JSONObject;

    move-result-object v0

    move-object v5, v0

    .line 83
    .local v5, "o":Lorg/json/JSONObject;
    const-string v0, "media_type"

    invoke-virtual {v5, v0, v1}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    move-object v6, v0

    .line 84
    .local v6, "mt":Ljava/lang/String;
    const-string v0, "person"

    invoke-virtual {v0, v6}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_0

    move-object/from16 v19, v1

    move-object/from16 v21, v2

    goto/16 :goto_7

    .line 85
    :cond_0
    invoke-virtual {v1, v6}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_4

    move v7, v0

    .line 86
    .local v7, "mov":Z
    if-eqz v7, :cond_1

    :try_start_1
    const-string v0, "title"

    invoke-virtual {v5, v0}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0

    goto :goto_1

    .line 101
    .end local v5    # "o":Lorg/json/JSONObject;
    .end local v6    # "mt":Ljava/lang/String;
    .end local v7    # "mov":Z
    :catch_0
    move-exception v0

    move-object/from16 v19, v1

    move-object/from16 v21, v2

    goto/16 :goto_7

    .line 86
    .restart local v5    # "o":Lorg/json/JSONObject;
    .restart local v6    # "mt":Ljava/lang/String;
    .restart local v7    # "mov":Z
    :cond_1
    :try_start_2
    const-string v0, "name"

    invoke-virtual {v5, v0}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    :goto_1
    move-object/from16 v18, v0

    .line 87
    .local v18, "title":Ljava/lang/String;
    if-eqz v18, :cond_6

    invoke-virtual/range {v18 .. v18}, Ljava/lang/String;->isEmpty()Z

    move-result v0

    if-eqz v0, :cond_2

    move-object/from16 v19, v1

    move-object/from16 v21, v2

    goto/16 :goto_7

    .line 88
    :cond_2
    const-string v0, "id"

    const/4 v8, 0x0

    invoke-virtual {v5, v0, v8}, Lorg/json/JSONObject;->optInt(Ljava/lang/String;I)I

    move-result v0

    move v9, v0

    .line 89
    .local v9, "id":I
    const-string v0, "genre_ids"

    invoke-virtual {v5, v0}, Lorg/json/JSONObject;->optJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v0

    invoke-static {v0}, Lcom/ultragol/app/network/TmdbApi;->genre(Lorg/json/JSONArray;)Ljava/lang/String;

    move-result-object v10
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_4

    .line 90
    .local v10, "g":Ljava/lang/String;
    if-eqz v7, :cond_3

    :try_start_3
    const-string v0, "release_date"
    :try_end_3
    .catch Ljava/lang/Exception; {:try_start_3 .. :try_end_3} :catch_0

    goto :goto_2

    :cond_3
    :try_start_4
    const-string v0, "first_air_date"

    :goto_2
    invoke-virtual {v5, v0}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Lcom/ultragol/app/network/TmdbApi;->year(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v11

    .line 91
    .local v11, "y":Ljava/lang/String;
    const-string v0, "vote_average"

    const-wide/high16 v12, 0x401c000000000000L    # 7.0

    invoke-virtual {v5, v0, v12, v13}, Lorg/json/JSONObject;->optDouble(Ljava/lang/String;D)D

    move-result-wide v12

    invoke-static {v12, v13}, Lcom/ultragol/app/network/TmdbApi;->rating(D)Ljava/lang/String;

    move-result-object v12

    .line 92
    .local v12, "r":Ljava/lang/String;
    const-string v0, "poster_path"

    invoke-virtual {v5, v0}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Lcom/ultragol/app/network/TmdbApi;->poster(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v13

    .line 93
    .local v13, "post":Ljava/lang/String;
    const-string v0, "backdrop_path"

    invoke-virtual {v5, v0}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Lcom/ultragol/app/network/TmdbApi;->backdrop(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    move-object v15, v0

    .line 94
    .local v15, "back":Ljava/lang/String;
    const-string v0, "overview"

    const-string v14, ""

    invoke-virtual {v5, v0, v14}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v14
    :try_end_4
    .catch Ljava/lang/Exception; {:try_start_4 .. :try_end_4} :catch_4

    .line 95
    .local v14, "ov":Ljava/lang/String;
    if-eqz v7, :cond_4

    const/16 v16, 0x0

    goto :goto_3

    :cond_4
    const/16 v16, 0x1

    :goto_3
    move-object/from16 v19, v1

    move-object v1, v15

    .end local v15    # "back":Ljava/lang/String;
    .local v1, "back":Ljava/lang/String;
    move/from16 v15, v16

    .line 96
    .local v15, "type":I
    const/16 v16, 0x0

    .line 97
    .local v16, "isNew":Z
    :try_start_5
    invoke-static {v11}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I

    move-result v0
    :try_end_5
    .catch Ljava/lang/Exception; {:try_start_5 .. :try_end_5} :catch_1

    const/16 v8, 0x7e8

    if-lt v0, v8, :cond_5

    const/4 v8, 0x1

    goto :goto_4

    :cond_5
    const/4 v8, 0x0

    :goto_4
    move/from16 v16, v8

    goto :goto_5

    :catch_1
    move-exception v0

    :goto_5
    move/from16 v0, v16

    .line 98
    .end local v16    # "isNew":Z
    .local v0, "isNew":Z
    :try_start_6
    new-instance v20, Lcom/ultragol/app/models/ContentItem;
    :try_end_6
    .catch Ljava/lang/Exception; {:try_start_6 .. :try_end_6} :catch_3

    const/16 v17, 0x0

    move-object/from16 v8, v20

    move-object/from16 v21, v2

    move v2, v9

    .end local v9    # "id":I
    .local v2, "id":I
    .local v21, "arr":Lorg/json/JSONArray;
    move-object/from16 v9, v18

    move/from16 v16, v0

    :try_start_7
    invoke-direct/range {v8 .. v17}, Lcom/ultragol/app/models/ContentItem;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IZZ)V

    move-object/from16 v8, v20

    .line 99
    .local v8, "item":Lcom/ultragol/app/models/ContentItem;
    invoke-virtual {v8, v2}, Lcom/ultragol/app/models/ContentItem;->setTmdbId(I)V

    invoke-virtual {v8, v1}, Lcom/ultragol/app/models/ContentItem;->setBackdropUrl(Ljava/lang/String;)V

    .line 100
    invoke-interface {v3, v8}, Ljava/util/List;->add(Ljava/lang/Object;)Z
    :try_end_7
    .catch Ljava/lang/Exception; {:try_start_7 .. :try_end_7} :catch_2

    .line 101
    nop

    .end local v0    # "isNew":Z
    .end local v1    # "back":Ljava/lang/String;
    .end local v2    # "id":I
    .end local v5    # "o":Lorg/json/JSONObject;
    .end local v6    # "mt":Ljava/lang/String;
    .end local v7    # "mov":Z
    .end local v8    # "item":Lcom/ultragol/app/models/ContentItem;
    .end local v10    # "g":Ljava/lang/String;
    .end local v11    # "y":Ljava/lang/String;
    .end local v12    # "r":Ljava/lang/String;
    .end local v13    # "post":Ljava/lang/String;
    .end local v14    # "ov":Ljava/lang/String;
    .end local v15    # "type":I
    .end local v18    # "title":Ljava/lang/String;
    goto :goto_7

    :catch_2
    move-exception v0

    goto :goto_7

    .end local v21    # "arr":Lorg/json/JSONArray;
    .local v2, "arr":Lorg/json/JSONArray;
    :catch_3
    move-exception v0

    goto :goto_6

    .line 87
    .restart local v5    # "o":Lorg/json/JSONObject;
    .restart local v6    # "mt":Ljava/lang/String;
    .restart local v7    # "mov":Z
    .restart local v18    # "title":Ljava/lang/String;
    :cond_6
    move-object/from16 v19, v1

    move-object/from16 v21, v2

    .end local v2    # "arr":Lorg/json/JSONArray;
    .restart local v21    # "arr":Lorg/json/JSONArray;
    goto :goto_7

    .line 101
    .end local v5    # "o":Lorg/json/JSONObject;
    .end local v6    # "mt":Ljava/lang/String;
    .end local v7    # "mov":Z
    .end local v18    # "title":Ljava/lang/String;
    .end local v21    # "arr":Lorg/json/JSONArray;
    .restart local v2    # "arr":Lorg/json/JSONArray;
    :catch_4
    move-exception v0

    move-object/from16 v19, v1

    :goto_6
    move-object/from16 v21, v2

    .line 80
    .end local v2    # "arr":Lorg/json/JSONArray;
    .restart local v21    # "arr":Lorg/json/JSONArray;
    :goto_7
    add-int/lit8 v4, v4, 0x1

    move-object/from16 v1, v19

    move-object/from16 v2, v21

    goto/16 :goto_0

    .end local v21    # "arr":Lorg/json/JSONArray;
    .restart local v2    # "arr":Lorg/json/JSONArray;
    :cond_7
    move-object/from16 v21, v2

    .line 103
    .end local v2    # "arr":Lorg/json/JSONArray;
    .end local v4    # "i":I
    .restart local v21    # "arr":Lorg/json/JSONArray;
    return-object v3
.end method

.method private static genre(Lorg/json/JSONArray;)Ljava/lang/String;
    .locals 3
    .param p0, "ids"    # Lorg/json/JSONArray;

    .line 34
    const-string v0, "Drama"

    if-eqz p0, :cond_1

    invoke-virtual {p0}, Lorg/json/JSONArray;->length()I

    move-result v1

    if-nez v1, :cond_0

    goto :goto_0

    .line 35
    :cond_0
    const/4 v1, 0x0

    const/16 v2, 0x12

    invoke-virtual {p0, v1, v2}, Lorg/json/JSONArray;->optInt(II)I

    move-result v1

    sparse-switch v1, :sswitch_data_0

    .line 43
    return-object v0

    .line 39
    :sswitch_0
    const-string v0, "Familia"

    return-object v0

    .line 41
    :sswitch_1
    const-string v0, "Romance"

    return-object v0

    :sswitch_2
    const-string v0, "Misterio"

    return-object v0

    .line 42
    :sswitch_3
    const-string v0, "Sci-Fi"

    return-object v0

    .line 38
    :sswitch_4
    const-string v0, "Documental"

    return-object v0

    :sswitch_5
    const-string v0, "Crimen"

    return-object v0

    .line 42
    :sswitch_6
    const-string v0, "Thriller"

    return-object v0

    .line 37
    :sswitch_7
    const-string v0, "Comedia"

    return-object v0

    .line 36
    :sswitch_8
    const-string v0, "Acci\u00f3n"

    return-object v0

    .line 40
    :sswitch_9
    const-string v0, "Terror"

    return-object v0

    .line 39
    :sswitch_a
    return-object v0

    .line 37
    :sswitch_b
    const-string v0, "Animaci\u00f3n"

    return-object v0

    .line 40
    :sswitch_c
    const-string v0, "Fantas\u00eda"

    return-object v0

    .line 36
    :sswitch_d
    const-string v0, "Aventura"

    return-object v0

    .line 34
    :cond_1
    :goto_0
    return-object v0

    nop

    :sswitch_data_0
    .sparse-switch
        0xc -> :sswitch_d
        0xe -> :sswitch_c
        0x10 -> :sswitch_b
        0x12 -> :sswitch_a
        0x1b -> :sswitch_9
        0x1c -> :sswitch_8
        0x23 -> :sswitch_7
        0x35 -> :sswitch_6
        0x50 -> :sswitch_5
        0x63 -> :sswitch_4
        0x36e -> :sswitch_3
        0x25b0 -> :sswitch_2
        0x29fd -> :sswitch_1
        0x29ff -> :sswitch_0
        0x2a07 -> :sswitch_8
        0x2a0d -> :sswitch_c
    .end sparse-switch
.end method

.method private static parse(Lorg/json/JSONArray;I)Ljava/util/List;
    .locals 20
    .param p0, "arr"    # Lorg/json/JSONArray;
    .param p1, "type"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Lorg/json/JSONArray;",
            "I)",
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/ContentItem;",
            ">;"
        }
    .end annotation

    .line 53
    const-string v1, "title"

    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    move-object v2, v0

    .line 54
    .local v2, "list":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    const/4 v0, 0x0

    move v3, v0

    .local v3, "i":I
    :goto_0
    invoke-virtual/range {p0 .. p0}, Lorg/json/JSONArray;->length()I

    move-result v0

    if-ge v3, v0, :cond_5

    .line 56
    move-object/from16 v4, p0

    :try_start_0
    invoke-virtual {v4, v3}, Lorg/json/JSONArray;->getJSONObject(I)Lorg/json/JSONObject;

    move-result-object v0

    move-object v5, v0

    .line 57
    .local v5, "o":Lorg/json/JSONObject;
    invoke-virtual {v5, v1}, Lorg/json/JSONObject;->has(Ljava/lang/String;)Z

    move-result v0

    move v6, v0

    .line 58
    .local v6, "mov":Z
    if-eqz v6, :cond_0

    invoke-virtual {v5, v1}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    goto :goto_1

    :cond_0
    const-string v0, "name"

    invoke-virtual {v5, v0}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    :goto_1
    move-object/from16 v17, v0

    .line 59
    .local v17, "title":Ljava/lang/String;
    if-eqz v17, :cond_4

    invoke-virtual/range {v17 .. v17}, Ljava/lang/String;->isEmpty()Z

    move-result v0

    if-eqz v0, :cond_1

    move-object/from16 v19, v1

    goto/16 :goto_5

    .line 60
    :cond_1
    const-string v0, "id"

    const/4 v7, 0x0

    invoke-virtual {v5, v0, v7}, Lorg/json/JSONObject;->optInt(Ljava/lang/String;I)I

    move-result v0

    move v15, v0

    .line 61
    .local v15, "id":I
    const-string v0, "genre_ids"

    invoke-virtual {v5, v0}, Lorg/json/JSONObject;->optJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v0

    invoke-static {v0}, Lcom/ultragol/app/network/TmdbApi;->genre(Lorg/json/JSONArray;)Ljava/lang/String;

    move-result-object v9

    .line 62
    .local v9, "g":Ljava/lang/String;
    if-eqz v6, :cond_2

    const-string v0, "release_date"

    goto :goto_2

    :cond_2
    const-string v0, "first_air_date"

    :goto_2
    invoke-virtual {v5, v0}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Lcom/ultragol/app/network/TmdbApi;->year(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v10

    .line 63
    .local v10, "y":Ljava/lang/String;
    const-string v0, "vote_average"

    const-wide/high16 v11, 0x401c000000000000L    # 7.0

    invoke-virtual {v5, v0, v11, v12}, Lorg/json/JSONObject;->optDouble(Ljava/lang/String;D)D

    move-result-wide v11

    invoke-static {v11, v12}, Lcom/ultragol/app/network/TmdbApi;->rating(D)Ljava/lang/String;

    move-result-object v11

    .line 64
    .local v11, "r":Ljava/lang/String;
    const-string v0, "poster_path"

    invoke-virtual {v5, v0}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Lcom/ultragol/app/network/TmdbApi;->poster(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v12

    .line 65
    .local v12, "post":Ljava/lang/String;
    const-string v0, "backdrop_path"

    invoke-virtual {v5, v0}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Lcom/ultragol/app/network/TmdbApi;->backdrop(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    move-object v14, v0

    .line 66
    .local v14, "back":Ljava/lang/String;
    const-string v0, "overview"

    const-string v8, ""

    invoke-virtual {v5, v0, v8}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v13
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_2

    .line 67
    .local v13, "ov":Ljava/lang/String;
    const/4 v8, 0x0

    .line 68
    .local v8, "isNew":Z
    :try_start_1
    invoke-static {v10}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I

    move-result v0
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0

    const/16 v7, 0x7e8

    if-lt v0, v7, :cond_3

    const/4 v7, 0x1

    goto :goto_3

    :cond_3
    const/4 v7, 0x0

    :goto_3
    move v8, v7

    goto :goto_4

    :catch_0
    move-exception v0

    :goto_4
    move v0, v8

    .line 69
    .end local v8    # "isNew":Z
    .local v0, "isNew":Z
    :try_start_2
    new-instance v18, Lcom/ultragol/app/models/ContentItem;
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_2

    const/16 v16, 0x0

    move-object/from16 v7, v18

    move-object/from16 v8, v17

    move-object/from16 v19, v1

    move-object v1, v14

    .end local v14    # "back":Ljava/lang/String;
    .local v1, "back":Ljava/lang/String;
    move/from16 v14, p1

    move v4, v15

    .end local v15    # "id":I
    .local v4, "id":I
    move v15, v0

    :try_start_3
    invoke-direct/range {v7 .. v16}, Lcom/ultragol/app/models/ContentItem;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IZZ)V

    move-object/from16 v7, v18

    .line 70
    .local v7, "item":Lcom/ultragol/app/models/ContentItem;
    invoke-virtual {v7, v4}, Lcom/ultragol/app/models/ContentItem;->setTmdbId(I)V

    invoke-virtual {v7, v1}, Lcom/ultragol/app/models/ContentItem;->setBackdropUrl(Ljava/lang/String;)V

    .line 71
    invoke-interface {v2, v7}, Ljava/util/List;->add(Ljava/lang/Object;)Z
    :try_end_3
    .catch Ljava/lang/Exception; {:try_start_3 .. :try_end_3} :catch_1

    .line 72
    nop

    .end local v0    # "isNew":Z
    .end local v1    # "back":Ljava/lang/String;
    .end local v4    # "id":I
    .end local v5    # "o":Lorg/json/JSONObject;
    .end local v6    # "mov":Z
    .end local v7    # "item":Lcom/ultragol/app/models/ContentItem;
    .end local v9    # "g":Ljava/lang/String;
    .end local v10    # "y":Ljava/lang/String;
    .end local v11    # "r":Ljava/lang/String;
    .end local v12    # "post":Ljava/lang/String;
    .end local v13    # "ov":Ljava/lang/String;
    .end local v17    # "title":Ljava/lang/String;
    goto :goto_5

    :catch_1
    move-exception v0

    goto :goto_5

    .line 59
    .restart local v5    # "o":Lorg/json/JSONObject;
    .restart local v6    # "mov":Z
    .restart local v17    # "title":Ljava/lang/String;
    :cond_4
    move-object/from16 v19, v1

    goto :goto_5

    .line 72
    .end local v5    # "o":Lorg/json/JSONObject;
    .end local v6    # "mov":Z
    .end local v17    # "title":Ljava/lang/String;
    :catch_2
    move-exception v0

    move-object/from16 v19, v1

    .line 54
    :goto_5
    add-int/lit8 v3, v3, 0x1

    move-object/from16 v1, v19

    goto/16 :goto_0

    .line 74
    .end local v3    # "i":I
    :cond_5
    return-object v2
.end method

.method private static poster(Ljava/lang/String;)Ljava/lang/String;
    .locals 2
    .param p0, "p"    # Ljava/lang/String;

    .line 49
    if-eqz p0, :cond_1

    invoke-virtual {p0}, Ljava/lang/String;->isEmpty()Z

    move-result v0

    if-eqz v0, :cond_0

    goto :goto_0

    :cond_0
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "https://image.tmdb.org/t/p/w342"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    goto :goto_1

    :cond_1
    :goto_0
    const-string v0, ""

    :goto_1
    return-object v0
.end method

.method private static rating(D)Ljava/lang/String;
    .locals 3
    .param p0, "v"    # D

    .line 48
    const/4 v0, 0x1

    new-array v0, v0, [Ljava/lang/Object;

    const/4 v1, 0x0

    invoke-static {p0, p1}, Ljava/lang/Double;->valueOf(D)Ljava/lang/Double;

    move-result-object v2

    aput-object v2, v0, v1

    const-string v1, "%.1f"

    invoke-static {v1, v0}, Ljava/lang/String;->format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public static searchMulti(Ljava/lang/String;)Ljava/util/List;
    .locals 23
    .param p0, "query"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/String;",
            ")",
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/ContentItem;",
            ">;"
        }
    .end annotation

    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .line 136
    const-string v1, "movie"

    const-string v0, "UTF-8"

    move-object/from16 v2, p0

    invoke-static {v2, v0}, Ljava/net/URLEncoder;->encode(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    .line 137
    .local v3, "enc":Ljava/lang/String;
    new-instance v0, Lorg/json/JSONObject;

    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "/search/multi?query="

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v4

    invoke-virtual {v4, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v4

    const-string v5, "&language=es-MX&page=1"

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v4

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-static {v4}, Lcom/ultragol/app/network/TmdbApi;->fetch(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v4

    invoke-direct {v0, v4}, Lorg/json/JSONObject;-><init>(Ljava/lang/String;)V

    const-string v4, "results"

    invoke-virtual {v0, v4}, Lorg/json/JSONObject;->getJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v4

    .line 138
    .local v4, "arr":Lorg/json/JSONArray;
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    move-object v5, v0

    .line 139
    .local v5, "list":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    const/4 v0, 0x0

    move v6, v0

    .local v6, "i":I
    :goto_0
    invoke-virtual {v4}, Lorg/json/JSONArray;->length()I

    move-result v0

    if-ge v6, v0, :cond_6

    .line 141
    :try_start_0
    invoke-virtual {v4, v6}, Lorg/json/JSONArray;->getJSONObject(I)Lorg/json/JSONObject;

    move-result-object v0

    .line 142
    .local v0, "o":Lorg/json/JSONObject;
    const-string v7, "media_type"

    invoke-virtual {v0, v7, v1}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v7

    .line 143
    .local v7, "mt":Ljava/lang/String;
    const-string v8, "person"

    invoke-virtual {v8, v7}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v8

    if-eqz v8, :cond_0

    move-object/from16 v21, v1

    goto/16 :goto_4

    .line 144
    :cond_0
    invoke-virtual {v1, v7}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v8

    .line 145
    .local v8, "mov":Z
    if-eqz v8, :cond_1

    const-string v9, "title"

    goto :goto_1

    :cond_1
    const-string v9, "name"

    :goto_1
    invoke-virtual {v0, v9}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v9

    .line 146
    .local v9, "title":Ljava/lang/String;
    if-eqz v9, :cond_5

    invoke-virtual {v9}, Ljava/lang/String;->isEmpty()Z

    move-result v10

    if-eqz v10, :cond_2

    move-object/from16 v22, v0

    move-object/from16 v21, v1

    goto/16 :goto_4

    .line 147
    :cond_2
    const-string v10, "id"

    const/4 v11, 0x0

    invoke-virtual {v0, v10, v11}, Lorg/json/JSONObject;->optInt(Ljava/lang/String;I)I

    move-result v10

    .line 148
    .local v10, "id":I
    const-string v12, "genre_ids"

    invoke-virtual {v0, v12}, Lorg/json/JSONObject;->optJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v12

    invoke-static {v12}, Lcom/ultragol/app/network/TmdbApi;->genre(Lorg/json/JSONArray;)Ljava/lang/String;

    move-result-object v12

    .line 149
    .local v12, "g":Ljava/lang/String;
    if-eqz v8, :cond_3

    const-string v13, "release_date"

    goto :goto_2

    :cond_3
    const-string v13, "first_air_date"

    :goto_2
    invoke-virtual {v0, v13}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v13

    invoke-static {v13}, Lcom/ultragol/app/network/TmdbApi;->year(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v13

    .line 150
    .local v13, "y":Ljava/lang/String;
    const-string v14, "vote_average"

    move-object/from16 v20, v12

    .end local v12    # "g":Ljava/lang/String;
    .local v20, "g":Ljava/lang/String;
    const-wide/high16 v11, 0x401c000000000000L    # 7.0

    invoke-virtual {v0, v14, v11, v12}, Lorg/json/JSONObject;->optDouble(Ljava/lang/String;D)D

    move-result-wide v11

    invoke-static {v11, v12}, Lcom/ultragol/app/network/TmdbApi;->rating(D)Ljava/lang/String;

    move-result-object v14

    .line 151
    .local v14, "r":Ljava/lang/String;
    const-string v11, "poster_path"

    invoke-virtual {v0, v11}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v11

    invoke-static {v11}, Lcom/ultragol/app/network/TmdbApi;->poster(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v15

    .line 152
    .local v15, "post":Ljava/lang/String;
    const-string v11, "backdrop_path"

    invoke-virtual {v0, v11}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v11

    invoke-static {v11}, Lcom/ultragol/app/network/TmdbApi;->backdrop(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v11

    move-object v12, v11

    .line 153
    .local v12, "back":Ljava/lang/String;
    const-string v11, "overview"
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_1

    move-object/from16 v21, v1

    :try_start_1
    const-string v1, ""

    invoke-virtual {v0, v11, v1}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v16

    .line 154
    .local v16, "ov":Ljava/lang/String;
    if-eqz v8, :cond_4

    const/16 v17, 0x0

    goto :goto_3

    :cond_4
    const/4 v1, 0x1

    const/16 v17, 0x1

    .line 155
    .local v17, "type":I
    :goto_3
    new-instance v1, Lcom/ultragol/app/models/ContentItem;

    const/16 v18, 0x0

    const/16 v19, 0x0

    move v11, v10

    .end local v10    # "id":I
    .local v11, "id":I
    move-object v10, v1

    move-object/from16 v22, v0

    move v0, v11

    .end local v11    # "id":I
    .local v0, "id":I
    .local v22, "o":Lorg/json/JSONObject;
    move-object v11, v9

    move-object v2, v12

    move-object/from16 v12, v20

    .end local v20    # "g":Ljava/lang/String;
    .local v2, "back":Ljava/lang/String;
    .local v12, "g":Ljava/lang/String;
    invoke-direct/range {v10 .. v19}, Lcom/ultragol/app/models/ContentItem;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IZZ)V

    .line 156
    .local v1, "item":Lcom/ultragol/app/models/ContentItem;
    invoke-virtual {v1, v0}, Lcom/ultragol/app/models/ContentItem;->setTmdbId(I)V

    invoke-virtual {v1, v2}, Lcom/ultragol/app/models/ContentItem;->setBackdropUrl(Ljava/lang/String;)V

    .line 157
    invoke-interface {v5, v1}, Ljava/util/List;->add(Ljava/lang/Object;)Z
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0

    .line 158
    nop

    .end local v0    # "id":I
    .end local v1    # "item":Lcom/ultragol/app/models/ContentItem;
    .end local v2    # "back":Ljava/lang/String;
    .end local v7    # "mt":Ljava/lang/String;
    .end local v8    # "mov":Z
    .end local v9    # "title":Ljava/lang/String;
    .end local v12    # "g":Ljava/lang/String;
    .end local v13    # "y":Ljava/lang/String;
    .end local v14    # "r":Ljava/lang/String;
    .end local v15    # "post":Ljava/lang/String;
    .end local v16    # "ov":Ljava/lang/String;
    .end local v17    # "type":I
    .end local v22    # "o":Lorg/json/JSONObject;
    goto :goto_4

    :catch_0
    move-exception v0

    goto :goto_4

    .line 146
    .local v0, "o":Lorg/json/JSONObject;
    .restart local v7    # "mt":Ljava/lang/String;
    .restart local v8    # "mov":Z
    .restart local v9    # "title":Ljava/lang/String;
    :cond_5
    move-object/from16 v22, v0

    move-object/from16 v21, v1

    .end local v0    # "o":Lorg/json/JSONObject;
    .restart local v22    # "o":Lorg/json/JSONObject;
    goto :goto_4

    .line 158
    .end local v7    # "mt":Ljava/lang/String;
    .end local v8    # "mov":Z
    .end local v9    # "title":Ljava/lang/String;
    .end local v22    # "o":Lorg/json/JSONObject;
    :catch_1
    move-exception v0

    move-object/from16 v21, v1

    .line 139
    :goto_4
    add-int/lit8 v6, v6, 0x1

    move-object/from16 v2, p0

    move-object/from16 v1, v21

    goto/16 :goto_0

    .line 160
    .end local v6    # "i":I
    :cond_6
    return-object v5
.end method

.method private static year(Ljava/lang/String;)Ljava/lang/String;
    .locals 2
    .param p0, "d"    # Ljava/lang/String;

    .line 47
    if-eqz p0, :cond_0

    invoke-virtual {p0}, Ljava/lang/String;->length()I

    move-result v0

    const/4 v1, 0x4

    if-lt v0, v1, :cond_0

    const/4 v0, 0x0

    invoke-virtual {p0, v0, v1}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v0

    goto :goto_0

    :cond_0
    const-string v0, "2024"

    :goto_0
    return-object v0
.end method
