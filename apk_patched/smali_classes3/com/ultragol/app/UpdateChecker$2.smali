.class Lcom/ultragol/app/UpdateChecker$2;
.super Ljava/lang/Object;
.implements Ljava/lang/Runnable;

.field final synthetic val$activity:Landroidx/appcompat/app/AppCompatActivity;
.field final synthetic val$versionName:Ljava/lang/String;
.field final synthetic val$changelog:Ljava/lang/String;
.field final synthetic val$dlUrl:Ljava/lang/String;
.field final synthetic val$force:Z

.method constructor <init>(Landroidx/appcompat/app/AppCompatActivity;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    iput-object p1, p0, Lcom/ultragol/app/UpdateChecker$2;->val$activity:Landroidx/appcompat/app/AppCompatActivity;
    iput-object p2, p0, Lcom/ultragol/app/UpdateChecker$2;->val$versionName:Ljava/lang/String;
    iput-object p3, p0, Lcom/ultragol/app/UpdateChecker$2;->val$changelog:Ljava/lang/String;
    iput-object p4, p0, Lcom/ultragol/app/UpdateChecker$2;->val$dlUrl:Ljava/lang/String;
    iput-boolean p5, p0, Lcom/ultragol/app/UpdateChecker$2;->val$force:Z

    return-void
.end method

.method public run()V
    .locals 8

    iget-object v0, p0, Lcom/ultragol/app/UpdateChecker$2;->val$activity:Landroidx/appcompat/app/AppCompatActivity;

    invoke-virtual {v0}, Landroidx/appcompat/app/AppCompatActivity;->isFinishing()Z
    move-result v1
    if-nez v1, :end

    invoke-virtual {v0}, Landroidx/appcompat/app/AppCompatActivity;->isDestroyed()Z
    move-result v1
    if-nez v1, :end

    iget-object v2, p0, Lcom/ultragol/app/UpdateChecker$2;->val$versionName:Ljava/lang/String;
    iget-object v3, p0, Lcom/ultragol/app/UpdateChecker$2;->val$changelog:Ljava/lang/String;

    new-instance v4, Ljava/lang/StringBuilder;
    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "Version "
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    invoke-virtual {v4, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    const-string v5, " disponible"
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/String;->isEmpty()Z
    move-result v5
    if-nez v5, :no_changelog

    const-string v5, "\n\n"
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    invoke-virtual {v4, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    :no_changelog
    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
    move-result-object v4

    iget-boolean v5, p0, Lcom/ultragol/app/UpdateChecker$2;->val$force:Z
    iget-object v6, p0, Lcom/ultragol/app/UpdateChecker$2;->val$dlUrl:Ljava/lang/String;

    new-instance v1, Landroidx/appcompat/app/AlertDialog$Builder;
    invoke-direct {v1, v0}, Landroidx/appcompat/app/AlertDialog$Builder;-><init>(Landroid/content/Context;)V

    const-string v7, "Actualizacion disponible"
    invoke-virtual {v1, v7}, Landroidx/appcompat/app/AlertDialog$Builder;->setTitle(Ljava/lang/CharSequence;)Landroidx/appcompat/app/AlertDialog$Builder;

    invoke-virtual {v1, v4}, Landroidx/appcompat/app/AlertDialog$Builder;->setMessage(Ljava/lang/CharSequence;)Landroidx/appcompat/app/AlertDialog$Builder;

    new-instance v7, Lcom/ultragol/app/UpdateChecker$3;
    invoke-direct {v7, v0, v6, v5}, Lcom/ultragol/app/UpdateChecker$3;-><init>(Landroidx/appcompat/app/AppCompatActivity;Ljava/lang/String;Z)V

    const-string v4, "Descargar"
    invoke-virtual {v1, v4, v7}, Landroidx/appcompat/app/AlertDialog$Builder;->setPositiveButton(Ljava/lang/CharSequence;Landroid/content/DialogInterface$OnClickListener;)Landroidx/appcompat/app/AlertDialog$Builder;

    if-nez v5, :skip_negative

    new-instance v7, Lcom/ultragol/app/UpdateChecker$4;
    invoke-direct {v7, v0}, Lcom/ultragol/app/UpdateChecker$4;-><init>(Landroidx/appcompat/app/AppCompatActivity;)V

    const-string v4, "Mas tarde"
    invoke-virtual {v1, v4, v7}, Landroidx/appcompat/app/AlertDialog$Builder;->setNegativeButton(Ljava/lang/CharSequence;Landroid/content/DialogInterface$OnClickListener;)Landroidx/appcompat/app/AlertDialog$Builder;

    :skip_negative
    if-nez v5, :not_cancelable
    const/4 v4, 0x1
    goto :set_cancelable
    :not_cancelable
    const/4 v4, 0x0
    :set_cancelable
    invoke-virtual {v1, v4}, Landroidx/appcompat/app/AlertDialog$Builder;->setCancelable(Z)Landroidx/appcompat/app/AlertDialog$Builder;

    invoke-virtual {v1}, Landroidx/appcompat/app/AlertDialog$Builder;->show()Landroidx/appcompat/app/AlertDialog;

    :end
    return-void
.end method
