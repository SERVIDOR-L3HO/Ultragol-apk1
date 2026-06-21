.class public Lcom/ultragol/app/fragments/HomeFragment;
.super Landroidx/fragment/app/Fragment;
.source "HomeFragment.java"


# instance fields
.field private final autoHandler:Landroid/os/Handler;

.field private final autoScroll:Ljava/lang/Runnable;

.field private bannerAdapter:Lcom/ultragol/app/adapters/BannerAdapter;

.field private bannerCount:I

.field private final bannerItems:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/ContentItem;",
            ">;"
        }
    .end annotation
.end field

.field private bannerPage:I

.field private dots:Landroid/widget/LinearLayout;

.field private hero:Landroidx/viewpager2/widget/ViewPager2;

.field private rowAnime:Landroid/view/View;

.field private rowDoramas:Landroid/view/View;

.field private rowMovies:Landroid/view/View;

.field private rowNew:Landroid/view/View;

.field private rowSeries:Landroid/view/View;

.field private rowTop10:Landroid/view/View;

.field private rowTrending:Landroid/view/View;

.field private trendingAdapter:Lcom/ultragol/app/adapters/TrendingAdapter;

.field private final trendingItems:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/ContentItem;",
            ">;"
        }
    .end annotation
.end field

.field private trendingPage:I

.field private trendingPager:Landroidx/viewpager2/widget/ViewPager2;


# direct methods
.method public constructor <init>()V
    .locals 2

    .line 19
    invoke-direct {p0}, Landroidx/fragment/app/Fragment;-><init>()V

    .line 24
    new-instance v0, Landroid/os/Handler;

    invoke-direct {v0}, Landroid/os/Handler;-><init>()V

    iput-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->autoHandler:Landroid/os/Handler;

    .line 25
    const/4 v0, 0x0

    iput v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->bannerPage:I

    iput v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->bannerCount:I

    .line 27
    new-instance v1, Ljava/util/ArrayList;

    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    iput-object v1, p0, Lcom/ultragol/app/fragments/HomeFragment;->bannerItems:Ljava/util/List;

    .line 31
    new-instance v1, Ljava/util/ArrayList;

    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    iput-object v1, p0, Lcom/ultragol/app/fragments/HomeFragment;->trendingItems:Ljava/util/List;

    .line 33
    iput v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->trendingPage:I

    .line 80
    new-instance v0, Lcom/ultragol/app/fragments/HomeFragment$2;

    invoke-direct {v0, p0}, Lcom/ultragol/app/fragments/HomeFragment$2;-><init>(Lcom/ultragol/app/fragments/HomeFragment;)V

    iput-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->autoScroll:Ljava/lang/Runnable;

    return-void
.end method

.method static synthetic access$000(Lcom/ultragol/app/fragments/HomeFragment;)I
    .locals 1
    .param p0, "x0"    # Lcom/ultragol/app/fragments/HomeFragment;

    .line 19
    iget v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->bannerPage:I

    return v0
.end method

.method static synthetic access$002(Lcom/ultragol/app/fragments/HomeFragment;I)I
    .locals 0
    .param p0, "x0"    # Lcom/ultragol/app/fragments/HomeFragment;
    .param p1, "x1"    # I

    .line 19
    iput p1, p0, Lcom/ultragol/app/fragments/HomeFragment;->bannerPage:I

    return p1
.end method

.method static synthetic access$100(Lcom/ultragol/app/fragments/HomeFragment;I)V
    .locals 0
    .param p0, "x0"    # Lcom/ultragol/app/fragments/HomeFragment;
    .param p1, "x1"    # I

    .line 19
    invoke-direct {p0, p1}, Lcom/ultragol/app/fragments/HomeFragment;->updateDots(I)V

    return-void
.end method

.method static synthetic access$200(Lcom/ultragol/app/fragments/HomeFragment;)Landroidx/viewpager2/widget/ViewPager2;
    .locals 1
    .param p0, "x0"    # Lcom/ultragol/app/fragments/HomeFragment;

    .line 19
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->hero:Landroidx/viewpager2/widget/ViewPager2;

    return-object v0
.end method

.method static synthetic access$300(Lcom/ultragol/app/fragments/HomeFragment;)I
    .locals 1
    .param p0, "x0"    # Lcom/ultragol/app/fragments/HomeFragment;

    .line 19
    iget v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->bannerCount:I

    return v0
.end method

.method static synthetic access$400(Lcom/ultragol/app/fragments/HomeFragment;)Landroid/os/Handler;
    .locals 1
    .param p0, "x0"    # Lcom/ultragol/app/fragments/HomeFragment;

    .line 19
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->autoHandler:Landroid/os/Handler;

    return-object v0
.end method

.method static synthetic access$502(Lcom/ultragol/app/fragments/HomeFragment;I)I
    .locals 0
    .param p0, "x0"    # Lcom/ultragol/app/fragments/HomeFragment;
    .param p1, "x1"    # I

    .line 19
    iput p1, p0, Lcom/ultragol/app/fragments/HomeFragment;->trendingPage:I

    return p1
.end method

