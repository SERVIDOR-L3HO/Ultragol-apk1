.class public Lcom/ultragol/app/SplashActivity;
.super Landroidx/appcompat/app/AppCompatActivity;
.source "SplashActivity.java"


# instance fields
.field private dot1:Landroid/view/View;

.field private dot2:Landroid/view/View;

.field private dot3:Landroid/view/View;

.field private final dotHandler:Landroid/os/Handler;

.field private final dotRunnable:Ljava/lang/Runnable;

.field private dotStep:I


# direct methods
.method public constructor <init>()V
    .locals 1

    .line 13
    invoke-direct {p0}, Landroidx/appcompat/app/AppCompatActivity;-><init>()V

    .line 16
    const/4 v0, 0x0

    iput v0, p0, Lcom/ultragol/app/SplashActivity;->dotStep:I

    .line 17
    new-instance v0, Landroid/os/Handler;

    invoke-direct {v0}, Landroid/os/Handler;-><init>()V

    iput-object v0, p0, Lcom/ultragol/app/SplashActivity;->dotHandler:Landroid/os/Handler;

    .line 18
    new-instance v0, Lcom/ultragol/app/SplashActivity$1;

    invoke-direct {v0, p0}, Lcom/ultragol/app/SplashActivity$1;-><init>(Lcom/ultragol/app/SplashActivity;)V

    iput-object v0, p0, Lcom/ultragol/app/SplashActivity;->dotRunnable:Ljava/lang/Runnable;

    return-void
.end method

.method static synthetic access$000(Lcom/ultragol/app/SplashActivity;)V
    .locals 0
    .param p0, "x0"    # Lcom/ultragol/app/SplashActivity;

    .line 13
    invoke-direct {p0}, Lcom/ultragol/app/SplashActivity;->animateDots()V

    return-void
.end method

.method static synthetic access$100(Lcom/ultragol/app/SplashActivity;)Landroid/os/Handler;
    .locals 1
    .param p0, "x0"    # Lcom/ultragol/app/SplashActivity;

    .line 13
    iget-object v0, p0, Lcom/ultragol/app/SplashActivity;->dotHandler:Landroid/os/Handler;

    return-object v0
.end method

.method private animateDots()V
    .locals 3

    .line 97
    iget-object v0, p0, Lcom/ultragol/app/SplashActivity;->dot1:Landroid/view/View;

    if-eqz v0, :cond_4

    iget-object v1, p0, Lcom/ultragol/app/SplashActivity;->dot2:Landroid/view/View;

    if-eqz v1, :cond_4

    iget-object v1, p0, Lcom/ultragol/app/SplashActivity;->dot3:Landroid/view/View;

    if-nez v1, :cond_0

    goto :goto_3

    .line 98
    :cond_0
    iget v1, p0, Lcom/ultragol/app/SplashActivity;->dotStep:I

    const/4 v2, 0x1

    add-int/2addr v1, v2

    rem-int/lit8 v1, v1, 0x3

    iput v1, p0, Lcom/ultragol/app/SplashActivity;->dotStep:I

    .line 99
    if-nez v1, :cond_1

    sget v1, Lcom/ultragol/app/R$drawable;->splash_dot:I

    goto :goto_0

    :cond_1
    sget v1, Lcom/ultragol/app/R$drawable;->splash_dot_dim:I

    :goto_0
    invoke-virtual {p0, v1}, Lcom/ultragol/app/SplashActivity;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/view/View;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 100
    iget-object v0, p0, Lcom/ultragol/app/SplashActivity;->dot2:Landroid/view/View;

    iget v1, p0, Lcom/ultragol/app/SplashActivity;->dotStep:I

    if-ne v1, v2, :cond_2

    sget v1, Lcom/ultragol/app/R$drawable;->splash_dot:I

    goto :goto_1

    :cond_2
    sget v1, Lcom/ultragol/app/R$drawable;->splash_dot_dim:I

    :goto_1
    invoke-virtual {p0, v1}, Lcom/ultragol/app/SplashActivity;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/view/View;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 101
    iget-object v0, p0, Lcom/ultragol/app/SplashActivity;->dot3:Landroid/view/View;

    iget v1, p0, Lcom/ultragol/app/SplashActivity;->dotStep:I

    const/4 v2, 0x2

    if-ne v1, v2, :cond_3

    sget v1, Lcom/ultragol/app/R$drawable;->splash_dot:I

    goto :goto_2

    :cond_3
    sget v1, Lcom/ultragol/app/R$drawable;->splash_dot_dim:I

    :goto_2
    invoke-virtual {p0, v1}, Lcom/ultragol/app/SplashActivity;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/view/View;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 102
    return-void

    .line 97
    :cond_4
    :goto_3
    return-void
