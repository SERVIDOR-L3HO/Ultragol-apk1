.class public final synthetic Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda9;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Landroid/view/View$OnClickListener;


# instance fields
.field public final synthetic f$0:Landroid/content/Context;

.field public final synthetic f$1:Landroid/app/Dialog;

.field public final synthetic f$2:Lcom/ultragol/app/models/ContentItem;

.field public final synthetic f$3:[I

.field public final synthetic f$4:[I


# direct methods
.method public synthetic constructor <init>(Landroid/content/Context;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;[I[I)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda9;->f$0:Landroid/content/Context;

    iput-object p2, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda9;->f$1:Landroid/app/Dialog;

    iput-object p3, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda9;->f$2:Lcom/ultragol/app/models/ContentItem;

    iput-object p4, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda9;->f$3:[I

    iput-object p5, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda9;->f$4:[I

    return-void
.end method


# virtual methods
.method public final onClick(Landroid/view/View;)V
    .locals 6

    iget-object v0, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda9;->f$0:Landroid/content/Context;

    iget-object v1, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda9;->f$1:Landroid/app/Dialog;

    iget-object v2, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda9;->f$2:Lcom/ultragol/app/models/ContentItem;

    iget-object v3, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda9;->f$3:[I

    iget-object v4, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda9;->f$4:[I

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/ultragol/app/ServerSelectDialog;->lambda$show$5(Landroid/content/Context;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;[I[ILandroid/view/View;)V

    return-void
.end method
