.class public final synthetic Lcom/ultragol/app/UltragolApp$$ExternalSyntheticLambda0;
.super Ljava/lang/Object;
.source "D8$$SyntheticClass"

# interfaces
.implements Ljava/lang/Thread$UncaughtExceptionHandler;


# instance fields
.field public final synthetic f$0:Lcom/ultragol/app/UltragolApp;

.field public final synthetic f$1:Ljava/lang/Thread$UncaughtExceptionHandler;


# direct methods
.method public synthetic constructor <init>(Lcom/ultragol/app/UltragolApp;Ljava/lang/Thread$UncaughtExceptionHandler;)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/ultragol/app/UltragolApp$$ExternalSyntheticLambda0;->f$0:Lcom/ultragol/app/UltragolApp;

    iput-object p2, p0, Lcom/ultragol/app/UltragolApp$$ExternalSyntheticLambda0;->f$1:Ljava/lang/Thread$UncaughtExceptionHandler;

    return-void
.end method


# virtual methods
.method public final uncaughtException(Ljava/lang/Thread;Ljava/lang/Throwable;)V
    .locals 2

    iget-object v0, p0, Lcom/ultragol/app/UltragolApp$$ExternalSyntheticLambda0;->f$0:Lcom/ultragol/app/UltragolApp;

    iget-object v1, p0, Lcom/ultragol/app/UltragolApp$$ExternalSyntheticLambda0;->f$1:Ljava/lang/Thread$UncaughtExceptionHandler;

    invoke-virtual {v0, v1, p1, p2}, Lcom/ultragol/app/UltragolApp;->lambda$onCreate$0$com-ultragol-app-UltragolApp(Ljava/lang/Thread$UncaughtExceptionHandler;Ljava/lang/Thread;Ljava/lang/Throwable;)V

    return-void
.end method
