.class public Lcom/ultragol/app/MainActivity;
.super Landroidx/appcompat/app/AppCompatActivity;
.source "MainActivity.java"


# instance fields
.field private drawerOverlay:Landroid/widget/FrameLayout;


# direct methods
.method public constructor <init>()V
    .locals 0

    .line 15
    invoke-direct {p0}, Landroidx/appcompat/app/AppCompatActivity;-><init>()V

    return-void
.end method

.method static synthetic access$000(Lcom/ultragol/app/MainActivity;)Landroid/widget/FrameLayout;
    .locals 1
    .param p0, "x0"    # Lcom/ultragol/app/MainActivity;

    .line 15
    iget-object v0, p0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    return-object v0
.end method

.method private checkAndShowCrash()V
    .locals 6

    .line 109
    invoke-static {p0}, Lcom/ultragol/app/UltragolApp;->getLastCrash(Landroid/content/Context;)Ljava/lang/String;

    move-result-object v0

    .line 110
    .local v0, "crash":Ljava/lang/String;
    if-nez v0, :cond_0

    return-void

    .line 111
    :cond_0
    invoke-static {p0}, Lcom/ultragol/app/UltragolApp;->clearCrash(Landroid/content/Context;)V

    .line 112
    new-instance v1, Landroid/widget/TextView;

    invoke-direct {v1, p0}, Landroid/widget/TextView;-><init>(Landroid/content/Context;)V

    .line 113
    .local v1, "tv":Landroid/widget/TextView;
    invoke-virtual {v1, v0}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 114
    const/high16 v2, 0x41300000    # 11.0f

    invoke-virtual {v1, v2}, Landroid/widget/TextView;->setTextSize(F)V

    .line 115
    const/16 v2, 0x10

    invoke-virtual {v1, v2, v2, v2, v2}, Landroid/widget/TextView;->setPadding(IIII)V

    .line 116
    const/4 v2, 0x1

    invoke-virtual {v1, v2}, Landroid/widget/TextView;->setTextIsSelectable(Z)V

    .line 117
    new-instance v2, Landroid/widget/ScrollView;

    invoke-direct {v2, p0}, Landroid/widget/ScrollView;-><init>(Landroid/content/Context;)V

    .line 118
    .local v2, "sv":Landroid/widget/ScrollView;
    invoke-virtual {v2, v1}, Landroid/widget/ScrollView;->addView(Landroid/view/View;)V

    .line 119
    new-instance v3, Landroid/app/AlertDialog$Builder;

    invoke-direct {v3, p0}, Landroid/app/AlertDialog$Builder;-><init>(Landroid/content/Context;)V

    .line 120
    const-string v4, "CRASH DETECTADO"

    invoke-virtual {v3, v4}, Landroid/app/AlertDialog$Builder;->setTitle(Ljava/lang/CharSequence;)Landroid/app/AlertDialog$Builder;

    move-result-object v3

    .line 121
    invoke-virtual {v3, v2}, Landroid/app/AlertDialog$Builder;->setView(Landroid/view/View;)Landroid/app/AlertDialog$Builder;

    move-result-object v3

    .line 122
    const-string v4, "OK"

    const/4 v5, 0x0

    invoke-virtual {v3, v4, v5}, Landroid/app/AlertDialog$Builder;->setPositiveButton(Ljava/lang/CharSequence;Landroid/content/DialogInterface$OnClickListener;)Landroid/app/AlertDialog$Builder;

    move-result-object v3

    .line 123
    invoke-virtual {v3}, Landroid/app/AlertDialog$Builder;->show()Landroid/app/AlertDialog;

    .line 124
    return-void
.end method

