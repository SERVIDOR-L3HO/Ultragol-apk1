.class public Lcom/ultragol/app/network/StreamingApi$ServerData;
.super Ljava/lang/Object;
.source "StreamingApi.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/ultragol/app/network/StreamingApi;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "ServerData"
.end annotation


# instance fields
.field public embedUrl:Ljava/lang/String;

.field public final espanol:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Lcom/ultragol/app/network/StreamingApi$Server;",
            ">;"
        }
    .end annotation
.end field

.field public final latino:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Lcom/ultragol/app/network/StreamingApi$Server;",
            ">;"
        }
    .end annotation
.end field

.field public final subtitulado:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Lcom/ultragol/app/network/StreamingApi$Server;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method public constructor <init>()V
    .locals 1

    .line 23
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 24
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Lcom/ultragol/app/network/StreamingApi$ServerData;->latino:Ljava/util/List;

    .line 25
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Lcom/ultragol/app/network/StreamingApi$ServerData;->espanol:Ljava/util/List;

    .line 26
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Lcom/ultragol/app/network/StreamingApi$ServerData;->subtitulado:Ljava/util/List;

    .line 27
    const-string v0, ""

    iput-object v0, p0, Lcom/ultragol/app/network/StreamingApi$ServerData;->embedUrl:Ljava/lang/String;

    return-void
.end method
