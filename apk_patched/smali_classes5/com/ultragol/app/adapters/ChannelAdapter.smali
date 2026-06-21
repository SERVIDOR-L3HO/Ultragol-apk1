.class public Lcom/ultragol/app/adapters/ChannelAdapter;
.super Landroidx/recyclerview/widget/RecyclerView$Adapter;
.source "ChannelAdapter.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/ultragol/app/adapters/ChannelAdapter$OnChannelClick;,
        Lcom/ultragol/app/adapters/ChannelAdapter$VH;
    }
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Landroidx/recyclerview/widget/RecyclerView$Adapter<",
        "Lcom/ultragol/app/adapters/ChannelAdapter$VH;",
        ">;"
    }
.end annotation


# instance fields
.field private final ctx:Landroid/content/Context;

.field private final items:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/Channel;",
            ">;"
        }
    .end annotation
.end field

.field private final listener:Lcom/ultragol/app/adapters/ChannelAdapter$OnChannelClick;


# direct methods
.method public constructor <init>(Landroid/content/Context;Ljava/util/List;Lcom/ultragol/app/adapters/ChannelAdapter$OnChannelClick;)V
    .locals 0
    .param p1, "ctx"    # Landroid/content/Context;
    .param p3, "listener"    # Lcom/ultragol/app/adapters/ChannelAdapter$OnChannelClick;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/content/Context;",
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/Channel;",
            ">;",
            "Lcom/ultragol/app/adapters/ChannelAdapter$OnChannelClick;",
            ")V"
        }
    .end annotation

    .line 21
    .local p2, "items":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/Channel;>;"
    invoke-direct {p0}, Landroidx/recyclerview/widget/RecyclerView$Adapter;-><init>()V

    .line 22
    iput-object p1, p0, Lcom/ultragol/app/adapters/ChannelAdapter;->ctx:Landroid/content/Context;

    iput-object p2, p0, Lcom/ultragol/app/adapters/ChannelAdapter;->items:Ljava/util/List;

    iput-object p3, p0, Lcom/ultragol/app/adapters/ChannelAdapter;->listener:Lcom/ultragol/app/adapters/ChannelAdapter$OnChannelClick;

    .line 23
    return-void
.end method


# virtual methods
.method public getItemCount()I
    .locals 1

    .line 47
    iget-object v0, p0, Lcom/ultragol/app/adapters/ChannelAdapter;->items:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->size()I

    move-result v0

    return v0
.end method

.method synthetic lambda$onBindViewHolder$0$com-ultragol-app-adapters-ChannelAdapter(Lcom/ultragol/app/models/Channel;Landroid/view/View;)V
    .locals 1
    .param p1, "ch"    # Lcom/ultragol/app/models/Channel;
    .param p2, "v"    # Landroid/view/View;

    .line 44
    iget-object v0, p0, Lcom/ultragol/app/adapters/ChannelAdapter;->listener:Lcom/ultragol/app/adapters/ChannelAdapter$OnChannelClick;

    if-eqz v0, :cond_0

    invoke-interface {v0, p1}, Lcom/ultragol/app/adapters/ChannelAdapter$OnChannelClick;->onClick(Lcom/ultragol/app/models/Channel;)V

    :cond_0
    return-void
.end method

.method public bridge synthetic onBindViewHolder(Landroidx/recyclerview/widget/RecyclerView$ViewHolder;I)V
    .locals 0
    .annotation system Ldalvik/annotation/MethodParameters;
        accessFlags = {
            0x1000,
            0x1000
        }
        names = {
            null,
            null
        }
    .end annotation

    .line 14
    check-cast p1, Lcom/ultragol/app/adapters/ChannelAdapter$VH;

    invoke-virtual {p0, p1, p2}, Lcom/ultragol/app/adapters/ChannelAdapter;->onBindViewHolder(Lcom/ultragol/app/adapters/ChannelAdapter$VH;I)V

    return-void
.end method

