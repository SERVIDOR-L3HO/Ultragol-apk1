.class public final synthetic Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda13;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Landroid/view/View$OnClickListener;


# instance fields
.field public final synthetic f$0:Landroid/content/Context;

.field public final synthetic f$1:Ljava/util/List;

.field public final synthetic f$2:I

.field public final synthetic f$3:Lcom/ultragol/app/models/ContentItem;

.field public final synthetic f$4:Landroid/app/Dialog;


# direct methods
.method public synthetic constructor <init>(Landroid/content/Context;Ljava/util/List;ILcom/ultragol/app/models/ContentItem;Landroid/app/Dialog;)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda13;->f$0:Landroid/content/Context;

    iput-object p2, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda13;->f$1:Ljava/util/List;

    iput p3, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda13;->f$2:I

    iput-object p4, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda13;->f$3:Lcom/ultragol/app/models/ContentItem;

    iput-object p5, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda13;->f$4:Landroid/app/Dialog;

    return-void
.end method


# virtual methods
.method public final onClick(Landroid/view/View;)V
    .locals 6

    iget-object v0, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda13;->f$0:Landroid/content/Context;

    iget-object v1, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda13;->f$1:Ljava/util/List;

    iget v2, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda13;->f$2:I

    iget-object v3, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda13;->f$3:Lcom/ultragol/app/models/ContentItem;

    iget-object v4, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda13;->f$4:Landroid/app/Dialog;

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/ultragol/app/ServerSelectDialog;->lambda$addRows$12(Landroid/content/Context;Ljava/util/List;ILcom/ultragol/app/models/ContentItem;Landroid/app/Dialog;Landroid/view/View;)V

    return-void
.end method
