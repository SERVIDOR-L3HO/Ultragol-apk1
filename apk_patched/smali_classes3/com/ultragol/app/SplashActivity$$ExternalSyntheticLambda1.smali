.class public final synthetic Lcom/ultragol/app/SplashActivity$$ExternalSyntheticLambda1;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Ljava/lang/Runnable;


# instance fields
.field public final synthetic f$0:Lcom/ultragol/app/SplashActivity;

.field public final synthetic f$1:Landroid/view/View;


# direct methods
.method public synthetic constructor <init>(Lcom/ultragol/app/SplashActivity;Landroid/view/View;)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/ultragol/app/SplashActivity$$ExternalSyntheticLambda1;->f$0:Lcom/ultragol/app/SplashActivity;

    iput-object p2, p0, Lcom/ultragol/app/SplashActivity$$ExternalSyntheticLambda1;->f$1:Landroid/view/View;

    return-void
.end method


# virtual methods
.method public final run()V
    .locals 2

    iget-object v0, p0, Lcom/ultragol/app/SplashActivity$$ExternalSyntheticLambda1;->f$0:Lcom/ultragol/app/SplashActivity;

    iget-object v1, p0, Lcom/ultragol/app/SplashActivity$$ExternalSyntheticLambda1;->f$1:Landroid/view/View;

    invoke-virtual {v0, v1}, Lcom/ultragol/app/SplashActivity;->lambda$onCreate$1$com-ultragol-app-SplashActivity(Landroid/view/View;)V

    return-void
.end method