.method public onBindViewHolder(Lcom/ultragol/app/adapters/ChannelAdapter$VH;I)V
    .locals 4
    .param p1, "h"    # Lcom/ultragol/app/adapters/ChannelAdapter$VH;
    .param p2, "pos"    # I

    .line 32
    iget-object v0, p0, Lcom/ultragol/app/adapters/ChannelAdapter;->items:Ljava/util/List;

    invoke-interface {v0, p2}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/ultragol/app/models/Channel;

    .line 33
    .local v0, "ch":Lcom/ultragol/app/models/Channel;
    iget-object v1, p1, Lcom/ultragol/app/adapters/ChannelAdapter$VH;->name:Landroid/widget/TextView;

    invoke-virtual {v0}, Lcom/ultragol/app/models/Channel;->getName()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 34
    iget-object v1, p1, Lcom/ultragol/app/adapters/ChannelAdapter$VH;->country:Landroid/widget/TextView;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v0}, Lcom/ultragol/app/models/Channel;->getFlag()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    const-string v3, " "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v0}, Lcom/ultragol/app/models/Channel;->getCountry()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 35
    iget-object v1, p1, Lcom/ultragol/app/adapters/ChannelAdapter$VH;->category:Landroid/widget/TextView;

    invoke-virtual {v0}, Lcom/ultragol/app/models/Channel;->getCategory()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 36
    iget-object v1, p1, Lcom/ultragol/app/adapters/ChannelAdapter$VH;->liveBadge:Landroid/widget/TextView;

    const-string v2, "\u25cf EN VIVO"

    invoke-virtual {v1, v2}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 37
    invoke-virtual {v0}, Lcom/ultragol/app/models/Channel;->getLogo()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/String;->isEmpty()Z

    move-result v1

    if-nez v1, :cond_0

    .line 38
    iget-object v1, p0, Lcom/ultragol/app/adapters/ChannelAdapter;->ctx:Landroid/content/Context;

    invoke-static {v1}, Lcom/bumptech/glide/Glide;->with(Landroid/content/Context;)Lcom/bumptech/glide/RequestManager;

    move-result-object v1

    invoke-virtual {v0}, Lcom/ultragol/app/models/Channel;->getLogo()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/bumptech/glide/RequestManager;->load(Ljava/lang/String;)Lcom/bumptech/glide/RequestBuilder;

    move-result-object v1

    .line 39
    invoke-static {}, Lcom/bumptech/glide/load/resource/drawable/DrawableTransitionOptions;->withCrossFade()Lcom/bumptech/glide/load/resource/drawable/DrawableTransitionOptions;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/bumptech/glide/RequestBuilder;->transition(Lcom/bumptech/glide/TransitionOptions;)Lcom/bumptech/glide/RequestBuilder;

    move-result-object v1

    sget v2, Lcom/ultragol/app/R$drawable;->ic_channel_placeholder:I

    .line 40
    invoke-virtual {v1, v2}, Lcom/bumptech/glide/RequestBuilder;->placeholder(I)Lcom/bumptech/glide/request/BaseRequestOptions;

    move-result-object v1

    check-cast v1, Lcom/bumptech/glide/RequestBuilder;

    sget v2, Lcom/ultragol/app/R$drawable;->ic_channel_placeholder:I

    .line 41
    invoke-virtual {v1, v2}, Lcom/bumptech/glide/RequestBuilder;->error(I)Lcom/bumptech/glide/request/BaseRequestOptions;

    move-result-object v1

    check-cast v1, Lcom/bumptech/glide/RequestBuilder;

    .line 42
    invoke-virtual {v1}, Lcom/bumptech/glide/RequestBuilder;->centerInside()Lcom/bumptech/glide/request/BaseRequestOptions;

    move-result-object v1

    check-cast v1, Lcom/bumptech/glide/RequestBuilder;

    iget-object v2, p1, Lcom/ultragol/app/adapters/ChannelAdapter$VH;->logo:Landroid/widget/ImageView;

    invoke-virtual {v1, v2}, Lcom/bumptech/glide/RequestBuilder;->into(Landroid/widget/ImageView;)Lcom/bumptech/glide/request/target/ViewTarget;

    goto :goto_0

    .line 43
    :cond_0
    iget-object v1, p1, Lcom/ultragol/app/adapters/ChannelAdapter$VH;->logo:Landroid/widget/ImageView;

    sget v2, Lcom/ultragol/app/R$drawable;->ic_channel_placeholder:I

    invoke-virtual {v1, v2}, Landroid/widget/ImageView;->setImageResource(I)V

    .line 44
    :goto_0
    iget-object v1, p1, Lcom/ultragol/app/adapters/ChannelAdapter$VH;->itemView:Landroid/view/View;

    new-instance v2, Lcom/ultragol/app/adapters/ChannelAdapter$$ExternalSyntheticLambda0;

    invoke-direct {v2, p0, v0}, Lcom/ultragol/app/adapters/ChannelAdapter$$ExternalSyntheticLambda0;-><init>(Lcom/ultragol/app/adapters/ChannelAdapter;Lcom/ultragol/app/models/Channel;)V

    invoke-virtual {v1, v2}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 45
    return-void
.end method

.method public bridge synthetic onCreateViewHolder(Landroid/view/ViewGroup;I)Landroidx/recyclerview/widget/RecyclerView$ViewHolder;
    .locals 0
    .annotation system Ldalvik/annotation/MethodParameters;
        accessFlags = {
            0x1000,
            0x1000
        }
        names = {
            null,
            null
        }
    .end annotation

    .line 14
    invoke-virtual {p0, p1, p2}, Lcom/ultragol/app/adapters/ChannelAdapter;->onCreateViewHolder(Landroid/view/ViewGroup;I)Lcom/ultragol/app/adapters/ChannelAdapter$VH;

    move-result-object p1

    return-object p1
.end method

.method public onCreateViewHolder(Landroid/view/ViewGroup;I)Lcom/ultragol/app/adapters/ChannelAdapter$VH;
    .locals 4
    .param p1, "parent"    # Landroid/view/ViewGroup;
    .param p2, "viewType"    # I

    .line 27
    new-instance v0, Lcom/ultragol/app/adapters/ChannelAdapter$VH;

    iget-object v1, p0, Lcom/ultragol/app/adapters/ChannelAdapter;->ctx:Landroid/content/Context;

    invoke-static {v1}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    move-result-object v1

    sget v2, Lcom/ultragol/app/R$layout;->item_channel:I

    const/4 v3, 0x0

    invoke-virtual {v1, v2, p1, v3}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    move-result-object v1

    invoke-direct {v0, v1}, Lcom/ultragol/app/adapters/ChannelAdapter$VH;-><init>(Landroid/view/View;)V

    return-object v0
.end method
