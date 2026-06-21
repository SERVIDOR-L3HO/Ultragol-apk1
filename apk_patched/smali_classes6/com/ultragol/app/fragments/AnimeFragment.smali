.class public Lcom/ultragol/app/fragments/AnimeFragment;
.super Landroidx/fragment/app/Fragment;
.source "AnimeFragment.java"


# direct methods
.method public constructor <init>()V
    .locals 0

    .line 17
    invoke-direct {p0}, Landroidx/fragment/app/Fragment;-><init>()V

    return-void
.end method

.method static synthetic lambda$onViewCreated$0(Ljava/util/List;Ljava/util/List;Lcom/ultragol/app/adapters/ContentGridAdapter;Landroid/widget/ProgressBar;)V
    .locals 1
    .param p0, "items"    # Ljava/util/List;
    .param p1, "r"    # Ljava/util/List;
    .param p2, "adapter"    # Lcom/ultragol/app/adapters/ContentGridAdapter;
    .param p3, "pb"    # Landroid/widget/ProgressBar;

    .line 37
    invoke-interface {p0, p1}, Ljava/util/List;->addAll(Ljava/util/Collection;)Z

    invoke-virtual {p2}, Lcom/ultragol/app/adapters/ContentGridAdapter;->notifyDataSetChanged()V

    if-eqz p3, :cond_0

    const/16 v0, 0x8

    invoke-virtual {p3, v0}, Landroid/widget/ProgressBar;->setVisibility(I)V

    :cond_0
    return-void
.end method

.method static synthetic lambda$onViewCreated$1(Landroid/widget/ProgressBar;)V
    .locals 1
    .param p0, "pb"    # Landroid/widget/ProgressBar;

    .line 38
    if-eqz p0, :cond_0

    const/16 v0, 0x8

    invoke-virtual {p0, v0}, Landroid/widget/ProgressBar;->setVisibility(I)V

    :cond_0
    return-void
.end method


