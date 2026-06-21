.class public Lcom/ultragol/app/MyListManager;
.super Ljava/lang/Object;
.source "MyListManager.java"


# static fields
.field private static final KEY:Ljava/lang/String; = "items"

.field private static final PREFS:Ljava/lang/String; = "mylist"


# direct methods
.method public constructor <init>()V
    .locals 0

    .line 11
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static add(Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;)V
    .locals 5
    .param p0, "ctx"    # Landroid/content/Context;
    .param p1, "item"    # Lcom/ultragol/app/models/ContentItem;

    .line 25
    invoke-static {p0}, Lcom/ultragol/app/MyListManager;->getAll(Landroid/content/Context;)Ljava/util/List;

    move-result-object v0

    .line 26
    .local v0, "list":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    invoke-interface {v0}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_0
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_1

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/ultragol/app/models/ContentItem;

    .local v2, "c":Lcom/ultragol/app/models/ContentItem;
    invoke-virtual {v2}, Lcom/ultragol/app/models/ContentItem;->getTmdbId()I

    move-result v3

    invoke-virtual {p1}, Lcom/ultragol/app/models/ContentItem;->getTmdbId()I

    move-result v4

    if-ne v3, v4, :cond_0

    return-void

    .end local v2    # "c":Lcom/ultragol/app/models/ContentItem;
    :cond_0
    goto :goto_0

    .line 27
    :cond_1
    const/4 v1, 0x0

    invoke-interface {v0, v1, p1}, Ljava/util/List;->add(ILjava/lang/Object;)V

    .line 28
    invoke-static {p0, v0}, Lcom/ultragol/app/MyListManager;->save(Landroid/content/Context;Ljava/util/List;)V

    .line 29
    return-void
.end method

.method public static getAll(Landroid/content/Context;)Ljava/util/List;
    .locals 18
    .param p0, "ctx"    # Landroid/content/Context;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/content/Context;",
            ")",
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/ContentItem;",
            ">;"
        }
    .end annotation

    .line 38
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    move-object v1, v0

    .line 40
    .local v1, "list":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    :try_start_0
    const-string v0, "mylist"
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_1

    const/4 v2, 0x0

    move-object/from16 v3, p0

    :try_start_1
    invoke-virtual {v3, v0, v2}, Landroid/content/Context;->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;

    move-result-object v0

    const-string v4, "items"

    const-string v5, "[]"

    invoke-interface {v0, v4, v5}, Landroid/content/SharedPreferences;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 41
    .local v0, "json":Ljava/lang/String;
    new-instance v4, Lorg/json/JSONArray;

    invoke-direct {v4, v0}, Lorg/json/JSONArray;-><init>(Ljava/lang/String;)V

    .line 42
    .local v4, "arr":Lorg/json/JSONArray;
    const/4 v5, 0x0

    .local v5, "i":I
    :goto_0
    invoke-virtual {v4}, Lorg/json/JSONArray;->length()I

    move-result v6

    if-ge v5, v6, :cond_0

    .line 43
    invoke-virtual {v4, v5}, Lorg/json/JSONArray;->getJSONObject(I)Lorg/json/JSONObject;

    move-result-object v6

    .line 44
    .local v6, "o":Lorg/json/JSONObject;
    new-instance v17, Lcom/ultragol/app/models/ContentItem;

    const-string v7, "title"

    .line 45
    invoke-virtual {v6, v7}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v8

    const-string v7, "genre"

    invoke-virtual {v6, v7}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v9

    const-string v7, "year"

    invoke-virtual {v6, v7}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v10

    const-string v7, "rating"

    .line 46
    invoke-virtual {v6, v7}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v11

    const-string v7, "posterUrl"

    invoke-virtual {v6, v7}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v12

    const-string v7, "overview"

    invoke-virtual {v6, v7}, Lorg/json/JSONObject;->optString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v13

    const-string v7, "type"

    .line 47
    invoke-virtual {v6, v7, v2}, Lorg/json/JSONObject;->optInt(Ljava/lang/String;I)I

    move-result v14

    const/4 v15, 0x0

    const/16 v16, 0x0

    move-object/from16 v7, v17

    invoke-direct/range {v7 .. v16}, Lcom/ultragol/app/models/ContentItem;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IZZ)V

    move-object/from16 v7, v17

    .line 48
    .local v7, "item":Lcom/ultragol/app/models/ContentItem;
    const-string v8, "tmdbId"

    invoke-virtual {v6, v8, v2}, Lorg/json/JSONObject;->optInt(Ljava/lang/String;I)I

    move-result v8

    invoke-virtual {v7, v8}, Lcom/ultragol/app/models/ContentItem;->setTmdbId(I)V

    .line 49
    const-string v8, "backdropUrl"

    const-string v9, ""

    invoke-virtual {v6, v8, v9}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v8

    invoke-virtual {v7, v8}, Lcom/ultragol/app/models/ContentItem;->setBackdropUrl(Ljava/lang/String;)V

    .line 50
    invoke-interface {v1, v7}, Ljava/util/List;->add(Ljava/lang/Object;)Z
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0

    .line 42
    nop

    .end local v6    # "o":Lorg/json/JSONObject;
    .end local v7    # "item":Lcom/ultragol/app/models/ContentItem;
    add-int/lit8 v5, v5, 0x1

    goto :goto_0

    .end local v0    # "json":Ljava/lang/String;
    .end local v4    # "arr":Lorg/json/JSONArray;
    .end local v5    # "i":I
    :cond_0
    goto :goto_1

    .line 52
    :catch_0
    move-exception v0

    goto :goto_1

    :catch_1
    move-exception v0

    move-object/from16 v3, p0

    :goto_1
    nop

    .line 53
    return-object v1
