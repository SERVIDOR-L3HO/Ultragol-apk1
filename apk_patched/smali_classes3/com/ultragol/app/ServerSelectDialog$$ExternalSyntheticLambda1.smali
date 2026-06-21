.class public final synthetic Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda1;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Landroid/view/View$OnClickListener;


# instance fields
.field public final synthetic f$0:[I

.field public final synthetic f$1:I

.field public final synthetic f$2:Landroid/widget/LinearLayout;

.field public final synthetic f$3:Ljava/util/List;

.field public final synthetic f$4:Landroid/content/Context;

.field public final synthetic f$5:Lcom/ultragol/app/models/ContentItem;

.field public final synthetic f$6:Landroid/app/Dialog;

.field public final synthetic f$7:Ljava/lang/String;


# direct methods
.method public synthetic constructor <init>([IILandroid/widget/LinearLayout;Ljava/util/List;Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;Landroid/app/Dialog;Ljava/lang/String;)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda1;->f$0:[I

    iput p2, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda1;->f$1:I

    iput-object p3, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda1;->f$2:Landroid/widget/LinearLayout;

    iput-object p4, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda1;->f$3:Ljava/util/List;

    iput-object p5, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda1;->f$4:Landroid/content/Context;

    iput-object p6, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda1;->f$5:Lcom/ultragol/app/models/ContentItem;

    iput-object p7, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda1;->f$6:Landroid/app/Dialog;

    iput-object p8, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda1;->f$7:Ljava/lang/String;

    return-void
.end method


# virtual methods
.method public final onClick(Landroid/view/View;)V
    .locals 9

    iget-object v0, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda1;->f$0:[I

    iget v1, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda1;->f$1:I

    iget-object v2, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda1;->f$2:Landroid/widget/LinearLayout;

    iget-object v3, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda1;->f$3:Ljava/util/List;

    iget-object v4, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda1;->f$4:Landroid/content/Context;

    iget-object v5, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda1;->f$5:Lcom/ultragol/app/models/ContentItem;

    iget-object v6, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda1;->f$6:Landroid/app/Dialog;

    iget-object v7, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda1;->f$7:Ljava/lang/String;

    move-object v8, p1

    invoke-static/range {v0 .. v8}, Lcom/ultragol/app/ServerSelectDialog;->lambda$addRows$13([IILandroid/widget/LinearLayout;Ljava/util/List;Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;Landroid/app/Dialog;Ljava/lang/String;Landroid/view/View;)V

    return-void
.end method
