.class Lcom/ultragol/app/fragments/HomeFragment$2;
.super Ljava/lang/Object;
.source "HomeFragment.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/ultragol/app/fragments/HomeFragment;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/ultragol/app/fragments/HomeFragment;


# direct methods
.method constructor <init>(Lcom/ultragol/app/fragments/HomeFragment;)V
    .locals 0
    .param p1, "this$0"    # Lcom/ultragol/app/fragments/HomeFragment;
    .annotation system Ldalvik/annotation/MethodParameters;
        accessFlags = {
            0x8010
        }
        names = {
            null
        }
    .end annotation

    .line 80
    iput-object p1, p0, Lcom/ultragol/app/fragments/HomeFragment$2;->this$0:Lcom/ultragol/app/fragments/HomeFragment;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 4

    .line 82
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment$2;->this$0:Lcom/ultragol/app/fragments/HomeFragment;

    invoke-static {v0}, Lcom/ultragol/app/fragments/HomeFragment;->access$200(Lcom/ultragol/app/fragments/HomeFragment;)Landroidx/viewpager2/widget/ViewPager2;

    move-result-object v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment$2;->this$0:Lcom/ultragol/app/fragments/HomeFragment;

    invoke-static {v0}, Lcom/ultragol/app/fragments/HomeFragment;->access$300(Lcom/ultragol/app/fragments/HomeFragment;)I

    move-result v0

    if-lez v0, :cond_0

    .line 83
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment$2;->this$0:Lcom/ultragol/app/fragments/HomeFragment;

    invoke-static {v0}, Lcom/ultragol/app/fragments/HomeFragment;->access$000(Lcom/ultragol/app/fragments/HomeFragment;)I

    move-result v1

    const/4 v2, 0x1

    add-int/2addr v1, v2

    iget-object v3, p0, Lcom/ultragol/app/fragments/HomeFragment$2;->this$0:Lcom/ultragol/app/fragments/HomeFragment;

    invoke-static {v3}, Lcom/ultragol/app/fragments/HomeFragment;->access$300(Lcom/ultragol/app/fragments/HomeFragment;)I

    move-result v3

    rem-int/2addr v1, v3

    invoke-static {v0, v1}, Lcom/ultragol/app/fragments/HomeFragment;->access$002(Lcom/ultragol/app/fragments/HomeFragment;I)I

    .line 84
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment$2;->this$0:Lcom/ultragol/app/fragments/HomeFragment;

    invoke-static {v0}, Lcom/ultragol/app/fragments/HomeFragment;->access$200(Lcom/ultragol/app/fragments/HomeFragment;)Landroidx/viewpager2/widget/ViewPager2;

    move-result-object v0

    iget-object v1, p0, Lcom/ultragol/app/fragments/HomeFragment$2;->this$0:Lcom/ultragol/app/fragments/HomeFragment;

    invoke-static {v1}, Lcom/ultragol/app/fragments/HomeFragment;->access$000(Lcom/ultragol/app/fragments/HomeFragment;)I

    move-result v1

    invoke-virtual {v0, v1, v2}, Landroidx/viewpager2/widget/ViewPager2;->setCurrentItem(IZ)V

    .line 85
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment$2;->this$0:Lcom/ultragol/app/fragments/HomeFragment;

    invoke-static {v0}, Lcom/ultragol/app/fragments/HomeFragment;->access$400(Lcom/ultragol/app/fragments/HomeFragment;)Landroid/os/Handler;

    move-result-object v0

    const-wide/16 v1, 0x1388

    invoke-virtual {v0, p0, v1, v2}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    .line 87
    :cond_0
    return-void
.end method
