.class public Lcom/ultragol/app/PlayerActivity;
.super Landroidx/appcompat/app/AppCompatActivity;
.source "PlayerActivity.java"


# instance fields
.field private customView:Landroid/view/View;

.field private customViewCallback:Landroid/webkit/WebChromeClient$CustomViewCallback;

.field private fullscreenContainer:Landroid/widget/FrameLayout;

.field private progressBar:Landroid/widget/ProgressBar;

.field private webView:Landroid/webkit/WebView;

.field private webviewContainer:Landroid/view/View;


# direct methods
.method public constructor <init>()V
    .locals 0

    .line 15
    invoke-direct {p0}, Landroidx/appcompat/app/AppCompatActivity;-><init>()V

    return-void
.end method

.method static synthetic access$000(Lcom/ultragol/app/PlayerActivity;)Landroid/widget/ProgressBar;
    .locals 1
    .param p0, "x0"    # Lcom/ultragol/app/PlayerActivity;

    .line 15
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity;->progressBar:Landroid/widget/ProgressBar;

    return-object v0
.end method

.method static synthetic access$100(Lcom/ultragol/app/PlayerActivity;)Landroid/view/View;
    .locals 1
    .param p0, "x0"    # Lcom/ultragol/app/PlayerActivity;

    .line 15
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity;->customView:Landroid/view/View;

    return-object v0
.end method

.method static synthetic access$102(Lcom/ultragol/app/PlayerActivity;Landroid/view/View;)Landroid/view/View;
    .locals 0
    .param p0, "x0"    # Lcom/ultragol/app/PlayerActivity;
    .param p1, "x1"    # Landroid/view/View;

    .line 15
    iput-object p1, p0, Lcom/ultragol/app/PlayerActivity;->customView:Landroid/view/View;

    return-object p1
.end method

.method static synthetic access$200(Lcom/ultragol/app/PlayerActivity;)Landroid/webkit/WebChromeClient$CustomViewCallback;
    .locals 1
    .param p0, "x0"    # Lcom/ultragol/app/PlayerActivity;

    .line 15
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity;->customViewCallback:Landroid/webkit/WebChromeClient$CustomViewCallback;

    return-object v0
.end method

.method static synthetic access$202(Lcom/ultragol/app/PlayerActivity;Landroid/webkit/WebChromeClient$CustomViewCallback;)Landroid/webkit/WebChromeClient$CustomViewCallback;
    .locals 0
    .param p0, "x0"    # Lcom/ultragol/app/PlayerActivity;
    .param p1, "x1"    # Landroid/webkit/WebChromeClient$CustomViewCallback;

    .line 15
    iput-object p1, p0, Lcom/ultragol/app/PlayerActivity;->customViewCallback:Landroid/webkit/WebChromeClient$CustomViewCallback;

    return-object p1
.end method

.method static synthetic access$300(Lcom/ultragol/app/PlayerActivity;)Landroid/view/View;
    .locals 1
    .param p0, "x0"    # Lcom/ultragol/app/PlayerActivity;

    .line 15
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity;->webviewContainer:Landroid/view/View;

    return-object v0
.end method

.method static synthetic access$400(Lcom/ultragol/app/PlayerActivity;)Landroid/widget/FrameLayout;
    .locals 1
    .param p0, "x0"    # Lcom/ultragol/app/PlayerActivity;

    .line 15
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity;->fullscreenContainer:Landroid/widget/FrameLayout;

    return-object v0
.end method

.method static synthetic access$500(Lcom/ultragol/app/PlayerActivity;)V
    .locals 0
    .param p0, "x0"    # Lcom/ultragol/app/PlayerActivity;

    .line 15
    invoke-direct {p0}, Lcom/ultragol/app/PlayerActivity;->hideSystemUI()V

    return-void
.end method

.method static synthetic access$600(Lcom/ultragol/app/PlayerActivity;)Landroid/webkit/WebView;
    .locals 1
    .param p0, "x0"    # Lcom/ultragol/app/PlayerActivity;

    .line 15
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity;->webView:Landroid/webkit/WebView;

    return-object v0
.end method

.method private hideSystemUI()V
    .locals 2

    .line 98
    invoke-virtual {p0}, Lcom/ultragol/app/PlayerActivity;->getWindow()Landroid/view/Window;

    move-result-object v0

    invoke-virtual {v0}, Landroid/view/Window;->getDecorView()Landroid/view/View;

    move-result-object v0

    const/16 v1, 0x1006

    invoke-virtual {v0, v1}, Landroid/view/View;->setSystemUiVisibility(I)V

    .line 100
    return-void
.end method


# virtual methods
.method synthetic lambda$onCreate$0$com-ultragol-app-PlayerActivity(Landroid/view/View;)V
    .locals 0
    .param p1, "v"    # Landroid/view/View;

    .line 40
    invoke-virtual {p0}, Lcom/ultragol/app/PlayerActivity;->finish()V

    return-void
