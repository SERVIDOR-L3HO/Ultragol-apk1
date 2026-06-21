.class public Lcom/ultragol/app/adapters/TrendingAdapter;
.super Landroidx/recyclerview/widget/RecyclerView$Adapter;
.source "TrendingAdapter.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/ultragol/app/adapters/TrendingAdapter$VH;
    }
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Landroidx/recyclerview/widget/RecyclerView$Adapter<",
        "Lcom/ultragol/app/adapters/TrendingAdapter$VH;",
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

    .line 22
    .local p2, "items":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    invoke-direct {p0}, Landroidx/recyclerview/widget/RecyclerView$Adapter;-><init>()V

    .line 23
    iput-object p1, p0, Lcom/ultragol/app/adapters/TrendingAdapter;->ctx:Landroid/content/Context;

    iput-object p2, p0, Lcom/ultragol/app/adapters/TrendingAdapter;->items:Ljava/util/List;

    .line 24
    return-void
.end method


# virtual methods
.method public getItemCount()I
    .locals 1

    .line 78
    iget-object v0, p0, Lcom/ultragol/app/adapters/TrendingAdapter;->items:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->size()I

    move-result v0

    return v0
.end method

.method synthetic lambda$onBindViewHolder$0$com-ultragol-app-adapters-TrendingAdapter(Lcom/ultragol/app/models/ContentItem;Landroid/view/View;)V
    .locals 3
    .param p1, "item"    # Lcom/ultragol/app/models/ContentItem;
    .param p2, "v"    # Landroid/view/View;

    .line 72
    new-instance v0, Landroid/content/Intent;

    iget-object v1, p0, Lcom/ultragol/app/adapters/TrendingAdapter;->ctx:Landroid/content/Context;

    const-class v2, Lcom/ultragol/app/DetailActivity;

    invoke-direct {v0, v1, v2}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    .line 73
    .local v0, "i":Landroid/content/Intent;
    const-string v1, "item"

    invoke-virtual {v0, v1, p1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/io/Serializable;)Landroid/content/Intent;

    .line 74
    iget-object v1, p0, Lcom/ultragol/app/adapters/TrendingAdapter;->ctx:Landroid/content/Context;

    invoke-virtual {v1, v0}, Landroid/content/Context;->startActivity(Landroid/content/Intent;)V

    .line 75
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

    .line 18
    check-cast p1, Lcom/ultragol/app/adapters/TrendingAdapter$VH;

    invoke-virtual {p0, p1, p2}, Lcom/ultragol/app/adapters/TrendingAdapter;->onBindViewHolder(Lcom/ultragol/app/adapters/TrendingAdapter$VH;I)V

    return-void
.end method