.method private loadFragment(Landroidx/fragment/app/Fragment;)V
    .locals 3
    .param p1, "fragment"    # Landroidx/fragment/app/Fragment;

    .line 163
    invoke-virtual {p0}, Lcom/ultragol/app/MainActivity;->getSupportFragmentManager()Landroidx/fragment/app/FragmentManager;

    move-result-object v0

    invoke-virtual {v0}, Landroidx/fragment/app/FragmentManager;->beginTransaction()Landroidx/fragment/app/FragmentTransaction;

    move-result-object v0

    .line 164
    const/high16 v1, 0x10a0000

    const v2, 0x10a0001

    invoke-virtual {v0, v1, v2}, Landroidx/fragment/app/FragmentTransaction;->setCustomAnimations(II)Landroidx/fragment/app/FragmentTransaction;

    move-result-object v0

    sget v1, Lcom/ultragol/app/R$id;->fragmentContainer:I

    .line 165
    invoke-virtual {v0, v1, p1}, Landroidx/fragment/app/FragmentTransaction;->replace(ILandroidx/fragment/app/Fragment;)Landroidx/fragment/app/FragmentTransaction;

    move-result-object v0

    .line 166
    invoke-virtual {v0}, Landroidx/fragment/app/FragmentTransaction;->commit()I

    .line 167
    return-void
.end method

.method private navigate(Landroidx/fragment/app/Fragment;)V
    .locals 0
    .param p1, "fragment"    # Landroidx/fragment/app/Fragment;

    .line 127
    invoke-virtual {p0}, Lcom/ultragol/app/MainActivity;->hideMenu()V

    .line 128
    invoke-direct {p0, p1}, Lcom/ultragol/app/MainActivity;->loadFragment(Landroidx/fragment/app/Fragment;)V

    .line 129
    return-void
.end method