.end method

.method public static isInList(Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;)Z
    .locals 4
    .param p0, "ctx"    # Landroid/content/Context;
    .param p1, "item"    # Lcom/ultragol/app/models/ContentItem;

    .line 20
    invoke-static {p0}, Lcom/ultragol/app/MyListManager;->getAll(Landroid/content/Context;)Ljava/util/List;

    move-result-object v0

    invoke-interface {v0}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v0

    :goto_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_1

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/ultragol/app/models/ContentItem;

    .local v1, "c":Lcom/ultragol/app/models/ContentItem;
    invoke-virtual {v1}, Lcom/ultragol/app/models/ContentItem;->getTmdbId()I

    move-result v2

    invoke-virtual {p1}, Lcom/ultragol/app/models/ContentItem;->getTmdbId()I

    move-result v3

    if-ne v2, v3, :cond_0

    const/4 v0, 0x1

    return v0

    .end local v1    # "c":Lcom/ultragol/app/models/ContentItem;
    :cond_0
    goto :goto_0

    .line 21
    :cond_1
    const/4 v0, 0x0

    return v0
.end method

.method static synthetic lambda$remove$0(Lcom/ultragol/app/models/ContentItem;Lcom/ultragol/app/models/ContentItem;)Z
    .locals 2
    .param p0, "item"    # Lcom/ultragol/app/models/ContentItem;
    .param p1, "c"    # Lcom/ultragol/app/models/ContentItem;

    .line 33
    invoke-virtual {p1}, Lcom/ultragol/app/models/ContentItem;->getTmdbId()I

    move-result v0

    invoke-virtual {p0}, Lcom/ultragol/app/models/ContentItem;->getTmdbId()I

    move-result v1

    if-ne v0, v1, :cond_0

    const/4 v0, 0x1

    goto :goto_0

    :cond_0
    const/4 v0, 0x0

    :goto_0
    return v0
.end method

.method public static remove(Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;)V
    .locals 2
    .param p0, "ctx"    # Landroid/content/Context;
    .param p1, "item"    # Lcom/ultragol/app/models/ContentItem;

    .line 32
    invoke-static {p0}, Lcom/ultragol/app/MyListManager;->getAll(Landroid/content/Context;)Ljava/util/List;

    move-result-object v0

    .line 33
    .local v0, "list":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    new-instance v1, Lcom/ultragol/app/MyListManager$$ExternalSyntheticLambda0;

    invoke-direct {v1, p1}, Lcom/ultragol/app/MyListManager$$ExternalSyntheticLambda0;-><init>(Lcom/ultragol/app/models/ContentItem;)V

    invoke-interface {v0, v1}, Ljava/util/List;->removeIf(Ljava/util/function/Predicate;)Z

    .line 34
    invoke-static {p0, v0}, Lcom/ultragol/app/MyListManager;->save(Landroid/content/Context;Ljava/util/List;)V

    .line 35
    return-void
.end method

