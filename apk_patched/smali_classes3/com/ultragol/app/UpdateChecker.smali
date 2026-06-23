.class public Lcom/ultragol/app/UpdateChecker;
.super Ljava/lang/Object;

.method public constructor <init>()V
    .locals 0
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
.end method

.method public static check(Landroidx/appcompat/app/AppCompatActivity;)V
    .locals 2
    .param p0, "activity"

    new-instance v0, Ljava/lang/Thread;
    new-instance v1, Lcom/ultragol/app/UpdateChecker$1;
    invoke-direct {v1, p0}, Lcom/ultragol/app/UpdateChecker$1;-><init>(Landroidx/appcompat/app/AppCompatActivity;)V
    invoke-direct {v0, v1}, Ljava/lang/Thread;-><init>(Ljava/lang/Runnable;)V
    invoke-virtual {v0}, Ljava/lang/Thread;->start()V

    return-void
.end method