.end method

.method public onBackPressed()V
    .locals 1

    .line 103
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity;->customView:Landroid/view/View;

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity;->webView:Landroid/webkit/WebView;

    invoke-virtual {v0}, Landroid/webkit/WebView;->getWebChromeClient()Landroid/webkit/WebChromeClient;

    move-result-object v0

    invoke-virtual {v0}, Landroid/webkit/WebChromeClient;->onHideCustomView()V

    goto :goto_0

    .line 104
    :cond_0
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity;->webView:Landroid/webkit/WebView;

    invoke-virtual {v0}, Landroid/webkit/WebView;->canGoBack()Z

    move-result v0

    if-eqz v0, :cond_1

    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity;->webView:Landroid/webkit/WebView;

    invoke-virtual {v0}, Landroid/webkit/WebView;->goBack()V

    goto :goto_0

    .line 105
    :cond_1
    invoke-super {p0}, Landroidx/appcompat/app/AppCompatActivity;->onBackPressed()V

    .line 106
    :goto_0
    return-void
.end method

.method protected onCreate(Landroid/os/Bundle;)V
    .locals 8
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    .line 25
    invoke-super {p0, p1}, Landroidx/appcompat/app/AppCompatActivity;->onCreate(Landroid/os/Bundle;)V

    .line 26
    invoke-virtual {p0}, Lcom/ultragol/app/PlayerActivity;->getWindow()Landroid/view/Window;

    move-result-object v0

    const/16 v1, 0x80

    invoke-virtual {v0, v1}, Landroid/view/Window;->addFlags(I)V

    .line 27
    sget v0, Lcom/ultragol/app/R$layout;->activity_player:I

    invoke-virtual {p0, v0}, Lcom/ultragol/app/PlayerActivity;->setContentView(I)V

    .line 29
    invoke-virtual {p0}, Lcom/ultragol/app/PlayerActivity;->getIntent()Landroid/content/Intent;

    move-result-object v0

    const-string v1, "url"

    invoke-virtual {v0, v1}, Landroid/content/Intent;->getStringExtra(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 30
    .local v0, "url":Ljava/lang/String;
    invoke-virtual {p0}, Lcom/ultragol/app/PlayerActivity;->getIntent()Landroid/content/Intent;

    move-result-object v1

    const-string v2, "title"

    invoke-virtual {v1, v2}, Landroid/content/Intent;->getStringExtra(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    .line 32
    .local v1, "title":Ljava/lang/String;
    sget v2, Lcom/ultragol/app/R$id;->playerWebView:I

    invoke-virtual {p0, v2}, Lcom/ultragol/app/PlayerActivity;->findViewById(I)Landroid/view/View;

    move-result-object v2

    check-cast v2, Landroid/webkit/WebView;

    iput-object v2, p0, Lcom/ultragol/app/PlayerActivity;->webView:Landroid/webkit/WebView;

    .line 33
    sget v2, Lcom/ultragol/app/R$id;->playerProgress:I

    invoke-virtual {p0, v2}, Lcom/ultragol/app/PlayerActivity;->findViewById(I)Landroid/view/View;

    move-result-object v2

    check-cast v2, Landroid/widget/ProgressBar;

    iput-object v2, p0, Lcom/ultragol/app/PlayerActivity;->progressBar:Landroid/widget/ProgressBar;

    .line 34
    sget v2, Lcom/ultragol/app/R$id;->fullscreenContainer:I

    invoke-virtual {p0, v2}, Lcom/ultragol/app/PlayerActivity;->findViewById(I)Landroid/view/View;

    move-result-object v2

    check-cast v2, Landroid/widget/FrameLayout;

    iput-object v2, p0, Lcom/ultragol/app/PlayerActivity;->fullscreenContainer:Landroid/widget/FrameLayout;

    .line 35
    sget v2, Lcom/ultragol/app/R$id;->webviewContainer:I

    invoke-virtual {p0, v2}, Lcom/ultragol/app/PlayerActivity;->findViewById(I)Landroid/view/View;

    move-result-object v2

    iput-object v2, p0, Lcom/ultragol/app/PlayerActivity;->webviewContainer:Landroid/view/View;

    .line 37
    sget v2, Lcom/ultragol/app/R$id;->playerTitle:I

    invoke-virtual {p0, v2}, Lcom/ultragol/app/PlayerActivity;->findViewById(I)Landroid/view/View;

    move-result-object v2

    check-cast v2, Landroid/widget/TextView;

    .line 38
    .local v2, "tvTitle":Landroid/widget/TextView;
    if-eqz v1, :cond_0

    if-eqz v2, :cond_0

    invoke-virtual {v2, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 39
    :cond_0
    sget v3, Lcom/ultragol/app/R$id;->btnPlayerBack:I

    invoke-virtual {p0, v3}, Lcom/ultragol/app/PlayerActivity;->findViewById(I)Landroid/view/View;

    move-result-object v3

    .line 40
    .local v3, "btnBack":Landroid/view/View;
    if-eqz v3, :cond_1

    new-instance v4, Lcom/ultragol/app/PlayerActivity$$ExternalSyntheticLambda0;

    invoke-direct {v4, p0}, Lcom/ultragol/app/PlayerActivity$$ExternalSyntheticLambda0;-><init>(Lcom/ultragol/app/PlayerActivity;)V

    invoke-virtual {v3, v4}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 42
    :cond_1
    iget-object v4, p0, Lcom/ultragol/app/PlayerActivity;->webView:Landroid/webkit/WebView;

    invoke-virtual {v4}, Landroid/webkit/WebView;->getSettings()Landroid/webkit/WebSettings;

    move-result-object v4

    .line 43
    .local v4, "s":Landroid/webkit/WebSettings;
    const/4 v5, 0x1

    invoke-virtual {v4, v5}, Landroid/webkit/WebSettings;->setJavaScriptEnabled(Z)V

    invoke-virtual {v4, v5}, Landroid/webkit/WebSettings;->setDomStorageEnabled(Z)V

    .line 44
    const/4 v6, 0x0

    invoke-virtual {v4, v6}, Landroid/webkit/WebSettings;->setMediaPlaybackRequiresUserGesture(Z)V

    .line 45
    invoke-virtual {v4, v5}, Landroid/webkit/WebSettings;->setLoadWithOverviewMode(Z)V

    invoke-virtual {v4, v5}, Landroid/webkit/WebSettings;->setUseWideViewPort(Z)V

    .line 46
    invoke-virtual {v4, v6}, Landroid/webkit/WebSettings;->setSupportZoom(Z)V

    invoke-virtual {v4, v6}, Landroid/webkit/WebSettings;->setBuiltInZoomControls(Z)V

    .line 47
    const/4 v7, -0x1

    invoke-virtual {v4, v7}, Landroid/webkit/WebSettings;->setCacheMode(I)V

    .line 48
    invoke-virtual {v4, v6}, Landroid/webkit/WebSettings;->setMixedContentMode(I)V

    .line 49
    const-string v6, "Mozilla/5.0 (Linux; Android 12; Pixel 6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Mobile Safari/537.36"

    invoke-virtual {v4, v6}, Landroid/webkit/WebSettings;->setUserAgentString(Ljava/lang/String;)V

    .line 50
    invoke-virtual {v4, v5}, Landroid/webkit/WebSettings;->setSupportMultipleWindows(Z)V

    .line 52
    iget-object v5, p0, Lcom/ultragol/app/PlayerActivity;->webView:Landroid/webkit/WebView;

    new-instance v6, Lcom/ultragol/app/PlayerActivity$1;

    invoke-direct {v6, p0}, Lcom/ultragol/app/PlayerActivity$1;-><init>(Lcom/ultragol/app/PlayerActivity;)V

    invoke-virtual {v5, v6}, Landroid/webkit/WebView;->setWebViewClient(Landroid/webkit/WebViewClient;)V

    .line 62
    iget-object v5, p0, Lcom/ultragol/app/PlayerActivity;->webView:Landroid/webkit/WebView;

    new-instance v6, Lcom/ultragol/app/PlayerActivity$2;

    invoke-direct {v6, p0}, Lcom/ultragol/app/PlayerActivity$2;-><init>(Lcom/ultragol/app/PlayerActivity;)V

    invoke-virtual {v5, v6}, Landroid/webkit/WebView;->setWebChromeClient(Landroid/webkit/WebChromeClient;)V

    .line 94
    if-eqz v0, :cond_2

    iget-object v5, p0, Lcom/ultragol/app/PlayerActivity;->webView:Landroid/webkit/WebView;

    invoke-virtual {v5, v0}, Landroid/webkit/WebView;->loadUrl(Ljava/lang/String;)V

    .line 95
    :cond_2
    return-void
.end method

.method protected onDestroy()V
    .locals 1

    .line 109
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity;->webView:Landroid/webkit/WebView;

    invoke-virtual {v0}, Landroid/webkit/WebView;->destroy()V

    invoke-super {p0}, Landroidx/appcompat/app/AppCompatActivity;->onDestroy()V

    return-void
.end method

.method protected onPause()V
    .locals 1

    .line 107
    invoke-super {p0}, Landroidx/appcompat/app/AppCompatActivity;->onPause()V

    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity;->webView:Landroid/webkit/WebView;

    invoke-virtual {v0}, Landroid/webkit/WebView;->onPause()V

    return-void
.end method

.method protected onResume()V
    .locals 1

    .line 108
    invoke-super {p0}, Landroidx/appcompat/app/AppCompatActivity;->onResume()V

    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity;->webView:Landroid/webkit/WebView;

    invoke-virtual {v0}, Landroid/webkit/WebView;->onResume()V

    return-void
.end method