# virtual methods
.method synthetic lambda$onViewCreated$2$com-ultragol-app-fragments-AnimeFragment(Ljava/util/List;Lcom/ultragol/app/adapters/ContentGridAdapter;Landroid/widget/ProgressBar;)V
    .locals 3
    .param p1, "items"    # Ljava/util/List;
    .param p2, "adapter"    # Lcom/ultragol/app/adapters/ContentGridAdapter;
    .param p3, "pb"    # Landroid/widget/ProgressBar;

    .line 36
    :try_start_0
    invoke-static {}, Lcom/ultragol/app/network/TmdbApi;->fetchAnime()Ljava/util/List;

    move-result-object v0

    .line 37
    .local v0, "r":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    invoke-virtual {p0}, Lcom/ultragol/app/fragments/AnimeFragment;->requireActivity()Landroidx/fragment/app/FragmentActivity;

    move-result-object v1

    new-instance v2, Lcom/ultragol/app/fragments/AnimeFragment$$ExternalSyntheticLambda1;

    invoke-direct {v2, p1, v0, p2, p3}, Lcom/ultragol/app/fragments/AnimeFragment$$ExternalSyntheticLambda1;-><init>(Ljava/util/List;Ljava/util/List;Lcom/ultragol/app/adapters/ContentGridAdapter;Landroid/widget/ProgressBar;)V

    invoke-virtual {v1, v2}, Landroidx/fragment/app/FragmentActivity;->runOnUiThread(Ljava/lang/Runnable;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 38
    .end local v0    # "r":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    goto :goto_0

    :catch_0
    move-exception v0

    .local v0, "e":Ljava/lang/Exception;
    invoke-virtual {p0}, Lcom/ultragol/app/fragments/AnimeFragment;->requireActivity()Landroidx/fragment/app/FragmentActivity;

    move-result-object v1

    new-instance v2, Lcom/ultragol/app/fragments/AnimeFragment$$ExternalSyntheticLambda2;

    invoke-direct {v2, p3}, Lcom/ultragol/app/fragments/AnimeFragment$$ExternalSyntheticLambda2;-><init>(Landroid/widget/ProgressBar;)V

    invoke-virtual {v1, v2}, Landroidx/fragment/app/FragmentActivity;->runOnUiThread(Ljava/lang/Runnable;)V

    .line 39
    .end local v0    # "e":Ljava/lang/Exception;
    :goto_0
    return-void
.end method

.method public onCreateView(Landroid/view/LayoutInflater;Landroid/view/ViewGroup;Landroid/os/Bundle;)Landroid/view/View;
    .locals 2
    .param p1, "i"    # Landroid/view/LayoutInflater;
    .param p2, "p"    # Landroid/view/ViewGroup;
    .param p3, "s"    # Landroid/os/Bundle;

    .line 20
    sget v0, Lcom/ultragol/app/R$layout;->fragment_grid:I

    const/4 v1, 0x0

    invoke-virtual {p1, v0, p2, v1}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    move-result-object v0

    return-object v0
.end method

.method public onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V
    .locals 8
    .param p1, "view"    # Landroid/view/View;
    .param p2, "s"    # Landroid/os/Bundle;

    .line 24
    invoke-super {p0, p1, p2}, Landroidx/fragment/app/Fragment;->onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V

    .line 25
    sget v0, Lcom/ultragol/app/R$id;->gridTitle:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    .line 26
    .local v0, "title":Landroid/widget/TextView;
    if-eqz v0, :cond_0

    const-string v1, "\ud83c\udf8c Anime & Doramas"

    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 27
    :cond_0
    sget v1, Lcom/ultragol/app/R$id;->contentGrid:I

    invoke-virtual {p1, v1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v1

    check-cast v1, Landroidx/recyclerview/widget/RecyclerView;

    .line 28
    .local v1, "grid":Landroidx/recyclerview/widget/RecyclerView;
    sget v2, Lcom/ultragol/app/R$id;->gridLoading:I

    invoke-virtual {p1, v2}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v2

    check-cast v2, Landroid/widget/ProgressBar;

    .line 29
    .local v2, "pb":Landroid/widget/ProgressBar;
    new-instance v3, Ljava/util/ArrayList;

    invoke-direct {v3}, Ljava/util/ArrayList;-><init>()V

    .line 30
    .local v3, "items":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    new-instance v4, Lcom/ultragol/app/adapters/ContentGridAdapter;

    invoke-virtual {p0}, Lcom/ultragol/app/fragments/AnimeFragment;->requireContext()Landroid/content/Context;

    move-result-object v5

    invoke-direct {v4, v5, v3}, Lcom/ultragol/app/adapters/ContentGridAdapter;-><init>(Landroid/content/Context;Ljava/util/List;)V

    .line 31
    .local v4, "adapter":Lcom/ultragol/app/adapters/ContentGridAdapter;
    new-instance v5, Landroidx/recyclerview/widget/GridLayoutManager;

    invoke-virtual {p0}, Lcom/ultragol/app/fragments/AnimeFragment;->requireContext()Landroid/content/Context;

    move-result-object v6

    const/4 v7, 0x3

    invoke-direct {v5, v6, v7}, Landroidx/recyclerview/widget/GridLayoutManager;-><init>(Landroid/content/Context;I)V

    invoke-virtual {v1, v5}, Landroidx/recyclerview/widget/RecyclerView;->setLayoutManager(Landroidx/recyclerview/widget/RecyclerView$LayoutManager;)V

    .line 32
    invoke-virtual {v1, v4}, Landroidx/recyclerview/widget/RecyclerView;->setAdapter(Landroidx/recyclerview/widget/RecyclerView$Adapter;)V

    .line 33
    if-eqz v2, :cond_1

    const/4 v5, 0x0

    invoke-virtual {v2, v5}, Landroid/widget/ProgressBar;->setVisibility(I)V

    .line 34
    :cond_1
    invoke-static {}, Ljava/util/concurrent/Executors;->newSingleThreadExecutor()Ljava/util/concurrent/ExecutorService;

    move-result-object v5

    new-instance v6, Lcom/ultragol/app/fragments/AnimeFragment$$ExternalSyntheticLambda0;

    invoke-direct {v6, p0, v3, v4, v2}, Lcom/ultragol/app/fragments/AnimeFragment$$ExternalSyntheticLambda0;-><init>(Lcom/ultragol/app/fragments/AnimeFragment;Ljava/util/List;Lcom/ultragol/app/adapters/ContentGridAdapter;Landroid/widget/ProgressBar;)V

    invoke-interface {v5, v6}, Ljava/util/concurrent/ExecutorService;->execute(Ljava/lang/Runnable;)V

    .line 40
    return-void
.end method
