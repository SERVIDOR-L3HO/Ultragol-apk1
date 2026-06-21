.class public final synthetic Lcom/ultragol/app/fragments/MoviesFragment$$ExternalSyntheticLambda2;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Ljava/lang/Runnable;


# instance fields
.field public final synthetic f$0:Lcom/ultragol/app/fragments/MoviesFragment;

.field public final synthetic f$1:Ljava/util/List;

.field public final synthetic f$2:Lcom/ultragol/app/adapters/ContentGridAdapter;

.field public final synthetic f$3:Landroid/widget/ProgressBar;


# direct methods
.method public synthetic constructor <init>(Lcom/ultragol/app/fragments/MoviesFragment;Ljava/util/List;Lcom/ultragol/app/adapters/ContentGridAdapter;Landroid/widget/ProgressBar;)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/ultragol/app/fragments/MoviesFragment$$ExternalSyntheticLambda2;->f$0:Lcom/ultragol/app/fragments/MoviesFragment;

    iput-object p2, p0, Lcom/ultragol/app/fragments/MoviesFragment$$ExternalSyntheticLambda2;->f$1:Ljava/util/List;

    iput-object p3, p0, Lcom/ultragol/app/fragments/MoviesFragment$$ExternalSyntheticLambda2;->f$2:Lcom/ultragol/app/adapters/ContentGridAdapter;

    iput-object p4, p0, Lcom/ultragol/app/fragments/MoviesFragment$$ExternalSyntheticLambda2;->f$3:Landroid/widget/ProgressBar;

    return-void
.end method


# virtual methods
.method public final run()V
    .locals 4

    iget-object v0, p0, Lcom/ultragol/app/fragments/MoviesFragment$$ExternalSyntheticLambda2;->f$0:Lcom/ultragol/app/fragments/MoviesFragment;

    iget-object v1, p0, Lcom/ultragol/app/fragments/MoviesFragment$$ExternalSyntheticLambda2;->f$1:Ljava/util/List;

    iget-object v2, p0, Lcom/ultragol/app/fragments/MoviesFragment$$ExternalSyntheticLambda2;->f$2:Lcom/ultragol/app/adapters/ContentGridAdapter;

    iget-object v3, p0, Lcom/ultragol/app/fragments/MoviesFragment$$ExternalSyntheticLambda2;->f$3:Landroid/widget/ProgressBar;

    invoke-virtual {v0, v1, v2, v3}, Lcom/ultragol/app/fragments/MoviesFragment;->lambda$onViewCreated$2$com-ultragol-app-fragments-MoviesFragment(Ljava/util/List;Lcom/ultragol/app/adapters/ContentGridAdapter;Landroid/widget/ProgressBar;)V

    return-void
.end method
