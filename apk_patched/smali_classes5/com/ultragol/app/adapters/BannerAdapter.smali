.class public Lcom/ultragol/app/adapters/BannerAdapter;
.super Landroidx/recyclerview/widget/RecyclerView$Adapter;
.source "BannerAdapter.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/ultragol/app/adapters/BannerAdapter$VH;
    }
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Landroidx/recyclerview/widget/RecyclerView$Adapter<",
        "Lcom/ultragol/app/adapters/BannerAdapter$VH;",
        ">;"
    }
.end annotation


# instance fields
.field private final ctx:Landroid/content/Context;

.field private final items:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/ContentItem;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method public constructor <init>(Landroid/content/Context;Ljava/util/List;)V
    .locals 0
    .param p1, "ctx"    # Landroid/content/Context;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/content/Context;",
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/ContentItem;",
            ">;)V"
        }
    .end annotation

    .line 20
    .local p2, "items":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    invoke-direct {p0}, Landroidx/recyclerview/widget/RecyclerView$Adapter;-><init>()V

    .line 21
    iput-object p1, p0, Lcom/ultragol/app/adapters/BannerAdapter;->ctx:Landroid/content/Context;

    iput-object p2, p0, Lcom/ultragol/app/adapters/BannerAdapter;->items:Ljava/util/List;

    .line 22
    return-void
.end method


# virtual methods
.method public getItemCount()I
    .locals 1

    .line 99
    iget-object v0, p0, Lcom/ultragol/app/adapters/BannerAdapter;->items:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->size()I

    move-result v0

    return v0
.end method

.method synthetic lambda$onBindViewHolder$0$com-ultragol-app-adapters-BannerAdapter(Lcom/ultragol/app/models/ContentItem;Landroid/view/View;)V
    .locals 3
    .param p1, "item"    # Lcom/ultragol/app/models/ContentItem;
    .param p2, "v"    # Landroid/view/View;

    .line 90
    new-instance v0, Landroid/content/Intent;

    iget-object v1, p0, Lcom/ultragol/app/adapters/BannerAdapter;->ctx:Landroid/content/Context;

    const-class v2, Lcom/ultragol/app/DetailActivity;

    invoke-direct {v0, v1, v2}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    .line 91
    .local v0, "i":Landroid/content/Intent;
    const-string v1, "item"

    invoke-virtual {v0, v1, p1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/io/Serializable;)Landroid/content/Intent;

    .line 92
    iget-object v1, p0, Lcom/ultragol/app/adapters/BannerAdapter;->ctx:Landroid/content/Context;

    invoke-virtual {v1, v0}, Landroid/content/Context;->startActivity(Landroid/content/Intent;)V

    .line 93
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

    .line 16
    check-cast p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;

    invoke-virtual {p0, p1, p2}, Lcom/ultragol/app/adapters/BannerAdapter;->onBindViewHolder(Lcom/ultragol/app/adapters/BannerAdapter$VH;I)V

    return-void
.end method

