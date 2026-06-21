.class public final synthetic Lcom/ultragol/app/SplashActivity$$ExternalSyntheticLambda3;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Ljava/lang/Runnable;


# instance fields
.field public final synthetic f$0:J

.field public final synthetic f$1:Landroid/view/View;


# direct methods
.method public synthetic constructor <init>(JLandroid/view/View;)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-wide p1, p0, Lcom/ultragol/app/SplashActivity$$ExternalSyntheticLambda3;->f$0:J

    iput-object p3, p0, Lcom/ultragol/app/SplashActivity$$ExternalSyntheticLambda3;->f$1:Landroid/view/View;

    return-void
.end method


# virtual methods
.method public final run()V
    .locals 3

    iget-wide v0, p0, Lcom/ultragol/app/SplashActivity$$ExternalSyntheticLambda3;->f$0:J

    iget-object v2, p0, Lcom/ultragol/app/SplashActivity$$ExternalSyntheticLambda3;->f$1:Landroid/view/View;

    invoke-static {v0, v1, v2}, Lcom/ultragol/app/SplashActivity;->lambda$animateFadeIn$3(JLandroid/view/View;)V

    return-void
.end method
