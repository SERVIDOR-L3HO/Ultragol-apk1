.class public Lcom/ultragol/app/fragments/SportsFragment;
.super Landroidx/fragment/app/Fragment;
.source "SportsFragment.java"


# instance fields
.field private adapter:Lcom/ultragol/app/adapters/ChannelAdapter;

.field private final allChannels:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/Channel;",
            ">;"
        }
    .end annotation
.end field

.field private emptyView:Landroid/view/View;

.field private final filtered:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/Channel;",
            ">;"
        }
    .end annotation
.end field

.field private loadingView:Landroid/view/View;


# direct methods
.method public constructor <init>()V
    .locals 1

    .line 20
    invoke-direct {p0}, Landroidx/fragment/app/Fragment;-><init>()V

    .line 22
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Lcom/ultragol/app/fragments/SportsFragment;->allChannels:Ljava/util/List;

    .line 23
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Lcom/ultragol/app/fragments/SportsFragment;->filtered:Ljava/util/List;

    return-void
.end method

.method static synthetic access$000(Lcom/ultragol/app/fragments/SportsFragment;Ljava/lang/String;)V
    .locals 0
    .param p0, "x0"    # Lcom/ultragol/app/fragments/SportsFragment;
    .param p1, "x1"    # Ljava/lang/String;

    .line 20
    invoke-direct {p0, p1}, Lcom/ultragol/app/fragments/SportsFragment;->filterChannels(Ljava/lang/String;)V

    return-void
.end method

