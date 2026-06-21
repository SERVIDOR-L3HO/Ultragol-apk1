.class public Lcom/ultragol/app/SearchActivity;
.super Landroidx/appcompat/app/AppCompatActivity;
.source "SearchActivity.java"


# instance fields
.field private adapter:Lcom/ultragol/app/adapters/ContentGridAdapter;

.field private emptyState:Landroid/widget/TextView;

.field private final handler:Landroid/os/Handler;

.field private loadingView:Landroid/view/View;

.field private final results:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Lcom/ultragol/app/models/ContentItem;",
            ">;"
        }
    .end annotation
.end field

.field private resultsGrid:Landroidx/recyclerview/widget/RecyclerView;

.field private searchInput:Landroid/widget/EditText;

.field private searchRunnable:Ljava/lang/Runnable;


# direct methods
.method public constructor <init>()V
    .locals 1

    .line 23
    invoke-direct {p0}, Landroidx/appcompat/app/AppCompatActivity;-><init>()V

    .line 29
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Lcom/ultragol/app/SearchActivity;->results:Ljava/util/List;

    .line 30
    new-instance v0, Landroid/os/Handler;

    invoke-direct {v0}, Landroid/os/Handler;-><init>()V

    iput-object v0, p0, Lcom/ultragol/app/SearchActivity;->handler:Landroid/os/Handler;

    return-void
.end method

.method static synthetic access$000(Lcom/ultragol/app/SearchActivity;)Ljava/lang/Runnable;
    .locals 1
    .param p0, "x0"    # Lcom/ultragol/app/SearchActivity;

    .line 23
    iget-object v0, p0, Lcom/ultragol/app/SearchActivity;->searchRunnable:Ljava/lang/Runnable;

    return-object v0
.end method

.method static synthetic access$002(Lcom/ultragol/app/SearchActivity;Ljava/lang/Runnable;)Ljava/lang/Runnable;
    .locals 0
    .param p0, "x0"    # Lcom/ultragol/app/SearchActivity;
    .param p1, "x1"    # Ljava/lang/Runnable;

    .line 23
    iput-object p1, p0, Lcom/ultragol/app/SearchActivity;->searchRunnable:Ljava/lang/Runnable;

    return-object p1
.end method

.method static synthetic access$100(Lcom/ultragol/app/SearchActivity;)Landroid/os/Handler;
    .locals 1
    .param p0, "x0"    # Lcom/ultragol/app/SearchActivity;

    .line 23
    iget-object v0, p0, Lcom/ultragol/app/SearchActivity;->handler:Landroid/os/Handler;

    return-object v0
.end method

.method static synthetic access$200(Lcom/ultragol/app/SearchActivity;)V
    .locals 0
    .param p0, "x0"    # Lcom/ultragol/app/SearchActivity;

    .line 23
    invoke-direct {p0}, Lcom/ultragol/app/SearchActivity;->showEmpty()V

    return-void
.end method

.method static synthetic access$300(Lcom/ultragol/app/SearchActivity;Ljava/lang/String;)V
    .locals 0
    .param p0, "x0"    # Lcom/ultragol/app/SearchActivity;
    .param p1, "x1"    # Ljava/lang/String;

    .line 23
    invoke-direct {p0, p1}, Lcom/ultragol/app/SearchActivity;->doSearch(Ljava/lang/String;)V

    return-void
.end method

.method private doSearch(Ljava/lang/String;)V
    .locals 2
    .param p1, "query"    # Ljava/lang/String;

    .line 66
    iget-object v0, p0, Lcom/ultragol/app/SearchActivity;->loadingView:Landroid/view/View;

    if-eqz v0, :cond_0

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 67
    :cond_0
    iget-object v0, p0, Lcom/ultragol/app/SearchActivity;->emptyState:Landroid/widget/TextView;

    if-eqz v0, :cond_1

    const/16 v1, 0x8

    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setVisibility(I)V

    .line 68
    :cond_1
    invoke-static {}, Ljava/util/concurrent/Executors;->newSingleThreadExecutor()Ljava/util/concurrent/ExecutorService;

    move-result-object v0

    .line 69
    .local v0, "exec":Ljava/util/concurrent/ExecutorService;
    new-instance v1, Lcom/ultragol/app/SearchActivity$$ExternalSyntheticLambda1;

    invoke-direct {v1, p0, p1}, Lcom/ultragol/app/SearchActivity$$ExternalSyntheticLambda1;-><init>(Lcom/ultragol/app/SearchActivity;Ljava/lang/String;)V

    invoke-interface {v0, v1}, Ljava/util/concurrent/ExecutorService;->execute(Ljava/lang/Runnable;)V

    .line 85
    invoke-interface {v0}, Ljava/util/concurrent/ExecutorService;->shutdown()V

    .line 86
    return-void
.end method