.method private static save(Landroid/content/Context;Ljava/util/List;)V
    .locals 6
    .param p0, "ctx"    # Landroid/content/Context;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/content/Context;",
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/ContentItem;",
            ">;)V"
        }
    .end annotation

    .line 58
    .local p1, "list":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    :try_start_0
    new-instance v0, Lorg/json/JSONArray;

    invoke-direct {v0}, Lorg/json/JSONArray;-><init>()V

    .line 59
    .local v0, "arr":Lorg/json/JSONArray;
    invoke-interface {p1}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_0
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_0

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/ultragol/app/models/ContentItem;

    .line 60
    .local v2, "item":Lcom/ultragol/app/models/ContentItem;
    new-instance v3, Lorg/json/JSONObject;

    invoke-direct {v3}, Lorg/json/JSONObject;-><init>()V

    .line 61
    .local v3, "o":Lorg/json/JSONObject;
    const-string v4, "title"

    invoke-virtual {v2}, Lcom/ultragol/app/models/ContentItem;->getTitle()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {v3, v4, v5}, Lorg/json/JSONObject;->put(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;

    const-string v4, "genre"

    invoke-virtual {v2}, Lcom/ultragol/app/models/ContentItem;->getGenre()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {v3, v4, v5}, Lorg/json/JSONObject;->put(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;

    .line 62
    const-string v4, "year"

    invoke-virtual {v2}, Lcom/ultragol/app/models/ContentItem;->getYear()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {v3, v4, v5}, Lorg/json/JSONObject;->put(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;

    const-string v4, "rating"

    invoke-virtual {v2}, Lcom/ultragol/app/models/ContentItem;->getRating()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {v3, v4, v5}, Lorg/json/JSONObject;->put(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;

    .line 63
    const-string v4, "posterUrl"

    invoke-virtual {v2}, Lcom/ultragol/app/models/ContentItem;->getPosterUrl()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {v3, v4, v5}, Lorg/json/JSONObject;->put(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;

    const-string v4, "overview"

    invoke-virtual {v2}, Lcom/ultragol/app/models/ContentItem;->getOverview()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {v3, v4, v5}, Lorg/json/JSONObject;->put(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;

    .line 64
    const-string v4, "type"

    invoke-virtual {v2}, Lcom/ultragol/app/models/ContentItem;->getContentType()I

    move-result v5

    invoke-virtual {v3, v4, v5}, Lorg/json/JSONObject;->put(Ljava/lang/String;I)Lorg/json/JSONObject;

    const-string v4, "tmdbId"

    invoke-virtual {v2}, Lcom/ultragol/app/models/ContentItem;->getTmdbId()I

    move-result v5

    invoke-virtual {v3, v4, v5}, Lorg/json/JSONObject;->put(Ljava/lang/String;I)Lorg/json/JSONObject;

    .line 65
    const-string v4, "backdropUrl"

    invoke-virtual {v2}, Lcom/ultragol/app/models/ContentItem;->getBackdropUrl()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {v3, v4, v5}, Lorg/json/JSONObject;->put(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;

    .line 66
    invoke-virtual {v0, v3}, Lorg/json/JSONArray;->put(Ljava/lang/Object;)Lorg/json/JSONArray;

    .line 67
    nop

    .end local v2    # "item":Lcom/ultragol/app/models/ContentItem;
    .end local v3    # "o":Lorg/json/JSONObject;
    goto :goto_0

    .line 68
    :cond_0
    const-string v1, "mylist"

    const/4 v2, 0x0

    invoke-virtual {p0, v1, v2}, Landroid/content/Context;->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;

    move-result-object v1

    invoke-interface {v1}, Landroid/content/SharedPreferences;->edit()Landroid/content/SharedPreferences$Editor;

    move-result-object v1

    const-string v2, "items"

    invoke-virtual {v0}, Lorg/json/JSONArray;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-interface {v1, v2, v3}, Landroid/content/SharedPreferences$Editor;->putString(Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;

    move-result-object v1

    invoke-interface {v1}, Landroid/content/SharedPreferences$Editor;->apply()V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .end local v0    # "arr":Lorg/json/JSONArray;
    goto :goto_1

    .line 69
    :catch_0
    move-exception v0

    :goto_1
    nop

    .line 70
    return-void
.end method

.method public static toggle(Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;)V
    .locals 1
    .param p0, "ctx"    # Landroid/content/Context;
    .param p1, "item"    # Lcom/ultragol/app/models/ContentItem;

    .line 16
    invoke-static {p0, p1}, Lcom/ultragol/app/MyListManager;->isInList(Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;)Z

    move-result v0

    if-eqz v0, :cond_0

    invoke-static {p0, p1}, Lcom/ultragol/app/MyListManager;->remove(Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;)V

    goto :goto_0

    :cond_0
    invoke-static {p0, p1}, Lcom/ultragol/app/MyListManager;->add(Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;)V

    .line 17
    :goto_0
    return-void
.end method
