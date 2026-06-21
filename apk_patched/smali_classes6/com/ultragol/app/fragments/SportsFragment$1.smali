.class Lcom/ultragol/app/fragments/SportsFragment$1;
.super Ljava/lang/Object;
.source "SportsFragment.java"

# interfaces
.implements Landroid/text/TextWatcher;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/ultragol/app/fragments/SportsFragment;->onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/ultragol/app/fragments/SportsFragment;


# direct methods
.method constructor <init>(Lcom/ultragol/app/fragments/SportsFragment;)V
    .locals 0
    .param p1, "this$0"    # Lcom/ultragol/app/fragments/SportsFragment;
    .annotation system Ldalvik/annotation/MethodParameters;
        accessFlags = {
            0x8010
        }
        names = {
            null
        }
    .end annotation

    .line 53
    iput-object p1, p0, Lcom/ultragol/app/fragments/SportsFragment$1;->this$0:Lcom/ultragol/app/fragments/SportsFragment;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public afterTextChanged(Landroid/text/Editable;)V
    .locals 0
    .param p1, "s"    # Landroid/text/Editable;

    .line 56
    return-void
.end method

.method public beforeTextChanged(Ljava/lang/CharSequence;III)V
    .locals 0
    .param p1, "s"    # Ljava/lang/CharSequence;
    .param p2, "a"    # I
    .param p3, "b"    # I
    .param p4, "c"    # I

    .line 54
    return-void
.end method

.method public onTextChanged(Ljava/lang/CharSequence;III)V
    .locals 2
    .param p1, "s"    # Ljava/lang/CharSequence;
    .param p2, "a"    # I
    .param p3, "b"    # I
    .param p4, "c"    # I

    .line 55
    iget-object v0, p0, Lcom/ultragol/app/fragments/SportsFragment$1;->this$0:Lcom/ultragol/app/fragments/SportsFragment;

    invoke-interface {p1}, Ljava/lang/CharSequence;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v0, v1}, Lcom/ultragol/app/fragments/SportsFragment;->access$000(Lcom/ultragol/app/fragments/SportsFragment;Ljava/lang/String;)V

    return-void
.end method
