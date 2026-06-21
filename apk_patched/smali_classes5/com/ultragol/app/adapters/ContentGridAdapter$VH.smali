.class Lcom/ultragol/app/adapters/ContentGridAdapter$VH;
.super Landroidx/recyclerview/widget/RecyclerView$ViewHolder;
.source "ContentGridAdapter.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/ultragol/app/adapters/ContentGridAdapter;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "VH"
.end annotation


# instance fields
.field poster:Landroid/widget/ImageView;

.field title:Landroid/widget/TextView;

.field year:Landroid/widget/TextView;


# direct methods
.method constructor <init>(Landroid/view/View;)V
    .locals 1
    .param p1, "v"    # Landroid/view/View;

    .line 49
    invoke-direct {p0, p1}, Landroidx/recyclerview/widget/RecyclerView$ViewHolder;-><init>(Landroid/view/View;)V

    .line 50
    sget v0, Lcom/ultragol/app/R$id;->gridPoster:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/ContentGridAdapter$VH;->poster:Landroid/widget/ImageView;

    .line 51
    sget v0, Lcom/ultragol/app/R$id;->gridTitle:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/ContentGridAdapter$VH;->title:Landroid/widget/TextView;

    .line 52
    sget v0, Lcom/ultragol/app/R$id;->gridYear:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/ContentGridAdapter$VH;->year:Landroid/widget/TextView;

    .line 53
    return-void
.end method
