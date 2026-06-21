.class public final synthetic Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda0;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Landroid/view/View$OnClickListener;


# instance fields
.field public final synthetic f$0:Landroid/app/Dialog;


# direct methods
.method public synthetic constructor <init>(Landroid/app/Dialog;)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda0;->f$0:Landroid/app/Dialog;

    return-void
.end method


# virtual methods
.method public final onClick(Landroid/view/View;)V
    .locals 1

    iget-object v0, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda0;->f$0:Landroid/app/Dialog;

    invoke-static {v0, p1}, Lcom/ultragol/app/ServerSelectDialog;->lambda$show$0(Landroid/app/Dialog;Landroid/view/View;)V

    return-void
.end method