.method private buildDots()V
    .locals 7

    .line 91
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->dots:Landroid/widget/LinearLayout;

    if-nez v0, :cond_0

    return-void

    .line 92
    :cond_0
    invoke-virtual {v0}, Landroid/widget/LinearLayout;->removeAllViews()V

    .line 93
    const/4 v0, 0x0

    .local v0, "i":I
    :goto_0
    iget v1, p0, Lcom/ultragol/app/fragments/HomeFragment;->bannerCount:I

    if-ge v0, v1, :cond_3

    .line 94
    new-instance v1, Landroid/view/View;

    invoke-virtual {p0}, Lcom/ultragol/app/fragments/HomeFragment;->requireContext()Landroid/content/Context;

    move-result-object v2

    invoke-direct {v1, v2}, Landroid/view/View;-><init>(Landroid/content/Context;)V

    .line 95
    .local v1, "d":Landroid/view/View;
    if-nez v0, :cond_1

    const/16 v2, 0xa

    goto :goto_1

    :cond_1
    const/4 v2, 0x6

    :goto_1
    invoke-direct {p0, v2}, Lcom/ultragol/app/fragments/HomeFragment;->dp(I)I

    move-result v2

    .line 96
    .local v2, "sz":I
    new-instance v3, Landroid/widget/LinearLayout$LayoutParams;

    invoke-direct {v3, v2, v2}, Landroid/widget/LinearLayout$LayoutParams;-><init>(II)V

    .line 97
    .local v3, "p":Landroid/widget/LinearLayout$LayoutParams;
    const/4 v4, 0x4

    invoke-direct {p0, v4}, Lcom/ultragol/app/fragments/HomeFragment;->dp(I)I

    move-result v5

    invoke-direct {p0, v4}, Lcom/ultragol/app/fragments/HomeFragment;->dp(I)I

    move-result v4

    const/4 v6, 0x0

    invoke-virtual {v3, v5, v6, v4, v6}, Landroid/widget/LinearLayout$LayoutParams;->setMargins(IIII)V

    .line 98
    invoke-virtual {v1, v3}, Landroid/view/View;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    .line 99
    if-nez v0, :cond_2

    sget v4, Lcom/ultragol/app/R$drawable;->dot_active:I

    goto :goto_2

    :cond_2
    sget v4, Lcom/ultragol/app/R$drawable;->dot_inactive:I

    :goto_2
    invoke-virtual {v1, v4}, Landroid/view/View;->setBackgroundResource(I)V

    .line 100
    iget-object v4, p0, Lcom/ultragol/app/fragments/HomeFragment;->dots:Landroid/widget/LinearLayout;

    invoke-virtual {v4, v1}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 93
    .end local v1    # "d":Landroid/view/View;
    .end local v2    # "sz":I
    .end local v3    # "p":Landroid/widget/LinearLayout$LayoutParams;
    add-int/lit8 v0, v0, 0x1

    goto :goto_0

    .line 102
    .end local v0    # "i":I
    :cond_3
    return-void
.end method

.method private dp(I)I
    .locals 2
    .param p1, "v"    # I

    .line 242
    int-to-float v0, p1

    invoke-virtual {p0}, Lcom/ultragol/app/fragments/HomeFragment;->requireContext()Landroid/content/Context;

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

.method private fillRow(Landroid/view/View;Ljava/util/List;)V
    .locals 3
    .param p1, "row"    # Landroid/view/View;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/view/View;",
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/ContentItem;",
            ">;)V"
        }
    .end annotation

    .line 236
    .local p2, "items":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    if-eqz p1, :cond_2

    invoke-virtual {p0}, Lcom/ultragol/app/fragments/HomeFragment;->isAdded()Z

    move-result v0

    if-nez v0, :cond_0

    goto :goto_0

    .line 237
    :cond_0
    sget v0, Lcom/ultragol/app/R$id;->rowRv:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroidx/recyclerview/widget/RecyclerView;

    .line 238
    .local v0, "rv":Landroidx/recyclerview/widget/RecyclerView;
    if-eqz v0, :cond_1

    new-instance v1, Lcom/ultragol/app/adapters/ContentRowAdapter;

    invoke-virtual {p0}, Lcom/ultragol/app/fragments/HomeFragment;->requireContext()Landroid/content/Context;

    move-result-object v2

    invoke-direct {v1, v2, p2}, Lcom/ultragol/app/adapters/ContentRowAdapter;-><init>(Landroid/content/Context;Ljava/util/List;)V

    invoke-virtual {v0, v1}, Landroidx/recyclerview/widget/RecyclerView;->setAdapter(Landroidx/recyclerview/widget/RecyclerView$Adapter;)V

    .line 239
    :cond_1
    return-void

    .line 236
    .end local v0    # "rv":Landroidx/recyclerview/widget/RecyclerView;
    :cond_2
    :goto_0
    return-void
.end method

