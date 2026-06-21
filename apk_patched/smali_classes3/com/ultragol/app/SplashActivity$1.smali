.class Lcom/ultragol/app/SplashActivity$1;
.super Ljava/lang/Object;
.source "SplashActivity.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/ultragol/app/SplashActivity;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/ultragol/app/SplashActivity;


# direct methods
.method constructor <init>(Lcom/ultragol/app/SplashActivity;)V
    .locals 0
    .param p1, "this$0"    # Lcom/ultragol/app/SplashActivity;
    .annotation system Ldalvik/annotation/MethodParameters;
        accessFlags = {
            0x8010
        }
        names = {
            null
        }
    .end annotation

    .line 18
    iput-object p1, p0, Lcom/ultragol/app/SplashActivity$1;->this$0:Lcom/ultragol/app/SplashActivity;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 3

    .line 21
    iget-object v0, p0, Lcom/ultragol/app/SplashActivity$1;->this$0:Lcom/ultragol/app/SplashActivity;

    invoke-static {v0}, Lcom/ultragol/app/SplashActivity;->access$000(Lcom/ultragol/app/SplashActivity;)V

    .line 22
    iget-object v0, p0, Lcom/ultragol/app/SplashActivity$1;->this$0:Lcom/ultragol/app/SplashActivity;

    invoke-static {v0}, Lcom/ultragol/app/SplashActivity;->access$100(Lcom/ultragol/app/SplashActivity;)Landroid/os/Handler;

    move-result-object v0

    const-wide/16 v1, 0x15e

    invoke-virtual {v0, p0, v1, v2}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    .line 23
    return-void
.end method
