.class public final synthetic Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda1;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Ljava/lang/Runnable;


# instance fields
.field public final synthetic f$0:Lcom/ultragol/app/DetailActivity;

.field public final synthetic f$1:Landroid/os/Handler;

.field public final synthetic f$2:Landroidx/recyclerview/widget/RecyclerView;


# direct methods
.method public synthetic constructor <init>(Lcom/ultragol/app/DetailActivity;Landroid/os/Handler;Landroidx/recyclerview/widget/RecyclerView;)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda1;->f$0:Lcom/ultragol/app/DetailActivity;

    iput-object p2, p0, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda1;->f$1:Landroid/os/Handler;

    iput-object p3, p0, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda1;->f$2:Landroidx/recyclerview/widget/RecyclerView;

    return-void
.end method


# virtual methods
.method public final run()V
    .locals 3

    iget-object v0, p0, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda1;->f$0:Lcom/ultragol/app/DetailActivity;

    iget-object v1, p0, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda1;->f$1:Landroid/os/Handler;

    iget-object v2, p0, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda1;->f$2:Landroidx/recyclerview/widget/RecyclerView;

    invoke-virtual {v0, v1, v2}, Lcom/ultragol/app/DetailActivity;->lambda$loadRelated$5$com-ultragol-app-DetailActivity(Landroid/os/Handler;Landroidx/recyclerview/widget/RecyclerView;)V

    return-void
.end method