.method private setupDrawer()V
    .locals 21

    .line 34
    move-object/from16 v0, p0

    iget-object v1, v0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    sget v2, Lcom/ultragol/app/R$id;->drawerClose:I

    invoke-virtual {v1, v2}, Landroid/widget/FrameLayout;->findViewById(I)Landroid/view/View;

    move-result-object v1

    .line 35
    .local v1, "close":Landroid/view/View;
    if-eqz v1, :cond_0

    new-instance v2, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda0;

    invoke-direct {v2, v0}, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda0;-><init>(Lcom/ultragol/app/MainActivity;)V

    invoke-virtual {v1, v2}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 37
    :cond_0
    iget-object v2, v0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    sget v3, Lcom/ultragol/app/R$id;->drawerSearch:I

    invoke-virtual {v2, v3}, Landroid/widget/FrameLayout;->findViewById(I)Landroid/view/View;

    move-result-object v2

    .line 38
    .local v2, "search":Landroid/view/View;
    if-eqz v2, :cond_1

    new-instance v3, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda1;

    invoke-direct {v3, v0}, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda1;-><init>(Lcom/ultragol/app/MainActivity;)V

    invoke-virtual {v2, v3}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 44
    :cond_1
    iget-object v3, v0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    sget v4, Lcom/ultragol/app/R$id;->navInicio:I

    invoke-virtual {v3, v4}, Landroid/widget/FrameLayout;->findViewById(I)Landroid/view/View;

    move-result-object v3

    .line 45
    .local v3, "navInicio":Landroid/view/View;
    iget-object v4, v0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    sget v5, Lcom/ultragol/app/R$id;->navSeries:I

    invoke-virtual {v4, v5}, Landroid/widget/FrameLayout;->findViewById(I)Landroid/view/View;

    move-result-object v4

    .line 46
    .local v4, "navSeries":Landroid/view/View;
    iget-object v5, v0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    sget v6, Lcom/ultragol/app/R$id;->navMovies:I

    invoke-virtual {v5, v6}, Landroid/widget/FrameLayout;->findViewById(I)Landroid/view/View;

    move-result-object v5

    .line 47
    .local v5, "navMovies":Landroid/view/View;
    iget-object v6, v0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    sget v7, Lcom/ultragol/app/R$id;->navAnime:I

    invoke-virtual {v6, v7}, Landroid/widget/FrameLayout;->findViewById(I)Landroid/view/View;

    move-result-object v6

    .line 48
    .local v6, "navAnime":Landroid/view/View;
    iget-object v7, v0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    sget v8, Lcom/ultragol/app/R$id;->navDoramas:I

    invoke-virtual {v7, v8}, Landroid/widget/FrameLayout;->findViewById(I)Landroid/view/View;

    move-result-object v7

    .line 49
    .local v7, "navDoramas":Landroid/view/View;
    iget-object v8, v0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    sget v9, Lcom/ultragol/app/R$id;->navSearch:I

    invoke-virtual {v8, v9}, Landroid/widget/FrameLayout;->findViewById(I)Landroid/view/View;

    move-result-object v8

    .line 50
    .local v8, "navSearch":Landroid/view/View;
    iget-object v9, v0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    sget v10, Lcom/ultragol/app/R$id;->navFavorites:I

    invoke-virtual {v9, v10}, Landroid/widget/FrameLayout;->findViewById(I)Landroid/view/View;

    move-result-object v9

    .line 51
    .local v9, "navFavorites":Landroid/view/View;
    iget-object v10, v0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    sget v11, Lcom/ultragol/app/R$id;->navMyList:I

    invoke-virtual {v10, v11}, Landroid/widget/FrameLayout;->findViewById(I)Landroid/view/View;

    move-result-object v10

    .line 53
    .local v10, "navMyList":Landroid/view/View;
    if-eqz v3, :cond_2

    new-instance v11, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda2;

    invoke-direct {v11, v0}, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda2;-><init>(Lcom/ultragol/app/MainActivity;)V

    invoke-virtual {v3, v11}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 54
    :cond_2
    if-eqz v4, :cond_3

    new-instance v11, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda3;

    invoke-direct {v11, v0}, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda3;-><init>(Lcom/ultragol/app/MainActivity;)V

    invoke-virtual {v4, v11}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 55
    :cond_3
    if-eqz v5, :cond_4

    new-instance v11, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda4;

    invoke-direct {v11, v0}, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda4;-><init>(Lcom/ultragol/app/MainActivity;)V

    invoke-virtual {v5, v11}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 56
    :cond_4
    if-eqz v6, :cond_5

    new-instance v11, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda5;

    invoke-direct {v11, v0}, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda5;-><init>(Lcom/ultragol/app/MainActivity;)V

    invoke-virtual {v6, v11}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 57
    :cond_5
    if-eqz v7, :cond_6

    new-instance v11, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda6;

    invoke-direct {v11, v0}, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda6;-><init>(Lcom/ultragol/app/MainActivity;)V

    invoke-virtual {v7, v11}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 58
    :cond_6
    if-eqz v9, :cond_7

    new-instance v11, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda7;

    invoke-direct {v11, v0}, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda7;-><init>(Lcom/ultragol/app/MainActivity;)V

    invoke-virtual {v9, v11}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 59
    :cond_7
    if-eqz v10, :cond_8

    new-instance v11, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda8;

    invoke-direct {v11, v0}, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda8;-><init>(Lcom/ultragol/app/MainActivity;)V

    invoke-virtual {v10, v11}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 60
    :cond_8
    if-eqz v8, :cond_9

    new-instance v11, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda9;

    invoke-direct {v11, v0}, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda9;-><init>(Lcom/ultragol/app/MainActivity;)V

    invoke-virtual {v8, v11}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 69
    :cond_9
    iget-object v11, v0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    sget v12, Lcom/ultragol/app/R$id;->platNetflix:I

    invoke-virtual {v11, v12}, Landroid/widget/FrameLayout;->findViewById(I)Landroid/view/View;

    move-result-object v11

    .line 70
    .local v11, "platNetflix":Landroid/view/View;
    iget-object v12, v0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    sget v13, Lcom/ultragol/app/R$id;->platPrime:I

    invoke-virtual {v12, v13}, Landroid/widget/FrameLayout;->findViewById(I)Landroid/view/View;

    move-result-object v12

    .line 71
    .local v12, "platPrime":Landroid/view/View;
    iget-object v13, v0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    sget v14, Lcom/ultragol/app/R$id;->platDisney:I

    invoke-virtual {v13, v14}, Landroid/widget/FrameLayout;->findViewById(I)Landroid/view/View;

    move-result-object v13

    .line 72
    .local v13, "platDisney":Landroid/view/View;
    iget-object v14, v0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    sget v15, Lcom/ultragol/app/R$id;->platApple:I

    invoke-virtual {v14, v15}, Landroid/widget/FrameLayout;->findViewById(I)Landroid/view/View;

    move-result-object v14

    .line 73
    .local v14, "platApple":Landroid/view/View;
    iget-object v15, v0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    move-object/from16 v16, v1

    .end local v1    # "close":Landroid/view/View;
    .local v16, "close":Landroid/view/View;
    sget v1, Lcom/ultragol/app/R$id;->platHulu:I

    invoke-virtual {v15, v1}, Landroid/widget/FrameLayout;->findViewById(I)Landroid/view/View;

    move-result-object v1

    .line 74
    .local v1, "platHulu":Landroid/view/View;
    iget-object v15, v0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    move-object/from16 v17, v2

    .end local v2    # "search":Landroid/view/View;
    .local v17, "search":Landroid/view/View;
    sget v2, Lcom/ultragol/app/R$id;->platHbo:I

    invoke-virtual {v15, v2}, Landroid/widget/FrameLayout;->findViewById(I)Landroid/view/View;

    move-result-object v2

    .line 75
    .local v2, "platHbo":Landroid/view/View;
    iget-object v15, v0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    move-object/from16 v18, v3

    .end local v3    # "navInicio":Landroid/view/View;
    .local v18, "navInicio":Landroid/view/View;
    sget v3, Lcom/ultragol/app/R$id;->platCrunchyroll:I

    invoke-virtual {v15, v3}, Landroid/widget/FrameLayout;->findViewById(I)Landroid/view/View;

    move-result-object v3

    .line 76
    .local v3, "platCrunchyroll":Landroid/view/View;
    iget-object v15, v0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    move-object/from16 v19, v4

    .end local v4    # "navSeries":Landroid/view/View;
    .local v19, "navSeries":Landroid/view/View;
    sget v4, Lcom/ultragol/app/R$id;->platAtx:I

    invoke-virtual {v15, v4}, Landroid/widget/FrameLayout;->findViewById(I)Landroid/view/View;

    move-result-object v4

    .line 77
    .local v4, "platAtx":Landroid/view/View;
    iget-object v15, v0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    move-object/from16 v20, v5

    .end local v5    # "navMovies":Landroid/view/View;
    .local v20, "navMovies":Landroid/view/View;
    sget v5, Lcom/ultragol/app/R$id;->platTokyoMx:I

    invoke-virtual {v15, v5}, Landroid/widget/FrameLayout;->findViewById(I)Landroid/view/View;

    move-result-object v5

    .line 79
    .local v5, "platTokyoMx":Landroid/view/View;
    if-eqz v11, :cond_a

    .line 80
    new-instance v15, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda10;

    invoke-direct {v15, v0}, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda10;-><init>(Lcom/ultragol/app/MainActivity;)V

    invoke-virtual {v11, v15}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 82
    :cond_a
    if-eqz v12, :cond_b

    .line 83
    new-instance v15, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda11;

    invoke-direct {v15, v0}, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda11;-><init>(Lcom/ultragol/app/MainActivity;)V

    invoke-virtual {v12, v15}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 85
    :cond_b
    if-eqz v13, :cond_c

    .line 86
    new-instance v15, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda12;

    invoke-direct {v15, v0}, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda12;-><init>(Lcom/ultragol/app/MainActivity;)V

    invoke-virtual {v13, v15}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 88
    :cond_c
    if-eqz v14, :cond_d

    .line 89
    new-instance v15, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda13;

    invoke-direct {v15, v0}, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda13;-><init>(Lcom/ultragol/app/MainActivity;)V

    invoke-virtual {v14, v15}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 91
    :cond_d
    if-eqz v1, :cond_e

    .line 92
    new-instance v15, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda14;

    invoke-direct {v15, v0}, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda14;-><init>(Lcom/ultragol/app/MainActivity;)V

    invoke-virtual {v1, v15}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 94
    :cond_e
    if-eqz v2, :cond_f

    .line 95
    new-instance v15, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda15;

    invoke-direct {v15, v0}, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda15;-><init>(Lcom/ultragol/app/MainActivity;)V

    invoke-virtual {v2, v15}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 97
    :cond_f
    if-eqz v3, :cond_10

    .line 98
    new-instance v15, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda16;

    invoke-direct {v15, v0}, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda16;-><init>(Lcom/ultragol/app/MainActivity;)V

    invoke-virtual {v3, v15}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 100
    :cond_10
    if-eqz v4, :cond_11

    .line 101
    new-instance v15, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda17;

    invoke-direct {v15, v0}, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda17;-><init>(Lcom/ultragol/app/MainActivity;)V

    invoke-virtual {v4, v15}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 103
    :cond_11
    if-eqz v5, :cond_12

    .line 104
    new-instance v15, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda18;

    invoke-direct {v15, v0}, Lcom/ultragol/app/MainActivity$$ExternalSyntheticLambda18;-><init>(Lcom/ultragol/app/MainActivity;)V

    invoke-virtual {v5, v15}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 106
    :cond_12
    return-void
