.class Lcom/ultragol/app/PlayerActivity$1;
.super Landroid/webkit/WebViewClient;
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

    .line 52
    iput-object p1, p0, Lcom/ultragol/app/PlayerActivity$1;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-direct {p0}, Landroid/webkit/WebViewClient;-><init>()V

    return-void
.end method


# virtual methods
.method public onPageFinished(Landroid/webkit/WebView;Ljava/lang/String;)V
    .locals 2
    .param p1, "v"    # Landroid/webkit/WebView;
    .param p2, "u"    # Ljava/lang/String;

    .line 59
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$1;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-static {v0}, Lcom/ultragol/app/PlayerActivity;->access$000(Lcom/ultragol/app/PlayerActivity;)Landroid/widget/ProgressBar;

    move-result-object v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$1;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-static {v0}, Lcom/ultragol/app/PlayerActivity;->access$000(Lcom/ultragol/app/PlayerActivity;)Landroid/widget/ProgressBar;

    move-result-object v0

    const/16 v1, 0x8

    invoke-virtual {v0, v1}, Landroid/widget/ProgressBar;->setVisibility(I)V

    :cond_0
    return-void
.end method

.method public onPageStarted(Landroid/webkit/WebView;Ljava/lang/String;Landroid/graphics/Bitmap;)V
    .locals 2
    .param p1, "v"    # Landroid/webkit/WebView;
    .param p2, "u"    # Ljava/lang/String;
    .param p3, "f"    # Landroid/graphics/Bitmap;

    .line 58
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$1;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-static {v0}, Lcom/ultragol/app/PlayerActivity;->access$000(Lcom/ultragol/app/PlayerActivity;)Landroid/widget/ProgressBar;

    move-result-object v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$1;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-static {v0}, Lcom/ultragol/app/PlayerActivity;->access$000(Lcom/ultragol/app/PlayerActivity;)Landroid/widget/ProgressBar;

    move-result-object v0

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Landroid/widget/ProgressBar;->setVisibility(I)V

    :cond_0
    return-void
.end method

.method public shouldOverrideUrlLoading(Landroid/webkit/WebView;Landroid/webkit/WebResourceRequest;)Z
    .locals 3
    .param p1, "v"    # Landroid/webkit/WebView;
    .param p2, "r"    # Landroid/webkit/WebResourceRequest;

    .line 54
    invoke-interface {p2}, Landroid/webkit/WebResourceRequest;->getUrl()Landroid/net/Uri;

    move-result-object v0

    invoke-virtual {v0}, Landroid/net/Uri;->getScheme()Ljava/lang/String;

    move-result-object v0

    .line 55
    .local v0, "scheme":Ljava/lang/String;
    const-string v1, "http"

    invoke-virtual {v1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    const/4 v2, 0x1

    if-nez v1, :cond_1

    const-string v1, "https"

    invoke-virtual {v1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_0

    goto :goto_0

    .line 56
    :cond_0
    return v2

    .line 55
    :cond_1
    :goto_0
    invoke-interface {p2}, Landroid/webkit/WebResourceRequest;->getUrl()Landroid/net/Uri;

    move-result-object v1

    invoke-virtual {v1}, Landroid/net/Uri;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {p1, v1}, Landroid/webkit/WebView;->loadUrl(Ljava/lang/String;)V

    return v2
.end method
