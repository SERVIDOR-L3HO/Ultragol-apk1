.class Lcom/ultragol/app/adapters/ContentRowAdapter$VH;
.super Landroidx/recyclerview/widget/RecyclerView$ViewHolder;
.source "ContentRowAdapter.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/ultragol/app/adapters/ContentRowAdapter;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "VH"
.end annotation


# instance fields
.field badge:Landroid/widget/TextView;

.field cardNew:Landroid/widget/TextView;

.field poster:Landroid/widget/ImageView;

.field rating:Landroid/widget/TextView;

.field title:Landroid/widget/TextView;

.field year:Landroid/widget/TextView;


# direct methods
.method constructor <init>(Landroid/view/View;)V
    .locals 1
    .param p1, "v"    # Landroid/view/View;

    .line 93
    invoke-direct {p0, p1}, Landroidx/recyclerview/widget/RecyclerView$ViewHolder;-><init>(Landroid/view/View;)V

    .line 94
    sget v0, Lcom/ultragol/app/R$id;->cardPoster:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/ContentRowAdapter$VH;->poster:Landroid/widget/ImageView;

    .line 95
    sget v0, Lcom/ultragol/app/R$id;->cardTitle:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/ContentRowAdapter$VH;->title:Landroid/widget/TextView;

    .line 96
    sget v0, Lcom/ultragol/app/R$id;->cardBadge:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/ContentRowAdapter$VH;->badge:Landroid/widget/TextView;

    .line 97
    sget v0, Lcom/ultragol/app/R$id;->cardRating:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/ContentRowAdapter$VH;->rating:Landroid/widget/TextView;

    .line 98
    sget v0, Lcom/ultragol/app/R$id;->cardYear:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/ContentRowAdapter$VH;->year:Landroid/widget/TextView;

    .line 99
    sget v0, Lcom/ultragol/app/R$id;->cardNew:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/ContentRowAdapter$VH;->cardNew:Landroid/widget/TextView;

    .line 100
    return-void
.end method