.end method


# virtual methods
.method public hideMenu()V
    .locals 3

    .line 140
    iget-object v0, p0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    if-nez v0, :cond_0

    return-void

    .line 141
    :cond_0
    new-instance v0, Landroid/view/animation/AlphaAnimation;

    const/high16 v1, 0x3f800000    # 1.0f

    const/4 v2, 0x0

    invoke-direct {v0, v1, v2}, Landroid/view/animation/AlphaAnimation;-><init>(FF)V

    .line 142
    .local v0, "anim":Landroid/view/animation/AlphaAnimation;
    const-wide/16 v1, 0x96

    invoke-virtual {v0, v1, v2}, Landroid/view/animation/AlphaAnimation;->setDuration(J)V

    .line 143
    new-instance v1, Lcom/ultragol/app/MainActivity$1;

    invoke-direct {v1, p0}, Lcom/ultragol/app/MainActivity$1;-><init>(Lcom/ultragol/app/MainActivity;)V

    invoke-virtual {v0, v1}, Landroid/view/animation/AlphaAnimation;->setAnimationListener(Landroid/view/animation/Animation$AnimationListener;)V

    .line 150
    iget-object v1, p0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    invoke-virtual {v1, v0}, Landroid/widget/FrameLayout;->startAnimation(Landroid/view/animation/Animation;)V

    .line 151
    return-void
