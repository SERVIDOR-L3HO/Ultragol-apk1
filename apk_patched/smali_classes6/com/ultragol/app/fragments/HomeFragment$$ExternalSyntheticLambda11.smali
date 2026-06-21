.class public final synthetic Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda11;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Ljava/lang/Runnable;


# instance fields
.field public final synthetic f$0:Lcom/ultragol/app/fragments/HomeFragment;

.field public final synthetic f$1:Landroid/os/Handler;


# direct methods
.method public synthetic constructor <init>(Lcom/ultragol/app/fragments/HomeFragment;Landroid/os/Handler;)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda11;->f$0:Lcom/ultragol/app/fragments/HomeFragment;

    iput-object p2, p0, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda11;->f$1:Landroid/os/Handler;

    return-void
.end method


# virtual methods
.method public final run()V
    .locals 2

    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda11;->f$0:Lcom/ultragol/app/fragments/HomeFragment;

    iget-object v1, p0, Lcom/ultragol/app/fragments/HomeFragment$$ExternalSyntheticLambda11;->f$1:Landroid/os/Handler;

    invoke-virtual {v0, v1}, Lcom/ultragol/app/fragments/HomeFragment;->lambda$loadAll$7$com-ultragol-app-fragments-HomeFragment(Landroid/os/Handler;)V

    return-void
.end method
