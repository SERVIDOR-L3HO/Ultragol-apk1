.class public Lcom/ultragol/app/models/ContentItem;
.super Ljava/lang/Object;
.source "ContentItem.java"

# interfaces
.implements Ljava/io/Serializable;


# static fields
.field public static final TYPE_ANIME:I = 0x2

.field public static final TYPE_DORAMA:I = 0x3

.field public static final TYPE_MOVIE:I = 0x0

.field public static final TYPE_SERIES:I = 0x1

.field public static final TYPE_SPORT:I = 0x4

.field public static final TYPE_TV:I = 0x5


# instance fields
.field private backdropUrl:Ljava/lang/String;

.field private badge:Ljava/lang/String;

.field private contentType:I

.field private genre:Ljava/lang/String;

.field private isLive:Z

.field private isNew:Z

.field private overview:Ljava/lang/String;

.field private posterUrl:Ljava/lang/String;

.field private rating:Ljava/lang/String;

.field private streamUrl:Ljava/lang/String;

.field private title:Ljava/lang/String;

.field private tmdbId:I

.field private year:Ljava/lang/String;


# direct methods
.method public constructor <init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IZZ)V
    .locals 2
    .param p1, "title"    # Ljava/lang/String;
    .param p2, "genre"    # Ljava/lang/String;
    .param p3, "year"    # Ljava/lang/String;
    .param p4, "rating"    # Ljava/lang/String;
    .param p5, "posterUrl"    # Ljava/lang/String;
    .param p6, "overview"    # Ljava/lang/String;
    .param p7, "contentType"    # I
    .param p8, "isNew"    # Z
    .param p9, "isLive"    # Z

    .line 20
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 21
    iput-object p1, p0, Lcom/ultragol/app/models/ContentItem;->title:Ljava/lang/String;

    iput-object p2, p0, Lcom/ultragol/app/models/ContentItem;->genre:Ljava/lang/String;

    iput-object p3, p0, Lcom/ultragol/app/models/ContentItem;->year:Ljava/lang/String;

    .line 22
    iput-object p4, p0, Lcom/ultragol/app/models/ContentItem;->rating:Ljava/lang/String;

    const-string v0, ""

    if-eqz p5, :cond_0

    move-object v1, p5

    goto :goto_0

    :cond_0
    move-object v1, v0

    :goto_0
    iput-object v1, p0, Lcom/ultragol/app/models/ContentItem;->posterUrl:Ljava/lang/String;

    .line 23
    if-eqz p6, :cond_1

    move-object v1, p6

    goto :goto_1

    :cond_1
    move-object v1, v0

    :goto_1
    iput-object v1, p0, Lcom/ultragol/app/models/ContentItem;->overview:Ljava/lang/String;

    .line 24
    iput p7, p0, Lcom/ultragol/app/models/ContentItem;->contentType:I

    iput-boolean p8, p0, Lcom/ultragol/app/models/ContentItem;->isNew:Z

    iput-boolean p9, p0, Lcom/ultragol/app/models/ContentItem;->isLive:Z

    .line 25
    iput-object v0, p0, Lcom/ultragol/app/models/ContentItem;->backdropUrl:Ljava/lang/String;

    iput-object v0, p0, Lcom/ultragol/app/models/ContentItem;->streamUrl:Ljava/lang/String;

    const/4 v0, 0x0

    iput v0, p0, Lcom/ultragol/app/models/ContentItem;->tmdbId:I

    .line 26
    if-eqz p9, :cond_2

    const-string v0, "EN VIVO"

    iput-object v0, p0, Lcom/ultragol/app/models/ContentItem;->badge:Ljava/lang/String;

    goto :goto_2

    .line 27
    :cond_2
    if-eqz p8, :cond_3

    const-string v0, "NUEVO"

    iput-object v0, p0, Lcom/ultragol/app/models/ContentItem;->badge:Ljava/lang/String;

    goto :goto_2

    .line 28
    :cond_3
    const-string v0, "HD"

    iput-object v0, p0, Lcom/ultragol/app/models/ContentItem;->badge:Ljava/lang/String;

    .line 29
    :goto_2
    return-void
.end method


# virtual methods
.method public getBackdropUrl()Ljava/lang/String;
    .locals 1

    .line 34
    iget-object v0, p0, Lcom/ultragol/app/models/ContentItem;->backdropUrl:Ljava/lang/String;

    return-object v0
.end method

.method public getBadge()Ljava/lang/String;
    .locals 1

    .line 55
    iget-object v0, p0, Lcom/ultragol/app/models/ContentItem;->badge:Ljava/lang/String;

    return-object v0
.end method

.method public getContentType()I
    .locals 1

    .line 52
    iget v0, p0, Lcom/ultragol/app/models/ContentItem;->contentType:I

    return v0
.end method