.end method

.method synthetic lambda$setupDrawer$0$com-ultragol-app-MainActivity(Landroid/view/View;)V
    .locals 0
    .param p1, "v"    # Landroid/view/View;

    .line 35
    invoke-virtual {p0}, Lcom/ultragol/app/MainActivity;->hideMenu()V

    return-void
.end method

.method synthetic lambda$setupDrawer$1$com-ultragol-app-MainActivity(Landroid/view/View;)V
    .locals 2
    .param p1, "v"    # Landroid/view/View;

    .line 39
    invoke-virtual {p0}, Lcom/ultragol/app/MainActivity;->hideMenu()V

    .line 40
    new-instance v0, Landroid/content/Intent;

    const-class v1, Lcom/ultragol/app/SearchActivity;

    invoke-direct {v0, p0, v1}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    invoke-virtual {p0, v0}, Lcom/ultragol/app/MainActivity;->startActivity(Landroid/content/Intent;)V

    .line 41
    return-void
.end method

.method synthetic lambda$setupDrawer$10$com-ultragol-app-MainActivity(Landroid/view/View;)V
    .locals 3
    .param p1, "v"    # Landroid/view/View;

    .line 81
    const/16 v0, 0x8

    const-string v1, "all"

    const-string v2, "\ud83d\udd34 Netflix"

    invoke-static {v2, v0, v1}, Lcom/ultragol/app/fragments/PlatformFragment;->newInstance(Ljava/lang/String;ILjava/lang/String;)Lcom/ultragol/app/fragments/PlatformFragment;

    move-result-object v0

    invoke-direct {p0, v0}, Lcom/ultragol/app/MainActivity;->navigate(Landroidx/fragment/app/Fragment;)V

    return-void
.end method

.method synthetic lambda$setupDrawer$11$com-ultragol-app-MainActivity(Landroid/view/View;)V
    .locals 3
    .param p1, "v"    # Landroid/view/View;

    .line 84
    const/16 v0, 0x77

    const-string v1, "all"

    const-string v2, "\ud83d\udd35 Prime Video"

    invoke-static {v2, v0, v1}, Lcom/ultragol/app/fragments/PlatformFragment;->newInstance(Ljava/lang/String;ILjava/lang/String;)Lcom/ultragol/app/fragments/PlatformFragment;

    move-result-object v0

    invoke-direct {p0, v0}, Lcom/ultragol/app/MainActivity;->navigate(Landroidx/fragment/app/Fragment;)V

    return-void
.end method