.end method

.method private animateFadeIn(Landroid/view/View;JJ)V
    .locals 2
    .param p1, "v"    # Landroid/view/View;
    .param p2, "delay"    # J
    .param p4, "duration"    # J

    .line 88
    if-nez p1, :cond_0

    return-void

    .line 89
    :cond_0
    new-instance v0, Landroid/os/Handler;

    invoke-direct {v0}, Landroid/os/Handler;-><init>()V

    new-instance v1, Lcom/ultragol/app/SplashActivity$$ExternalSyntheticLambda3;

    invoke-direct {v1, p4, p5, p1}, Lcom/ultragol/app/SplashActivity$$ExternalSyntheticLambda3;-><init>(JLandroid/view/View;)V

    invoke-virtual {v0, v1, p2, p3}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    .line 94
    return-void
.end method

.method static synthetic lambda$animateFadeIn$3(JLandroid/view/View;)V
    .locals 3
    .param p0, "duration"    # J
    .param p2, "v"    # Landroid/view/View;

    .line 90
    new-instance v0, Landroid/view/animation/AlphaAnimation;

    const/4 v1, 0x0

    const/high16 v2, 0x3f800000    # 1.0f

    invoke-direct {v0, v1, v2}, Landroid/view/animation/AlphaAnimation;-><init>(FF)V

    .line 91
    .local v0, "a":Landroid/view/animation/AlphaAnimation;
    invoke-virtual {v0, p0, p1}, Landroid/view/animation/AlphaAnimation;->setDuration(J)V

    .line 92
    invoke-virtual {p2, v0}, Landroid/view/View;->startAnimation(Landroid/view/animation/Animation;)V

    .line 93
    return-void
.end method

