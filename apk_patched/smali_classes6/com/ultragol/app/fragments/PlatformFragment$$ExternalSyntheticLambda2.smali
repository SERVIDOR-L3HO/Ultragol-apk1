.class public final synthetic Lcom/ultragol/app/fragments/PlatformFragment$$ExternalSyntheticLambda2;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Ljava/lang/Runnable;


# instance fields
.field public final synthetic f$0:Lcom/ultragol/app/fragments/PlatformFragment;

.field public final synthetic f$1:Ljava/lang/String;

.field public final synthetic f$2:I

.field public final synthetic f$3:Ljava/util/List;

.field public final synthetic f$4:Lcom/ultragol/app/adapters/ContentGridAdapter;

.field public final synthetic f$5:Landroid/widget/ProgressBar;


# direct methods
.method public synthetic constructor <init>(Lcom/ultragol/app/fragments/PlatformFragment;Ljava/lang/String;ILjava/util/List;Lcom/ultragol/app/adapters/ContentGridAdapter;Landroid/widget/ProgressBar;)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/ultragol/app/fragments/PlatformFragment$$ExternalSyntheticLambda2;->f$0:Lcom/ultragol/app/fragments/PlatformFragment;

    iput-object p2, p0, Lcom/ultragol/app/fragments/PlatformFragment$$ExternalSyntheticLambda2;->f$1:Ljava/lang/String;

    iput p3, p0, Lcom/ultragol/app/fragments/PlatformFragment$$ExternalSyntheticLambda2;->f$2:I

    iput-object p4, p0, Lcom/ultragol/app/fragments/PlatformFragment$$ExternalSyntheticLambda2;->f$3:Ljava/util/List;

    iput-object p5, p0, Lcom/ultragol/app/fragments/PlatformFragment$$ExternalSyntheticLambda2;->f$4:Lcom/ultragol/app/adapters/ContentGridAdapter;

    iput-object p6, p0, Lcom/ultragol/app/fragments/PlatformFragment$$ExternalSyntheticLambda2;->f$5:Landroid/widget/ProgressBar;

    return-void
.end method


# virtual methods
.method public final run()V
    .locals 6

    iget-object v0, p0, Lcom/ultragol/app/fragments/PlatformFragment$$ExternalSyntheticLambda2;->f$0:Lcom/ultragol/app/fragments/PlatformFragment;

    iget-object v1, p0, Lcom/ultragol/app/fragments/PlatformFragment$$ExternalSyntheticLambda2;->f$1:Ljava/lang/String;

    iget v2, p0, Lcom/ultragol/app/fragments/PlatformFragment$$ExternalSyntheticLambda2;->f$2:I

    iget-object v3, p0, Lcom/ultragol/app/fragments/PlatformFragment$$ExternalSyntheticLambda2;->f$3:Ljava/util/List;

    iget-object v4, p0, Lcom/ultragol/app/fragments/PlatformFragment$$ExternalSyntheticLambda2;->f$4:Lcom/ultragol/app/adapters/ContentGridAdapter;

    iget-object v5, p0, Lcom/ultragol/app/fragments/PlatformFragment$$ExternalSyntheticLambda2;->f$5:Landroid/widget/ProgressBar;

    invoke-virtual/range {v0 .. v5}, Lcom/ultragol/app/fragments/PlatformFragment;->lambda$onViewCreated$2$com-ultragol-app-fragments-PlatformFragment(Ljava/lang/String;ILjava/util/List;Lcom/ultragol/app/adapters/ContentGridAdapter;Landroid/widget/ProgressBar;)V

    return-void
.end method
