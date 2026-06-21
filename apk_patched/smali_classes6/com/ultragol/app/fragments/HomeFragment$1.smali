.class Lcom/ultragol/app/fragments/HomeFragment$1;
.super Landroidx/viewpager2/widget/ViewPager2$OnPageChangeCallback;
.source "HomeFragment.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/ultragol/app/fragments/HomeFragment;->setupHero(Landroid/view/View;)V
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

    .line 74
    iput-object p1, p0, Lcom/ultragol/app/fragments/HomeFragment$1;->this$0:Lcom/ultragol/app/fragments/HomeFragment;

    invoke-direct {p0}, Landroidx/viewpager2/widget/ViewPager2$OnPageChangeCallback;-><init>()V

    return-void
.end method


# virtual methods
.method public onPageSelected(I)V
    .locals 1
    .param p1, "p"    # I

    .line 75
    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment$1;->this$0:Lcom/ultragol/app/fragments/HomeFragment;

    invoke-static {v0, p1}, Lcom/ultragol/app/fragments/HomeFragment;->access$002(Lcom/ultragol/app/fragments/HomeFragment;I)I

    iget-object v0, p0, Lcom/ultragol/app/fragments/HomeFragment$1;->this$0:Lcom/ultragol/app/fragments/HomeFragment;

    invoke-static {v0, p1}, Lcom/ultragol/app/fragments/HomeFragment;->access$100(Lcom/ultragol/app/fragments/HomeFragment;I)V

    return-void
.end method
