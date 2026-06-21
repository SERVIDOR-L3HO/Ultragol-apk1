.class Lcom/ultragol/app/adapters/TrendingAdapter$VH;
.super Landroidx/recyclerview/widget/RecyclerView$ViewHolder;
.source "TrendingAdapter.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/ultragol/app/adapters/TrendingAdapter;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "VH"
.end annotation


# instance fields
.field badge:Landroid/widget/TextView;

.field image:Landroid/widget/ImageView;

.field rating:Landroid/widget/TextView;

.field title:Landroid/widget/TextView;

.field year:Landroid/widget/TextView;


# direct methods
.method constructor <init>(Landroid/view/View;)V
    .locals 1
    .param p1, "v"    # Landroid/view/View;

    .line 85
    invoke-direct {p0, p1}, Landroidx/recyclerview/widget/RecyclerView$ViewHolder;-><init>(Landroid/view/View;)V

    .line 86
    sget v0, Lcom/ultragol/app/R$id;->trendingImage:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/TrendingAdapter$VH;->image:Landroid/widget/ImageView;

    .line 87
    sget v0, Lcom/ultragol/app/R$id;->trendingTitle:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/TrendingAdapter$VH;->title:Landroid/widget/TextView;

    .line 88
    sget v0, Lcom/ultragol/app/R$id;->trendingBadge:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/TrendingAdapter$VH;->badge:Landroid/widget/TextView;

    .line 89
    sget v0, Lcom/ultragol/app/R$id;->trendingYear:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/TrendingAdapter$VH;->year:Landroid/widget/TextView;

    .line 90
    sget v0, Lcom/ultragol/app/R$id;->trendingRating:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/TrendingAdapter$VH;->rating:Landroid/widget/TextView;

    .line 91
    return-void
.end method