.method public onBindViewHolder(Lcom/ultragol/app/adapters/TrendingAdapter$VH;I)V
    .locals 7
    .param p1, "h"    # Lcom/ultragol/app/adapters/TrendingAdapter$VH;
    .param p2, "pos"    # I

    .line 33
    iget-object v0, p0, Lcom/ultragol/app/adapters/TrendingAdapter;->items:Ljava/util/List;

    invoke-interface {v0, p2}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/ultragol/app/models/ContentItem;

    .line 35
    .local v0, "item":Lcom/ultragol/app/models/ContentItem;
    iget-object v1, p1, Lcom/ultragol/app/adapters/TrendingAdapter$VH;->title:Landroid/widget/TextView;

    invoke-virtual {v0}, Lcom/ultragol/app/models/ContentItem;->getTitle()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 36
    iget-object v1, p1, Lcom/ultragol/app/adapters/TrendingAdapter$VH;->year:Landroid/widget/TextView;

    invoke-virtual {v0}, Lcom/ultragol/app/models/ContentItem;->getYear()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 37
    iget-object v1, p1, Lcom/ultragol/app/adapters/TrendingAdapter$VH;->rating:Landroid/widget/TextView;

    invoke-virtual {v0}, Lcom/ultragol/app/models/ContentItem;->getRating()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 42
    invoke-virtual {v0}, Lcom/ultragol/app/models/ContentItem;->getContentType()I

    move-result v1

    packed-switch v1, :pswitch_data_0

    .line 50
    const-string v1, "PEL\u00cdCULA"

    .local v1, "typeLabel":Ljava/lang/String;
    const-string v2, "#FF8F00"

    invoke-static {v2}, Landroid/graphics/Color;->parseColor(Ljava/lang/String;)I

    move-result v2

    .local v2, "typeColor":I
    goto :goto_0

    .line 48
    .end local v1    # "typeLabel":Ljava/lang/String;
    .end local v2    # "typeColor":I
    :pswitch_0
    const-string v1, "DORAMA"

    .restart local v1    # "typeLabel":Ljava/lang/String;
    const-string v2, "#26A69A"

    invoke-static {v2}, Landroid/graphics/Color;->parseColor(Ljava/lang/String;)I

    move-result v2

    .restart local v2    # "typeColor":I
    goto :goto_0

    .line 46
    .end local v1    # "typeLabel":Ljava/lang/String;
    .end local v2    # "typeColor":I
    :pswitch_1
    const-string v1, "ANIME"

    .restart local v1    # "typeLabel":Ljava/lang/String;
    const-string v2, "#E91E63"

    invoke-static {v2}, Landroid/graphics/Color;->parseColor(Ljava/lang/String;)I

    move-result v2

    .restart local v2    # "typeColor":I
    goto :goto_0

    .line 44
    .end local v1    # "typeLabel":Ljava/lang/String;
    .end local v2    # "typeColor":I
    :pswitch_2
    const-string v1, "SERIES"

    .restart local v1    # "typeLabel":Ljava/lang/String;
    const-string v2, "#00BCD4"

    invoke-static {v2}, Landroid/graphics/Color;->parseColor(Ljava/lang/String;)I

    move-result v2

    .line 52
    .restart local v2    # "typeColor":I
    :goto_0
    iget-object v3, p1, Lcom/ultragol/app/adapters/TrendingAdapter$VH;->badge:Landroid/widget/TextView;

    invoke-virtual {v3, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 53
    new-instance v3, Landroid/graphics/drawable/GradientDrawable;

    invoke-direct {v3}, Landroid/graphics/drawable/GradientDrawable;-><init>()V

    .line 54
    .local v3, "bg":Landroid/graphics/drawable/GradientDrawable;
    const/4 v4, 0x0

    invoke-virtual {v3, v4}, Landroid/graphics/drawable/GradientDrawable;->setShape(I)V

    .line 55
    const/high16 v4, -0x34000000    # -3.3554432E7f

    invoke-virtual {v3, v4}, Landroid/graphics/drawable/GradientDrawable;->setColor(I)V

    .line 56
    const/4 v4, 0x2

    invoke-virtual {v3, v4, v2}, Landroid/graphics/drawable/GradientDrawable;->setStroke(II)V

    .line 57
    const/high16 v4, 0x40c00000    # 6.0f

    invoke-virtual {v3, v4}, Landroid/graphics/drawable/GradientDrawable;->setCornerRadius(F)V

    .line 58
    iget-object v4, p1, Lcom/ultragol/app/adapters/TrendingAdapter$VH;->badge:Landroid/widget/TextView;

    invoke-virtual {v4, v3}, Landroid/widget/TextView;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 59
    iget-object v4, p1, Lcom/ultragol/app/adapters/TrendingAdapter$VH;->badge:Landroid/widget/TextView;

    invoke-virtual {v4, v2}, Landroid/widget/TextView;->setTextColor(I)V

    .line 62
    invoke-virtual {v0}, Lcom/ultragol/app/models/ContentItem;->getBackdropUrl()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v4}, Ljava/lang/String;->isEmpty()Z

    move-result v4

    if-nez v4, :cond_0

    invoke-virtual {v0}, Lcom/ultragol/app/models/ContentItem;->getBackdropUrl()Ljava/lang/String;

    move-result-object v4

    goto :goto_1

    :cond_0
    invoke-virtual {v0}, Lcom/ultragol/app/models/ContentItem;->getPosterUrl()Ljava/lang/String;

    move-result-object v4

    .line 63
    .local v4, "img":Ljava/lang/String;
    :goto_1
    invoke-virtual {v4}, Ljava/lang/String;->isEmpty()Z

    move-result v5

    if-nez v5, :cond_1

    .line 64
    iget-object v5, p0, Lcom/ultragol/app/adapters/TrendingAdapter;->ctx:Landroid/content/Context;

    invoke-static {v5}, Lcom/bumptech/glide/Glide;->with(Landroid/content/Context;)Lcom/bumptech/glide/RequestManager;

    move-result-object v5

    invoke-virtual {v5, v4}, Lcom/bumptech/glide/RequestManager;->load(Ljava/lang/String;)Lcom/bumptech/glide/RequestBuilder;

    move-result-object v5

    .line 65
    invoke-static {}, Lcom/bumptech/glide/load/resource/drawable/DrawableTransitionOptions;->withCrossFade()Lcom/bumptech/glide/load/resource/drawable/DrawableTransitionOptions;

    move-result-object v6

    invoke-virtual {v5, v6}, Lcom/bumptech/glide/RequestBuilder;->transition(Lcom/bumptech/glide/TransitionOptions;)Lcom/bumptech/glide/RequestBuilder;

    move-result-object v5

    .line 66
    invoke-virtual {v5}, Lcom/bumptech/glide/RequestBuilder;->centerCrop()Lcom/bumptech/glide/request/BaseRequestOptions;

    move-result-object v5

    check-cast v5, Lcom/bumptech/glide/RequestBuilder;

    iget-object v6, p1, Lcom/ultragol/app/adapters/TrendingAdapter$VH;->image:Landroid/widget/ImageView;

    invoke-virtual {v5, v6}, Lcom/bumptech/glide/RequestBuilder;->into(Landroid/widget/ImageView;)Lcom/bumptech/glide/request/target/ViewTarget;

    goto :goto_2

    .line 68
    :cond_1
    iget-object v5, p1, Lcom/ultragol/app/adapters/TrendingAdapter$VH;->image:Landroid/widget/ImageView;

    sget v6, Lcom/ultragol/app/R$drawable;->gradient_hero:I

    invoke-virtual {v5, v6}, Landroid/widget/ImageView;->setImageResource(I)V

    .line 71
    :goto_2
    iget-object v5, p1, Lcom/ultragol/app/adapters/TrendingAdapter$VH;->itemView:Landroid/view/View;

    new-instance v6, Lcom/ultragol/app/adapters/TrendingAdapter$$ExternalSyntheticLambda0;

    invoke-direct {v6, p0, v0}, Lcom/ultragol/app/adapters/TrendingAdapter$$ExternalSyntheticLambda0;-><init>(Lcom/ultragol/app/adapters/TrendingAdapter;Lcom/ultragol/app/models/ContentItem;)V

    invoke-virtual {v5, v6}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 76
    return-void

    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_2
        :pswitch_1
        :pswitch_0
    .end packed-switch
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

    .line 18
    invoke-virtual {p0, p1, p2}, Lcom/ultragol/app/adapters/TrendingAdapter;->onCreateViewHolder(Landroid/view/ViewGroup;I)Lcom/ultragol/app/adapters/TrendingAdapter$VH;

    move-result-object p1

    return-object p1
.end method

.method public onCreateViewHolder(Landroid/view/ViewGroup;I)Lcom/ultragol/app/adapters/TrendingAdapter$VH;
    .locals 4
    .param p1, "parent"    # Landroid/view/ViewGroup;
    .param p2, "viewType"    # I

    .line 28
    new-instance v0, Lcom/ultragol/app/adapters/TrendingAdapter$VH;

    iget-object v1, p0, Lcom/ultragol/app/adapters/TrendingAdapter;->ctx:Landroid/content/Context;

    invoke-static {v1}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    move-result-object v1

    sget v2, Lcom/ultragol/app/R$layout;->item_trending_card:I

    const/4 v3, 0x0

    invoke-virtual {v1, v2, p1, v3}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    move-result-object v1

    invoke-direct {v0, v1}, Lcom/ultragol/app/adapters/TrendingAdapter$VH;-><init>(Landroid/view/View;)V

    return-object v0
.end method