.method synthetic lambda$setupDrawer$12$com-ultragol-app-MainActivity(Landroid/view/View;)V
    .locals 3
    .param p1, "v"    # Landroid/view/View;

    .line 87
    const/16 v0, 0x151

    const-string v1, "all"

    const-string v2, "\ud83d\udd37 Disney+"

    invoke-static {v2, v0, v1}, Lcom/ultragol/app/fragments/PlatformFragment;->newInstance(Ljava/lang/String;ILjava/lang/String;)Lcom/ultragol/app/fragments/PlatformFragment;

    move-result-object v0

    invoke-direct {p0, v0}, Lcom/ultragol/app/MainActivity;->navigate(Landroidx/fragment/app/Fragment;)V

    return-void
.end method

.method synthetic lambda$setupDrawer$13$com-ultragol-app-MainActivity(Landroid/view/View;)V
    .locals 3
    .param p1, "v"    # Landroid/view/View;

    .line 90
    const/16 v0, 0x15e

    const-string v1, "all"

    const-string v2, "\u2b1c Apple TV+"

    invoke-static {v2, v0, v1}, Lcom/ultragol/app/fragments/PlatformFragment;->newInstance(Ljava/lang/String;ILjava/lang/String;)Lcom/ultragol/app/fragments/PlatformFragment;

    move-result-object v0

    invoke-direct {p0, v0}, Lcom/ultragol/app/MainActivity;->navigate(Landroidx/fragment/app/Fragment;)V

    return-void
.end method

.method synthetic lambda$setupDrawer$14$com-ultragol-app-MainActivity(Landroid/view/View;)V
    .locals 3
    .param p1, "v"    # Landroid/view/View;

    .line 93
    const/16 v0, 0xf

    const-string v1, "all"

    const-string v2, "\ud83d\udfe2 Hulu"

    invoke-static {v2, v0, v1}, Lcom/ultragol/app/fragments/PlatformFragment;->newInstance(Ljava/lang/String;ILjava/lang/String;)Lcom/ultragol/app/fragments/PlatformFragment;

    move-result-object v0

    invoke-direct {p0, v0}, Lcom/ultragol/app/MainActivity;->navigate(Landroidx/fragment/app/Fragment;)V

    return-void
.end method

.method synthetic lambda$setupDrawer$15$com-ultragol-app-MainActivity(Landroid/view/View;)V
    .locals 3
    .param p1, "v"    # Landroid/view/View;

    .line 96
    const/16 v0, 0x76b

    const-string v1, "all"

    const-string v2, "\ud83d\udfe3 HBO Max"

    invoke-static {v2, v0, v1}, Lcom/ultragol/app/fragments/PlatformFragment;->newInstance(Ljava/lang/String;ILjava/lang/String;)Lcom/ultragol/app/fragments/PlatformFragment;

    move-result-object v0

    invoke-direct {p0, v0}, Lcom/ultragol/app/MainActivity;->navigate(Landroidx/fragment/app/Fragment;)V

    return-void
.end method

.method synthetic lambda$setupDrawer$16$com-ultragol-app-MainActivity(Landroid/view/View;)V
    .locals 3
    .param p1, "v"    # Landroid/view/View;

    .line 99
    const/16 v0, 0x11b

    const-string v1, "anime"

    const-string v2, "\ud83d\udfe0 Crunchyroll"

    invoke-static {v2, v0, v1}, Lcom/ultragol/app/fragments/PlatformFragment;->newInstance(Ljava/lang/String;ILjava/lang/String;)Lcom/ultragol/app/fragments/PlatformFragment;

    move-result-object v0

    invoke-direct {p0, v0}, Lcom/ultragol/app/MainActivity;->navigate(Landroidx/fragment/app/Fragment;)V

    return-void
.end method

.method synthetic lambda$setupDrawer$17$com-ultragol-app-MainActivity(Landroid/view/View;)V
    .locals 3
    .param p1, "v"    # Landroid/view/View;

    .line 102
    const/16 v0, 0x580

    const-string v1, "anime"

    const-string v2, "\u2b1c At-X"

    invoke-static {v2, v0, v1}, Lcom/ultragol/app/fragments/PlatformFragment;->newInstance(Ljava/lang/String;ILjava/lang/String;)Lcom/ultragol/app/fragments/PlatformFragment;

    move-result-object v0

    invoke-direct {p0, v0}, Lcom/ultragol/app/MainActivity;->navigate(Landroidx/fragment/app/Fragment;)V

    return-void
