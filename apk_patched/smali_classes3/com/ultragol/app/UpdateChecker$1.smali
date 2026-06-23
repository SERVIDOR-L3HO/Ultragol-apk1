.class Lcom/ultragol/app/UpdateChecker$1;
.super Ljava/lang/Object;
.implements Ljava/lang/Runnable;

.field final synthetic val$activity:Landroidx/appcompat/app/AppCompatActivity;

.method constructor <init>(Landroidx/appcompat/app/AppCompatActivity;)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    iput-object p1, p0, Lcom/ultragol/app/UpdateChecker$1;->val$activity:Landroidx/appcompat/app/AppCompatActivity;
    return-void
.end method

.method public run()V
    .locals 12

    :try_start_0

    # v0=URL, v1=conn, v2=temp, v3=BufferedReader, v4=InputStreamReader/StringBuilder, v5=response
    new-instance v0, Ljava/net/URL;
    const-string v2, "https://da8cd50c-b6a4-475c-8783-70b87f2e70be-00-3s6o17dujacbo.spock.replit.dev/ultragol1/version"
    invoke-direct {v0, v2}, Ljava/net/URL;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0}, Ljava/net/URL;->openConnection()Ljava/net/URLConnection;
    move-result-object v1
    check-cast v1, Ljava/net/HttpURLConnection;

    const-string v2, "GET"
    invoke-virtual {v1, v2}, Ljava/net/HttpURLConnection;->setRequestMethod(Ljava/lang/String;)V

    const/16 v2, 0x1388
    invoke-virtual {v1, v2}, Ljava/net/HttpURLConnection;->setConnectTimeout(I)V
    invoke-virtual {v1, v2}, Ljava/net/HttpURLConnection;->setReadTimeout(I)V

    invoke-virtual {v1}, Ljava/net/HttpURLConnection;->getResponseCode()I
    move-result v2
    const/16 v3, 0xc8
    if-ne v2, v3, :cond_go_main

    new-instance v3, Ljava/io/BufferedReader;
    new-instance v4, Ljava/io/InputStreamReader;
    invoke-virtual {v1}, Ljava/net/HttpURLConnection;->getInputStream()Ljava/io/InputStream;
    move-result-object v5
    const-string v2, "UTF-8"
    invoke-direct {v4, v5, v2}, Ljava/io/InputStreamReader;-><init>(Ljava/io/InputStream;Ljava/lang/String;)V
    invoke-direct {v3, v4}, Ljava/io/BufferedReader;-><init>(Ljava/io/Reader;)V

    new-instance v4, Ljava/lang/StringBuilder;
    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    :goto_0
    invoke-virtual {v3}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;
    move-result-object v5
    if-eqz v5, :done_read
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    goto :goto_0

    :done_read
    invoke-virtual {v3}, Ljava/io/BufferedReader;->close()V

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
    move-result-object v5

    # v6=JSONObject, v7=serverVersionCode
    new-instance v6, Lorg/json/JSONObject;
    invoke-direct {v6, v5}, Lorg/json/JSONObject;-><init>(Ljava/lang/String;)V

    const-string v2, "versionCode"
    const/4 v3, 0x0
    invoke-virtual {v6, v2, v3}, Lorg/json/JSONObject;->optInt(Ljava/lang/String;I)I
    move-result v7

    # v1=activity, v8=PackageManager/PackageInfo(int currentVC)
    iget-object v1, p0, Lcom/ultragol/app/UpdateChecker$1;->val$activity:Landroidx/appcompat/app/AppCompatActivity;
    invoke-virtual {v1}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;
    move-result-object v8
    invoke-virtual {v1}, Landroid/content/Context;->getPackageName()Ljava/lang/String;
    move-result-object v2
    const/4 v3, 0x0
    invoke-virtual {v8, v2, v3}, Landroid/content/pm/PackageManager;->getPackageInfo(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;
    move-result-object v8
    iget v8, v8, Landroid/content/pm/PackageInfo;->versionCode:I

    # if serverVC <= currentVC, go to main (no update)
    if-le v7, v8, :cond_go_main

    # Gather data into contiguous v1-v5 for invoke-direct/range
    # v1 = activity (already set)
    const-string v9, ""

    const-string v2, "versionName"
    invoke-virtual {v6, v2, v9}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    move-result-object v2

    const-string v3, "changelog"
    invoke-virtual {v6, v3, v9}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    move-result-object v3

    const-string v4, "downloadUrl"
    invoke-virtual {v6, v4, v9}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    move-result-object v4

    const-string v9, "forceUpdate"
    const/4 v5, 0x0
    invoke-virtual {v6, v9, v5}, Lorg/json/JSONObject;->optBoolean(Ljava/lang/String;Z)Z
    move-result v5

    # v0=new UpdateChecker$2, v1=activity, v2=versionName, v3=changelog, v4=dlUrl, v5=force
    new-instance v0, Lcom/ultragol/app/UpdateChecker$2;
    invoke-direct/range {v0 .. v5}, Lcom/ultragol/app/UpdateChecker$2;-><init>(Landroidx/appcompat/app/AppCompatActivity;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V

    new-instance v6, Landroid/os/Handler;
    invoke-static {}, Landroid/os/Looper;->getMainLooper()Landroid/os/Looper;
    move-result-object v7
    invoke-direct {v6, v7}, Landroid/os/Handler;-><init>(Landroid/os/Looper;)V
    invoke-virtual {v6, v0}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :cond_go_main

    return-void

    :cond_go_main
    iget-object v1, p0, Lcom/ultragol/app/UpdateChecker$1;->val$activity:Landroidx/appcompat/app/AppCompatActivity;

    new-instance v6, Landroid/os/Handler;
    invoke-static {}, Landroid/os/Looper;->getMainLooper()Landroid/os/Looper;
    move-result-object v7
    invoke-direct {v6, v7}, Landroid/os/Handler;-><init>(Landroid/os/Looper;)V

    new-instance v0, Lcom/ultragol/app/UpdateChecker$5;
    invoke-direct {v0, v1}, Lcom/ultragol/app/UpdateChecker$5;-><init>(Landroidx/appcompat/app/AppCompatActivity;)V
    invoke-virtual {v6, v0}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    return-void
.end method
