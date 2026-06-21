.class public Lcom/ultragol/app/fragments/FavoritesFragment;
.super Landroidx/fragment/app/Fragment;
.source "FavoritesFragment.java"


# direct methods
.method public constructor <init>()V
    .locals 0

    .line 15
    invoke-direct {p0}, Landroidx/fragment/app/Fragment;-><init>()V

    return-void
.end method


# virtual methods
.method public onCreateView(Landroid/view/LayoutInflater;Landroid/view/ViewGroup;Landroid/os/Bundle;)Landroid/view/View;
    .locals 2
    .param p1, "i"    # Landroid/view/LayoutInflater;
    .param p2, "p"    # Landroid/view/ViewGroup;
    .param p3, "s"    # Landroid/os/Bundle;

    .line 19
    sget v0, Lcom/ultragol/app/R$layout;->fragment_grid:I

    const/4 v1, 0x0

    invoke-virtual {p1, v0, p2, v1}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    move-result-object v0

    return-object v0
.end method

.method public onResume()V
    .locals 5

    .line 49
    invoke-super {p0}, Landroidx/fragment/app/Fragment;->onResume()V

    .line 50
    invoke-virtual {p0}, Lcom/ultragol/app/fragments/FavoritesFragment;->getView()Landroid/view/View;

    move-result-object v0

    .line 51
    .local v0, "view":Landroid/view/View;
    if-nez v0, :cond_0

    return-void

    .line 52
    :cond_0
    sget v1, Lcom/ultragol/app/R$id;->contentGrid:I

    invoke-virtual {v0, v1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v1

    check-cast v1, Landroidx/recyclerview/widget/RecyclerView;

    .line 53
    .local v1, "grid":Landroidx/recyclerview/widget/RecyclerView;
    if-eqz v1, :cond_1

    .line 54
    invoke-virtual {p0}, Lcom/ultragol/app/fragments/FavoritesFragment;->requireContext()Landroid/content/Context;

    move-result-object v2

    invoke-static {v2}, Lcom/ultragol/app/FavoritesManager;->getAll(Landroid/content/Context;)Ljava/util/List;

    move-result-object v2

    .line 55
    .local v2, "items":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    new-instance v3, Lcom/ultragol/app/adapters/ContentGridAdapter;

    invoke-virtual {p0}, Lcom/ultragol/app/fragments/FavoritesFragment;->requireContext()Landroid/content/Context;

    move-result-object v4

    invoke-direct {v3, v4, v2}, Lcom/ultragol/app/adapters/ContentGridAdapter;-><init>(Landroid/content/Context;Ljava/util/List;)V

    invoke-virtual {v1, v3}, Landroidx/recyclerview/widget/RecyclerView;->setAdapter(Landroidx/recyclerview/widget/RecyclerView$Adapter;)V

    .line 57
    .end local v2    # "items":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    :cond_1
    return-void
.end method

.method public onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V
    .locals 6
    .param p1, "view"    # Landroid/view/View;
    .param p2, "s"    # Landroid/os/Bundle;

    .line 24
    invoke-super {p0, p1, p2}, Landroidx/fragment/app/Fragment;->onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V

    .line 26
    sget v0, Lcom/ultragol/app/R$id;->gridTitle:I

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    .line 27
    .local v0, "title":Landroid/widget/TextView;
    if-eqz v0, :cond_0

    const-string v1, "\u2665 Favoritos"

    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 29
    :cond_0
    sget v1, Lcom/ultragol/app/R$id;->contentGrid:I

    invoke-virtual {p1, v1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v1

    check-cast v1, Landroidx/recyclerview/widget/RecyclerView;

    .line 30
    .local v1, "grid":Landroidx/recyclerview/widget/RecyclerView;
    new-instance v2, Landroidx/recyclerview/widget/GridLayoutManager;

    invoke-virtual {p0}, Lcom/ultragol/app/fragments/FavoritesFragment;->requireContext()Landroid/content/Context;

    move-result-object v3

    const/4 v4, 0x3

    invoke-direct {v2, v3, v4}, Landroidx/recyclerview/widget/GridLayoutManager;-><init>(Landroid/content/Context;I)V

    invoke-virtual {v1, v2}, Landroidx/recyclerview/widget/RecyclerView;->setLayoutManager(Landroidx/recyclerview/widget/RecyclerView$LayoutManager;)V

    .line 32
    invoke-virtual {p0}, Lcom/ultragol/app/fragments/FavoritesFragment;->requireContext()Landroid/content/Context;

    move-result-object v2

    invoke-static {v2}, Lcom/ultragol/app/FavoritesManager;->getAll(Landroid/content/Context;)Ljava/util/List;

    move-result-object v2

    .line 34
    .local v2, "items":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    invoke-interface {v2}, Ljava/util/List;->isEmpty()Z

    move-result v3

    if-eqz v3, :cond_1

    .line 35
    new-instance v3, Landroid/widget/TextView;

    invoke-virtual {p0}, Lcom/ultragol/app/fragments/FavoritesFragment;->requireContext()Landroid/content/Context;

    move-result-object v4

    invoke-direct {v3, v4}, Landroid/widget/TextView;-><init>(Landroid/content/Context;)V

    .line 36
    .local v3, "empty":Landroid/widget/TextView;
    const-string v4, "A\u00fan no tienes favoritos.\nAgrega contenido desde su detalle."

    invoke-virtual {v3, v4}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 37
    const v4, -0x77000001

    invoke-virtual {v3, v4}, Landroid/widget/TextView;->setTextColor(I)V

    .line 38
    const/high16 v4, 0x41600000    # 14.0f

    invoke-virtual {v3, v4}, Landroid/widget/TextView;->setTextSize(F)V

    .line 39
    const/16 v4, 0x11

    invoke-virtual {v3, v4}, Landroid/widget/TextView;->setGravity(I)V

    .line 40
    const/16 v4, 0x20

    const/16 v5, 0x50

    invoke-virtual {v3, v4, v5, v4, v5}, Landroid/widget/TextView;->setPadding(IIII)V

    .line 41
    move-object v4, p1

    check-cast v4, Landroid/view/ViewGroup;

    invoke-virtual {v4, v3}, Landroid/view/ViewGroup;->addView(Landroid/view/View;)V

    .line 42
    .end local v3    # "empty":Landroid/widget/TextView;
    goto :goto_0

    .line 43
    :cond_1
    new-instance v3, Lcom/ultragol/app/adapters/ContentGridAdapter;

    invoke-virtual {p0}, Lcom/ultragol/app/fragments/FavoritesFragment;->requireContext()Landroid/content/Context;

    move-result-object v4

    invoke-direct {v3, v4, v2}, Lcom/ultragol/app/adapters/ContentGridAdapter;-><init>(Landroid/content/Context;Ljava/util/List;)V

    invoke-virtual {v1, v3}, Landroidx/recyclerview/widget/RecyclerView;->setAdapter(Landroidx/recyclerview/widget/RecyclerView$Adapter;)V

    .line 45
    :goto_0
    return-void
.end method