.method private showEmpty()V
    .locals 2

    .line 89
    iget-object v0, p0, Lcom/ultragol/app/SearchActivity;->results:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->clear()V

    iget-object v0, p0, Lcom/ultragol/app/SearchActivity;->adapter:Lcom/ultragol/app/adapters/ContentGridAdapter;

    invoke-virtual {v0}, Lcom/ultragol/app/adapters/ContentGridAdapter;->notifyDataSetChanged()V

    .line 90
    iget-object v0, p0, Lcom/ultragol/app/SearchActivity;->loadingView:Landroid/view/View;

    if-eqz v0, :cond_0

    const/16 v1, 0x8

    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 91
    :cond_0
    iget-object v0, p0, Lcom/ultragol/app/SearchActivity;->emptyState:Landroid/widget/TextView;

    if-eqz v0, :cond_1

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setVisibility(I)V

    .line 92
    :cond_1
    return-void
.end method


# virtual methods
.method synthetic lambda$doSearch$1$com-ultragol-app-SearchActivity(Ljava/util/List;)V
    .locals 3
    .param p1, "r"    # Ljava/util/List;

    .line 73
    iget-object v0, p0, Lcom/ultragol/app/SearchActivity;->results:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->clear()V

    iget-object v0, p0, Lcom/ultragol/app/SearchActivity;->results:Ljava/util/List;

    invoke-interface {v0, p1}, Ljava/util/List;->addAll(Ljava/util/Collection;)Z

    .line 74
    iget-object v0, p0, Lcom/ultragol/app/SearchActivity;->adapter:Lcom/ultragol/app/adapters/ContentGridAdapter;

    invoke-virtual {v0}, Lcom/ultragol/app/adapters/ContentGridAdapter;->notifyDataSetChanged()V

    .line 75
    iget-object v0, p0, Lcom/ultragol/app/SearchActivity;->loadingView:Landroid/view/View;

    const/16 v1, 0x8

    if-eqz v0, :cond_0

    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 76
    :cond_0
    iget-object v0, p0, Lcom/ultragol/app/SearchActivity;->emptyState:Landroid/widget/TextView;

    if-eqz v0, :cond_2

    iget-object v2, p0, Lcom/ultragol/app/SearchActivity;->results:Ljava/util/List;

    invoke-interface {v2}, Ljava/util/List;->isEmpty()Z

    move-result v2

    if-eqz v2, :cond_1

    const/4 v1, 0x0

    :cond_1
    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setVisibility(I)V

    .line 77
    :cond_2
    return-void
.end method

.method synthetic lambda$doSearch$2$com-ultragol-app-SearchActivity()V
    .locals 2

    .line 80
    iget-object v0, p0, Lcom/ultragol/app/SearchActivity;->loadingView:Landroid/view/View;

    if-eqz v0, :cond_0

    const/16 v1, 0x8

    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 81
    :cond_0
    iget-object v0, p0, Lcom/ultragol/app/SearchActivity;->emptyState:Landroid/widget/TextView;

    if-eqz v0, :cond_1

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setVisibility(I)V

    .line 82
    :cond_1
    return-void
.end method