.method private initRow(Landroid/view/View;Ljava/lang/String;)V
    .locals 5
    .param p1, "row"    # Landroid/view/View;
    .param p2, "title"    # Ljava/lang/String;

    .line 167
    if-nez p1, :cond_0

    return-void

    .line 168
    :cond_0
    sget v0, Lcom/ultragol/app/R$id;->rowTitle:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    .line 169
    .local v0, "tv":Landroid/widget/TextView;
    sget v1, Lcom/ultragol/app/R$id;->rowRv:I

    invoke-virtual {p1, v1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v1

    check-cast v1, Landroidx/recyclerview/widget/RecyclerView;

    .line 170
    .local v1, "rv":Landroidx/recyclerview/widget/RecyclerView;
    if-eqz v0, :cond_1

    invoke-virtual {v0, p2}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 171
    :cond_1
    if-eqz v1, :cond_2

    new-instance v2, Landroidx/recyclerview/widget/LinearLayoutManager;

    .line 172
    invoke-virtual {p0}, Lcom/ultragol/app/fragments/HomeFragment;->requireContext()Landroid/content/Context;

    move-result-object v3

    const/4 v4, 0x0

    invoke-direct {v2, v3, v4, v4}, Landroidx/recyclerview/widget/LinearLayoutManager;-><init>(Landroid/content/Context;IZ)V

    .line 171
    invoke-virtual {v1, v2}, Landroidx/recyclerview/widget/RecyclerView;->setLayoutManager(Landroidx/recyclerview/widget/RecyclerView$LayoutManager;)V

    .line 173
    :cond_2
    return-void
.end method

.method private loadAll()V
    .locals 3

    .line 178
    const/4 v0, 0x4

    invoke-static {v0}, Ljava/util/concurrent/Executors;->newFixedThreadPool(I)Ljava/util/concurrent/ExecutorService;

    move-result-object v0

    .line 179
    .local v0, "pool":Ljava/util/concurrent/ExecutorService;
    new-instance v1, Landroid/os/Handler;

    invoke-static {}, Landroid/os/Looper;->getMainLooper()Landroid/os/Looper;

    move-result-object v2

    invoke-direct {v1, v2}, Landroid/os/Handler;-><init>(Landroid/os/Looper;)V

    .line 181
    .local v1, "h":Landroid/os/Handler;
    new-instance v2, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda10;

    invoke-direct {v2, p0, v1}, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda10;-><init>(Lcom/ultragol/app/fragments/HomeFragment;Landroid/os/Handler;)V

    invoke-interface {v0, v2}, Ljava/util/concurrent/ExecutorService;->execute(Ljava/lang/Runnable;)V

    .line 202
    new-instance v2, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda11;

    invoke-direct {v2, p0, v1}, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda11;-><init>(Lcom/ultragol/app/fragments/HomeFragment;Landroid/os/Handler;)V

    invoke-interface {v0, v2}, Ljava/util/concurrent/ExecutorService;->execute(Ljava/lang/Runnable;)V

    .line 207
    new-instance v2, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda12;

    invoke-direct {v2, p0, v1}, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda12;-><init>(Lcom/ultragol/app/fragments/HomeFragment;Landroid/os/Handler;)V

    invoke-interface {v0, v2}, Ljava/util/concurrent/ExecutorService;->execute(Ljava/lang/Runnable;)V

    .line 212
    new-instance v2, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda13;

    invoke-direct {v2, p0, v1}, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda13;-><init>(Lcom/ultragol/app/fragments/HomeFragment;Landroid/os/Handler;)V

    invoke-interface {v0, v2}, Ljava/util/concurrent/ExecutorService;->execute(Ljava/lang/Runnable;)V

    .line 217
    new-instance v2, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda14;

    invoke-direct {v2, p0, v1}, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda14;-><init>(Lcom/ultragol/app/fragments/HomeFragment;Landroid/os/Handler;)V

    invoke-interface {v0, v2}, Ljava/util/concurrent/ExecutorService;->execute(Ljava/lang/Runnable;)V

    .line 222
    new-instance v2, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda15;

    invoke-direct {v2, p0, v1}, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda15;-><init>(Lcom/ultragol/app/fragments/HomeFragment;Landroid/os/Handler;)V

    invoke-interface {v0, v2}, Ljava/util/concurrent/ExecutorService;->execute(Ljava/lang/Runnable;)V

    .line 227
    new-instance v2, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda16;

    invoke-direct {v2, p0, v1}, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda16;-><init>(Lcom/ultragol/app/fragments/HomeFragment;Landroid/os/Handler;)V

    invoke-interface {v0, v2}, Ljava/util/concurrent/ExecutorService;->execute(Ljava/lang/Runnable;)V

    .line 232
    invoke-interface {v0}, Ljava/util/concurrent/ExecutorService;->shutdown()V

    .line 233
    return-void
.end method

.method private setupHero(Landroid/view/View;)V
    .locals 4
    .param p1, "view"    # Landroid/view/View;

    .line 69
    sget v0, Lcom/ultragol/app/R$id;->heroBanner:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroidx/viewpager2/widget/ViewPager2;

    iput-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->hero:Landroidx/viewpager2/widget/ViewPager2;

    .line 70
    sget v0, Lcom/ultragol/app/R$id;->bannerDots:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/LinearLayout;

    iput-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->dots:Landroid/widget/LinearLayout;

    .line 71
    new-instance v0, Lcom/ultragol/app/adapters/BannerAdapter;

    invoke-virtual {p0}, Lcom/ultragol/app/fragments/HomeFragment;->requireContext()Landroid/content/Context;

    move-result-object v1

    iget-object v2, p0, Lcom/ultragol/app/fragments/HomeFragment;->bannerItems:Ljava/util/List;

    invoke-direct {v0, v1, v2}, Lcom/ultragol/app/adapters/BannerAdapter;-><init>(Landroid/content/Context;Ljava/util/List;)V

    iput-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->bannerAdapter:Lcom/ultragol/app/adapters/BannerAdapter;

    .line 72
    iget-object v1, p0, Lcom/ultragol/app/fragments/HomeFragment;->hero:Landroidx/viewpager2/widget/ViewPager2;

    if-nez v1, :cond_0

    return-void

    .line 73
    :cond_0
    invoke-virtual {v1, v0}, Landroidx/viewpager2/widget/ViewPager2;->setAdapter(Landroidx/recyclerview/widget/RecyclerView$Adapter;)V

    .line 74
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->hero:Landroidx/viewpager2/widget/ViewPager2;

    new-instance v1, Lcom/ultragol/app/fragments/HomeFragment$1;

    invoke-direct {v1, p0}, Lcom/ultragol/app/fragments/HomeFragment$1;-><init>(Lcom/ultragol/app/fragments/HomeFragment;)V

    invoke-virtual {v0, v1}, Landroidx/viewpager2/widget/ViewPager2;->registerOnPageChangeCallback(Landroidx/viewpager2/widget/ViewPager2$OnPageChangeCallback;)V

    .line 77
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->autoHandler:Landroid/os/Handler;

    iget-object v1, p0, Lcom/ultragol/app/fragments/HomeFragment;->autoScroll:Ljava/lang/Runnable;

    const-wide/16 v2, 0x1388

    invoke-virtual {v0, v1, v2, v3}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    .line 78
    return-void
.end method

.method private setupRows(Landroid/view/View;)V
    .locals 2
    .param p1, "view"    # Landroid/view/View;

    .line 149
    sget v0, Lcom/ultragol/app/R$id;->rowTrending:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->rowTrending:Landroid/view/View;

    .line 150
    sget v0, Lcom/ultragol/app/R$id;->rowTop10:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->rowTop10:Landroid/view/View;

    .line 151
    sget v0, Lcom/ultragol/app/R$id;->rowNew:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->rowNew:Landroid/view/View;

    .line 152
    sget v0, Lcom/ultragol/app/R$id;->rowMovies:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->rowMovies:Landroid/view/View;

    .line 153
    sget v0, Lcom/ultragol/app/R$id;->rowSeries:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->rowSeries:Landroid/view/View;

    .line 154
    sget v0, Lcom/ultragol/app/R$id;->rowAnime:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->rowAnime:Landroid/view/View;

    .line 155
    sget v0, Lcom/ultragol/app/R$id;->rowDoramas:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->rowDoramas:Landroid/view/View;

    .line 157
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->rowTrending:Landroid/view/View;

    const-string v1, "Tendencias"

    invoke-direct {p0, v0, v1}, Lcom/ultragol/app/fragments/HomeFragment;->initRow(Landroid/view/View;Ljava/lang/String;)V

    .line 158
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->rowNew:Landroid/view/View;

    const-string v1, "\u00daltimos Estrenos"

    invoke-direct {p0, v0, v1}, Lcom/ultragol/app/fragments/HomeFragment;->initRow(Landroid/view/View;Ljava/lang/String;)V

    .line 159
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->rowMovies:Landroid/view/View;

    const-string v1, "Pel\u00edculas Populares"

    invoke-direct {p0, v0, v1}, Lcom/ultragol/app/fragments/HomeFragment;->initRow(Landroid/view/View;Ljava/lang/String;)V

    .line 160
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->rowSeries:Landroid/view/View;

    const-string v1, "Series Populares"

    invoke-direct {p0, v0, v1}, Lcom/ultragol/app/fragments/HomeFragment;->initRow(Landroid/view/View;Ljava/lang/String;)V

    .line 161
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->rowAnime:Landroid/view/View;

    const-string v1, "Animes"

    invoke-direct {p0, v0, v1}, Lcom/ultragol/app/fragments/HomeFragment;->initRow(Landroid/view/View;Ljava/lang/String;)V

    .line 162
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->rowDoramas:Landroid/view/View;

    const-string v1, "Doramas"

    invoke-direct {p0, v0, v1}, Lcom/ultragol/app/fragments/HomeFragment;->initRow(Landroid/view/View;Ljava/lang/String;)V

    .line 163
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->rowTop10:Landroid/view/View;

    const-string v1, "Top 10"

    invoke-direct {p0, v0, v1}, Lcom/ultragol/app/fragments/HomeFragment;->initRow(Landroid/view/View;Ljava/lang/String;)V

    .line 164
    return-void
.end method

.method private setupTopBar(Landroid/view/View;)V
    .locals 3
    .param p1, "view"    # Landroid/view/View;

    .line 54
    sget v0, Lcom/ultragol/app/R$id;->btnSearch:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    .line 55
    .local v0, "search":Landroid/view/View;
    if-eqz v0, :cond_0

    new-instance v1, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda6;

    invoke-direct {v1, p0}, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda6;-><init>(Lcom/ultragol/app/fragments/HomeFragment;)V

    invoke-virtual {v0, v1}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 58
    :cond_0
    sget v1, Lcom/ultragol/app/R$id;->btnMenu:I

    invoke-virtual {p1, v1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v1

    .line 59
    .local v1, "menu":Landroid/view/View;
    if-eqz v1, :cond_1

    new-instance v2, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda7;

    invoke-direct {v2, p0}, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda7;-><init>(Lcom/ultragol/app/fragments/HomeFragment;)V

    invoke-virtual {v1, v2}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 64
    :cond_1
    return-void
.end method

.method private setupTrendingCarousel(Landroid/view/View;)V
    .locals 6
    .param p1, "view"    # Landroid/view/View;

    .line 121
    :try_start_0
    sget v0, Lcom/ultragol/app/R$id;->rowTrendingCarousel:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    .line 122
    .local v0, "carouselRoot":Landroid/view/View;
    if-nez v0, :cond_0

    return-void

    .line 124
    :cond_0
    sget v1, Lcom/ultragol/app/R$id;->trendingPager:I

    invoke-virtual {v0, v1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v1

    check-cast v1, Landroidx/viewpager2/widget/ViewPager2;

    iput-object v1, p0, Lcom/ultragol/app/fragments/HomeFragment;->trendingPager:Landroidx/viewpager2/widget/ViewPager2;

    .line 125
    sget v1, Lcom/ultragol/app/R$id;->trendingPrev:I

    invoke-virtual {v0, v1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v1

    .line 126
    .local v1, "btnPrev":Landroid/view/View;
    sget v2, Lcom/ultragol/app/R$id;->trendingNext:I

    invoke-virtual {v0, v2}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v2

    .line 128
    .local v2, "btnNext":Landroid/view/View;
    new-instance v3, Lcom/ultragol/app/adapters/TrendingAdapter;

    invoke-virtual {p0}, Lcom/ultragol/app/fragments/HomeFragment;->requireContext()Landroid/content/Context;

    move-result-object v4

    iget-object v5, p0, Lcom/ultragol/app/fragments/HomeFragment;->trendingItems:Ljava/util/List;

    invoke-direct {v3, v4, v5}, Lcom/ultragol/app/adapters/TrendingAdapter;-><init>(Landroid/content/Context;Ljava/util/List;)V

    iput-object v3, p0, Lcom/ultragol/app/fragments/HomeFragment;->trendingAdapter:Lcom/ultragol/app/adapters/TrendingAdapter;

    .line 129
    iget-object v4, p0, Lcom/ultragol/app/fragments/HomeFragment;->trendingPager:Landroidx/viewpager2/widget/ViewPager2;

    if-nez v4, :cond_1

    return-void

    .line 130
    :cond_1
    invoke-virtual {v4, v3}, Landroidx/viewpager2/widget/ViewPager2;->setAdapter(Landroidx/recyclerview/widget/RecyclerView$Adapter;)V

    .line 131
    iget-object v3, p0, Lcom/ultragol/app/fragments/HomeFragment;->trendingPager:Landroidx/viewpager2/widget/ViewPager2;

    const/4 v4, 0x1

    invoke-virtual {v3, v4}, Landroidx/viewpager2/widget/ViewPager2;->setOffscreenPageLimit(I)V

    .line 132
    iget-object v3, p0, Lcom/ultragol/app/fragments/HomeFragment;->trendingPager:Landroidx/viewpager2/widget/ViewPager2;

    new-instance v4, Lcom/ultragol/app/fragments/HomeFragment$3;

    invoke-direct {v4, p0}, Lcom/ultragol/app/fragments/HomeFragment$3;-><init>(Lcom/ultragol/app/fragments/HomeFragment;)V

    invoke-virtual {v3, v4}, Landroidx/viewpager2/widget/ViewPager2;->registerOnPageChangeCallback(Landroidx/viewpager2/widget/ViewPager2$OnPageChangeCallback;)V

    .line 136
    if-eqz v1, :cond_2

    new-instance v3, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda0;

    invoke-direct {v3, p0}, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda0;-><init>(Lcom/ultragol/app/fragments/HomeFragment;)V

    invoke-virtual {v1, v3}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 139
    :cond_2
    if-eqz v2, :cond_3

    new-instance v3, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda9;

    invoke-direct {v3, p0}, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda9;-><init>(Lcom/ultragol/app/fragments/HomeFragment;)V

    invoke-virtual {v2, v3}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    .line 143
    .end local v0    # "carouselRoot":Landroid/view/View;
    .end local v1    # "btnPrev":Landroid/view/View;
    .end local v2    # "btnNext":Landroid/view/View;
    :catch_0
    move-exception v0

    :cond_3
    :goto_0
    nop

    .line 144
    return-void
.end method

.method private updateDots(I)V
    .locals 8
    .param p1, "active"    # I

    .line 105
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->dots:Landroid/widget/LinearLayout;

    if-nez v0, :cond_0

    return-void

    .line 106
    :cond_0
    const/4 v0, 0x0

    .local v0, "i":I
    :goto_0
    iget-object v1, p0, Lcom/ultragol/app/fragments/HomeFragment;->dots:Landroid/widget/LinearLayout;

    invoke-virtual {v1}, Landroid/widget/LinearLayout;->getChildCount()I

    move-result v1

    if-ge v0, v1, :cond_4

    .line 107
    iget-object v1, p0, Lcom/ultragol/app/fragments/HomeFragment;->dots:Landroid/widget/LinearLayout;

    invoke-virtual {v1, v0}, Landroid/widget/LinearLayout;->getChildAt(I)Landroid/view/View;

    move-result-object v1

    .line 108
    .local v1, "d":Landroid/view/View;
    const/4 v2, 0x0

    if-ne v0, p1, :cond_1

    const/4 v3, 0x1

    goto :goto_1

    :cond_1
    const/4 v3, 0x0

    .line 109
    .local v3, "a":Z
    :goto_1
    if-eqz v3, :cond_2

    const/16 v4, 0xa

    goto :goto_2

    :cond_2
    const/4 v4, 0x6

    :goto_2
    invoke-direct {p0, v4}, Lcom/ultragol/app/fragments/HomeFragment;->dp(I)I

    move-result v4

    .line 110
    .local v4, "sz":I
    new-instance v5, Landroid/widget/LinearLayout$LayoutParams;

    invoke-direct {v5, v4, v4}, Landroid/widget/LinearLayout$LayoutParams;-><init>(II)V

    .line 111
    .local v5, "p":Landroid/widget/LinearLayout$LayoutParams;
    const/4 v6, 0x4

    invoke-direct {p0, v6}, Lcom/ultragol/app/fragments/HomeFragment;->dp(I)I

    move-result v7

    invoke-direct {p0, v6}, Lcom/ultragol/app/fragments/HomeFragment;->dp(I)I

    move-result v6

    invoke-virtual {v5, v7, v2, v6, v2}, Landroid/widget/LinearLayout$LayoutParams;->setMargins(IIII)V

    .line 112
    invoke-virtual {v1, v5}, Landroid/view/View;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    .line 113
    if-eqz v3, :cond_3

    sget v2, Lcom/ultragol/app/R$drawable;->dot_active:I

    goto :goto_3

    :cond_3
    sget v2, Lcom/ultragol/app/R$drawable;->dot_inactive:I

    :goto_3
    invoke-virtual {v1, v2}, Landroid/view/View;->setBackgroundResource(I)V

    .line 106
    .end local v1    # "d":Landroid/view/View;
    .end local v3    # "a":Z
    .end local v4    # "sz":I
    .end local v5    # "p":Landroid/widget/LinearLayout$LayoutParams;
    add-int/lit8 v0, v0, 0x1

    goto :goto_0

    .line 115
    .end local v0    # "i":I
    :cond_4
    return-void
.end method


# virtual methods
.method synthetic lambda$loadAll$10$com-ultragol-app-fragments-HomeFragment(Ljava/util/List;)V
    .locals 1
    .param p1, "r"    # Ljava/util/List;

    .line 214
    :try_start_0
    invoke-virtual {p0}, Lcom/ultragol/app/fragments/HomeFragment;->isAdded()Z

    move-result v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->rowSeries:Landroid/view/View;

    invoke-direct {p0, v0, p1}, Lcom/ultragol/app/fragments/HomeFragment;->fillRow(Landroid/view/View;Ljava/util/List;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception v0

    :cond_0
    :goto_0
    return-void
.end method

.method synthetic lambda$loadAll$11$com-ultragol-app-fragments-HomeFragment(Landroid/os/Handler;)V
    .locals 2
    .param p1, "h"    # Landroid/os/Handler;

    .line 213
    :try_start_0
    invoke-static {}, Lcom/ultragol/app/network/TmdbApi;->fetchSeries()Ljava/util/List;

    move-result-object v0

    .line 214
    .local v0, "r":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    new-instance v1, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda3;

    invoke-direct {v1, p0, v0}, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda3;-><init>(Lcom/ultragol/app/fragments/HomeFragment;Ljava/util/List;)V

    invoke-virtual {p1, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 215
    nop

    .end local v0    # "r":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    goto :goto_0

    :catch_0
    move-exception v0

    :goto_0
    return-void
.end method

.method synthetic lambda$loadAll$12$com-ultragol-app-fragments-HomeFragment(Ljava/util/List;)V
    .locals 1
    .param p1, "r"    # Ljava/util/List;

    .line 219
    :try_start_0
    invoke-virtual {p0}, Lcom/ultragol/app/fragments/HomeFragment;->isAdded()Z

    move-result v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->rowAnime:Landroid/view/View;

    invoke-direct {p0, v0, p1}, Lcom/ultragol/app/fragments/HomeFragment;->fillRow(Landroid/view/View;Ljava/util/List;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception v0

    :cond_0
    :goto_0
    return-void
.end method

.method synthetic lambda$loadAll$13$com-ultragol-app-fragments-HomeFragment(Landroid/os/Handler;)V
    .locals 2
    .param p1, "h"    # Landroid/os/Handler;

    .line 218
    :try_start_0
    invoke-static {}, Lcom/ultragol/app/network/TmdbApi;->fetchAnime()Ljava/util/List;

    move-result-object v0

    .line 219
    .local v0, "r":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    new-instance v1, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda2;

    invoke-direct {v1, p0, v0}, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda2;-><init>(Lcom/ultragol/app/fragments/HomeFragment;Ljava/util/List;)V

    invoke-virtual {p1, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 220
    nop

    .end local v0    # "r":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    goto :goto_0

    :catch_0
    move-exception v0

    :goto_0
    return-void
.end method

.method synthetic lambda$loadAll$14$com-ultragol-app-fragments-HomeFragment(Ljava/util/List;)V
    .locals 1
    .param p1, "r"    # Ljava/util/List;

    .line 224
    :try_start_0
    invoke-virtual {p0}, Lcom/ultragol/app/fragments/HomeFragment;->isAdded()Z

    move-result v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->rowDoramas:Landroid/view/View;

    invoke-direct {p0, v0, p1}, Lcom/ultragol/app/fragments/HomeFragment;->fillRow(Landroid/view/View;Ljava/util/List;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception v0

    :cond_0
    :goto_0
    return-void
.end method

.method synthetic lambda$loadAll$15$com-ultragol-app-fragments-HomeFragment(Landroid/os/Handler;)V
    .locals 2
    .param p1, "h"    # Landroid/os/Handler;

    .line 223
    :try_start_0
    invoke-static {}, Lcom/ultragol/app/network/TmdbApi;->fetchDoramas()Ljava/util/List;

    move-result-object v0

    .line 224
    .local v0, "r":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    new-instance v1, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda1;

    invoke-direct {v1, p0, v0}, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda1;-><init>(Lcom/ultragol/app/fragments/HomeFragment;Ljava/util/List;)V

    invoke-virtual {p1, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 225
    nop

    .end local v0    # "r":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    goto :goto_0

    :catch_0
    move-exception v0

    :goto_0
    return-void
.end method

.method synthetic lambda$loadAll$16$com-ultragol-app-fragments-HomeFragment(Ljava/util/List;)V
    .locals 1
    .param p1, "r"    # Ljava/util/List;

    .line 229
    :try_start_0
    invoke-virtual {p0}, Lcom/ultragol/app/fragments/HomeFragment;->isAdded()Z

    move-result v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->rowTop10:Landroid/view/View;

    invoke-direct {p0, v0, p1}, Lcom/ultragol/app/fragments/HomeFragment;->fillRow(Landroid/view/View;Ljava/util/List;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception v0

    :cond_0
    :goto_0
    return-void
.end method

.method synthetic lambda$loadAll$17$com-ultragol-app-fragments-HomeFragment(Landroid/os/Handler;)V
    .locals 2
    .param p1, "h"    # Landroid/os/Handler;

    .line 228
    :try_start_0
    invoke-static {}, Lcom/ultragol/app/network/TmdbApi;->fetchTopMovies()Ljava/util/List;

    move-result-object v0

    .line 229
    .local v0, "r":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    new-instance v1, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda4;

    invoke-direct {v1, p0, v0}, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda4;-><init>(Lcom/ultragol/app/fragments/HomeFragment;Ljava/util/List;)V

    invoke-virtual {p1, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 230
    nop

    .end local v0    # "r":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    goto :goto_0

    :catch_0
    move-exception v0

    :goto_0
    return-void
.end method

.method synthetic lambda$loadAll$4$com-ultragol-app-fragments-HomeFragment(Ljava/util/List;)V
    .locals 3
    .param p1, "r"    # Ljava/util/List;

    .line 185
    :try_start_0
    invoke-virtual {p0}, Lcom/ultragol/app/fragments/HomeFragment;->isAdded()Z

    move-result v0

    if-nez v0, :cond_0

    return-void

    .line 187
    :cond_0
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->bannerItems:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->clear()V

    .line 188
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->bannerItems:Ljava/util/List;

    invoke-interface {p1}, Ljava/util/List;->size()I

    move-result v1

    const/4 v2, 0x6

    if-le v1, v2, :cond_1

    const/4 v1, 0x0

    invoke-interface {p1, v1, v2}, Ljava/util/List;->subList(II)Ljava/util/List;

    move-result-object v1

    goto :goto_0

    :cond_1
    move-object v1, p1

    :goto_0
    invoke-interface {v0, v1}, Ljava/util/List;->addAll(Ljava/util/Collection;)Z

    .line 189
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->bannerAdapter:Lcom/ultragol/app/adapters/BannerAdapter;

    if-eqz v0, :cond_2

    invoke-virtual {v0}, Lcom/ultragol/app/adapters/BannerAdapter;->notifyDataSetChanged()V

    .line 190
    :cond_2
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->bannerItems:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->size()I

    move-result v0

    iput v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->bannerCount:I

    .line 191
    invoke-direct {p0}, Lcom/ultragol/app/fragments/HomeFragment;->buildDots()V

    .line 193
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->trendingItems:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->clear()V

    .line 194
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->trendingItems:Ljava/util/List;

    invoke-interface {v0, p1}, Ljava/util/List;->addAll(Ljava/util/Collection;)Z

    .line 195
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->trendingAdapter:Lcom/ultragol/app/adapters/TrendingAdapter;

    if-eqz v0, :cond_3

    invoke-virtual {v0}, Lcom/ultragol/app/adapters/TrendingAdapter;->notifyDataSetChanged()V

    .line 197
    :cond_3
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->rowTrending:Landroid/view/View;

    invoke-direct {p0, v0, p1}, Lcom/ultragol/app/fragments/HomeFragment;->fillRow(Landroid/view/View;Ljava/util/List;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_1

    .line 198
    :catch_0
    move-exception v0

    :goto_1
    nop

    .line 199
    return-void
.end method

.method synthetic lambda$loadAll$5$com-ultragol-app-fragments-HomeFragment(Landroid/os/Handler;)V
    .locals 2
    .param p1, "h"    # Landroid/os/Handler;

    .line 182
    :try_start_0
    invoke-static {}, Lcom/ultragol/app/network/TmdbApi;->fetchTrending()Ljava/util/List;

    move-result-object v0

    .line 183
    .local v0, "r":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    new-instance v1, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda5;

    invoke-direct {v1, p0, v0}, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda5;-><init>(Lcom/ultragol/app/fragments/HomeFragment;Ljava/util/List;)V

    invoke-virtual {p1, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 200
    nop

    .end local v0    # "r":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    goto :goto_0

    :catch_0
    move-exception v0

    :goto_0
    return-void
.end method

.method synthetic lambda$loadAll$6$com-ultragol-app-fragments-HomeFragment(Ljava/util/List;)V
    .locals 1
    .param p1, "r"    # Ljava/util/List;

    .line 204
    :try_start_0
    invoke-virtual {p0}, Lcom/ultragol/app/fragments/HomeFragment;->isAdded()Z

    move-result v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->rowNew:Landroid/view/View;

    invoke-direct {p0, v0, p1}, Lcom/ultragol/app/fragments/HomeFragment;->fillRow(Landroid/view/View;Ljava/util/List;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception v0

    :cond_0
    :goto_0
    return-void
.end method

.method synthetic lambda$loadAll$7$com-ultragol-app-fragments-HomeFragment(Landroid/os/Handler;)V
    .locals 2
    .param p1, "h"    # Landroid/os/Handler;

    .line 203
    :try_start_0
    invoke-static {}, Lcom/ultragol/app/network/TmdbApi;->fetchNewMovies()Ljava/util/List;

    move-result-object v0

    .line 204
    .local v0, "r":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    new-instance v1, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda17;

    invoke-direct {v1, p0, v0}, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda17;-><init>(Lcom/ultragol/app/fragments/HomeFragment;Ljava/util/List;)V

    invoke-virtual {p1, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 205
    nop

    .end local v0    # "r":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    goto :goto_0

    :catch_0
    move-exception v0

    :goto_0
    return-void
.end method

.method synthetic lambda$loadAll$8$com-ultragol-app-fragments-HomeFragment(Ljava/util/List;)V
    .locals 1
    .param p1, "r"    # Ljava/util/List;

    .line 209
    :try_start_0
    invoke-virtual {p0}, Lcom/ultragol/app/fragments/HomeFragment;->isAdded()Z

    move-result v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->rowMovies:Landroid/view/View;

    invoke-direct {p0, v0, p1}, Lcom/ultragol/app/fragments/HomeFragment;->fillRow(Landroid/view/View;Ljava/util/List;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception v0

    :cond_0
    :goto_0
    return-void
.end method

.method synthetic lambda$loadAll$9$com-ultragol-app-fragments-HomeFragment(Landroid/os/Handler;)V
    .locals 2
    .param p1, "h"    # Landroid/os/Handler;

    .line 208
    :try_start_0
    invoke-static {}, Lcom/ultragol/app/network/TmdbApi;->fetchMovies()Ljava/util/List;

    move-result-object v0

    .line 209
    .local v0, "r":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    new-instance v1, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda8;

    invoke-direct {v1, p0, v0}, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda8;-><init>(Lcom/ultragol/app/fragments/HomeFragment;Ljava/util/List;)V

    invoke-virtual {p1, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 210
    nop

    .end local v0    # "r":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    goto :goto_0

    :catch_0
    move-exception v0

    :goto_0
    return-void
.end method

.method synthetic lambda$setupTopBar$0$com-ultragol-app-fragments-HomeFragment(Landroid/view/View;)V
    .locals 3
    .param p1, "v"    # Landroid/view/View;

    .line 56
    new-instance v0, Landroid/content/Intent;

    invoke-virtual {p0}, Lcom/ultragol/app/fragments/HomeFragment;->requireContext()Landroid/content/Context;

    move-result-object v1

    const-class v2, Lcom/ultragol/app/SearchActivity;

    invoke-direct {v0, v1, v2}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    invoke-virtual {p0, v0}, Lcom/ultragol/app/fragments/HomeFragment;->startActivity(Landroid/content/Intent;)V

    return-void
.end method

.method synthetic lambda$setupTopBar$1$com-ultragol-app-fragments-HomeFragment(Landroid/view/View;)V
    .locals 1
    .param p1, "v"    # Landroid/view/View;

    .line 60
    invoke-virtual {p0}, Lcom/ultragol/app/fragments/HomeFragment;->requireActivity()Landroidx/fragment/app/FragmentActivity;

    move-result-object v0

    instance-of v0, v0, Lcom/ultragol/app/MainActivity;

    if-eqz v0, :cond_0

    .line 61
    invoke-virtual {p0}, Lcom/ultragol/app/fragments/HomeFragment;->requireActivity()Landroidx/fragment/app/FragmentActivity;

    move-result-object v0

    check-cast v0, Lcom/ultragol/app/MainActivity;

    invoke-virtual {v0}, Lcom/ultragol/app/MainActivity;->showMenu()V

    .line 63
    :cond_0
    return-void
.end method

.method synthetic lambda$setupTrendingCarousel$2$com-ultragol-app-fragments-HomeFragment(Landroid/view/View;)V
    .locals 3
    .param p1, "v"    # Landroid/view/View;

    .line 137
    iget v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->trendingPage:I

    if-lez v0, :cond_0

    iget-object v1, p0, Lcom/ultragol/app/fragments/HomeFragment;->trendingPager:Landroidx/viewpager2/widget/ViewPager2;

    const/4 v2, 0x1

    sub-int/2addr v0, v2

    invoke-virtual {v1, v0, v2}, Landroidx/viewpager2/widget/ViewPager2;->setCurrentItem(IZ)V

    .line 138
    :cond_0
    return-void
.end method

.method synthetic lambda$setupTrendingCarousel$3$com-ultragol-app-fragments-HomeFragment(Landroid/view/View;)V
    .locals 3
    .param p1, "v"    # Landroid/view/View;

    .line 140
    iget v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->trendingPage:I

    iget-object v1, p0, Lcom/ultragol/app/fragments/HomeFragment;->trendingItems:Ljava/util/List;

    invoke-interface {v1}, Ljava/util/List;->size()I

    move-result v1

    const/4 v2, 0x1

    sub-int/2addr v1, v2

    if-ge v0, v1, :cond_0

    .line 141
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->trendingPager:Landroidx/viewpager2/widget/ViewPager2;

    iget v1, p0, Lcom/ultragol/app/fragments/HomeFragment;->trendingPage:I

    add-int/2addr v1, v2

    invoke-virtual {v0, v1, v2}, Landroidx/viewpager2/widget/ViewPager2;->setCurrentItem(IZ)V

    .line 142
    :cond_0
    return-void
.end method

.method public onCreateView(Landroid/view/LayoutInflater;Landroid/view/ViewGroup;Landroid/os/Bundle;)Landroid/view/View;
    .locals 2
    .param p1, "inflater"    # Landroid/view/LayoutInflater;
    .param p2, "parent"    # Landroid/view/ViewGroup;
    .param p3, "state"    # Landroid/os/Bundle;

    .line 40
    sget v0, Lcom/ultragol/app/R$layout;->fragment_home:I

    const/4 v1, 0x0

    invoke-virtual {p1, v0, p2, v1}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    move-result-object v0

    return-object v0
.end method

.method public onDestroyView()V
    .locals 2

    .line 246
    invoke-super {p0}, Landroidx/fragment/app/Fragment;->onDestroyView()V

    .line 247
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment;->autoHandler:Landroid/os/Handler;

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeCallbacksAndMessages(Ljava/lang/Object;)V

    .line 248
    return-void
.end method

.method public onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V
    .locals 0
    .param p1, "view"    # Landroid/view/View;
    .param p2, "state"    # Landroid/os/Bundle;

    .line 45
    invoke-super {p0, p1, p2}, Landroidx/fragment/app/Fragment;->onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V

    .line 46
    invoke-direct {p0, p1}, Lcom/ultragol/app/fragments/HomeFragment;->setupTopBar(Landroid/view/View;)V

    .line 47
    invoke-direct {p0, p1}, Lcom/ultragol/app/fragments/HomeFragment;->setupHero(Landroid/view/View;)V

    .line 48
    invoke-direct {p0, p1}, Lcom/ultragol/app/fragments/HomeFragment;->setupTrendingCarousel(Landroid/view/View;)V

    .line 49
    invoke-direct {p0, p1}, Lcom/ultragol/app/fragments/HomeFragment;->setupRows(Landroid/view/View;)V

    .line 50
    invoke-direct {p0}, Lcom/ultragol/app/fragments/HomeFragment;->loadAll()V

    .line 51
    return-void
.end method