.end method

.method synthetic lambda$setupDrawer$18$com-ultragol-app-MainActivity(Landroid/view/View;)V
    .locals 3
    .param p1, "v"    # Landroid/view/View;

    .line 105
    const/16 v0, 0x937

    const-string v1, "anime"

    const-string v2, "\ud83d\udcfa Tokyo MX"

    invoke-static {v2, v0, v1}, Lcom/ultragol/app/fragments/PlatformFragment;->newInstance(Ljava/lang/String;ILjava/lang/String;)Lcom/ultragol/app/fragments/PlatformFragment;

    move-result-object v0

    invoke-direct {p0, v0}, Lcom/ultragol/app/MainActivity;->navigate(Landroidx/fragment/app/Fragment;)V

    return-void
.end method

.method synthetic lambda$setupDrawer$2$com-ultragol-app-MainActivity(Landroid/view/View;)V
    .locals 1
    .param p1, "v"    # Landroid/view/View;

    .line 53
    new-instance v0, Lcom/ultragol/app/fragments/HomeFragment;

    invoke-direct {v0}, Lcom/ultragol/app/fragments/HomeFragment;-><init>()V

    invoke-direct {p0, v0}, Lcom/ultragol/app/MainActivity;->navigate(Landroidx/fragment/app/Fragment;)V

    return-void
.end method

.method synthetic lambda$setupDrawer$3$com-ultragol-app-MainActivity(Landroid/view/View;)V
    .locals 1
    .param p1, "v"    # Landroid/view/View;

    .line 54
    new-instance v0, Lcom/ultragol/app/fragments/SeriesFragment;

    invoke-direct {v0}, Lcom/ultragol/app/fragments/SeriesFragment;-><init>()V

    invoke-direct {p0, v0}, Lcom/ultragol/app/MainActivity;->navigate(Landroidx/fragment/app/Fragment;)V

    return-void
.end method

.method synthetic lambda$setupDrawer$4$com-ultragol-app-MainActivity(Landroid/view/View;)V
    .locals 1
    .param p1, "v"    # Landroid/view/View;

    .line 55
    new-instance v0, Lcom/ultragol/app/fragments/MoviesFragment;

    invoke-direct {v0}, Lcom/ultragol/app/fragments/MoviesFragment;-><init>()V

    invoke-direct {p0, v0}, Lcom/ultragol/app/MainActivity;->navigate(Landroidx/fragment/app/Fragment;)V

    return-void
.end method

.method synthetic lambda$setupDrawer$5$com-ultragol-app-MainActivity(Landroid/view/View;)V
    .locals 1
    .param p1, "v"    # Landroid/view/View;

    .line 56
    new-instance v0, Lcom/ultragol/app/fragments/AnimeFragment;

    invoke-direct {v0}, Lcom/ultragol/app/fragments/AnimeFragment;-><init>()V

    invoke-direct {p0, v0}, Lcom/ultragol/app/MainActivity;->navigate(Landroidx/fragment/app/Fragment;)V

    return-void
.end method

.method synthetic lambda$setupDrawer$6$com-ultragol-app-MainActivity(Landroid/view/View;)V
    .locals 1
    .param p1, "v"    # Landroid/view/View;

    .line 57
    new-instance v0, Lcom/ultragol/app/fragments/DoramasFragment;

    invoke-direct {v0}, Lcom/ultragol/app/fragments/DoramasFragment;-><init>()V

    invoke-direct {p0, v0}, Lcom/ultragol/app/MainActivity;->navigate(Landroidx/fragment/app/Fragment;)V

    return-void
.end method

.method synthetic lambda$setupDrawer$7$com-ultragol-app-MainActivity(Landroid/view/View;)V
    .locals 1
    .param p1, "v"    # Landroid/view/View;

    .line 58
    new-instance v0, Lcom/ultragol/app/fragments/FavoritesFragment;

    invoke-direct {v0}, Lcom/ultragol/app/fragments/FavoritesFragment;-><init>()V

    invoke-direct {p0, v0}, Lcom/ultragol/app/MainActivity;->navigate(Landroidx/fragment/app/Fragment;)V

    return-void
.end method

