.class public final synthetic Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda0;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Ljava/lang/Runnable;


# instance fields
.field public final synthetic f$0:Lcom/ultragol/app/DetailActivity;

.field public final synthetic f$1:Landroidx/recyclerview/widget/RecyclerView;

.field public final synthetic f$2:Ljava/util/List;


# direct methods
.method public synthetic constructor <init>(Lcom/ultragol/app/DetailActivity;Landroidx/recyclerview/widget/RecyclerView;Ljava/util/List;)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda0;->f$0:Lcom/ultragol/app/DetailActivity;

    iput-object p2, p0, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda0;->f$1:Landroidx/recyclerview/widget/RecyclerView;

    iput-object p3, p0, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda0;->f$2:Ljava/util/List;

    return-void
.end method


# virtual methods
.method public final run()V
    .locals 3

    iget-object v0, p0, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda0;->f$0:Lcom/ultragol/app/DetailActivity;

    iget-object v1, p0, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda0;->f$1:Landroidx/recyclerview/widget/RecyclerView;

    iget-object v2, p0, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda0;->f$2:Ljava/util/List;

    invoke-virtual {v0, v1, v2}, Lcom/ultragol/app/DetailActivity;->lambda$loadRelated$4$com-ultragol-app-DetailActivity(Landroidx/recyclerview/widget/RecyclerView;Ljava/util/List;)V

    return-void
.end method
