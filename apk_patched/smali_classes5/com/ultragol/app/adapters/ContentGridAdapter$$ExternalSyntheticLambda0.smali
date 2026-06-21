.class public final synthetic Lcom/ultragol/app/adapters/ContentGridAdapter$$ExternalSyntheticLambda0;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Landroid/view/View$OnClickListener;


# instance fields
.field public final synthetic f$0:Lcom/ultragol/app/adapters/ContentGridAdapter;

.field public final synthetic f$1:Lcom/ultragol/app/models/ContentItem;


# direct methods
.method public synthetic constructor <init>(Lcom/ultragol/app/adapters/ContentGridAdapter;Lcom/ultragol/app/models/ContentItem;)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/ultragol/app/adapters/ContentGridAdapter$$ExternalSyntheticLambda0;->f$0:Lcom/ultragol/app/adapters/ContentGridAdapter;

    iput-object p2, p0, Lcom/ultragol/app/adapters/ContentGridAdapter$$ExternalSyntheticLambda0;->f$1:Lcom/ultragol/app/models/ContentItem;

    return-void
.end method


# virtual methods
.method public final onClick(Landroid/view/View;)V
    .locals 2

    iget-object v0, p0, Lcom/ultragol/app/adapters/ContentGridAdapter$$ExternalSyntheticLambda0;->f$0:Lcom/ultragol/app/adapters/ContentGridAdapter;

    iget-object v1, p0, Lcom/ultragol/app/adapters/ContentGridAdapter$$ExternalSyntheticLambda0;->f$1:Lcom/ultragol/app/models/ContentItem;

    invoke-virtual {v0, v1, p1}, Lcom/ultragol/app/adapters/ContentGridAdapter;->lambda$onBindViewHolder$0$com-ultragol-app-adapters-ContentGridAdapter(Lcom/ultragol/app/models/ContentItem;Landroid/view/View;)V

    return-void
.end method
