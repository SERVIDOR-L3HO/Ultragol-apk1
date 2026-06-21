.class Lcom/ultragol/app/adapters/BannerAdapter$VH;
.super Landroidx/recyclerview/widget/RecyclerView$ViewHolder;
.source "BannerAdapter.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/ultragol/app/adapters/BannerAdapter;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "VH"
.end annotation


# instance fields
.field btnBookmark:Landroid/view/View;

.field btnInfo:Landroid/view/View;

.field btnPlay:Landroid/view/View;

.field genre1:Landroid/widget/TextView;

.field genre2:Landroid/widget/TextView;

.field genre3:Landroid/widget/TextView;

.field genreDot:Landroid/widget/TextView;

.field image:Landroid/widget/ImageView;

.field meta:Landroid/widget/TextView;

.field ratingPct:Landroid/widget/TextView;

.field stars:Landroid/widget/TextView;

.field title:Landroid/widget/TextView;

.field year:Landroid/widget/TextView;


# direct methods
.method constructor <init>(Landroid/view/View;)V
    .locals 1
    .param p1, "v"    # Landroid/view/View;

    .line 107
    invoke-direct {p0, p1}, Landroidx/recyclerview/widget/RecyclerView$ViewHolder;-><init>(Landroid/view/View;)V

    .line 108
    sget v0, Lcom/ultragol/app/R$id;->bannerImage:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/BannerAdapter$VH;->image:Landroid/widget/ImageView;

    .line 109
    sget v0, Lcom/ultragol/app/R$id;->bannerTitle:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/BannerAdapter$VH;->title:Landroid/widget/TextView;

    .line 110
    sget v0, Lcom/ultragol/app/R$id;->bannerMeta:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/BannerAdapter$VH;->meta:Landroid/widget/TextView;

    .line 111
    sget v0, Lcom/ultragol/app/R$id;->bannerGenre1:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/BannerAdapter$VH;->genre1:Landroid/widget/TextView;

    .line 112
    sget v0, Lcom/ultragol/app/R$id;->bannerGenre2:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/BannerAdapter$VH;->genre2:Landroid/widget/TextView;

    .line 113
    sget v0, Lcom/ultragol/app/R$id;->bannerGenre3:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/BannerAdapter$VH;->genre3:Landroid/widget/TextView;

    .line 114
    sget v0, Lcom/ultragol/app/R$id;->bannerGenreDot:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/BannerAdapter$VH;->genreDot:Landroid/widget/TextView;

    .line 115
    sget v0, Lcom/ultragol/app/R$id;->bannerStars:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/BannerAdapter$VH;->stars:Landroid/widget/TextView;

    .line 116
    sget v0, Lcom/ultragol/app/R$id;->bannerRatingPct:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/BannerAdapter$VH;->ratingPct:Landroid/widget/TextView;

    .line 117
    sget v0, Lcom/ultragol/app/R$id;->bannerYear:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/BannerAdapter$VH;->year:Landroid/widget/TextView;

    .line 118
    sget v0, Lcom/ultragol/app/R$id;->btnBannerPlay:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/ultragol/app/adapters/BannerAdapter$VH;->btnPlay:Landroid/view/View;

    .line 119
    sget v0, Lcom/ultragol/app/R$id;->btnBannerInfo:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/ultragol/app/adapters/BannerAdapter$VH;->btnInfo:Landroid/view/View;

    .line 120
    sget v0, Lcom/ultragol/app/R$id;->btnBannerBookmark:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/ultragol/app/adapters/BannerAdapter$VH;->btnBookmark:Landroid/view/View;

    .line 121
    return-void
.end method
