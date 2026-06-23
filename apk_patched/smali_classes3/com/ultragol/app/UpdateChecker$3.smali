.class Lcom/ultragol/app/UpdateChecker$3;
.super Ljava/lang/Object;
.implements Landroid/content/DialogInterface$OnClickListener;

.field final synthetic val$activity:Landroidx/appcompat/app/AppCompatActivity;
.field final synthetic val$dlUrl:Ljava/lang/String;
.field final synthetic val$force:Z

.method constructor <init>(Landroidx/appcompat/app/AppCompatActivity;Ljava/lang/String;Z)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    iput-object p1, p0, Lcom/ultragol/app/UpdateChecker$3;->val$activity:Landroidx/appcompat/app/AppCompatActivity;
    iput-object p2, p0, Lcom/ultragol/app/UpdateChecker$3;->val$dlUrl:Ljava/lang/String;
    iput-boolean p3, p0, Lcom/ultragol/app/UpdateChecker$3;->val$force:Z

    return-void
.end method

.method public onClick(Landroid/content/DialogInterface;I)V
    .locals 4

    invoke-interface {p1}, Landroid/content/DialogInterface;->dismiss()V

    iget-object v0, p0, Lcom/ultragol/app/UpdateChecker$3;->val$activity:Landroidx/appcompat/app/AppCompatActivity;
    iget-object v1, p0, Lcom/ultragol/app/UpdateChecker$3;->val$dlUrl:Ljava/lang/String;

    invoke-virtual {v1}, Ljava/lang/String;->isEmpty()Z
    move-result v2
    if-nez v2, :skip_dl

    :try_start_0
    new-instance v2, Landroid/content/Intent;
    const-string v3, "android.intent.action.VIEW"
    invoke-direct {v2, v3}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V
    invoke-static {v1}, Landroid/net/Uri;->parse(Ljava/lang/String;)Landroid/net/Uri;
    move-result-object v3
    invoke-virtual {v2, v3}, Landroid/content/Intent;->setData(Landroid/net/Uri;)Landroid/content/Intent;
    invoke-virtual {v0, v2}, Landroidx/appcompat/app/AppCompatActivity;->startActivity(Landroid/content/Intent;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :skip_dl

    :skip_dl
    iget-boolean v2, p0, Lcom/ultragol/app/UpdateChecker$3;->val$force:Z
    if-nez v2, :end

    invoke-virtual {v0}, Landroidx/appcompat/app/AppCompatActivity;->isFinishing()Z
    move-result v2
    if-nez v2, :end

    new-instance v2, Landroid/content/Intent;
    const-class v3, Lcom/ultragol/app/MainActivity;
    invoke-direct {v2, v0, v3}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V
    invoke-virtual {v0, v2}, Landroidx/appcompat/app/AppCompatActivity;->startActivity(Landroid/content/Intent;)V

    const/high16 v2, 0x10a0000
    const v3, 0x10a0001
    invoke-virtual {v0, v2, v3}, Landroidx/appcompat/app/AppCompatActivity;->overridePendingTransition(II)V

    invoke-virtual {v0}, Landroidx/appcompat/app/AppCompatActivity;->finish()V

    :end
    return-void
.end method
