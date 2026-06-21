.class public Lcom/ultragol/app/adapters/ContentGridAdapter;
.super Landroidx/recyclerview/widget/RecyclerView$Adapter;
.source "ContentGridAdapter.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/ultragol/app/adapters/ContentGridAdapter$VH;
    }
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Landroidx/recyclerview/widget/RecyclerView$Adapter<",
        "Lcom/ultragol/app/adapters/ContentGridAdapter$VH;",
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
    iput-object p1, p0, Lcom/ultragol/app/adapters/ContentGridAdapter;->ctx:Landroid/content/Context;

    iput-object p2, p0, Lcom/ultragol/app/adapters/ContentGridAdapter;->items:Ljava/util/List;

    .line 22
    return-void
.end method


# virtual methods
.method public getItemCount()I
    .locals 1

    .line 44
    iget-object v0, p0, Lcom/ultragol/app/adapters/ContentGridAdapter;->items:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->size()I

    move-result v0

    return v0
.end method

.method synthetic lambda$onBindViewHolder$0$com-ultragol-app-adapters-ContentGridAdapter(Lcom/ultragol/app/models/ContentItem;Landroid/view/View;)V
    .locals 3
    .param p1, "item"    # Lcom/ultragol/app/models/ContentItem;
    .param p2, "v"    # Landroid/view/View;

    .line 38
    new-instance v0, Landroid/content/Intent;

    iget-object v1, p0, Lcom/ultragol/app/adapters/ContentGridAdapter;->ctx:Landroid/content/Context;

    const-class v2, Lcom/ultragol/app/DetailActivity;

    invoke-direct {v0, v1, v2}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    .line 39
    .local v0, "i":Landroid/content/Intent;
    const-string v1, "item"

    invoke-virtual {v0, v1, p1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/io/Serializable;)Landroid/content/Intent;

    .line 40
    iget-object v1, p0, Lcom/ultragol/app/adapters/ContentGridAdapter;->ctx:Landroid/content/Context;

    invoke-virtual {v1, v0}, Landroid/content/Context;->startActivity(Landroid/content/Intent;)V

    .line 41
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
    check-cast p1, Lcom/ultragol/app/adapters/ContentGridAdapter$VH;

    invoke-virtual {p0, p1, p2}, Lcom/ultragol/app/adapters/ContentGridAdapter;->onBindViewHolder(Lcom/ultragol/app/adapters/ContentGridAdapter$VH;I)V

    return-void
.end method

.method public onBindViewHolder(Lcom/ultragol/app/adapters/ContentGridAdapter$VH;I)V
    .locals 3
    .param p1, "h"    # Lcom/ultragol/app/adapters/ContentGridAdapter$VH;
    .param p2, "pos"    # I

    .line 31
    iget-object v0, p0, Lcom/ultragol/app/adapters/ContentGridAdapter;->items:Ljava/util/List;

    invoke-interface {v0, p2}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/ultragol/app/models/ContentItem;

    .line 32
    .local v0, "item":Lcom/ultragol/app/models/ContentItem;
    iget-object v1, p1, Lcom/ultragol/app/adapters/ContentGridAdapter$VH;->title:Landroid/widget/TextView;

    invoke-virtual {v0}, Lcom/ultragol/app/models/ContentItem;->getTitle()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 33
    iget-object v1, p1, Lcom/ultragol/app/adapters/ContentGridAdapter$VH;->year:Landroid/widget/TextView;

    invoke-virtual {v0}, Lcom/ultragol/app/models/ContentItem;->getYear()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 34
    invoke-virtual {v0}, Lcom/ultragol/app/models/ContentItem;->getPosterUrl()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/String;->isEmpty()Z

    move-result v1

    if-nez v1, :cond_0

    .line 35
    iget-object v1, p0, Lcom/ultragol/app/adapters/ContentGridAdapter;->ctx:Landroid/content/Context;

    invoke-static {v1}, Lcom/bumptech/glide/Glide;->with(Landroid/content/Context;)Lcom/bumptech/glide/RequestManager;

    move-result-object v1

    invoke-virtual {v0}, Lcom/ultragol/app/models/ContentItem;->getPosterUrl()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/bumptech/glide/RequestManager;->load(Ljava/lang/String;)Lcom/bumptech/glide/RequestBuilder;

    move-result-object v1

    invoke-static {}, Lcom/bumptech/glide/load/resource/drawable/DrawableTransitionOptions;->withCrossFade()Lcom/bumptech/glide/load/resource/drawable/DrawableTransitionOptions;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/bumptech/glide/RequestBuilder;->transition(Lcom/bumptech/glide/TransitionOptions;)Lcom/bumptech/glide/RequestBuilder;

    move-result-object v1

    invoke-virtual {v1}, Lcom/bumptech/glide/RequestBuilder;->centerCrop()Lcom/bumptech/glide/request/BaseRequestOptions;

    move-result-object v1

    check-cast v1, Lcom/bumptech/glide/RequestBuilder;

    iget-object v2, p1, Lcom/ultragol/app/adapters/ContentGridAdapter$VH;->poster:Landroid/widget/ImageView;

    invoke-virtual {v1, v2}, Lcom/bumptech/glide/RequestBuilder;->into(Landroid/widget/ImageView;)Lcom/bumptech/glide/request/target/ViewTarget;

    goto :goto_0

    .line 36
    :cond_0
    iget-object v1, p1, Lcom/ultragol/app/adapters/ContentGridAdapter$VH;->poster:Landroid/widget/ImageView;

    sget v2, Lcom/ultragol/app/R$drawable;->gradient_hero:I

    invoke-virtual {v1, v2}, Landroid/widget/ImageView;->setImageResource(I)V

    .line 37
    :goto_0
    iget-object v1, p1, Lcom/ultragol/app/adapters/ContentGridAdapter$VH;->itemView:Landroid/view/View;

    new-instance v2, Lcom/ultragol/app/adapters/ContentGridAdapter$$ExternalSyntheticLambda0;

    invoke-direct {v2, p0, v0}, Lcom/ultragol/app/adapters/ContentGridAdapter$$ExternalSyntheticLambda0;-><init>(Lcom/ultragol/app/adapters/ContentGridAdapter;Lcom/ultragol/app/models/ContentItem;)V

    invoke-virtual {v1, v2}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 42
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
    invoke-virtual {p0, p1, p2}, Lcom/ultragol/app/adapters/ContentGridAdapter;->onCreateViewHolder(Landroid/view/ViewGroup;I)Lcom/ultragol/app/adapters/ContentGridAdapter$VH;

    move-result-object p1

    return-object p1
.end method

.method public onCreateViewHolder(Landroid/view/ViewGroup;I)Lcom/ultragol/app/adapters/ContentGridAdapter$VH;
    .locals 4
    .param p1, "parent"    # Landroid/view/ViewGroup;
    .param p2, "viewType"    # I

    .line 26
    new-instance v0, Lcom/ultragol/app/adapters/ContentGridAdapter$VH;

    iget-object v1, p0, Lcom/ultragol/app/adapters/ContentGridAdapter;->ctx:Landroid/content/Context;

    invoke-static {v1}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    move-result-object v1

    sget v2, Lcom/ultragol/app/R$layout;->item_content_grid:I

    const/4 v3, 0x0

    invoke-virtual {v1, v2, p1, v3}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    move-result-object v1

    invoke-direct {v0, v1}, Lcom/ultragol/app/adapters/ContentGridAdapter$VH;-><init>(Landroid/view/View;)V

    return-object v0
.end method
