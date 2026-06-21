.class Lcom/ultragol/app/PlayerActivity$2$1;
.super Landroid/webkit/WebViewClient;
.source "PlayerActivity.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/ultragol/app/PlayerActivity$2;->onCreateWindow(Landroid/webkit/WebView;ZZLandroid/os/Message;)Z
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$1:Lcom/ultragol/app/PlayerActivity$2;


# direct methods
.method constructor <init>(Lcom/ultragol/app/PlayerActivity$2;)V
    .locals 0
    .param p1, "this$1"    # Lcom/ultragol/app/PlayerActivity$2;
    .annotation system Ldalvik/annotation/MethodParameters;
        accessFlags = {
            0x8010
        }
        names = {
            null
        }
    .end annotation

    .line 86
    iput-object p1, p0, Lcom/ultragol/app/PlayerActivity$2$1;->this$1:Lcom/ultragol/app/PlayerActivity$2;

    invoke-direct {p0}, Landroid/webkit/WebViewClient;-><init>()V

    return-void
.end method


# virtual methods
.method public shouldOverrideUrlLoading(Landroid/webkit/WebView;Landroid/webkit/WebResourceRequest;)Z
    .locals 2
    .param p1, "v"    # Landroid/webkit/WebView;
    .param p2, "r"    # Landroid/webkit/WebResourceRequest;

    .line 87
    iget-object v0, p0, Lcom/ultragol/app/PlayerActivity$2$1;->this$1:Lcom/ultragol/app/PlayerActivity$2;

    iget-object v0, v0, Lcom/ultragol/app/PlayerActivity$2;->this$0:Lcom/ultragol/app/PlayerActivity;

    invoke-static {v0}, Lcom/ultragol/app/PlayerActivity;->access$600(Lcom/ultragol/app/PlayerActivity;)Landroid/webkit/WebView;

    move-result-object v0

    invoke-interface {p2}, Landroid/webkit/WebResourceRequest;->getUrl()Landroid/net/Uri;

    move-result-object v1

    invoke-virtual {v1}, Landroid/net/Uri;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/webkit/WebView;->loadUrl(Ljava/lang/String;)V

    const/4 v0, 0x1

    return v0
.end method
