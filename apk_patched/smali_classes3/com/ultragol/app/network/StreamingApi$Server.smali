.class public Lcom/ultragol/app/network/StreamingApi$Server;
.super Ljava/lang/Object;
.source "StreamingApi.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/ultragol/app/network/StreamingApi;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "Server"
.end annotation


# instance fields
.field public final name:Ljava/lang/String;

.field public final tipo:Ljava/lang/String;

.field public final url:Ljava/lang/String;


# direct methods
.method public constructor <init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    .locals 0
    .param p1, "name"    # Ljava/lang/String;
    .param p2, "url"    # Ljava/lang/String;
    .param p3, "tipo"    # Ljava/lang/String;

    .line 18
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 19
    iput-object p1, p0, Lcom/ultragol/app/network/StreamingApi$Server;->name:Ljava/lang/String;

    iput-object p2, p0, Lcom/ultragol/app/network/StreamingApi$Server;->url:Ljava/lang/String;

    iput-object p3, p0, Lcom/ultragol/app/network/StreamingApi$Server;->tipo:Ljava/lang/String;

    .line 20
    return-void
.end method