.method static synthetic lambda$onCreate$0(Landroid/view/View;)V
    .locals 3
    .param p0, "tagline"    # Landroid/view/View;

    .line 59
    if-eqz p0, :cond_0

    .line 60
    new-instance v0, Landroid/view/animation/AlphaAnimation;

    const/4 v1, 0x0

    const/high16 v2, 0x3f800000    # 1.0f

    invoke-direct {v0, v1, v2}, Landroid/view/animation/AlphaAnimation;-><init>(FF)V

    .line 61
    .local v0, "a2":Landroid/view/animation/AlphaAnimation;
    const-wide/16 v1, 0x1f4

    invoke-virtual {v0, v1, v2}, Landroid/view/animation/AlphaAnimation;->setDuration(J)V

    .line 62
    const/4 v1, 0x0

    invoke-virtual {p0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 63
    invoke-virtual {p0, v0}, Landroid/view/View;->startAnimation(Landroid/view/animation/Animation;)V

    .line 65
    .end local v0    # "a2":Landroid/view/animation/AlphaAnimation;
    :cond_0
    return-void
.end method


# virtual methods
.method synthetic lambda$onCreate$1$com-ultragol-app-SplashActivity(Landroid/view/View;)V
    .locals 3
    .param p1, "progress"    # Landroid/view/View;

    .line 69
    if-eqz p1, :cond_0

    .line 70
    new-instance v0, Landroid/view/animation/AlphaAnimation;

    const/4 v1, 0x0

    const/high16 v2, 0x3f800000    # 1.0f

    invoke-direct {v0, v1, v2}, Landroid/view/animation/AlphaAnimation;-><init>(FF)V

    .line 71
    .local v0, "a3":Landroid/view/animation/AlphaAnimation;
    const-wide/16 v1, 0x12c

    invoke-virtual {v0, v1, v2}, Landroid/view/animation/AlphaAnimation;->setDuration(J)V

    .line 72
    const/4 v1, 0x0

    invoke-virtual {p1, v1}, Landroid/view/View;->setVisibility(I)V

    .line 73
    invoke-virtual {p1, v0}, Landroid/view/View;->startAnimation(Landroid/view/animation/Animation;)V

    .line 74
    iget-object v1, p0, Lcom/ultragol/app/SplashActivity;->dotHandler:Landroid/os/Handler;

    iget-object v2, p0, Lcom/ultragol/app/SplashActivity;->dotRunnable:Ljava/lang/Runnable;

    invoke-virtual {v1, v2}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    .line 76
    .end local v0    # "a3":Landroid/view/animation/AlphaAnimation;
    :cond_0
    return-void
.end method

.method synthetic lambda$onCreate$2$com-ultragol-app-SplashActivity()V
    .locals 2

    iget-object v0, p0, Lcom/ultragol/app/SplashActivity;->dotHandler:Landroid/os/Handler;

    iget-object v1, p0, Lcom/ultragol/app/SplashActivity;->dotRunnable:Ljava/lang/Runnable;

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeCallbacks(Ljava/lang/Runnable;)V

    invoke-static {p0}, Lcom/ultragol/app/UpdateChecker;->check(Landroidx/appcompat/app/AppCompatActivity;)V

    return-void
.end method

.method protected onCreate(Landroid/os/Bundle;)V
    .locals 22
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    .line 28
    move-object/from16 v6, p0

    invoke-super/range {p0 .. p1}, Landroidx/appcompat/app/AppCompatActivity;->onCreate(Landroid/os/Bundle;)V

    .line 29
    sget v0, Lcom/ultragol/app/R$layout;->activity_splash:I

    invoke-virtual {v6, v0}, Lcom/ultragol/app/SplashActivity;->setContentView(I)V

    .line 31
    sget v0, Lcom/ultragol/app/R$id;->splashLogo:I

    invoke-virtual {v6, v0}, Lcom/ultragol/app/SplashActivity;->findViewById(I)Landroid/view/View;

    move-result-object v7

    .line 32
    .local v7, "logo":Landroid/view/View;
    sget v0, Lcom/ultragol/app/R$id;->splashTagline:I

    invoke-virtual {v6, v0}, Lcom/ultragol/app/SplashActivity;->findViewById(I)Landroid/view/View;

    move-result-object v8

    .line 33
    .local v8, "tagline":Landroid/view/View;
    sget v0, Lcom/ultragol/app/R$id;->splashProgress:I

    invoke-virtual {v6, v0}, Lcom/ultragol/app/SplashActivity;->findViewById(I)Landroid/view/View;

    move-result-object v9

    .line 34
    .local v9, "progress":Landroid/view/View;
    sget v0, Lcom/ultragol/app/R$id;->ringOuter:I

    invoke-virtual {v6, v0}, Lcom/ultragol/app/SplashActivity;->findViewById(I)Landroid/view/View;

    move-result-object v10

    .line 35
    .local v10, "ringO":Landroid/view/View;
    sget v0, Lcom/ultragol/app/R$id;->ringMid:I

    invoke-virtual {v6, v0}, Lcom/ultragol/app/SplashActivity;->findViewById(I)Landroid/view/View;

    move-result-object v11

    .line 36
    .local v11, "ringM":Landroid/view/View;
    sget v0, Lcom/ultragol/app/R$id;->ringInner:I

    invoke-virtual {v6, v0}, Lcom/ultragol/app/SplashActivity;->findViewById(I)Landroid/view/View;

    move-result-object v12

    .line 37
    .local v12, "ringI":Landroid/view/View;
    sget v0, Lcom/ultragol/app/R$id;->dot1:I

    invoke-virtual {v6, v0}, Lcom/ultragol/app/SplashActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, v6, Lcom/ultragol/app/SplashActivity;->dot1:Landroid/view/View;

    .line 38
    sget v0, Lcom/ultragol/app/R$id;->dot2:I

    invoke-virtual {v6, v0}, Lcom/ultragol/app/SplashActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, v6, Lcom/ultragol/app/SplashActivity;->dot2:Landroid/view/View;

    .line 39
    sget v0, Lcom/ultragol/app/R$id;->dot3:I

    invoke-virtual {v6, v0}, Lcom/ultragol/app/SplashActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    iput-object v0, v6, Lcom/ultragol/app/SplashActivity;->dot3:Landroid/view/View;

    .line 42
    const-wide/16 v2, 0x0

    const-wide/16 v4, 0x320

    move-object/from16 v0, p0

    move-object v1, v10

    invoke-direct/range {v0 .. v5}, Lcom/ultragol/app/SplashActivity;->animateFadeIn(Landroid/view/View;JJ)V

    .line 43
    const-wide/16 v2, 0x96

    move-object v1, v11

    invoke-direct/range {v0 .. v5}, Lcom/ultragol/app/SplashActivity;->animateFadeIn(Landroid/view/View;JJ)V

    .line 44
    const-wide/16 v2, 0x12c

    move-object v1, v12

    invoke-direct/range {v0 .. v5}, Lcom/ultragol/app/SplashActivity;->animateFadeIn(Landroid/view/View;JJ)V

    .line 47
    new-instance v0, Landroid/view/animation/AnimationSet;

    const/4 v1, 0x1

    invoke-direct {v0, v1}, Landroid/view/animation/AnimationSet;-><init>(Z)V

    .line 48
    .local v0, "anim":Landroid/view/animation/AnimationSet;
    new-instance v1, Landroid/view/animation/ScaleAnimation;

    const v14, 0x3f19999a    # 0.6f

    const/high16 v15, 0x3f800000    # 1.0f

    const v16, 0x3f19999a    # 0.6f

    const/high16 v17, 0x3f800000    # 1.0f

    const/16 v18, 0x1

    const/high16 v19, 0x3f000000    # 0.5f

    const/16 v20, 0x1

    const/high16 v21, 0x3f000000    # 0.5f

    move-object v13, v1

    invoke-direct/range {v13 .. v21}, Landroid/view/animation/ScaleAnimation;-><init>(FFFFIFIF)V

    .line 50
    .local v1, "scale":Landroid/view/animation/ScaleAnimation;
    const-wide/16 v2, 0x2bc

    invoke-virtual {v1, v2, v3}, Landroid/view/animation/ScaleAnimation;->setDuration(J)V

    .line 51
    new-instance v4, Landroid/view/animation/AlphaAnimation;

    const/4 v5, 0x0

    const/high16 v13, 0x3f800000    # 1.0f

    invoke-direct {v4, v5, v13}, Landroid/view/animation/AlphaAnimation;-><init>(FF)V

    .line 52
    .local v4, "alpha":Landroid/view/animation/AlphaAnimation;
    invoke-virtual {v4, v2, v3}, Landroid/view/animation/AlphaAnimation;->setDuration(J)V

    .line 53
    invoke-virtual {v0, v1}, Landroid/view/animation/AnimationSet;->addAnimation(Landroid/view/animation/Animation;)V

    .line 54
    invoke-virtual {v0, v4}, Landroid/view/animation/AnimationSet;->addAnimation(Landroid/view/animation/Animation;)V

    .line 55
    if-eqz v7, :cond_0

    invoke-virtual {v7, v0}, Landroid/view/View;->startAnimation(Landroid/view/animation/Animation;)V

    .line 58
    :cond_0
    new-instance v2, Landroid/os/Handler;

    invoke-direct {v2}, Landroid/os/Handler;-><init>()V

    new-instance v3, Lcom/ultragol/app/SplashActivity$$ExternalSyntheticLambda0;

    invoke-direct {v3, v8}, Lcom/ultragol/app/SplashActivity$$ExternalSyntheticLambda0;-><init>(Landroid/view/View;)V

    const-wide/16 v13, 0x258

    invoke-virtual {v2, v3, v13, v14}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    .line 68
    new-instance v2, Landroid/os/Handler;

    invoke-direct {v2}, Landroid/os/Handler;-><init>()V

    new-instance v3, Lcom/ultragol/app/SplashActivity$$ExternalSyntheticLambda1;

    invoke-direct {v3, v6, v9}, Lcom/ultragol/app/SplashActivity$$ExternalSyntheticLambda1;-><init>(Lcom/ultragol/app/SplashActivity;Landroid/view/View;)V

    const-wide/16 v13, 0x384

    invoke-virtual {v2, v3, v13, v14}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    .line 79
    new-instance v2, Landroid/os/Handler;

    invoke-direct {v2}, Landroid/os/Handler;-><init>()V

    new-instance v3, Lcom/ultragol/app/SplashActivity$$ExternalSyntheticLambda2;

    invoke-direct {v3, v6}, Lcom/ultragol/app/SplashActivity$$ExternalSyntheticLambda2;-><init>(Lcom/ultragol/app/SplashActivity;)V

    const-wide/16 v13, 0x960

    invoke-virtual {v2, v3, v13, v14}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    .line 85
    return-void
.end method

.method protected onDestroy()V
    .locals 2

    .line 106
    invoke-super {p0}, Landroidx/appcompat/app/AppCompatActivity;->onDestroy()V

    .line 107
    iget-object v0, p0, Lcom/ultragol/app/SplashActivity;->dotHandler:Landroid/os/Handler;

    iget-object v1, p0, Lcom/ultragol/app/SplashActivity;->dotRunnable:Ljava/lang/Runnable;

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeCallbacks(Ljava/lang/Runnable;)V

    .line 108
    return-void
.end method