.method public getGenre()Ljava/lang/String;
    .locals 1

    .line 47
    iget-object v0, p0, Lcom/ultragol/app/models/ContentItem;->genre:Ljava/lang/String;

    return-object v0
.end method

.method public getGenreYear()Ljava/lang/String;
    .locals 2

    .line 56
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    iget-object v1, p0, Lcom/ultragol/app/models/ContentItem;->genre:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v1, " \u2022 "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    iget-object v1, p0, Lcom/ultragol/app/models/ContentItem;->year:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getOverview()Ljava/lang/String;
    .locals 1

    .line 51
    iget-object v0, p0, Lcom/ultragol/app/models/ContentItem;->overview:Ljava/lang/String;

    return-object v0
.end method

.method public getPosterUrl()Ljava/lang/String;
    .locals 1

    .line 50
    iget-object v0, p0, Lcom/ultragol/app/models/ContentItem;->posterUrl:Ljava/lang/String;

    return-object v0
.end method

.method public getRating()Ljava/lang/String;
    .locals 1

    .line 49
    iget-object v0, p0, Lcom/ultragol/app/models/ContentItem;->rating:Ljava/lang/String;

    return-object v0
.end method

.method public getRatingDisplay()Ljava/lang/String;
    .locals 2

    .line 57
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "\u2605 "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    iget-object v1, p0, Lcom/ultragol/app/models/ContentItem;->rating:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getStreamUrl()Ljava/lang/String;
    .locals 3

    .line 38
    iget-object v0, p0, Lcom/ultragol/app/models/ContentItem;->streamUrl:Ljava/lang/String;

    invoke-virtual {v0}, Ljava/lang/String;->isEmpty()Z

    move-result v0

    if-nez v0, :cond_0

    iget-object v0, p0, Lcom/ultragol/app/models/ContentItem;->streamUrl:Ljava/lang/String;

    return-object v0

    .line 39
    :cond_0
    iget v0, p0, Lcom/ultragol/app/models/ContentItem;->tmdbId:I

    if-nez v0, :cond_1

    const-string v0, "https://unlimplay.com/"

    return-object v0

    .line 40
    :cond_1
    const-string v0, "?sub=es&lang=es&audio=es&autoplay=1"

    .line 41
    .local v0, "p":Ljava/lang/String;
    iget v1, p0, Lcom/ultragol/app/models/ContentItem;->contentType:I

    if-nez v1, :cond_2

    .line 42
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "https://unlimplay.com/play/embed/movie/"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    iget v2, p0, Lcom/ultragol/app/models/ContentItem;->tmdbId:I

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    goto :goto_0

    .line 43
    :cond_2
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "https://unlimplay.com/play/embed/tv/"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    iget v2, p0, Lcom/ultragol/app/models/ContentItem;->tmdbId:I

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string v2, "/1/1"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    .line 41
    :goto_0
    return-object v1
.end method

.method public getTitle()Ljava/lang/String;
    .locals 1

    .line 46
    iget-object v0, p0, Lcom/ultragol/app/models/ContentItem;->title:Ljava/lang/String;

    return-object v0
.end method

.method public getTmdbId()I
    .locals 1

    .line 32
    iget v0, p0, Lcom/ultragol/app/models/ContentItem;->tmdbId:I

    return v0
.end method

.method public getYear()Ljava/lang/String;
    .locals 1

    .line 48
    iget-object v0, p0, Lcom/ultragol/app/models/ContentItem;->year:Ljava/lang/String;

    return-object v0
.end method

.method public isLive()Z
    .locals 1

    .line 54
    iget-boolean v0, p0, Lcom/ultragol/app/models/ContentItem;->isLive:Z

    return v0
.end method

.method public isNew()Z
    .locals 1

    .line 53
    iget-boolean v0, p0, Lcom/ultragol/app/models/ContentItem;->isNew:Z

    return v0
.end method

.method public setBackdropUrl(Ljava/lang/String;)V
    .locals 1
    .param p1, "v"    # Ljava/lang/String;

    .line 33
    if-eqz p1, :cond_0

    move-object v0, p1

    goto :goto_0

    :cond_0
    const-string v0, ""

    :goto_0
    iput-object v0, p0, Lcom/ultragol/app/models/ContentItem;->backdropUrl:Ljava/lang/String;

    return-void
.end method

.method public setStreamUrl(Ljava/lang/String;)V
    .locals 1
    .param p1, "v"    # Ljava/lang/String;

    .line 35
    if-eqz p1, :cond_0

    move-object v0, p1

    goto :goto_0

    :cond_0
    const-string v0, ""

    :goto_0
    iput-object v0, p0, Lcom/ultragol/app/models/ContentItem;->streamUrl:Ljava/lang/String;

    return-void
.end method

.method public setTmdbId(I)V
    .locals 0
    .param p1, "v"    # I

    .line 31
    iput p1, p0, Lcom/ultragol/app/models/ContentItem;->tmdbId:I

    return-void
.end method
