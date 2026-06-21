.class public Lcom/ultragol/app/fragments/PlatformFragment;
.super Landroidx/fragment/app/Fragment;
.source "PlatformFragment.java"


# static fields
.field private static final ARG_NAME:Ljava/lang/String; = "platform_name"

.field private static final ARG_PROVIDER:Ljava/lang/String; = "provider_id"

.field private static final ARG_TYPE:Ljava/lang/String; = "content_type"


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

    .line 69
    invoke-interface {p0, p1}, Ljava/util/List;->addAll(Ljava/util/Collection;)Z

    .line 70
    invoke-virtual {p2}, Lcom/ultragol/app/adapters/ContentGridAdapter;->notifyDataSetChanged()V

    .line 71
    if-eqz p3, :cond_0

    const/16 v0, 0x8

    invoke-virtual {p3, v0}, Landroid/widget/ProgressBar;->setVisibility(I)V

    .line 72
    :cond_0
    return-void
.end method

.method static synthetic lambda$onViewCreated$1(Landroid/widget/ProgressBar;)V
    .locals 1
    .param p0, "pb"    # Landroid/widget/ProgressBar;

    .line 75
    if-eqz p0, :cond_0

    const/16 v0, 0x8

    invoke-virtual {p0, v0}, Landroid/widget/ProgressBar;->setVisibility(I)V

    .line 76
    :cond_0
    return-void
.end method

.method public static newInstance(Ljava/lang/String;ILjava/lang/String;)Lcom/ultragol/app/fragments/PlatformFragment;
    .locals 3
    .param p0, "platformName"    # Ljava/lang/String;
    .param p1, "providerId"    # I
    .param p2, "type"    # Ljava/lang/String;

    .line 24
    new-instance v0, Lcom/ultragol/app/fragments/PlatformFragment;

    invoke-direct {v0}, Lcom/ultragol/app/fragments/PlatformFragment;-><init>()V

    .line 25
    .local v0, "f":Lcom/ultragol/app/fragments/PlatformFragment;
    new-instance v1, Landroid/os/Bundle;

    invoke-direct {v1}, Landroid/os/Bundle;-><init>()V

    .line 26
    .local v1, "b":Landroid/os/Bundle;
    const-string v2, "platform_name"

    invoke-virtual {v1, v2, p0}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    .line 27
    const-string v2, "provider_id"

    invoke-virtual {v1, v2, p1}, Landroid/os/Bundle;->putInt(Ljava/lang/String;I)V

    .line 28
    const-string v2, "content_type"

    invoke-virtual {v1, v2, p2}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    .line 29
    invoke-virtual {v0, v1}, Lcom/ultragol/app/fragments/PlatformFragment;->setArguments(Landroid/os/Bundle;)V

    .line 30
    return-object v0
.end method


