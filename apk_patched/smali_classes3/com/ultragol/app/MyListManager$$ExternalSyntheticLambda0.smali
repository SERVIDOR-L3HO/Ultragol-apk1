.class public final synthetic Lcom/ultragol/app/MyListManager$$ExternalSyntheticLambda0;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Ljava/util/function/Predicate;


# instance fields
.field public final synthetic f$0:Lcom/ultragol/app/models/ContentItem;


# direct methods
.method public synthetic constructor <init>(Lcom/ultragol/app/models/ContentItem;)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/ultragol/app/MyListManager$$ExternalSyntheticLambda0;->f$0:Lcom/ultragol/app/models/ContentItem;

    return-void
.end method


# virtual methods
.method public final test(Ljava/lang/Object;)Z
    .locals 1

    iget-object v0, p0, Lcom/ultragol/app/MyListManager$$ExternalSyntheticLambda0;->f$0:Lcom/ultragol/app/models/ContentItem;

    check-cast p1, Lcom/ultragol/app/models/ContentItem;

    invoke-static {v0, p1}, Lcom/ultragol/app/MyListManager;->lambda$remove$0(Lcom/ultragol/app/models/ContentItem;Lcom/ultragol/app/models/ContentItem;)Z

    move-result p1

    return p1
.end method
