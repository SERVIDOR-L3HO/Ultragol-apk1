.class Lcom/ultragol/app/PlayerActivity$2;
.super Landroid/webkit/WebChromeClient;
.source "PlayerActivity.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/ultragol/app/PlayerActivity;->onCreate(Landroid/os/Bundle;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/ultragol/app/PlayerActivity;


# direct methods
.method constructor <init>(Lcom/ultragol/app/PlayerActivity;)V
    .locals 0
    .param p1, "this$0"    # Lcom/ultragol/app/PlayerActivity;
    .annotation system Ldalvik/annotation/MethodParameters;
        accessFlags = {
            0x8010
        }
        names = {
            null
        }
    .end annotation

    .line 62
    iput-object p1, p0, Lcom/ultragol/app/PlayerActivity$2;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-direct {p0}, Landroid/webkit/WebChromeClient;-><init>()V

    return-void
.end method


# virtual methods
.method public onCreateWindow(Landroid/webkit/WebView;ZZLandroid/os/Message;)Z
    .locals 3
    .param p1, "view"    # Landroid/webkit/WebView;
    .param p2, "isDialog"    # Z
    .param p3, "isUserGesture"    # Z
    .param p4, "resultMsg"    # Landroid/os/Message;

    .line 84
    new-instance v0, Landroid/webkit/WebView;

    iget-object v1, p0, Lcom/ultragol/app/PlayerActivity$2;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-direct {v0, v1}, Landroid/webkit/WebView;-><init>(Landroid/content/Context;)V

    .line 85
    .local v0, "popup":Landroid/webkit/WebView;
    invoke-virtual {v0}, Landroid/webkit/WebView;->getSettings()Landroid/webkit/WebSettings;

    move-result-object v1

    const/4 v2, 0x1

    invoke-virtual {v1, v2}, Landroid/webkit/WebSettings;->setJavaScriptEnabled(Z)V

    .line 86
    new-instance v1, Lcom/ultragol/app/PlayerActivity$2$1;

    invoke-direct {v1, p0}, Lcom/ultragol/app/PlayerActivity$2$1;-><init>(Lcom/ultragol/app/PlayerActivity$2;)V

    invoke-virtual {v0, v1}, Landroid/webkit/WebView;->setWebViewClient(Landroid/webkit/WebViewClient;)V

    .line 89
    iget-object v1, p4, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v1, Landroid/webkit/WebView$WebViewTransport;

    invoke-virtual {v1, v0}, Landroid/webkit/WebView$WebViewTransport;->setWebView(Landroid/webkit/WebView;)V

    .line 90
    invoke-virtual {p4}, Landroid/os/Message;->sendToTarget()V

    return v2
.end method

.method public onHideCustomView()V
    .locals 2

    .line 73
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$2;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-static {v0}, Lcom/ultragol/app/PlayerActivity;->access$100(Lcom/ultragol/app/PlayerActivity;)Landroid/view/View;

    move-result-object v0

    if-nez v0, :cond_0

    return-void

    .line 74
    :cond_0
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$2;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-static {v0}, Lcom/ultragol/app/PlayerActivity;->access$400(Lcom/ultragol/app/PlayerActivity;)Landroid/widget/FrameLayout;

    move-result-object v0

    iget-object v1, p0, Lcom/ultragol/app/PlayerActivity$2;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-static {v1}, Lcom/ultragol/app/PlayerActivity;->access$100(Lcom/ultragol/app/PlayerActivity;)Landroid/view/View;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/widget/FrameLayout;->removeView(Landroid/view/View;)V

    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$2;->this$0:Lcom/ultragol/app/PlayerActivity;

    const/4 v1, 0x0

    invoke-static {v0, v1}, Lcom/ultragol/app/PlayerActivity;->access$102(Lcom/ultragol/app/PlayerActivity;Landroid/view/View;)Landroid/view/View;

    .line 75
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$2;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-static {v0}, Lcom/ultragol/app/PlayerActivity;->access$400(Lcom/ultragol/app/PlayerActivity;)Landroid/widget/FrameLayout;

    move-result-object v0

    const/16 v1, 0x8

    invoke-virtual {v0, v1}, Landroid/widget/FrameLayout;->setVisibility(I)V

    .line 76
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$2;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-static {v0}, Lcom/ultragol/app/PlayerActivity;->access$300(Lcom/ultragol/app/PlayerActivity;)Landroid/view/View;

    move-result-object v0

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 77
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$2;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-static {v0}, Lcom/ultragol/app/PlayerActivity;->access$200(Lcom/ultragol/app/PlayerActivity;)Landroid/webkit/WebChromeClient$CustomViewCallback;

    move-result-object v0

    invoke-interface {v0}, Landroid/webkit/WebChromeClient$CustomViewCallback;->onCustomViewHidden()V

    .line 78
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$2;->this$0:Lcom/ultragol/app/PlayerActivity;

    const/4 v1, 0x1

    invoke-virtual {v0, v1}, Lcom/ultragol/app/PlayerActivity;->setRequestedOrientation(I)V

    .line 79
    return-void