# virtual methods
.method synthetic lambda$onViewCreated$2$com-ultragol-app-fragments-PlatformFragment(Ljava/lang/String;ILjava/util/List;Lcom/ultragol/app/adapters/ContentGridAdapter;Landroid/widget/ProgressBar;)V
    .locals 4
    .param p1, "type"    # Ljava/lang/String;
    .param p2, "providerId"    # I
    .param p3, "items"    # Ljava/util/List;
    .param p4, "adapter"    # Lcom/ultragol/app/adapters/ContentGridAdapter;
    .param p5, "pb"    # Landroid/widget/ProgressBar;

    .line 59
    const-string v0, "movie"

    :try_start_0
    new-instance v1, Ljava/util/ArrayList;

    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    .line 60
    .local v1, "r":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    const-string v2, "anime"

    invoke-virtual {v2, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v2
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    const-string v3, "tv"

    if-eqz v2, :cond_0

    .line 61
    :try_start_1
    invoke-static {p2, v3}, Lcom/ultragol/app/network/TmdbApi;->fetchByProvider(ILjava/lang/String;)Ljava/util/List;

    move-result-object v0

    invoke-interface {v1, v0}, Ljava/util/List;->addAll(Ljava/util/Collection;)Z

    goto :goto_0

    .line 62
    :cond_0
    invoke-virtual {v0, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v2

    if-eqz v2, :cond_1

    .line 63
    invoke-static {p2, v0}, Lcom/ultragol/app/network/TmdbApi;->fetchByProvider(ILjava/lang/String;)Ljava/util/List;

    move-result-object v0

    invoke-interface {v1, v0}, Ljava/util/List;->addAll(Ljava/util/Collection;)Z

    goto :goto_0

    .line 65
    :cond_1
    invoke-static {p2, v0}, Lcom/ultragol/app/network/TmdbApi;->fetchByProvider(ILjava/lang/String;)Ljava/util/List;

    move-result-object v0

    invoke-interface {v1, v0}, Ljava/util/List;->addAll(Ljava/util/Collection;)Z

    .line 66
    invoke-static {p2, v3}, Lcom/ultragol/app/network/TmdbApi;->fetchByProvider(ILjava/lang/String;)Ljava/util/List;

    move-result-object v0

    invoke-interface {v1, v0}, Ljava/util/List;->addAll(Ljava/util/Collection;)Z

    .line 68
    :goto_0
    invoke-virtual {p0}, Lcom/ultragol/app/fragments/PlatformFragment;->requireActivity()Landroidx/fragment/app/FragmentActivity;

    move-result-object v0

    new-instance v2, Lcom/ultragol/app/fragments/PlatformFragment$$ExternalSyntheticLambda0;

    invoke-direct {v2, p3, v1, p4, p5}, Lcom/ultragol/app/fragments/PlatformFragment$$ExternalSyntheticLambda0;-><init>(Ljava/util/List;Ljava/util/List;Lcom/ultragol/app/adapters/ContentGridAdapter;Landroid/widget/ProgressBar;)V

    invoke-virtual {v0, v2}, Landroidx/fragment/app/FragmentActivity;->runOnUiThread(Ljava/lang/Runnable;)V
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0

    .line 77
    .end local v1    # "r":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    goto :goto_1

    .line 73
    :catch_0
    move-exception v0

    .line 74
    .local v0, "e":Ljava/lang/Exception;
    invoke-virtual {p0}, Lcom/ultragol/app/fragments/PlatformFragment;->requireActivity()Landroidx/fragment/app/FragmentActivity;

    move-result-object v1

    new-instance v2, Lcom/ultragol/app/fragments/PlatformFragment$$ExternalSyntheticLambda1;

    invoke-direct {v2, p5}, Lcom/ultragol/app/fragments/PlatformFragment$$ExternalSyntheticLambda1;-><init>(Landroid/widget/ProgressBar;)V

    invoke-virtual {v1, v2}, Landroidx/fragment/app/FragmentActivity;->runOnUiThread(Ljava/lang/Runnable;)V

    .line 78
    .end local v0    # "e":Ljava/lang/Exception;
    :goto_1
    return-void
.end method

.method public onCreateView(Landroid/view/LayoutInflater;Landroid/view/ViewGroup;Landroid/os/Bundle;)Landroid/view/View;
    .locals 2
    .param p1, "i"    # Landroid/view/LayoutInflater;
    .param p2, "p"    # Landroid/view/ViewGroup;
    .param p3, "s"    # Landroid/os/Bundle;

    .line 35
    sget v0, Lcom/ultragol/app/R$layout;->fragment_grid:I

    const/4 v1, 0x0

    invoke-virtual {p1, v0, p2, v1}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    move-result-object v0

    return-object v0
.end method

.method public onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V
    .locals 16
    .param p1, "view"    # Landroid/view/View;
    .param p2, "s"    # Landroid/os/Bundle;

    .line 40
    move-object/from16 v0, p1

    invoke-super/range {p0 .. p2}, Landroidx/fragment/app/Fragment;->onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V

    .line 42
    invoke-virtual/range {p0 .. p0}, Lcom/ultragol/app/fragments/PlatformFragment;->getArguments()Landroid/os/Bundle;

    move-result-object v1

    const-string v2, "Plataforma"

    if-eqz v1, :cond_0

    invoke-virtual/range {p0 .. p0}, Lcom/ultragol/app/fragments/PlatformFragment;->getArguments()Landroid/os/Bundle;

    move-result-object v1

    const-string v3, "platform_name"

    invoke-virtual {v1, v3, v2}, Landroid/os/Bundle;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    :cond_0
    move-object v1, v2

    .line 43
    .local v1, "platformName":Ljava/lang/String;
    invoke-virtual/range {p0 .. p0}, Lcom/ultragol/app/fragments/PlatformFragment;->getArguments()Landroid/os/Bundle;

    move-result-object v2

    const/16 v3, 0x8

    if-eqz v2, :cond_1

    invoke-virtual/range {p0 .. p0}, Lcom/ultragol/app/fragments/PlatformFragment;->getArguments()Landroid/os/Bundle;

    move-result-object v2

    const-string v4, "provider_id"

    invoke-virtual {v2, v4, v3}, Landroid/os/Bundle;->getInt(Ljava/lang/String;I)I

    move-result v3

    move v7, v3

    goto :goto_0

    :cond_1
    const/16 v7, 0x8

    .line 44
    .local v7, "providerId":I
    :goto_0
    invoke-virtual/range {p0 .. p0}, Lcom/ultragol/app/fragments/PlatformFragment;->getArguments()Landroid/os/Bundle;

    move-result-object v2

    const-string v3, "all"

    if-eqz v2, :cond_2

    invoke-virtual/range {p0 .. p0}, Lcom/ultragol/app/fragments/PlatformFragment;->getArguments()Landroid/os/Bundle;

    move-result-object v2

    const-string v4, "content_type"

    invoke-virtual {v2, v4, v3}, Landroid/os/Bundle;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    move-object v6, v2

    goto :goto_1

    :cond_2
    move-object v6, v3

    .line 46
    .local v6, "type":Ljava/lang/String;
    :goto_1
    sget v2, Lcom/ultragol/app/R$id;->gridTitle:I

    invoke-virtual {v0, v2}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v2

    check-cast v2, Landroid/widget/TextView;

    .line 47
    .local v2, "title":Landroid/widget/TextView;
    if-eqz v2, :cond_3

    invoke-virtual {v2, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 49
    :cond_3
    sget v3, Lcom/ultragol/app/R$id;->contentGrid:I

    invoke-virtual {v0, v3}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v3

    check-cast v3, Landroidx/recyclerview/widget/RecyclerView;

    .line 50
    .local v3, "grid":Landroidx/recyclerview/widget/RecyclerView;
    sget v4, Lcom/ultragol/app/R$id;->gridLoading:I

    invoke-virtual {v0, v4}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v4

    move-object v11, v4

    check-cast v11, Landroid/widget/ProgressBar;

    .line 51
    .local v11, "pb":Landroid/widget/ProgressBar;
    new-instance v4, Ljava/util/ArrayList;

    invoke-direct {v4}, Ljava/util/ArrayList;-><init>()V

    move-object v12, v4

    .line 52
    .local v12, "items":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    new-instance v4, Lcom/ultragol/app/adapters/ContentGridAdapter;

    invoke-virtual/range {p0 .. p0}, Lcom/ultragol/app/fragments/PlatformFragment;->requireContext()Landroid/content/Context;

    move-result-object v5

    invoke-direct {v4, v5, v12}, Lcom/ultragol/app/adapters/ContentGridAdapter;-><init>(Landroid/content/Context;Ljava/util/List;)V

    move-object v13, v4

    .line 53
    .local v13, "adapter":Lcom/ultragol/app/adapters/ContentGridAdapter;
    new-instance v4, Landroidx/recyclerview/widget/GridLayoutManager;

    invoke-virtual/range {p0 .. p0}, Lcom/ultragol/app/fragments/PlatformFragment;->requireContext()Landroid/content/Context;

    move-result-object v5

    const/4 v8, 0x3

    invoke-direct {v4, v5, v8}, Landroidx/recyclerview/widget/GridLayoutManager;-><init>(Landroid/content/Context;I)V

    invoke-virtual {v3, v4}, Landroidx/recyclerview/widget/RecyclerView;->setLayoutManager(Landroidx/recyclerview/widget/RecyclerView$LayoutManager;)V

    .line 54
    invoke-virtual {v3, v13}, Landroidx/recyclerview/widget/RecyclerView;->setAdapter(Landroidx/recyclerview/widget/RecyclerView$Adapter;)V

    .line 55
    if-eqz v11, :cond_4

    const/4 v4, 0x0

    invoke-virtual {v11, v4}, Landroid/widget/ProgressBar;->setVisibility(I)V

    .line 57
    :cond_4
    invoke-static {}, Ljava/util/concurrent/Executors;->newSingleThreadExecutor()Ljava/util/concurrent/ExecutorService;

    move-result-object v14

    new-instance v15, Lcom/ultragol/app/fragments/PlatformFragment$$ExternalSyntheticLambda2;

    move-object v4, v15

    move-object/from16 v5, p0

    move-object v8, v12

    move-object v9, v13

    move-object v10, v11

    invoke-direct/range {v4 .. v10}, Lcom/ultragol/app/fragments/PlatformFragment$$ExternalSyntheticLambda2;-><init>(Lcom/ultragol/app/fragments/PlatformFragment;Ljava/lang/String;ILjava/util/List;Lcom/ultragol/app/adapters/ContentGridAdapter;Landroid/widget/ProgressBar;)V

    invoke-interface {v14, v15}, Ljava/util/concurrent/ExecutorService;->execute(Ljava/lang/Runnable;)V

    .line 79
    return-void
.end method
