.class Lcom/ultragol/app/SearchActivity$1;
.super Ljava/lang/Object;
.source "SearchActivity.java"

# interfaces
.implements Landroid/text/TextWatcher;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/ultragol/app/SearchActivity;->onCreate(Landroid/os/Bundle;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/ultragol/app/SearchActivity;


# direct methods
.method constructor <init>(Lcom/ultragol/app/SearchActivity;)V
    .locals 0
    .param p1, "this$0"    # Lcom/ultragol/app/SearchActivity;
    .annotation system Ldalvik/annotation/MethodParameters;
        accessFlags = {
            0x8010
        }
        names = {
            null
        }
    .end annotation

    .line 50
    iput-object p1, p0, Lcom/ultragol/app/SearchActivity$1;->this$0:Lcom/ultragol/app/SearchActivity;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public afterTextChanged(Landroid/text/Editable;)V
    .locals 0
    .param p1, "s"    # Landroid/text/Editable;

    .line 59
    return-void
.end method

.method public beforeTextChanged(Ljava/lang/CharSequence;III)V
    .locals 0
    .param p1, "s"    # Ljava/lang/CharSequence;
    .param p2, "a"    # I
    .param p3, "b"    # I
    .param p4, "c"    # I

    .line 51
    return-void
.end method

.method synthetic lambda$onTextChanged$0$com-ultragol-app-SearchActivity$1(Ljava/lang/String;)V
    .locals 1
    .param p1, "q"    # Ljava/lang/String;

    .line 56
    iget-object v0, p0, Lcom/ultragol/app/SearchActivity$1;->this$0:Lcom/ultragol/app/SearchActivity;

    invoke-static {v0, p1}, Lcom/ultragol/app/SearchActivity;->access$300(Lcom/ultragol/app/SearchActivity;Ljava/lang/String;)V

    return-void
.end method

.method public onTextChanged(Ljava/lang/CharSequence;III)V
    .locals 5
    .param p1, "s"    # Ljava/lang/CharSequence;
    .param p2, "a"    # I
    .param p3, "b"    # I
    .param p4, "c"    # I

    .line 53
    iget-object v0, p0, Lcom/ultragol/app/SearchActivity$1;->this$0:Lcom/ultragol/app/SearchActivity;

    invoke-static {v0}, Lcom/ultragol/app/SearchActivity;->access$000(Lcom/ultragol/app/SearchActivity;)Ljava/lang/Runnable;

    move-result-object v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/ultragol/app/SearchActivity$1;->this$0:Lcom/ultragol/app/SearchActivity;

    invoke-static {v0}, Lcom/ultragol/app/SearchActivity;->access$100(Lcom/ultragol/app/SearchActivity;)Landroid/os/Handler;

    move-result-object v0

    iget-object v1, p0, Lcom/ultragol/app/SearchActivity$1;->this$0:Lcom/ultragol/app/SearchActivity;

    invoke-static {v1}, Lcom/ultragol/app/SearchActivity;->access$000(Lcom/ultragol/app/SearchActivity;)Ljava/lang/Runnable;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeCallbacks(Ljava/lang/Runnable;)V

    .line 54
    :cond_0
    invoke-interface {p1}, Ljava/lang/CharSequence;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/String;->trim()Ljava/lang/String;

    move-result-object v0

    .line 55
    .local v0, "q":Ljava/lang/String;
    invoke-virtual {v0}, Ljava/lang/String;->length()I

    move-result v1

    const/4 v2, 0x2

    if-ge v1, v2, :cond_1

    iget-object v1, p0, Lcom/ultragol/app/SearchActivity$1;->this$0:Lcom/ultragol/app/SearchActivity;

    invoke-static {v1}, Lcom/ultragol/app/SearchActivity;->access$200(Lcom/ultragol/app/SearchActivity;)V

    return-void

    .line 56
    :cond_1
    iget-object v1, p0, Lcom/ultragol/app/SearchActivity$1;->this$0:Lcom/ultragol/app/SearchActivity;

    new-instance v2, Lcom/ultragol/app/SearchActivity$1$$ExternalSyntheticLambda0;

    invoke-direct {v2, p0, v0}, Lcom/ultragol/app/SearchActivity$1$$ExternalSyntheticLambda0;-><init>(Lcom/ultragol/app/SearchActivity$1;Ljava/lang/String;)V

    invoke-static {v1, v2}, Lcom/ultragol/app/SearchActivity;->access$002(Lcom/ultragol/app/SearchActivity;Ljava/lang/Runnable;)Ljava/lang/Runnable;

    .line 57
    iget-object v1, p0, Lcom/ultragol/app/SearchActivity$1;->this$0:Lcom/ultragol/app/SearchActivity;

    invoke-static {v1}, Lcom/ultragol/app/SearchActivity;->access$100(Lcom/ultragol/app/SearchActivity;)Landroid/os/Handler;

    move-result-object v1

    iget-object v2, p0, Lcom/ultragol/app/SearchActivity$1;->this$0:Lcom/ultragol/app/SearchActivity;

    invoke-static {v2}, Lcom/ultragol/app/SearchActivity;->access$000(Lcom/ultragol/app/SearchActivity;)Ljava/lang/Runnable;

    move-result-object v2

    const-wide/16 v3, 0x1f4

    invoke-virtual {v1, v2, v3, v4}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    .line 58
    return-void
.end method
