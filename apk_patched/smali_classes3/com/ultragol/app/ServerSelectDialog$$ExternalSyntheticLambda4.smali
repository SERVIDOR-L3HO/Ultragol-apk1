.class public final synthetic Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda4;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Landroid/view/View$OnClickListener;


# instance fields
.field public final synthetic f$0:Landroid/widget/TextView;

.field public final synthetic f$1:Landroid/widget/TextView;

.field public final synthetic f$2:Landroid/widget/TextView;

.field public final synthetic f$3:Landroid/widget/LinearLayout;

.field public final synthetic f$4:Landroid/content/Context;

.field public final synthetic f$5:Landroid/app/Dialog;

.field public final synthetic f$6:Lcom/ultragol/app/models/ContentItem;

.field public final synthetic f$7:Lcom/ultragol/app/network/StreamingApi$ServerData;


# direct methods
.method public synthetic constructor <init>(Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/LinearLayout;Landroid/content/Context;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;Lcom/ultragol/app/network/StreamingApi$ServerData;)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda4;->f$0:Landroid/widget/TextView;

    iput-object p2, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda4;->f$1:Landroid/widget/TextView;

    iput-object p3, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda4;->f$2:Landroid/widget/TextView;

    iput-object p4, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda4;->f$3:Landroid/widget/LinearLayout;

    iput-object p5, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda4;->f$4:Landroid/content/Context;

    iput-object p6, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda4;->f$5:Landroid/app/Dialog;

    iput-object p7, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda4;->f$6:Lcom/ultragol/app/models/ContentItem;

    iput-object p8, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda4;->f$7:Lcom/ultragol/app/network/StreamingApi$ServerData;

    return-void
.end method


# virtual methods
.method public final onClick(Landroid/view/View;)V
    .locals 9

    iget-object v0, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda4;->f$0:Landroid/widget/TextView;

    iget-object v1, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda4;->f$1:Landroid/widget/TextView;

    iget-object v2, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda4;->f$2:Landroid/widget/TextView;

    iget-object v3, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda4;->f$3:Landroid/widget/LinearLayout;

    iget-object v4, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda4;->f$4:Landroid/content/Context;

    iget-object v5, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda4;->f$5:Landroid/app/Dialog;

    iget-object v6, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda4;->f$6:Lcom/ultragol/app/models/ContentItem;

    iget-object v7, p0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda4;->f$7:Lcom/ultragol/app/network/StreamingApi$ServerData;

    move-object v8, p1

    invoke-static/range {v0 .. v8}, Lcom/ultragol/app/ServerSelectDialog;->lambda$setupTabs$11(Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/LinearLayout;Landroid/content/Context;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;Lcom/ultragol/app/network/StreamingApi$ServerData;Landroid/view/View;)V

    return-void
.end method
