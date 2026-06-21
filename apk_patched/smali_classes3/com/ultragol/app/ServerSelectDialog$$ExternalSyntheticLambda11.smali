.class public final synthetic Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda11;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Ljava/lang/Runnable;


# instance fields
.field public final synthetic f$0:Landroid/app/Dialog;

.field public final synthetic f$1:Landroid/view/View;

.field public final synthetic f$2:Landroid/content/Context;

.field public final synthetic f$3:Lcom/ultragol/app/models/ContentItem;

.field public final synthetic f$4:Lcom/ultragol/app/network/StreamingApi$ServerData;


# direct methods
.method public synthetic constructor <init>(Landroid/app/Dialog;Landroid/view/View;Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;Lcom/ultragol/app/network/StreamingApi$ServerData;)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda11;->f$0:Landroid/app/Dialog;

    iput-object p2, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda11;->f$1:Landroid/view/View;

    iput-object p3, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda11;->f$2:Landroid/content/Context;

    iput-object p4, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda11;->f$3:Lcom/ultragol/app/models/ContentItem;

    iput-object p5, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda11;->f$4:Lcom/ultragol/app/network/StreamingApi$ServerData;

    return-void
.end method


# virtual methods
.method public final run()V
    .locals 5

    iget-object v0, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda11;->f$0:Landroid/app/Dialog;

    iget-object v1, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda11;->f$1:Landroid/view/View;

    iget-object v2, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda11;->f$2:Landroid/content/Context;

    iget-object v3, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda11;->f$3:Lcom/ultragol/app/models/ContentItem;

    iget-object v4, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda11;->f$4:Lcom/ultragol/app/network/StreamingApi$ServerData;

    invoke-static {v0, v1, v2, v3, v4}, Lcom/ultragol/app/ServerSelectDialog;->lambda$loadServers$6(Landroid/app/Dialog;Landroid/view/View;Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;Lcom/ultragol/app/network/StreamingApi$ServerData;)V

    return-void
.end method
