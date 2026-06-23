.class Lcom/ultragol/app/UpdateChecker$5;
.super Ljava/lang/Object;
.implements Ljava/lang/Runnable;

.field final synthetic val$activity:Landroidx/appcompat/app/AppCompatActivity;

.method constructor <init>(Landroidx/appcompat/app/AppCompatActivity;)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    iput-object p1, p0, Lcom/ultragol/app/UpdateChecker$5;->val$activity:Landroidx/appcompat/app/AppCompatActivity;
    return-void
.end method

.method public run()V
    .locals 3

    iget-object v0, p0, Lcom/ultragol/app/UpdateChecker$5;->val$activity:Landroidx/appcompat/app/AppCompatActivity;

    invoke-virtual {v0}, Landroidx/appcompat/app/AppCompatActivity;->isFinishing()Z
    move-result v1
    if-nez v1, :end

    new-instance v1, Landroid/content/Intent;
    const-class v2, Lcom/ultragol/app/MainActivity;
    invoke-direct {v1, v0, v2}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V
    invoke-virtual {v0, v1}, Landroidx/appcompat/app/AppCompatActivity;->startActivity(Landroid/content/Intent;)V

    const/high16 v1, 0x10a0000
    const v2, 0x10a0001
    invoke-virtual {v0, v1, v2}, Landroidx/appcompat/app/AppCompatActivity;->overridePendingTransition(II)V

    invoke-virtual {v0}, Landroidx/appcompat/app/AppCompatActivity;->finish()V

    :end
    return-void
.end method