.method synthetic lambda$setupDrawer$8$com-ultragol-app-MainActivity(Landroid/view/View;)V
    .locals 1
    .param p1, "v"    # Landroid/view/View;

    .line 59
    new-instance v0, Lcom/ultragol/app/fragments/MyListFragment;

    invoke-direct {v0}, Lcom/ultragol/app/fragments/MyListFragment;-><init>()V

    invoke-direct {p0, v0}, Lcom/ultragol/app/MainActivity;->navigate(Landroidx/fragment/app/Fragment;)V

    return-void
.end method

.method synthetic lambda$setupDrawer$9$com-ultragol-app-MainActivity(Landroid/view/View;)V
    .locals 2
    .param p1, "v"    # Landroid/view/View;

    .line 61
    invoke-virtual {p0}, Lcom/ultragol/app/MainActivity;->hideMenu()V

    .line 62
    new-instance v0, Landroid/content/Intent;

    const-class v1, Lcom/ultragol/app/SearchActivity;

    invoke-direct {v0, p0, v1}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    invoke-virtual {p0, v0}, Lcom/ultragol/app/MainActivity;->startActivity(Landroid/content/Intent;)V

    .line 63
    return-void
.end method

.method public onBackPressed()V
    .locals 1

    .line 155
    iget-object v0, p0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    if-eqz v0, :cond_0

    invoke-virtual {v0}, Landroid/widget/FrameLayout;->getVisibility()I

    move-result v0

    if-nez v0, :cond_0

    .line 156
    invoke-virtual {p0}, Lcom/ultragol/app/MainActivity;->hideMenu()V

    goto :goto_0

    .line 158
    :cond_0
    invoke-super {p0}, Landroidx/appcompat/app/AppCompatActivity;->onBackPressed()V

    .line 160
    :goto_0
    return-void
.end method

.method protected onCreate(Landroid/os/Bundle;)V
    .locals 1
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    .line 21
    invoke-super {p0, p1}, Landroidx/appcompat/app/AppCompatActivity;->onCreate(Landroid/os/Bundle;)V

    .line 22
    sget v0, Lcom/ultragol/app/R$layout;->activity_main:I

    invoke-virtual {p0, v0}, Lcom/ultragol/app/MainActivity;->setContentView(I)V

    .line 24
    sget v0, Lcom/ultragol/app/R$id;->drawerOverlay:I

    invoke-virtual {p0, v0}, Lcom/ultragol/app/MainActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/FrameLayout;

    iput-object v0, p0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    .line 26
    invoke-direct {p0}, Lcom/ultragol/app/MainActivity;->checkAndShowCrash()V

    .line 28
    if-nez p1, :cond_0

    new-instance v0, Lcom/ultragol/app/fragments/HomeFragment;

    invoke-direct {v0}, Lcom/ultragol/app/fragments/HomeFragment;-><init>()V

    invoke-direct {p0, v0}, Lcom/ultragol/app/MainActivity;->loadFragment(Landroidx/fragment/app/Fragment;)V

    .line 30
    :cond_0
    invoke-direct {p0}, Lcom/ultragol/app/MainActivity;->setupDrawer()V

    .line 31
    return-void
.end method

.method public showMenu()V
    .locals 3

    .line 132
    iget-object v0, p0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    if-nez v0, :cond_0

    return-void

    .line 133
    :cond_0
    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Landroid/widget/FrameLayout;->setVisibility(I)V

    .line 134
    new-instance v0, Landroid/view/animation/AlphaAnimation;

    const/4 v1, 0x0

    const/high16 v2, 0x3f800000    # 1.0f

    invoke-direct {v0, v1, v2}, Landroid/view/animation/AlphaAnimation;-><init>(FF)V

    .line 135
    .local v0, "anim":Landroid/view/animation/AlphaAnimation;
    const-wide/16 v1, 0xc8

    invoke-virtual {v0, v1, v2}, Landroid/view/animation/AlphaAnimation;->setDuration(J)V

    .line 136
    iget-object v1, p0, Lcom/ultragol/app/MainActivity;->drawerOverlay:Landroid/widget/FrameLayout;

    invoke-virtual {v1, v0}, Landroid/widget/FrameLayout;->startAnimation(Landroid/view/animation/Animation;)V

    .line 137
    return-void
.end method