.end method

.method public onProgressChanged(Landroid/webkit/WebView;I)V
    .locals 2
    .param p1, "v"    # Landroid/webkit/WebView;
    .param p2, "p"    # I

    .line 81
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$2;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-static {v0}, Lcom/ultragol/app/PlayerActivity;->access$000(Lcom/ultragol/app/PlayerActivity;)Landroid/widget/ProgressBar;

    move-result-object v0

    if-eqz v0, :cond_1

    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$2;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-static {v0}, Lcom/ultragol/app/PlayerActivity;->access$000(Lcom/ultragol/app/PlayerActivity;)Landroid/widget/ProgressBar;

    move-result-object v0

    invoke-virtual {v0, p2}, Landroid/widget/ProgressBar;->setProgress(I)V

    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$2;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-static {v0}, Lcom/ultragol/app/PlayerActivity;->access$000(Lcom/ultragol/app/PlayerActivity;)Landroid/widget/ProgressBar;

    move-result-object v0

    const/16 v1, 0x64

    if-ne p2, v1, :cond_0

    const/16 v1, 0x8

    goto :goto_0

    :cond_0
    const/4 v1, 0x0

    :goto_0
    invoke-virtual {v0, v1}, Landroid/widget/ProgressBar;->setVisibility(I)V

    .line 82
    :cond_1
    return-void
.end method

.method public onShowCustomView(Landroid/view/View;Landroid/webkit/WebChromeClient$CustomViewCallback;)V
    .locals 2
    .param p1, "view"    # Landroid/view/View;
    .param p2, "cb"    # Landroid/webkit/WebChromeClient$CustomViewCallback;

    .line 64
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$2;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-static {v0}, Lcom/ultragol/app/PlayerActivity;->access$100(Lcom/ultragol/app/PlayerActivity;)Landroid/view/View;

    move-result-object v0

    if-eqz v0, :cond_0

    invoke-interface {p2}, Landroid/webkit/WebChromeClient$CustomViewCallback;->onCustomViewHidden()V

    return-void

    .line 65
    :cond_0
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$2;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-static {v0, p1}, Lcom/ultragol/app/PlayerActivity;->access$102(Lcom/ultragol/app/PlayerActivity;Landroid/view/View;)Landroid/view/View;

    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$2;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-static {v0, p2}, Lcom/ultragol/app/PlayerActivity;->access$202(Lcom/ultragol/app/PlayerActivity;Landroid/webkit/WebChromeClient$CustomViewCallback;)Landroid/webkit/WebChromeClient$CustomViewCallback;

    .line 66
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$2;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-static {v0}, Lcom/ultragol/app/PlayerActivity;->access$300(Lcom/ultragol/app/PlayerActivity;)Landroid/view/View;

    move-result-object v0

    const/16 v1, 0x8

    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 67
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$2;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-static {v0}, Lcom/ultragol/app/PlayerActivity;->access$400(Lcom/ultragol/app/PlayerActivity;)Landroid/widget/FrameLayout;

    move-result-object v0

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Landroid/widget/FrameLayout;->setVisibility(I)V

    .line 68
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$2;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-static {v0}, Lcom/ultragol/app/PlayerActivity;->access$400(Lcom/ultragol/app/PlayerActivity;)Landroid/widget/FrameLayout;

    move-result-object v0

    invoke-virtual {v0, p1}, Landroid/widget/FrameLayout;->addView(Landroid/view/View;)V

    .line 69
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$2;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-virtual {v0, v1}, Lcom/ultragol/app/PlayerActivity;->setRequestedOrientation(I)V

    .line 70
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$2;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-static {v0}, Lcom/ultragol/app/PlayerActivity;->access$500(Lcom/ultragol/app/PlayerActivity;)V

    .line 71
    return-void
.end method
