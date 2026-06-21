.class Lcom/ultragol/app/adapters/ChannelAdapter$VH;
.super Landroidx/recyclerview/widget/RecyclerView$ViewHolder;
.source "ChannelAdapter.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/ultragol/app/adapters/ChannelAdapter;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "VH"
.end annotation


# instance fields
.field category:Landroid/widget/TextView;

.field country:Landroid/widget/TextView;

.field liveBadge:Landroid/widget/TextView;

.field logo:Landroid/widget/ImageView;

.field name:Landroid/widget/TextView;


# direct methods
.method constructor <init>(Landroid/view/View;)V
    .locals 1
    .param p1, "v"    # Landroid/view/View;

    .line 52
    invoke-direct {p0, p1}, Landroidx/recyclerview/widget/RecyclerView$ViewHolder;-><init>(Landroid/view/View;)V

    .line 53
    sget v0, Lcom/ultragol/app/R$id;->channelLogo:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/ImageView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/ChannelAdapter$VH;->logo:Landroid/widget/ImageView;

    .line 54
    sget v0, Lcom/ultragol/app/R$id;->channelName:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/ChannelAdapter$VH;->name:Landroid/widget/TextView;

    .line 55
    sget v0, Lcom/ultragol/app/R$id;->channelCountry:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/ChannelAdapter$VH;->country:Landroid/widget/TextView;

    .line 56
    sget v0, Lcom/ultragol/app/R$id;->channelCategory:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/ChannelAdapter$VH;->category:Landroid/widget/TextView;

    .line 57
    sget v0, Lcom/ultragol/app/R$id;->channelLive:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/adapters/ChannelAdapter$VH;->liveBadge:Landroid/widget/TextView;

    .line 58
    return-void
.end method
