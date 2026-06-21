.class public Lcom/ultragol/app/models/Channel;
.super Ljava/lang/Object;
.source "Channel.java"


# instance fields
.field private category:Ljava/lang/String;

.field private country:Ljava/lang/String;

.field private flag:Ljava/lang/String;

.field private id:Ljava/lang/String;

.field private logo:Ljava/lang/String;

.field private name:Ljava/lang/String;

.field private playerUrl:Ljava/lang/String;


# direct methods
.method public constructor <init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    .locals 1
    .param p1, "id"    # Ljava/lang/String;
    .param p2, "name"    # Ljava/lang/String;
    .param p3, "country"    # Ljava/lang/String;
    .param p4, "flag"    # Ljava/lang/String;
    .param p5, "logo"    # Ljava/lang/String;
    .param p6, "playerUrl"    # Ljava/lang/String;
    .param p7, "category"    # Ljava/lang/String;

    .line 7
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 8
    iput-object p1, p0, Lcom/ultragol/app/models/Channel;->id:Ljava/lang/String;

    iput-object p2, p0, Lcom/ultragol/app/models/Channel;->name:Ljava/lang/String;

    iput-object p3, p0, Lcom/ultragol/app/models/Channel;->country:Ljava/lang/String;

    .line 9
    iput-object p4, p0, Lcom/ultragol/app/models/Channel;->flag:Ljava/lang/String;

    if-eqz p5, :cond_0

    move-object v0, p5

    goto :goto_0

    :cond_0
    const-string v0, ""

    :goto_0
    iput-object v0, p0, Lcom/ultragol/app/models/Channel;->logo:Ljava/lang/String;

    .line 10
    iput-object p6, p0, Lcom/ultragol/app/models/Channel;->playerUrl:Ljava/lang/String;

    iput-object p7, p0, Lcom/ultragol/app/models/Channel;->category:Ljava/lang/String;

    .line 11
    return-void
.end method


# virtual methods
.method public getCategory()Ljava/lang/String;
    .locals 1

    .line 19
    iget-object v0, p0, Lcom/ultragol/app/models/Channel;->category:Ljava/lang/String;

    return-object v0
.end method

.method public getCountry()Ljava/lang/String;
    .locals 1

    .line 15
    iget-object v0, p0, Lcom/ultragol/app/models/Channel;->country:Ljava/lang/String;

    return-object v0
.end method

.method public getDisplayName()Ljava/lang/String;
    .locals 2

    .line 20
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    iget-object v1, p0, Lcom/ultragol/app/models/Channel;->flag:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v1, " "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    iget-object v1, p0, Lcom/ultragol/app/models/Channel;->name:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getFlag()Ljava/lang/String;
    .locals 1

    .line 16
    iget-object v0, p0, Lcom/ultragol/app/models/Channel;->flag:Ljava/lang/String;

    return-object v0
.end method

.method public getId()Ljava/lang/String;
    .locals 1

    .line 13
    iget-object v0, p0, Lcom/ultragol/app/models/Channel;->id:Ljava/lang/String;

    return-object v0
.end method

.method public getLogo()Ljava/lang/String;
    .locals 1

    .line 17
    iget-object v0, p0, Lcom/ultragol/app/models/Channel;->logo:Ljava/lang/String;

    return-object v0
.end method

.method public getName()Ljava/lang/String;
    .locals 1

    .line 14
    iget-object v0, p0, Lcom/ultragol/app/models/Channel;->name:Ljava/lang/String;

    return-object v0
.end method

.method public getPlayerUrl()Ljava/lang/String;
    .locals 1

    .line 18
    iget-object v0, p0, Lcom/ultragol/app/models/Channel;->playerUrl:Ljava/lang/String;

    return-object v0
.end method