.method public onBindViewHolder(Lcom/ultragol/app/adapters/BannerAdapter$VH;I)V
    .locals 13
    .param p1, "h"    # Lcom/ultragol/app/adapters/BannerAdapter$VH;
    .param p2, "pos"    # I

    .line 31
    iget-object v0, p0, Lcom/ultragol/app/adapters/BannerAdapter;->items:Ljava/util/List;

    invoke-interface {v0, p2}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/ultragol/app/models/ContentItem;

    .line 33
    .local v0, "item":Lcom/ultragol/app/models/ContentItem;
    iget-object v1, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->title:Landroid/widget/TextView;

    invoke-virtual {v0}, Lcom/ultragol/app/models/ContentItem;->getTitle()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 36
    invoke-virtual {v0}, Lcom/ultragol/app/models/ContentItem;->getOverview()Ljava/lang/String;

    move-result-object v1

    .line 37
    .local v1, "desc":Ljava/lang/String;
    const/16 v2, 0x8

    const/4 v3, 0x0

    if-eqz v1, :cond_0

    invoke-virtual {v1}, Ljava/lang/String;->isEmpty()Z

    move-result v4

    if-nez v4, :cond_0

    .line 38
    iget-object v4, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->meta:Landroid/widget/TextView;

    invoke-virtual {v4, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 39
    iget-object v4, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->meta:Landroid/widget/TextView;

    invoke-virtual {v4, v3}, Landroid/widget/TextView;->setVisibility(I)V

    goto :goto_0

    .line 41
    :cond_0
    iget-object v4, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->meta:Landroid/widget/TextView;

    invoke-virtual {v4, v2}, Landroid/widget/TextView;->setVisibility(I)V

    .line 45
    :goto_0
    invoke-virtual {v0}, Lcom/ultragol/app/models/ContentItem;->getGenre()Ljava/lang/String;

    move-result-object v4

    .line 46
    .local v4, "genre":Ljava/lang/String;
    const/4 v5, 0x1

    if-eqz v4, :cond_3

    invoke-virtual {v4}, Ljava/lang/String;->isEmpty()Z

    move-result v6

    if-nez v6, :cond_3

    .line 47
    const-string v2, "[,/]"

    invoke-virtual {v4, v2}, Ljava/lang/String;->split(Ljava/lang/String;)[Ljava/lang/String;

    move-result-object v2

    .line 48
    .local v2, "parts":[Ljava/lang/String;
    array-length v6, v2

    if-lez v6, :cond_1

    .line 49
    iget-object v6, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->genre1:Landroid/widget/TextView;

    aget-object v7, v2, v3

    invoke-virtual {v7}, Ljava/lang/String;->trim()Ljava/lang/String;

    move-result-object v7

    invoke-virtual {v6, v7}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 50
    iget-object v6, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->genre1:Landroid/widget/TextView;

    invoke-virtual {v6, v3}, Landroid/widget/TextView;->setVisibility(I)V

    .line 52
    :cond_1
    array-length v6, v2

    if-le v6, v5, :cond_2

    .line 53
    iget-object v6, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->genreDot:Landroid/widget/TextView;

    invoke-virtual {v6, v3}, Landroid/widget/TextView;->setVisibility(I)V

    .line 54
    iget-object v6, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->genre2:Landroid/widget/TextView;

    aget-object v7, v2, v5

    invoke-virtual {v7}, Ljava/lang/String;->trim()Ljava/lang/String;

    move-result-object v7

    invoke-virtual {v6, v7}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 55
    iget-object v6, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->genre2:Landroid/widget/TextView;

    invoke-virtual {v6, v3}, Landroid/widget/TextView;->setVisibility(I)V

    .line 57
    .end local v2    # "parts":[Ljava/lang/String;
    :cond_2
    goto :goto_1

    .line 58
    :cond_3
    iget-object v6, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->genre1:Landroid/widget/TextView;

    invoke-virtual {v6, v2}, Landroid/widget/TextView;->setVisibility(I)V

    .line 59
    iget-object v6, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->genreDot:Landroid/widget/TextView;

    invoke-virtual {v6, v2}, Landroid/widget/TextView;->setVisibility(I)V

    .line 60
    iget-object v6, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->genre2:Landroid/widget/TextView;

    invoke-virtual {v6, v2}, Landroid/widget/TextView;->setVisibility(I)V

    .line 65
    :goto_1
    :try_start_0
    invoke-virtual {v0}, Lcom/ultragol/app/models/ContentItem;->getRating()Ljava/lang/String;

    move-result-object v2

    invoke-static {v2}, Ljava/lang/Double;->parseDouble(Ljava/lang/String;)D

    move-result-wide v6

    .line 66
    .local v6, "rating":D
    const-wide/high16 v8, 0x4000000000000000L    # 2.0

    div-double v8, v6, v8

    invoke-static {v8, v9}, Ljava/lang/Math;->round(D)J

    move-result-wide v8

    long-to-int v2, v8

    .line 67
    .local v2, "stars":I
    new-instance v8, Ljava/lang/StringBuilder;

    invoke-direct {v8}, Ljava/lang/StringBuilder;-><init>()V

    .line 68
    .local v8, "sb":Ljava/lang/StringBuilder;
    const/4 v9, 0x0

    .local v9, "i":I
    :goto_2
    const/4 v10, 0x5

    if-ge v9, v10, :cond_5

    if-ge v9, v2, :cond_4

    const-string v10, "\u2605"

    goto :goto_3

    :cond_4
    const-string v10, "\u2606"

    :goto_3
    invoke-virtual {v8, v10}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    add-int/lit8 v9, v9, 0x1

    goto :goto_2

    .line 69
    .end local v9    # "i":I
    :cond_5
    iget-object v9, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->stars:Landroid/widget/TextView;

    invoke-virtual {v8}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v10

    invoke-virtual {v9, v10}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 70
    iget-object v9, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->ratingPct:Landroid/widget/TextView;

    const-string v10, "%.0f%%"

    new-array v5, v5, [Ljava/lang/Object;

    const-wide/high16 v11, 0x4024000000000000L    # 10.0

    mul-double v11, v11, v6

    invoke-static {v11, v12}, Ljava/lang/Double;->valueOf(D)Ljava/lang/Double;

    move-result-object v11

    aput-object v11, v5, v3

    invoke-static {v10, v5}, Ljava/lang/String;->format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v9, v3}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 74
    .end local v2    # "stars":I
    .end local v6    # "rating":D
    .end local v8    # "sb":Ljava/lang/StringBuilder;
    goto :goto_4

    .line 71
    :catch_0
    move-exception v2

    .line 72
    .local v2, "e":Ljava/lang/Exception;
    iget-object v3, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->stars:Landroid/widget/TextView;

    const-string v5, "\u2605\u2605\u2605\u2605\u2606"

    invoke-virtual {v3, v5}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 73
    iget-object v3, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->ratingPct:Landroid/widget/TextView;

    const-string v5, ""

    invoke-virtual {v3, v5}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 77
    .end local v2    # "e":Ljava/lang/Exception;
    :goto_4
    iget-object v2, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->year:Landroid/widget/TextView;

    invoke-virtual {v0}, Lcom/ultragol/app/models/ContentItem;->getYear()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 80
    invoke-virtual {v0}, Lcom/ultragol/app/models/ContentItem;->getBackdropUrl()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/String;->isEmpty()Z

    move-result v2

    if-nez v2, :cond_6

    invoke-virtual {v0}, Lcom/ultragol/app/models/ContentItem;->getBackdropUrl()Ljava/lang/String;

    move-result-object v2

    goto :goto_5

    :cond_6
    invoke-virtual {v0}, Lcom/ultragol/app/models/ContentItem;->getPosterUrl()Ljava/lang/String;

    move-result-object v2

    .line 81
    .local v2, "img":Ljava/lang/String;
    :goto_5
    invoke-virtual {v2}, Ljava/lang/String;->isEmpty()Z

    move-result v3

    if-nez v3, :cond_7

    .line 82
    iget-object v3, p0, Lcom/ultragol/app/adapters/BannerAdapter;->ctx:Landroid/content/Context;

    invoke-static {v3}, Lcom/bumptech/glide/Glide;->with(Landroid/content/Context;)Lcom/bumptech/glide/RequestManager;

    move-result-object v3

    invoke-virtual {v3, v2}, Lcom/bumptech/glide/RequestManager;->load(Ljava/lang/String;)Lcom/bumptech/glide/RequestBuilder;

    move-result-object v3

    .line 83
    invoke-static {}, Lcom/bumptech/glide/load/resource/drawable/DrawableTransitionOptions;->withCrossFade()Lcom/bumptech/glide/load/resource/drawable/DrawableTransitionOptions;

    move-result-object v5

    invoke-virtual {v3, v5}, Lcom/bumptech/glide/RequestBuilder;->transition(Lcom/bumptech/glide/TransitionOptions;)Lcom/bumptech/glide/RequestBuilder;

    move-result-object v3

    .line 84
    invoke-virtual {v3}, Lcom/bumptech/glide/RequestBuilder;->centerCrop()Lcom/bumptech/glide/request/BaseRequestOptions;

    move-result-object v3

    check-cast v3, Lcom/bumptech/glide/RequestBuilder;

    iget-object v5, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->image:Landroid/widget/ImageView;

    invoke-virtual {v3, v5}, Lcom/bumptech/glide/RequestBuilder;->into(Landroid/widget/ImageView;)Lcom/bumptech/glide/request/target/ViewTarget;

    goto :goto_6

    .line 86
    :cond_7
    iget-object v3, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->image:Landroid/widget/ImageView;

    sget v5, Lcom/ultragol/app/R$drawable;->gradient_hero:I

    invoke-virtual {v3, v5}, Landroid/widget/ImageView;->setImageResource(I)V

    .line 89
    :goto_6
    new-instance v3, Lcom/ultragol/app/adapters/BannerAdapter$$ExternalSyntheticLambda0;

    invoke-direct {v3, p0, v0}, Lcom/ultragol/app/adapters/BannerAdapter$$ExternalSyntheticLambda0;-><init>(Lcom/ultragol/app/adapters/BannerAdapter;Lcom/ultragol/app/models/ContentItem;)V

    .line 94
    .local v3, "openDetail":Landroid/view/View$OnClickListener;
    iget-object v5, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->btnPlay:Landroid/view/View;

    invoke-virtual {v5, v3}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 95
    iget-object v5, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->btnInfo:Landroid/view/View;

    invoke-virtual {v5, v3}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 96
    iget-object v5, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->btnBookmark:Landroid/view/View;

    if-eqz v5, :cond_8

    iget-object v5, p1, Lcom/ultragol/app/adapters/BannerAdapter$VH;->btnBookmark:Landroid/view/View;

    invoke-virtual {v5, v3}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 97
    :cond_8
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

    .line 16
    invoke-virtual {p0, p1, p2}, Lcom/ultragol/app/adapters/BannerAdapter;->onCreateViewHolder(Landroid/view/ViewGroup;I)Lcom/ultragol/app/adapters/BannerAdapter$VH;

    move-result-object p1

    return-object p1
.end method

.method public onCreateViewHolder(Landroid/view/ViewGroup;I)Lcom/ultragol/app/adapters/BannerAdapter$VH;
    .locals 4
    .param p1, "parent"    # Landroid/view/ViewGroup;
    .param p2, "viewType"    # I

    .line 26
    new-instance v0, Lcom/ultragol/app/adapters/BannerAdapter$VH;

    iget-object v1, p0, Lcom/ultragol/app/adapters/BannerAdapter;->ctx:Landroid/content/Context;

    invoke-static {v1}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    move-result-object v1

    sget v2, Lcom/ultragol/app/R$layout;->item_banner:I

    const/4 v3, 0x0

    invoke-virtual {v1, v2, p1, v3}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    move-result-object v1

    invoke-direct {v0, v1}, Lcom/ultragol/app/adapters/BannerAdapter$VH;-><init>(Landroid/view/View;)V

    return-object v0
.end method
