.class public final synthetic Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda10;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Ljava/lang/Runnable;


# instance fields
.field public final synthetic f$0:Lcom/ultragol/app/models/ContentItem;

.field public final synthetic f$1:I

.field public final synthetic f$2:I

.field public final synthetic f$3:Landroid/os/Handler;

.field public final synthetic f$4:Landroid/app/Dialog;

.field public final synthetic f$5:Landroid/view/View;

.field public final synthetic f$6:Landroid/content/Context;

.field public final synthetic f$7:Landroid/view/View;


# direct methods
.method public synthetic constructor <init>(Lcom/ultragol/app/models/ContentItem;IILandroid/os/Handler;Landroid/app/Dialog;Landroid/view/View;Landroid/content/Context;Landroid/view/View;)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda10;->f$0:Lcom/ultragol/app/models/ContentItem;

    iput p2, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda10;->f$1:I

    iput p3, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda10;->f$2:I

    iput-object p4, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda10;->f$3:Landroid/os/Handler;

    iput-object p5, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda10;->f$4:Landroid/app/Dialog;

    iput-object p6, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda10;->f$5:Landroid/view/View;

    iput-object p7, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda10;->f$6:Landroid/content/Context;

    iput-object p8, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda10;->f$7:Landroid/view/View;

    return-void
.end method


# virtual methods
.method public final run()V
    .locals 8

    iget-object v0, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda10;->f$0:Lcom/ultragol/app/models/ContentItem;

    iget v1, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda10;->f$1:I

    iget v2, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda10;->f$2:I

    iget-object v3, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda10;->f$3:Landroid/os/Handler;

    iget-object v4, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda10;->f$4:Landroid/app/Dialog;

    iget-object v5, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda10;->f$5:Landroid/view/View;

    iget-object v6, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda10;->f$6:Landroid/content/Context;

    iget-object v7, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda10;->f$7:Landroid/view/View;

    invoke-static/range {v0 .. v7}, Lcom/ultragol/app/ServerSelectDialog;->lambda$loadServers$8(Lcom/ultragol/app/models/ContentItem;IILandroid/os/Handler;Landroid/app/Dialog;Landroid/view/View;Landroid/content/Context;Landroid/view/View;)V

    return-void
.end method