.method private dp(I)I
    .locals 2
    .param p1, "v"    # I

    .line 128
    int-to-float v0, p1

    invoke-virtual {p0}, Lcom/ultragol/app/fragments/SportsFragment;->requireContext()Landroid/content/Context;

    move-result-object v1

    invoke-virtual {v1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    invoke-virtual {v1}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    move-result-object v1

    iget v1, v1, Landroid/util/DisplayMetrics;->density:F

    mul-float v0, v0, v1

    invoke-static {v0}, Ljava/lang/Math;->round(F)I

    move-result v0

    return v0
.end method

.method private filterChannels(Ljava/lang/String;)V
    .locals 4
    .param p1, "query"    # Ljava/lang/String;

    .line 117
    iget-object v0, p0, Lcom/ultragol/app/fragments/SportsFragment;->filtered:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->clear()V

    .line 118
    invoke-virtual {p1}, Ljava/lang/String;->isEmpty()Z

    move-result v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/ultragol/app/fragments/SportsFragment;->filtered:Ljava/util/List;

    iget-object v1, p0, Lcom/ultragol/app/fragments/SportsFragment;->allChannels:Ljava/util/List;

    invoke-interface {v0, v1}, Ljava/util/List;->addAll(Ljava/util/Collection;)Z

    goto :goto_1

    .line 120
    :cond_0
    invoke-virtual {p1}, Ljava/lang/String;->toLowerCase()Ljava/lang/String;

    move-result-object v0

    .line 121
    .local v0, "q":Ljava/lang/String;
    iget-object v1, p0, Lcom/ultragol/app/fragments/SportsFragment;->allChannels:Ljava/util/List;

    invoke-interface {v1}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_0
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_3

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/ultragol/app/models/Channel;

    .line 122
    .local v2, "ch":Lcom/ultragol/app/models/Channel;
    invoke-virtual {v2}, Lcom/ultragol/app/models/Channel;->getName()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/String;->toLowerCase()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v3, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v3

    if-nez v3, :cond_1

    invoke-virtual {v2}, Lcom/ultragol/app/models/Channel;->getCountry()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/String;->toLowerCase()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v3, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v3

    if-eqz v3, :cond_2

    .line 123
    :cond_1
    iget-object v3, p0, Lcom/ultragol/app/fragments/SportsFragment;->filtered:Ljava/util/List;

    invoke-interface {v3, v2}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    .line 122
    .end local v2    # "ch":Lcom/ultragol/app/models/Channel;
    :cond_2
    goto :goto_0

    .line 125
    .end local v0    # "q":Ljava/lang/String;
    :cond_3
    :goto_1
    iget-object v0, p0, Lcom/ultragol/app/fragments/SportsFragment;->adapter:Lcom/ultragol/app/adapters/ChannelAdapter;

    invoke-virtual {v0}, Lcom/ultragol/app/adapters/ChannelAdapter;->notifyDataSetChanged()V

    .line 126
    return-void
.end method

.method private loadChannels(Ljava/lang/String;)V
    .locals 2
    .param p1, "category"    # Ljava/lang/String;

    .line 94
    iget-object v0, p0, Lcom/ultragol/app/fragments/SportsFragment;->loadingView:Landroid/view/View;

    if-eqz v0, :cond_0

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 95
    :cond_0
    iget-object v0, p0, Lcom/ultragol/app/fragments/SportsFragment;->emptyView:Landroid/view/View;

    if-eqz v0, :cond_1

    const/16 v1, 0x8

    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 96
    :cond_1
    iget-object v0, p0, Lcom/ultragol/app/fragments/SportsFragment;->allChannels:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->clear()V

    iget-object v0, p0, Lcom/ultragol/app/fragments/SportsFragment;->filtered:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->clear()V

    iget-object v0, p0, Lcom/ultragol/app/fragments/SportsFragment;->adapter:Lcom/ultragol/app/adapters/ChannelAdapter;

    invoke-virtual {v0}, Lcom/ultragol/app/adapters/ChannelAdapter;->notifyDataSetChanged()V

    .line 98
    invoke-static {}, Ljava/util/concurrent/Executors;->newSingleThreadExecutor()Ljava/util/concurrent/ExecutorService;

    move-result-object v0

    new-instance v1, Lcom/ultragol/app/fragments/SportsFragment$$ExternalSyntheticLambda3;

    invoke-direct {v1, p0, p1}, Lcom/ultragol/app/fragments/SportsFragment$$ExternalSyntheticLambda3;-><init>(Lcom/ultragol/app/fragments/SportsFragment;Ljava/lang/String;)V

    invoke-interface {v0, v1}, Ljava/util/concurrent/ExecutorService;->execute(Ljava/lang/Runnable;)V

    .line 114
    return-void
.end method

.method private setupCategoryChips(Landroid/view/View;)V
    .locals 11
    .param p1, "view"    # Landroid/view/View;

    .line 64
    const-string v0, "sports"

    const-string v1, "news"

    const-string v2, "entertainment"

    const-string v3, "music"

    const-string v4, "movies"

    const-string v5, "kids"

    filled-new-array/range {v0 .. v5}, [Ljava/lang/String;

    move-result-object v0

    .line 65
    .local v0, "cats":[Ljava/lang/String;
    const-string v1, "\u26bd Deportes"

    const-string v2, "\ud83d\udcf0 Noticias"

    const-string v3, "\ud83c\udfad Entretenimiento"

    const-string v4, "\ud83c\udfb5 M\u00fasica"

    const-string v5, "\ud83c\udfac Pel\u00edculas"

    const-string v6, "\ud83d\udc76 Ni\u00f1os"

    filled-new-array/range {v1 .. v6}, [Ljava/lang/String;

    move-result-object v1

    .line 66
    .local v1, "labels":[Ljava/lang/String;
    sget v2, Lcom/ultragol/app/R$id;->categoryChips:I

    invoke-virtual {p1, v2}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v2

    check-cast v2, Landroid/widget/LinearLayout;

    .line 67
    .local v2, "chipContainer":Landroid/widget/LinearLayout;
    if-nez v2, :cond_0

    return-void

    .line 68
    :cond_0
    const/4 v3, 0x0

    .local v3, "i":I
    :goto_0
    array-length v4, v0

    if-ge v3, v4, :cond_3

    .line 69
    aget-object v4, v0, v3

    .line 70
    .local v4, "cat":Ljava/lang/String;
    new-instance v5, Landroid/widget/TextView;

    invoke-virtual {p0}, Lcom/ultragol/app/fragments/SportsFragment;->requireContext()Landroid/content/Context;

    move-result-object v6

    invoke-direct {v5, v6}, Landroid/widget/TextView;-><init>(Landroid/content/Context;)V

    .line 71
    .local v5, "chip":Landroid/widget/TextView;
    aget-object v6, v1, v3

    invoke-virtual {v5, v6}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 72
    const/high16 v6, 0x41400000    # 12.0f

    invoke-virtual {v5, v6}, Landroid/widget/TextView;->setTextSize(F)V

    if-nez v3, :cond_1

    const/4 v6, -0x1

    goto :goto_1

    :cond_1
    const v6, -0x6f6f67

    :goto_1
    invoke-virtual {v5, v6}, Landroid/widget/TextView;->setTextColor(I)V

    .line 73
    if-nez v3, :cond_2

    sget v6, Lcom/ultragol/app/R$drawable;->tab_active:I

    goto :goto_2

    :cond_2
    sget v6, Lcom/ultragol/app/R$drawable;->tab_inactive:I

    :goto_2
    invoke-virtual {v5, v6}, Landroid/widget/TextView;->setBackgroundResource(I)V

    .line 74
    const/16 v6, 0xe

    invoke-direct {p0, v6}, Lcom/ultragol/app/fragments/SportsFragment;->dp(I)I

    move-result v7

    const/16 v8, 0x8

    invoke-direct {p0, v8}, Lcom/ultragol/app/fragments/SportsFragment;->dp(I)I

    move-result v9

    invoke-direct {p0, v6}, Lcom/ultragol/app/fragments/SportsFragment;->dp(I)I

    move-result v6

    invoke-direct {p0, v8}, Lcom/ultragol/app/fragments/SportsFragment;->dp(I)I

    move-result v10

    invoke-virtual {v5, v7, v9, v6, v10}, Landroid/widget/TextView;->setPadding(IIII)V

    .line 75
    new-instance v6, Landroid/widget/LinearLayout$LayoutParams;

    const/4 v7, -0x2

    invoke-direct {v6, v7, v7}, Landroid/widget/LinearLayout$LayoutParams;-><init>(II)V

    .line 76
    .local v6, "lp":Landroid/widget/LinearLayout$LayoutParams;
    invoke-direct {p0, v8}, Lcom/ultragol/app/fragments/SportsFragment;->dp(I)I

    move-result v7

    invoke-virtual {v6, v7}, Landroid/widget/LinearLayout$LayoutParams;->setMarginEnd(I)V

    .line 77
    invoke-virtual {v5, v6}, Landroid/widget/TextView;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    .line 78
    const/4 v7, 0x1

    invoke-virtual {v5, v7}, Landroid/widget/TextView;->setClickable(Z)V

    invoke-virtual {v5, v7}, Landroid/widget/TextView;->setFocusable(Z)V

    .line 79
    new-instance v7, Lcom/ultragol/app/fragments/SportsFragment$$ExternalSyntheticLambda4;

    invoke-direct {v7, p0, v2, v5, v4}, Lcom/ultragol/app/fragments/SportsFragment$$ExternalSyntheticLambda4;-><init>(Lcom/ultragol/app/fragments/SportsFragment;Landroid/widget/LinearLayout;Landroid/widget/TextView;Ljava/lang/String;)V

    invoke-virtual {v5, v7}, Landroid/widget/TextView;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 89
    invoke-virtual {v2, v5}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 68
    .end local v4    # "cat":Ljava/lang/String;
    .end local v5    # "chip":Landroid/widget/TextView;
    .end local v6    # "lp":Landroid/widget/LinearLayout$LayoutParams;
    add-int/lit8 v3, v3, 0x1

    goto :goto_0

    .line 91
    .end local v3    # "i":I
    :cond_3
    return-void
.end method


# virtual methods
.method synthetic lambda$loadChannels$2$com-ultragol-app-fragments-SportsFragment(Ljava/util/List;)V
    .locals 3
    .param p1, "r"    # Ljava/util/List;

    .line 102
    iget-object v0, p0, Lcom/ultragol/app/fragments/SportsFragment;->allChannels:Ljava/util/List;

    invoke-interface {v0, p1}, Ljava/util/List;->addAll(Ljava/util/Collection;)Z

    iget-object v0, p0, Lcom/ultragol/app/fragments/SportsFragment;->filtered:Ljava/util/List;

    invoke-interface {v0, p1}, Ljava/util/List;->addAll(Ljava/util/Collection;)Z

    .line 103
    iget-object v0, p0, Lcom/ultragol/app/fragments/SportsFragment;->adapter:Lcom/ultragol/app/adapters/ChannelAdapter;

    invoke-virtual {v0}, Lcom/ultragol/app/adapters/ChannelAdapter;->notifyDataSetChanged()V

    .line 104
    iget-object v0, p0, Lcom/ultragol/app/fragments/SportsFragment;->loadingView:Landroid/view/View;

    const/16 v1, 0x8

    if-eqz v0, :cond_0

    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 105
    :cond_0
    iget-object v0, p0, Lcom/ultragol/app/fragments/SportsFragment;->emptyView:Landroid/view/View;

    if-eqz v0, :cond_2

    invoke-interface {p1}, Ljava/util/List;->isEmpty()Z

    move-result v2

    if-eqz v2, :cond_1

    const/4 v1, 0x0

    :cond_1
    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 106
    :cond_2
    return-void
.end method

.method synthetic lambda$loadChannels$3$com-ultragol-app-fragments-SportsFragment()V
    .locals 2

    .line 109
    iget-object v0, p0, Lcom/ultragol/app/fragments/SportsFragment;->loadingView:Landroid/view/View;

    if-eqz v0, :cond_0

    const/16 v1, 0x8

    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 110
    :cond_0
    iget-object v0, p0, Lcom/ultragol/app/fragments/SportsFragment;->emptyView:Landroid/view/View;

    if-eqz v0, :cond_1

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 111
    :cond_1
    return-void
.end method

.method synthetic lambda$loadChannels$4$com-ultragol-app-fragments-SportsFragment(Ljava/lang/String;)V
    .locals 3
    .param p1, "category"    # Ljava/lang/String;

    .line 100
    :try_start_0
    invoke-static {p1}, Lcom/ultragol/app/network/StreamingApi;->fetchAllChannels(Ljava/lang/String;)Ljava/util/List;

    move-result-object v0

    .line 101
    .local v0, "r":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/Channel;>;"
    invoke-virtual {p0}, Lcom/ultragol/app/fragments/SportsFragment;->requireActivity()Landroidx/fragment/app/FragmentActivity;

    move-result-object v1

    new-instance v2, Lcom/ultragol/app/fragments/SportsFragment$$ExternalSyntheticLambda0;

    invoke-direct {v2, p0, v0}, Lcom/ultragol/app/fragments/SportsFragment$$ExternalSyntheticLambda0;-><init>(Lcom/ultragol/app/fragments/SportsFragment;Ljava/util/List;)V

    invoke-virtual {v1, v2}, Landroidx/fragment/app/FragmentActivity;->runOnUiThread(Ljava/lang/Runnable;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 112
    .end local v0    # "r":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/Channel;>;"
    goto :goto_0

    .line 107
    :catch_0
    move-exception v0

    .line 108
    .local v0, "e":Ljava/lang/Exception;
    invoke-virtual {p0}, Lcom/ultragol/app/fragments/SportsFragment;->requireActivity()Landroidx/fragment/app/FragmentActivity;

    move-result-object v1

    new-instance v2, Lcom/ultragol/app/fragments/SportsFragment$$ExternalSyntheticLambda1;

    invoke-direct {v2, p0}, Lcom/ultragol/app/fragments/SportsFragment$$ExternalSyntheticLambda1;-><init>(Lcom/ultragol/app/fragments/SportsFragment;)V

    invoke-virtual {v1, v2}, Landroidx/fragment/app/FragmentActivity;->runOnUiThread(Ljava/lang/Runnable;)V

    .line 113
    .end local v0    # "e":Ljava/lang/Exception;
    :goto_0
    return-void
.end method

.method synthetic lambda$onViewCreated$0$com-ultragol-app-fragments-SportsFragment(Lcom/ultragol/app/models/Channel;)V
    .locals 3
    .param p1, "ch"    # Lcom/ultragol/app/models/Channel;

    .line 40
    new-instance v0, Landroid/content/Intent;

    invoke-virtual {p0}, Lcom/ultragol/app/fragments/SportsFragment;->requireContext()Landroid/content/Context;

    move-result-object v1

    const-class v2, Lcom/ultragol/app/PlayerActivity;

    invoke-direct {v0, v1, v2}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    .line 41
    .local v0, "intent":Landroid/content/Intent;
    const-string v1, "url"

    invoke-virtual {p1}, Lcom/ultragol/app/models/Channel;->getPlayerUrl()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    .line 42
    const-string v1, "title"

    invoke-virtual {p1}, Lcom/ultragol/app/models/Channel;->getDisplayName()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    .line 43
    invoke-virtual {p0, v0}, Lcom/ultragol/app/fragments/SportsFragment;->startActivity(Landroid/content/Intent;)V

    .line 44
    return-void
.end method

.method synthetic lambda$setupCategoryChips$1$com-ultragol-app-fragments-SportsFragment(Landroid/widget/LinearLayout;Landroid/widget/TextView;Ljava/lang/String;Landroid/view/View;)V
    .locals 4
    .param p1, "chipContainer"    # Landroid/widget/LinearLayout;
    .param p2, "chip"    # Landroid/widget/TextView;
    .param p3, "cat"    # Ljava/lang/String;
    .param p4, "v"    # Landroid/view/View;

    .line 80
    const/4 v0, 0x0

    .local v0, "j":I
    :goto_0
    invoke-virtual {p1}, Landroid/widget/LinearLayout;->getChildCount()I

    move-result v1

    if-ge v0, v1, :cond_1

    .line 81
    invoke-virtual {p1, v0}, Landroid/widget/LinearLayout;->getChildAt(I)Landroid/view/View;

    move-result-object v1

    .line 82
    .local v1, "c":Landroid/view/View;
    sget v2, Lcom/ultragol/app/R$drawable;->tab_inactive:I

    invoke-virtual {v1, v2}, Landroid/view/View;->setBackgroundResource(I)V

    .line 83
    instance-of v2, v1, Landroid/widget/TextView;

    if-eqz v2, :cond_0

    move-object v2, v1

    check-cast v2, Landroid/widget/TextView;

    const v3, -0x6f6f67

    invoke-virtual {v2, v3}, Landroid/widget/TextView;->setTextColor(I)V

    .line 80
    .end local v1    # "c":Landroid/view/View;
    :cond_0
    add-int/lit8 v0, v0, 0x1

    goto :goto_0

    .line 85
    .end local v0    # "j":I
    :cond_1
    sget v0, Lcom/ultragol/app/R$drawable;->tab_active:I

    invoke-virtual {p2, v0}, Landroid/widget/TextView;->setBackgroundResource(I)V

    .line 86
    const/4 v0, -0x1

    invoke-virtual {p2, v0}, Landroid/widget/TextView;->setTextColor(I)V

    .line 87
    invoke-direct {p0, p3}, Lcom/ultragol/app/fragments/SportsFragment;->loadChannels(Ljava/lang/String;)V

    .line 88
    return-void
.end method

.method public onCreateView(Landroid/view/LayoutInflater;Landroid/view/ViewGroup;Landroid/os/Bundle;)Landroid/view/View;
    .locals 2
    .param p1, "i"    # Landroid/view/LayoutInflater;
    .param p2, "p"    # Landroid/view/ViewGroup;
    .param p3, "s"    # Landroid/os/Bundle;

    .line 28
    sget v0, Lcom/ultragol/app/R$layout;->fragment_sports:I

    const/4 v1, 0x0

    invoke-virtual {p1, v0, p2, v1}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    move-result-object v0

    return-object v0
.end method

.method public onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V
    .locals 5
    .param p1, "view"    # Landroid/view/View;
    .param p2, "s"    # Landroid/os/Bundle;

    .line 33
    invoke-super {p0, p1, p2}, Landroidx/fragment/app/Fragment;->onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V

    .line 34
    sget v0, Lcom/ultragol/app/R$id;->sportsLoading:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/ultragol/app/fragments/SportsFragment;->loadingView:Landroid/view/View;

    .line 35
    sget v0, Lcom/ultragol/app/R$id;->sportsEmpty:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/ultragol/app/fragments/SportsFragment;->emptyView:Landroid/view/View;

    .line 37
    sget v0, Lcom/ultragol/app/R$id;->channelsGrid:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroidx/recyclerview/widget/RecyclerView;

    .line 38
    .local v0, "grid":Landroidx/recyclerview/widget/RecyclerView;
    new-instance v1, Landroidx/recyclerview/widget/GridLayoutManager;

    invoke-virtual {p0}, Lcom/ultragol/app/fragments/SportsFragment;->requireContext()Landroid/content/Context;

    move-result-object v2

    const/4 v3, 0x2

    invoke-direct {v1, v2, v3}, Landroidx/recyclerview/widget/GridLayoutManager;-><init>(Landroid/content/Context;I)V

    invoke-virtual {v0, v1}, Landroidx/recyclerview/widget/RecyclerView;->setLayoutManager(Landroidx/recyclerview/widget/RecyclerView$LayoutManager;)V

    .line 39
    new-instance v1, Lcom/ultragol/app/adapters/ChannelAdapter;

    invoke-virtual {p0}, Lcom/ultragol/app/fragments/SportsFragment;->requireContext()Landroid/content/Context;

    move-result-object v2

    iget-object v3, p0, Lcom/ultragol/app/fragments/SportsFragment;->filtered:Ljava/util/List;

    new-instance v4, Lcom/ultragol/app/fragments/SportsFragment$$ExternalSyntheticLambda2;

    invoke-direct {v4, p0}, Lcom/ultragol/app/fragments/SportsFragment$$ExternalSyntheticLambda2;-><init>(Lcom/ultragol/app/fragments/SportsFragment;)V

    invoke-direct {v1, v2, v3, v4}, Lcom/ultragol/app/adapters/ChannelAdapter;-><init>(Landroid/content/Context;Ljava/util/List;Lcom/ultragol/app/adapters/ChannelAdapter$OnChannelClick;)V

    iput-object v1, p0, Lcom/ultragol/app/fragments/SportsFragment;->adapter:Lcom/ultragol/app/adapters/ChannelAdapter;

    .line 45
    invoke-virtual {v0, v1}, Landroidx/recyclerview/widget/RecyclerView;->setAdapter(Landroidx/recyclerview/widget/RecyclerView$Adapter;)V

    .line 48
    invoke-direct {p0, p1}, Lcom/ultragol/app/fragments/SportsFragment;->setupCategoryChips(Landroid/view/View;)V

    .line 51
    sget v1, Lcom/ultragol/app/R$id;->sportsSearch:I

    invoke-virtual {p1, v1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v1

    check-cast v1, Landroid/widget/EditText;

    .line 52
    .local v1, "search":Landroid/widget/EditText;
    if-eqz v1, :cond_0

    .line 53
    new-instance v2, Lcom/ultragol/app/fragments/SportsFragment$1;

    invoke-direct {v2, p0}, Lcom/ultragol/app/fragments/SportsFragment$1;-><init>(Lcom/ultragol/app/fragments/SportsFragment;)V

    invoke-virtual {v1, v2}, Landroid/widget/EditText;->addTextChangedListener(Landroid/text/TextWatcher;)V

    .line 60
    :cond_0
    const-string v2, "sports"

    invoke-direct {p0, v2}, Lcom/ultragol/app/fragments/SportsFragment;->loadChannels(Ljava/lang/String;)V

    .line 61
    return-void
.end method