.method synthetic lambda$doSearch$3$com-ultragol-app-SearchActivity(Ljava/lang/String;)V
    .locals 2
    .param p1, "query"    # Ljava/lang/String;

    .line 71
    :try_start_0
    invoke-static {p1}, Lcom/ultragol/app/network/TmdbApi;->searchMulti(Ljava/lang/String;)Ljava/util/List;

    move-result-object v0

    .line 72
    .local v0, "r":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    new-instance v1, Lcom/ultragol/app/SearchActivity$$ExternalSyntheticLambda2;

    invoke-direct {v1, p0, v0}, Lcom/ultragol/app/SearchActivity$$ExternalSyntheticLambda2;-><init>(Lcom/ultragol/app/SearchActivity;Ljava/util/List;)V

    invoke-virtual {p0, v1}, Lcom/ultragol/app/SearchActivity;->runOnUiThread(Ljava/lang/Runnable;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 83
    .end local v0    # "r":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    goto :goto_0

    .line 78
    :catch_0
    move-exception v0

    .line 79
    .local v0, "e":Ljava/lang/Exception;
    new-instance v1, Lcom/ultragol/app/SearchActivity$$ExternalSyntheticLambda3;

    invoke-direct {v1, p0}, Lcom/ultragol/app/SearchActivity$$ExternalSyntheticLambda3;-><init>(Lcom/ultragol/app/SearchActivity;)V

    invoke-virtual {p0, v1}, Lcom/ultragol/app/SearchActivity;->runOnUiThread(Ljava/lang/Runnable;)V

    .line 84
    .end local v0    # "e":Ljava/lang/Exception;
    :goto_0
    return-void
.end method

.method synthetic lambda$onCreate$0$com-ultragol-app-SearchActivity(Landroid/view/View;)V
    .locals 0
    .param p1, "v"    # Landroid/view/View;

    .line 44
    invoke-virtual {p0}, Lcom/ultragol/app/SearchActivity;->finish()V

    return-void
.end method

.method protected onCreate(Landroid/os/Bundle;)V
    .locals 4
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    .line 35
    invoke-super {p0, p1}, Landroidx/appcompat/app/AppCompatActivity;->onCreate(Landroid/os/Bundle;)V

    .line 36
    sget v0, Lcom/ultragol/app/R$layout;->activity_search:I

    invoke-virtual {p0, v0}, Lcom/ultragol/app/SearchActivity;->setContentView(I)V

    .line 38
    sget v0, Lcom/ultragol/app/R$id;->searchInput:I

    invoke-virtual {p0, v0}, Lcom/ultragol/app/SearchActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/EditText;

    iput-object v0, p0, Lcom/ultragol/app/SearchActivity;->searchInput:Landroid/widget/EditText;

    .line 39
    sget v0, Lcom/ultragol/app/R$id;->resultsGrid:I

    invoke-virtual {p0, v0}, Lcom/ultragol/app/SearchActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroidx/recyclerview/widget/RecyclerView;

    iput-object v0, p0, Lcom/ultragol/app/SearchActivity;->resultsGrid:Landroidx/recyclerview/widget/RecyclerView;

    .line 40
    sget v0, Lcom/ultragol/app/R$id;->emptyState:I

    invoke-virtual {p0, v0}, Lcom/ultragol/app/SearchActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    iput-object v0, p0, Lcom/ultragol/app/SearchActivity;->emptyState:Landroid/widget/TextView;

    .line 41
    sget v0, Lcom/ultragol/app/R$id;->loadingSearch:I

    invoke-virtual {p0, v0}, Lcom/ultragol/app/SearchActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Lcom/ultragol/app/SearchActivity;->loadingView:Landroid/view/View;

    .line 43
    sget v0, Lcom/ultragol/app/R$id;->btnBack:I

    invoke-virtual {p0, v0}, Lcom/ultragol/app/SearchActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    .line 44
    .local v0, "btnBack":Landroid/view/View;
    if-eqz v0, :cond_0

    new-instance v1, Lcom/ultragol/app/SearchActivity$$ExternalSyntheticLambda0;

    invoke-direct {v1, p0}, Lcom/ultragol/app/SearchActivity$$ExternalSyntheticLambda0;-><init>(Lcom/ultragol/app/SearchActivity;)V

    invoke-virtual {v0, v1}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 46
    :cond_0
    new-instance v1, Lcom/ultragol/app/adapters/ContentGridAdapter;

    iget-object v2, p0, Lcom/ultragol/app/SearchActivity;->results:Ljava/util/List;

    invoke-direct {v1, p0, v2}, Lcom/ultragol/app/adapters/ContentGridAdapter;-><init>(Landroid/content/Context;Ljava/util/List;)V

    iput-object v1, p0, Lcom/ultragol/app/SearchActivity;->adapter:Lcom/ultragol/app/adapters/ContentGridAdapter;

    .line 47
    iget-object v1, p0, Lcom/ultragol/app/SearchActivity;->resultsGrid:Landroidx/recyclerview/widget/RecyclerView;

    new-instance v2, Landroidx/recyclerview/widget/GridLayoutManager;

    const/4 v3, 0x3

    invoke-direct {v2, p0, v3}, Landroidx/recyclerview/widget/GridLayoutManager;-><init>(Landroid/content/Context;I)V

    invoke-virtual {v1, v2}, Landroidx/recyclerview/widget/RecyclerView;->setLayoutManager(Landroidx/recyclerview/widget/RecyclerView$LayoutManager;)V

    .line 48
    iget-object v1, p0, Lcom/ultragol/app/SearchActivity;->resultsGrid:Landroidx/recyclerview/widget/RecyclerView;

    iget-object v2, p0, Lcom/ultragol/app/SearchActivity;->adapter:Lcom/ultragol/app/adapters/ContentGridAdapter;

    invoke-virtual {v1, v2}, Landroidx/recyclerview/widget/RecyclerView;->setAdapter(Landroidx/recyclerview/widget/RecyclerView$Adapter;)V

    .line 50
    iget-object v1, p0, Lcom/ultragol/app/SearchActivity;->searchInput:Landroid/widget/EditText;

    new-instance v2, Lcom/ultragol/app/SearchActivity$1;

    invoke-direct {v2, p0}, Lcom/ultragol/app/SearchActivity$1;-><init>(Lcom/ultragol/app/SearchActivity;)V

    invoke-virtual {v1, v2}, Landroid/widget/EditText;->addTextChangedListener(Landroid/text/TextWatcher;)V

    .line 62
    iget-object v1, p0, Lcom/ultragol/app/SearchActivity;->searchInput:Landroid/widget/EditText;

    invoke-virtual {v1}, Landroid/widget/EditText;->requestFocus()Z

    .line 63
    return-void
.end method
