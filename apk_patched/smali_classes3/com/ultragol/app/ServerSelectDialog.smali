.class public Lcom/ultragol/app/ServerSelectDialog;
.super Ljava/lang/Object;
.source "ServerSelectDialog.java"


# direct methods
.method public constructor <init>()V
    .locals 0

    .line 19
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method private static addRows(Landroid/content/Context;Landroid/widget/LinearLayout;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;Ljava/util/List;I)V
    .locals 17
    .param p0, "ctx"    # Landroid/content/Context;
    .param p1, "cnt"    # Landroid/widget/LinearLayout;
    .param p2, "dialog"    # Landroid/app/Dialog;
    .param p3, "item"    # Lcom/ultragol/app/models/ContentItem;
    .param p5, "selectedIdx"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/content/Context;",
            "Landroid/widget/LinearLayout;",
            "Landroid/app/Dialog;",
            "Lcom/ultragol/app/models/ContentItem;",
            "Ljava/util/List<",
            "Lcom/ultragol/app/network/StreamingApi$Server;",
            ">;I)V"
        }
    .end annotation

    .line 161
    .local p4, "list":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/network/StreamingApi$Server;>;"
    move-object/from16 v9, p1

    move-object/from16 v10, p4

    if-eqz v9, :cond_3

    if-eqz v10, :cond_3

    invoke-interface/range {p4 .. p4}, Ljava/util/List;->isEmpty()Z

    move-result v0

    if-eqz v0, :cond_0

    move-object/from16 v14, p0

    move/from16 v13, p5

    goto :goto_2

    .line 162
    :cond_0
    filled-new-array/range {p5 .. p5}, [I

    move-result-object v1

    .line 163
    .local v1, "sel":[I
    const/4 v0, 0x0

    move v11, v0

    .local v11, "i":I
    :goto_0
    invoke-interface/range {p4 .. p4}, Ljava/util/List;->size()I

    move-result v0

    if-ge v11, v0, :cond_2

    .line 164
    move v2, v11

    .line 165
    .local v2, "idx":I
    invoke-interface {v10, v11}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v0

    move-object v12, v0

    check-cast v12, Lcom/ultragol/app/network/StreamingApi$Server;

    .line 166
    .local v12, "srv":Lcom/ultragol/app/network/StreamingApi$Server;
    move/from16 v13, p5

    if-ne v11, v13, :cond_1

    const/4 v0, 0x1

    goto :goto_1

    :cond_1
    const/4 v0, 0x0

    :goto_1
    move-object/from16 v14, p0

    invoke-static {v14, v12, v11, v0}, Lcom/ultragol/app/ServerSelectDialog;->buildRow(Landroid/content/Context;Lcom/ultragol/app/network/StreamingApi$Server;IZ)Landroid/view/View;

    move-result-object v15

    .line 167
    .local v15, "row":Landroid/view/View;
    iget-object v8, v12, Lcom/ultragol/app/network/StreamingApi$Server;->url:Ljava/lang/String;

    .line 168
    .local v8, "url":Ljava/lang/String;
    new-instance v7, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda1;

    move-object v0, v7

    move-object/from16 v3, p1

    move-object/from16 v4, p4

    move-object/from16 v5, p0

    move-object/from16 v6, p3

    move-object v10, v7

    move-object/from16 v7, p2

    move-object/from16 v16, v8

    .end local v8    # "url":Ljava/lang/String;
    .local v16, "url":Ljava/lang/String;
    invoke-direct/range {v0 .. v8}, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda1;-><init>([IILandroid/widget/LinearLayout;Ljava/util/List;Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;Landroid/app/Dialog;Ljava/lang/String;)V

    invoke-virtual {v15, v10}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 193
    invoke-virtual {v9, v15}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 163
    .end local v2    # "idx":I
    .end local v12    # "srv":Lcom/ultragol/app/network/StreamingApi$Server;
    .end local v15    # "row":Landroid/view/View;
    .end local v16    # "url":Ljava/lang/String;
    add-int/lit8 v11, v11, 0x1

    move-object/from16 v10, p4

    goto :goto_0

    :cond_2
    move-object/from16 v14, p0

    move/from16 v13, p5

    .line 195
    .end local v11    # "i":I
    return-void

    .line 161
    .end local v1    # "sel":[I
    :cond_3
    move-object/from16 v14, p0

    move/from16 v13, p5

    :goto_2
    return-void
.end method

.method private static buildRow(Landroid/content/Context;Lcom/ultragol/app/network/StreamingApi$Server;IZ)Landroid/view/View;
    .locals 16
    .param p0, "ctx"    # Landroid/content/Context;
    .param p1, "srv"    # Lcom/ultragol/app/network/StreamingApi$Server;
    .param p2, "idx"    # I
    .param p3, "selected"    # Z

    .line 199
    move-object/from16 v0, p0

    new-instance v1, Landroid/widget/LinearLayout;

    invoke-direct {v1, v0}, Landroid/widget/LinearLayout;-><init>(Landroid/content/Context;)V

    .line 200
    .local v1, "row":Landroid/widget/LinearLayout;
    const/4 v2, 0x0

    invoke-virtual {v1, v2}, Landroid/widget/LinearLayout;->setOrientation(I)V

    .line 201
    const v3, 0x800015

    invoke-virtual {v1, v3}, Landroid/widget/LinearLayout;->setGravity(I)V

    .line 202
    const/4 v3, 0x1

    invoke-virtual {v1, v3}, Landroid/widget/LinearLayout;->setClickable(Z)V

    .line 203
    invoke-virtual {v1, v3}, Landroid/widget/LinearLayout;->setFocusable(Z)V

    .line 206
    if-eqz p3, :cond_0

    const/16 v4, 0x44

    goto :goto_0

    :cond_0
    const/16 v4, 0x38

    :goto_0
    invoke-static {v0, v4}, Lcom/ultragol/app/ServerSelectDialog;->dp(Landroid/content/Context;I)I

    move-result v4

    .line 207
    .local v4, "rowH":I
    new-instance v5, Landroid/widget/LinearLayout$LayoutParams;

    const/4 v6, -0x2

    invoke-direct {v5, v6, v4}, Landroid/widget/LinearLayout$LayoutParams;-><init>(II)V

    .line 209
    .local v5, "lp":Landroid/widget/LinearLayout$LayoutParams;
    const/4 v7, 0x4

    invoke-static {v0, v7}, Lcom/ultragol/app/ServerSelectDialog;->dp(Landroid/content/Context;I)I

    move-result v7

    invoke-virtual {v5, v2, v2, v2, v7}, Landroid/widget/LinearLayout$LayoutParams;->setMargins(IIII)V

    .line 210
    invoke-virtual {v1, v5}, Landroid/widget/LinearLayout;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    .line 211
    const/4 v7, 0x0

    invoke-virtual {v1, v7}, Landroid/widget/LinearLayout;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 214
    new-instance v8, Landroid/widget/LinearLayout;

    invoke-direct {v8, v0}, Landroid/widget/LinearLayout;-><init>(Landroid/content/Context;)V

    .line 215
    .local v8, "textCol":Landroid/widget/LinearLayout;
    invoke-virtual {v8, v3}, Landroid/widget/LinearLayout;->setOrientation(I)V

    .line 216
    const v9, 0x800005

    invoke-virtual {v8, v9}, Landroid/widget/LinearLayout;->setGravity(I)V

    .line 217
    new-instance v10, Landroid/widget/LinearLayout$LayoutParams;

    invoke-direct {v10, v6, v6}, Landroid/widget/LinearLayout$LayoutParams;-><init>(II)V

    .line 219
    .local v10, "tcLp":Landroid/widget/LinearLayout$LayoutParams;
    const/16 v11, 0xe

    invoke-static {v0, v11}, Lcom/ultragol/app/ServerSelectDialog;->dp(Landroid/content/Context;I)I

    move-result v11

    invoke-virtual {v10, v11}, Landroid/widget/LinearLayout$LayoutParams;->setMarginEnd(I)V

    .line 220
    invoke-virtual {v8, v10}, Landroid/widget/LinearLayout;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    .line 223
    new-instance v11, Landroid/widget/TextView;

    invoke-direct {v11, v0}, Landroid/widget/TextView;-><init>(Landroid/content/Context;)V

    .line 224
    .local v11, "tvName":Landroid/widget/TextView;
    move-object/from16 v12, p1

    iget-object v13, v12, Lcom/ultragol/app/network/StreamingApi$Server;->name:Ljava/lang/String;

    invoke-virtual {v11, v13}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 225
    invoke-virtual {v11, v9}, Landroid/widget/TextView;->setGravity(I)V

    .line 226
    const/4 v13, 0x2

    if-eqz p3, :cond_1

    .line 227
    const/4 v14, -0x1

    invoke-virtual {v11, v14}, Landroid/widget/TextView;->setTextColor(I)V

    .line 228
    const/high16 v14, 0x41980000    # 19.0f

    invoke-virtual {v11, v13, v14}, Landroid/widget/TextView;->setTextSize(IF)V

    .line 229
    invoke-virtual {v11, v7, v3}, Landroid/widget/TextView;->setTypeface(Landroid/graphics/Typeface;I)V

    goto :goto_1

    .line 231
    :cond_1
    const v14, -0x77000001

    invoke-virtual {v11, v14}, Landroid/widget/TextView;->setTextColor(I)V

    .line 232
    const/high16 v14, 0x41700000    # 15.0f

    invoke-virtual {v11, v13, v14}, Landroid/widget/TextView;->setTextSize(IF)V

    .line 233
    invoke-virtual {v11, v7, v2}, Landroid/widget/TextView;->setTypeface(Landroid/graphics/Typeface;I)V

    .line 235
    :goto_1
    invoke-virtual {v8, v11}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 238
    new-instance v14, Landroid/widget/TextView;

    invoke-direct {v14, v0}, Landroid/widget/TextView;-><init>(Landroid/content/Context;)V

    .line 239
    .local v14, "tvStream":Landroid/widget/TextView;
    const-string v15, "STREAM"

    invoke-virtual {v14, v15}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 240
    invoke-virtual {v14, v9}, Landroid/widget/TextView;->setGravity(I)V

    .line 241
    if-eqz p3, :cond_2

    const v9, -0x66000001

    goto :goto_2

    :cond_2
    const v9, 0x44ffffff    # 2047.9999f

    :goto_2
    invoke-virtual {v14, v9}, Landroid/widget/TextView;->setTextColor(I)V

    .line 242
    const/high16 v9, 0x41200000    # 10.0f

    invoke-virtual {v14, v13, v9}, Landroid/widget/TextView;->setTextSize(IF)V

    .line 243
    const v9, 0x3df5c28f    # 0.12f

    invoke-virtual {v14, v9}, Landroid/widget/TextView;->setLetterSpacing(F)V

    .line 244
    invoke-virtual {v14, v7, v3}, Landroid/widget/TextView;->setTypeface(Landroid/graphics/Typeface;I)V

    .line 245
    new-instance v3, Landroid/widget/LinearLayout$LayoutParams;

    invoke-direct {v3, v6, v6}, Landroid/widget/LinearLayout$LayoutParams;-><init>(II)V

    .line 247
    .local v3, "slp":Landroid/widget/LinearLayout$LayoutParams;
    invoke-static {v0, v13}, Lcom/ultragol/app/ServerSelectDialog;->dp(Landroid/content/Context;I)I

    move-result v6

    invoke-virtual {v3, v2, v6, v2, v2}, Landroid/widget/LinearLayout$LayoutParams;->setMargins(IIII)V

    .line 248
    invoke-virtual {v14, v3}, Landroid/widget/TextView;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    .line 249
    invoke-virtual {v8, v14}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 251
    invoke-virtual {v1, v8}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 254
    new-instance v2, Landroid/view/View;

    invoke-direct {v2, v0}, Landroid/view/View;-><init>(Landroid/content/Context;)V

    .line 255
    .local v2, "radio":Landroid/view/View;
    if-eqz p3, :cond_3

    const/16 v6, 0x12

    goto :goto_3

    :cond_3
    const/16 v6, 0x8

    :goto_3
    invoke-static {v0, v6}, Lcom/ultragol/app/ServerSelectDialog;->dp(Landroid/content/Context;I)I

    move-result v6

    .line 256
    .local v6, "radioSize":I
    new-instance v7, Landroid/widget/LinearLayout$LayoutParams;

    invoke-direct {v7, v6, v6}, Landroid/widget/LinearLayout$LayoutParams;-><init>(II)V

    .line 257
    .local v7, "rLp":Landroid/widget/LinearLayout$LayoutParams;
    invoke-virtual {v2, v7}, Landroid/view/View;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    .line 258
    if-eqz p3, :cond_4

    sget v9, Lcom/ultragol/app/R$drawable;->server_radio_ring:I

    goto :goto_4

    :cond_4
    sget v9, Lcom/ultragol/app/R$drawable;->server_radio_dot:I

    :goto_4
    invoke-virtual {v2, v9}, Landroid/view/View;->setBackgroundResource(I)V

    .line 259
    invoke-virtual {v1, v2}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 261
    return-object v1
.end method

.method private static dp(Landroid/content/Context;I)I
    .locals 2
    .param p0, "ctx"    # Landroid/content/Context;
    .param p1, "v"    # I

    .line 273
    int-to-float v0, p1

    invoke-virtual {p0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    invoke-virtual {v1}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    move-result-object v1

    iget v1, v1, Landroid/util/DisplayMetrics;->density:F

    mul-float v0, v0, v1

    invoke-static {v0}, Ljava/lang/Math;->round(F)I

    move-result v0

    return v0
.end method

.method private static fallback(Landroid/content/Context;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;)V
    .locals 7
    .param p0, "ctx"    # Landroid/content/Context;
    .param p1, "dialog"    # Landroid/app/Dialog;
    .param p2, "item"    # Lcom/ultragol/app/models/ContentItem;

    .line 265
    sget v0, Lcom/ultragol/app/R$id;->serverContainer:I

    invoke-virtual {p1, v0}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/LinearLayout;

    .line 266
    .local v0, "cnt":Landroid/widget/LinearLayout;
    if-nez v0, :cond_0

    return-void

    .line 267
    :cond_0
    invoke-virtual {v0}, Landroid/widget/LinearLayout;->removeAllViews()V

    .line 268
    const/4 v1, 0x1

    new-array v1, v1, [Lcom/ultragol/app/network/StreamingApi$Server;

    new-instance v2, Lcom/ultragol/app/network/StreamingApi$Server;

    .line 269
    invoke-virtual {p2}, Lcom/ultragol/app/models/ContentItem;->getStreamUrl()Ljava/lang/String;

    move-result-object v3

    const-string v4, "embed"

    const-string v5, "UnlimPlay"

    invoke-direct {v2, v5, v3, v4}, Lcom/ultragol/app/network/StreamingApi$Server;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V

    const/4 v3, 0x0

    aput-object v2, v1, v3

    invoke-static {v1}, Ljava/util/Arrays;->asList([Ljava/lang/Object;)Ljava/util/List;

    move-result-object v5

    const/4 v6, 0x0

    .line 268
    move-object v1, p0

    move-object v2, v0

    move-object v3, p1

    move-object v4, p2

    invoke-static/range {v1 .. v6}, Lcom/ultragol/app/ServerSelectDialog;->addRows(Landroid/content/Context;Landroid/widget/LinearLayout;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;Ljava/util/List;I)V

    .line 270
    return-void
.end method

.method static synthetic lambda$addRows$12(Landroid/content/Context;Ljava/util/List;ILcom/ultragol/app/models/ContentItem;Landroid/app/Dialog;Landroid/view/View;)V
    .locals 3
    .param p0, "ctx"    # Landroid/content/Context;
    .param p1, "list"    # Ljava/util/List;
    .param p2, "jj"    # I
    .param p3, "item"    # Lcom/ultragol/app/models/ContentItem;
    .param p4, "dialog"    # Landroid/app/Dialog;
    .param p5, "vv"    # Landroid/view/View;

    .line 176
    new-instance v0, Landroid/content/Intent;

    const-class v1, Lcom/ultragol/app/PlayerActivity;

    invoke-direct {v0, p0, v1}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    .line 177
    .local v0, "intent":Landroid/content/Intent;
    invoke-interface {p1, p2}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/ultragol/app/network/StreamingApi$Server;

    iget-object v1, v1, Lcom/ultragol/app/network/StreamingApi$Server;->url:Ljava/lang/String;

    const-string v2, "url"

    invoke-virtual {v0, v2, v1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    .line 178
    const-string v1, "title"

    invoke-virtual {p3}, Lcom/ultragol/app/models/ContentItem;->getTitle()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    .line 179
    const/high16 v1, 0x10000000

    invoke-virtual {v0, v1}, Landroid/content/Intent;->addFlags(I)Landroid/content/Intent;

    .line 180
    invoke-virtual {p0, v0}, Landroid/content/Context;->startActivity(Landroid/content/Intent;)V

    .line 181
    invoke-virtual {p4}, Landroid/app/Dialog;->dismiss()V

    .line 182
    return-void
.end method

.method static synthetic lambda$addRows$13([IILandroid/widget/LinearLayout;Ljava/util/List;Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;Landroid/app/Dialog;Ljava/lang/String;Landroid/view/View;)V
    .locals 13
    .param p0, "sel"    # [I
    .param p1, "idx"    # I
    .param p2, "cnt"    # Landroid/widget/LinearLayout;
    .param p3, "list"    # Ljava/util/List;
    .param p4, "ctx"    # Landroid/content/Context;
    .param p5, "item"    # Lcom/ultragol/app/models/ContentItem;
    .param p6, "dialog"    # Landroid/app/Dialog;
    .param p7, "url"    # Ljava/lang/String;
    .param p8, "v"    # Landroid/view/View;

    .line 169
    move v0, p1

    move-object/from16 v7, p4

    const/4 v8, 0x0

    aput v0, p0, v8

    .line 171
    invoke-virtual {p2}, Landroid/widget/LinearLayout;->removeAllViews()V

    .line 172
    const/4 v1, 0x0

    move v9, v1

    .local v9, "j":I
    :goto_0
    invoke-interface/range {p3 .. p3}, Ljava/util/List;->size()I

    move-result v1

    if-ge v9, v1, :cond_1

    .line 173
    move v4, v9

    .line 174
    .local v4, "jj":I
    move-object/from16 v10, p3

    invoke-interface {v10, v9}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/ultragol/app/network/StreamingApi$Server;

    if-ne v9, v0, :cond_0

    const/4 v2, 0x1

    goto :goto_1

    :cond_0
    const/4 v2, 0x0

    :goto_1
    invoke-static {v7, v1, v9, v2}, Lcom/ultragol/app/ServerSelectDialog;->buildRow(Landroid/content/Context;Lcom/ultragol/app/network/StreamingApi$Server;IZ)Landroid/view/View;

    move-result-object v11

    .line 175
    .local v11, "r":Landroid/view/View;
    new-instance v12, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda13;

    move-object v1, v12

    move-object/from16 v2, p4

    move-object/from16 v3, p3

    move-object/from16 v5, p5

    move-object/from16 v6, p6

    invoke-direct/range {v1 .. v6}, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda13;-><init>(Landroid/content/Context;Ljava/util/List;ILcom/ultragol/app/models/ContentItem;Landroid/app/Dialog;)V

    invoke-virtual {v11, v12}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 183
    move-object v1, p2

    invoke-virtual {p2, v11}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 172
    .end local v4    # "jj":I
    .end local v11    # "r":Landroid/view/View;
    add-int/lit8 v9, v9, 0x1

    goto :goto_0

    :cond_1
    move-object v1, p2

    move-object/from16 v10, p3

    .line 186
    .end local v9    # "j":I
    new-instance v2, Landroid/content/Intent;

    const-class v3, Lcom/ultragol/app/PlayerActivity;

    invoke-direct {v2, v7, v3}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    .line 187
    .local v2, "intent":Landroid/content/Intent;
    const-string v3, "url"

    move-object/from16 v4, p7

    invoke-virtual {v2, v3, v4}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    .line 188
    const-string v3, "title"

    invoke-virtual/range {p5 .. p5}, Lcom/ultragol/app/models/ContentItem;->getTitle()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {v2, v3, v5}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    .line 189
    const/high16 v3, 0x10000000

    invoke-virtual {v2, v3}, Landroid/content/Intent;->addFlags(I)Landroid/content/Intent;

    .line 190
    invoke-virtual {v7, v2}, Landroid/content/Context;->startActivity(Landroid/content/Intent;)V

    .line 191
    invoke-virtual/range {p6 .. p6}, Landroid/app/Dialog;->dismiss()V

    .line 192
    return-void
.end method

.method static synthetic lambda$loadServers$6(Landroid/app/Dialog;Landroid/view/View;Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;Lcom/ultragol/app/network/StreamingApi$ServerData;)V
    .locals 1
    .param p0, "dialog"    # Landroid/app/Dialog;
    .param p1, "loading"    # Landroid/view/View;
    .param p2, "ctx"    # Landroid/content/Context;
    .param p3, "item"    # Lcom/ultragol/app/models/ContentItem;
    .param p4, "data"    # Lcom/ultragol/app/network/StreamingApi$ServerData;

    .line 103
    invoke-virtual {p0}, Landroid/app/Dialog;->isShowing()Z

    move-result v0

    if-nez v0, :cond_0

    return-void

    .line 104
    :cond_0
    if-eqz p1, :cond_1

    const/16 v0, 0x8

    invoke-virtual {p1, v0}, Landroid/view/View;->setVisibility(I)V

    .line 105
    :cond_1
    invoke-static {p2, p0, p3, p4}, Lcom/ultragol/app/ServerSelectDialog;->render(Landroid/content/Context;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;Lcom/ultragol/app/network/StreamingApi$ServerData;)V

    .line 106
    return-void
.end method

.method static synthetic lambda$loadServers$7(Landroid/app/Dialog;Landroid/view/View;Landroid/view/View;Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;)V
    .locals 1
    .param p0, "dialog"    # Landroid/app/Dialog;
    .param p1, "loading"    # Landroid/view/View;
    .param p2, "error"    # Landroid/view/View;
    .param p3, "ctx"    # Landroid/content/Context;
    .param p4, "item"    # Lcom/ultragol/app/models/ContentItem;

    .line 109
    invoke-virtual {p0}, Landroid/app/Dialog;->isShowing()Z

    move-result v0

    if-nez v0, :cond_0

    return-void

    .line 110
    :cond_0
    if-eqz p1, :cond_1

    const/16 v0, 0x8

    invoke-virtual {p1, v0}, Landroid/view/View;->setVisibility(I)V

    .line 111
    :cond_1
    if-eqz p2, :cond_2

    const/4 v0, 0x0

    invoke-virtual {p2, v0}, Landroid/view/View;->setVisibility(I)V

    .line 112
    :cond_2
    invoke-static {p3, p0, p4}, Lcom/ultragol/app/ServerSelectDialog;->fallback(Landroid/content/Context;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;)V

    .line 113
    return-void
.end method

.method static synthetic lambda$loadServers$8(Lcom/ultragol/app/models/ContentItem;IILandroid/os/Handler;Landroid/app/Dialog;Landroid/view/View;Landroid/content/Context;Landroid/view/View;)V
    .locals 8
    .param p0, "item"    # Lcom/ultragol/app/models/ContentItem;
    .param p1, "season"    # I
    .param p2, "episode"    # I
    .param p3, "handler"    # Landroid/os/Handler;
    .param p4, "dialog"    # Landroid/app/Dialog;
    .param p5, "loading"    # Landroid/view/View;
    .param p6, "ctx"    # Landroid/content/Context;
    .param p7, "error"    # Landroid/view/View;

    .line 99
    :try_start_0
    invoke-virtual {p0}, Lcom/ultragol/app/models/ContentItem;->getContentType()I

    move-result v0

    if-nez v0, :cond_0

    .line 100
    invoke-virtual {p0}, Lcom/ultragol/app/models/ContentItem;->getTmdbId()I

    move-result v0

    invoke-static {v0}, Lcom/ultragol/app/network/StreamingApi;->fetchMovieServers(I)Lcom/ultragol/app/network/StreamingApi$ServerData;

    move-result-object v0

    move-object v6, v0

    goto :goto_0

    .line 101
    :cond_0
    invoke-virtual {p0}, Lcom/ultragol/app/models/ContentItem;->getTmdbId()I

    move-result v0

    invoke-static {v0, p1, p2}, Lcom/ultragol/app/network/StreamingApi;->fetchSeriesServers(III)Lcom/ultragol/app/network/StreamingApi$ServerData;

    move-result-object v0

    move-object v6, v0

    :goto_0
    nop

    .line 102
    .local v6, "data":Lcom/ultragol/app/network/StreamingApi$ServerData;
    new-instance v0, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda11;

    move-object v1, v0

    move-object v2, p4

    move-object v3, p5

    move-object v4, p6

    move-object v5, p0

    invoke-direct/range {v1 .. v6}, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda11;-><init>(Landroid/app/Dialog;Landroid/view/View;Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;Lcom/ultragol/app/network/StreamingApi$ServerData;)V

    invoke-virtual {p3, v0}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 114
    nop

    .end local v6    # "data":Lcom/ultragol/app/network/StreamingApi$ServerData;
    goto :goto_1

    .line 107
    :catch_0
    move-exception v0

    .line 108
    .local v0, "e":Ljava/lang/Exception;
    new-instance v7, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda12;

    move-object v1, v7

    move-object v2, p4

    move-object v3, p5

    move-object v4, p7

    move-object v5, p6

    move-object v6, p0

    invoke-direct/range {v1 .. v6}, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda12;-><init>(Landroid/app/Dialog;Landroid/view/View;Landroid/view/View;Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;)V

    invoke-virtual {p3, v7}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    .line 115
    .end local v0    # "e":Ljava/lang/Exception;
    :goto_1
    return-void
.end method

.method static synthetic lambda$setupTabs$10(Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/LinearLayout;Landroid/content/Context;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;Lcom/ultragol/app/network/StreamingApi$ServerData;Landroid/view/View;)V
    .locals 8
    .param p0, "tE"    # Landroid/widget/TextView;
    .param p1, "tL"    # Landroid/widget/TextView;
    .param p2, "tS"    # Landroid/widget/TextView;
    .param p3, "cnt"    # Landroid/widget/LinearLayout;
    .param p4, "ctx"    # Landroid/content/Context;
    .param p5, "dialog"    # Landroid/app/Dialog;
    .param p6, "item"    # Lcom/ultragol/app/models/ContentItem;
    .param p7, "data"    # Lcom/ultragol/app/network/StreamingApi$ServerData;
    .param p8, "v"    # Landroid/view/View;

    .line 147
    move-object v0, p0

    const/4 v1, 0x3

    new-array v1, v1, [Landroid/widget/TextView;

    const/4 v2, 0x0

    aput-object p1, v1, v2

    const/4 v2, 0x1

    aput-object v0, v1, v2

    const/4 v2, 0x2

    aput-object p2, v1, v2

    invoke-static {p0, v1}, Lcom/ultragol/app/ServerSelectDialog;->setActive(Landroid/widget/TextView;[Landroid/widget/TextView;)V

    invoke-virtual {p3}, Landroid/widget/LinearLayout;->removeAllViews()V

    move-object v1, p7

    iget-object v6, v1, Lcom/ultragol/app/network/StreamingApi$ServerData;->espanol:Ljava/util/List;

    const/4 v7, 0x0

    move-object v2, p4

    move-object v3, p3

    move-object v4, p5

    move-object v5, p6

    invoke-static/range {v2 .. v7}, Lcom/ultragol/app/ServerSelectDialog;->addRows(Landroid/content/Context;Landroid/widget/LinearLayout;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;Ljava/util/List;I)V

    return-void
.end method

.method static synthetic lambda$setupTabs$11(Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/LinearLayout;Landroid/content/Context;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;Lcom/ultragol/app/network/StreamingApi$ServerData;Landroid/view/View;)V
    .locals 8
    .param p0, "tS"    # Landroid/widget/TextView;
    .param p1, "tL"    # Landroid/widget/TextView;
    .param p2, "tE"    # Landroid/widget/TextView;
    .param p3, "cnt"    # Landroid/widget/LinearLayout;
    .param p4, "ctx"    # Landroid/content/Context;
    .param p5, "dialog"    # Landroid/app/Dialog;
    .param p6, "item"    # Lcom/ultragol/app/models/ContentItem;
    .param p7, "data"    # Lcom/ultragol/app/network/StreamingApi$ServerData;
    .param p8, "v"    # Landroid/view/View;

    .line 148
    move-object v0, p0

    const/4 v1, 0x3

    new-array v1, v1, [Landroid/widget/TextView;

    const/4 v2, 0x0

    aput-object p1, v1, v2

    const/4 v2, 0x1

    aput-object p2, v1, v2

    const/4 v2, 0x2

    aput-object v0, v1, v2

    invoke-static {p0, v1}, Lcom/ultragol/app/ServerSelectDialog;->setActive(Landroid/widget/TextView;[Landroid/widget/TextView;)V

    invoke-virtual {p3}, Landroid/widget/LinearLayout;->removeAllViews()V

    move-object v1, p7

    iget-object v6, v1, Lcom/ultragol/app/network/StreamingApi$ServerData;->subtitulado:Ljava/util/List;

    const/4 v7, 0x0

    move-object v2, p4

    move-object v3, p3

    move-object v4, p5

    move-object v5, p6

    invoke-static/range {v2 .. v7}, Lcom/ultragol/app/ServerSelectDialog;->addRows(Landroid/content/Context;Landroid/widget/LinearLayout;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;Ljava/util/List;I)V

    return-void
.end method

.method static synthetic lambda$setupTabs$9(Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/LinearLayout;Landroid/content/Context;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;Lcom/ultragol/app/network/StreamingApi$ServerData;Landroid/view/View;)V
    .locals 8
    .param p0, "tL"    # Landroid/widget/TextView;
    .param p1, "tE"    # Landroid/widget/TextView;
    .param p2, "tS"    # Landroid/widget/TextView;
    .param p3, "cnt"    # Landroid/widget/LinearLayout;
    .param p4, "ctx"    # Landroid/content/Context;
    .param p5, "dialog"    # Landroid/app/Dialog;
    .param p6, "item"    # Lcom/ultragol/app/models/ContentItem;
    .param p7, "data"    # Lcom/ultragol/app/network/StreamingApi$ServerData;
    .param p8, "v"    # Landroid/view/View;

    .line 146
    move-object v0, p0

    const/4 v1, 0x3

    new-array v1, v1, [Landroid/widget/TextView;

    const/4 v2, 0x0

    aput-object v0, v1, v2

    const/4 v2, 0x1

    aput-object p1, v1, v2

    const/4 v2, 0x2

    aput-object p2, v1, v2

    invoke-static {p0, v1}, Lcom/ultragol/app/ServerSelectDialog;->setActive(Landroid/widget/TextView;[Landroid/widget/TextView;)V

    invoke-virtual {p3}, Landroid/widget/LinearLayout;->removeAllViews()V

    move-object v1, p7

    iget-object v6, v1, Lcom/ultragol/app/network/StreamingApi$ServerData;->latino:Ljava/util/List;

    const/4 v7, 0x0

    move-object v2, p4

    move-object v3, p3

    move-object v4, p5

    move-object v5, p6

    invoke-static/range {v2 .. v7}, Lcom/ultragol/app/ServerSelectDialog;->addRows(Landroid/content/Context;Landroid/widget/LinearLayout;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;Ljava/util/List;I)V

    return-void
.end method

.method static synthetic lambda$show$0(Landroid/app/Dialog;Landroid/view/View;)V
    .locals 0
    .param p0, "dialog"    # Landroid/app/Dialog;
    .param p1, "v"    # Landroid/view/View;

    .line 35
    invoke-virtual {p0}, Landroid/app/Dialog;->dismiss()V

    return-void
.end method

.method static synthetic lambda$show$1([ILandroid/widget/TextView;Landroid/view/View;)V
    .locals 3
    .param p0, "season"    # [I
    .param p1, "tvS"    # Landroid/widget/TextView;
    .param p2, "v"    # Landroid/view/View;

    .line 74
    const/4 v0, 0x0

    aget v1, p0, v0

    const/4 v2, 0x1

    if-le v1, v2, :cond_0

    aget v1, p0, v0

    sub-int/2addr v1, v2

    aput v1, p0, v0

    if-eqz p1, :cond_0

    aget v0, p0, v0

    invoke-static {v0}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v0

    invoke-virtual {p1, v0}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    :cond_0
    return-void
.end method

.method static synthetic lambda$show$2([ILandroid/widget/TextView;Landroid/view/View;)V
    .locals 3
    .param p0, "season"    # [I
    .param p1, "tvS"    # Landroid/widget/TextView;
    .param p2, "v"    # Landroid/view/View;

    .line 75
    const/4 v0, 0x0

    aget v1, p0, v0

    const/16 v2, 0x1e

    if-ge v1, v2, :cond_0

    aget v1, p0, v0

    add-int/lit8 v1, v1, 0x1

    aput v1, p0, v0

    if-eqz p1, :cond_0

    aget v0, p0, v0

    invoke-static {v0}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v0

    invoke-virtual {p1, v0}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    :cond_0
    return-void
.end method

.method static synthetic lambda$show$3([ILandroid/widget/TextView;Landroid/view/View;)V
    .locals 3
    .param p0, "episode"    # [I
    .param p1, "tvE"    # Landroid/widget/TextView;
    .param p2, "v"    # Landroid/view/View;

    .line 76
    const/4 v0, 0x0

    aget v1, p0, v0

    const/4 v2, 0x1

    if-le v1, v2, :cond_0

    aget v1, p0, v0

    sub-int/2addr v1, v2

    aput v1, p0, v0

    if-eqz p1, :cond_0

    aget v0, p0, v0

    invoke-static {v0}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v0

    invoke-virtual {p1, v0}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    :cond_0
    return-void
.end method

.method static synthetic lambda$show$4([ILandroid/widget/TextView;Landroid/view/View;)V
    .locals 3
    .param p0, "episode"    # [I
    .param p1, "tvE"    # Landroid/widget/TextView;
    .param p2, "v"    # Landroid/view/View;

    .line 77
    const/4 v0, 0x0

    aget v1, p0, v0

    const/16 v2, 0xc8

    if-ge v1, v2, :cond_0

    aget v1, p0, v0

    add-int/lit8 v1, v1, 0x1

    aput v1, p0, v0

    if-eqz p1, :cond_0

    aget v0, p0, v0

    invoke-static {v0}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v0

    invoke-virtual {p1, v0}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    :cond_0
    return-void
.end method

.method static synthetic lambda$show$5(Landroid/content/Context;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;[I[ILandroid/view/View;)V
    .locals 2
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "dialog"    # Landroid/app/Dialog;
    .param p2, "item"    # Lcom/ultragol/app/models/ContentItem;
    .param p3, "season"    # [I
    .param p4, "episode"    # [I
    .param p5, "v"    # Landroid/view/View;

    .line 78
    const/4 v0, 0x0

    aget v1, p3, v0

    aget v0, p4, v0

    invoke-static {p0, p1, p2, v1, v0}, Lcom/ultragol/app/ServerSelectDialog;->loadServers(Landroid/content/Context;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;II)V

    return-void
.end method

.method private static loadServers(Landroid/content/Context;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;II)V
    .locals 16
    .param p0, "ctx"    # Landroid/content/Context;
    .param p1, "dialog"    # Landroid/app/Dialog;
    .param p2, "item"    # Lcom/ultragol/app/models/ContentItem;
    .param p3, "season"    # I
    .param p4, "episode"    # I

    .line 86
    move-object/from16 v9, p1

    sget v0, Lcom/ultragol/app/R$id;->serverContainer:I

    invoke-virtual {v9, v0}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v0

    move-object v10, v0

    check-cast v10, Landroid/widget/LinearLayout;

    .line 87
    .local v10, "cnt":Landroid/widget/LinearLayout;
    sget v0, Lcom/ultragol/app/R$id;->loadingServers:I

    invoke-virtual {v9, v0}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v11

    .line 88
    .local v11, "loading":Landroid/view/View;
    sget v0, Lcom/ultragol/app/R$id;->errorServers:I

    invoke-virtual {v9, v0}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v12

    .line 89
    .local v12, "error":Landroid/view/View;
    sget v0, Lcom/ultragol/app/R$id;->langTabs:I

    invoke-virtual {v9, v0}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v13

    .line 90
    .local v13, "tabs":Landroid/view/View;
    if-eqz v10, :cond_0

    invoke-virtual {v10}, Landroid/widget/LinearLayout;->removeAllViews()V

    .line 91
    :cond_0
    if-eqz v11, :cond_1

    const/4 v0, 0x0

    invoke-virtual {v11, v0}, Landroid/view/View;->setVisibility(I)V

    .line 92
    :cond_1
    const/16 v0, 0x8

    if-eqz v12, :cond_2

    invoke-virtual {v12, v0}, Landroid/view/View;->setVisibility(I)V

    .line 93
    :cond_2
    if-eqz v13, :cond_3

    invoke-virtual {v13, v0}, Landroid/view/View;->setVisibility(I)V

    .line 95
    :cond_3
    invoke-static {}, Ljava/util/concurrent/Executors;->newSingleThreadExecutor()Ljava/util/concurrent/ExecutorService;

    move-result-object v14

    .line 96
    .local v14, "exec":Ljava/util/concurrent/ExecutorService;
    new-instance v4, Landroid/os/Handler;

    invoke-static {}, Landroid/os/Looper;->getMainLooper()Landroid/os/Looper;

    move-result-object v0

    invoke-direct {v4, v0}, Landroid/os/Handler;-><init>(Landroid/os/Looper;)V

    .line 97
    .local v4, "handler":Landroid/os/Handler;
    new-instance v15, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda10;

    move-object v0, v15

    move-object/from16 v1, p2

    move/from16 v2, p3

    move/from16 v3, p4

    move-object/from16 v5, p1

    move-object v6, v11

    move-object/from16 v7, p0

    move-object v8, v12

    invoke-direct/range {v0 .. v8}, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda10;-><init>(Lcom/ultragol/app/models/ContentItem;IILandroid/os/Handler;Landroid/app/Dialog;Landroid/view/View;Landroid/content/Context;Landroid/view/View;)V

    invoke-interface {v14, v15}, Ljava/util/concurrent/ExecutorService;->execute(Ljava/lang/Runnable;)V

    .line 116
    invoke-interface {v14}, Ljava/util/concurrent/ExecutorService;->shutdown()V

    .line 117
    return-void
.end method

.method private static render(Landroid/content/Context;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;Lcom/ultragol/app/network/StreamingApi$ServerData;)V
    .locals 15
    .param p0, "ctx"    # Landroid/content/Context;
    .param p1, "dialog"    # Landroid/app/Dialog;
    .param p2, "item"    # Lcom/ultragol/app/models/ContentItem;
    .param p3, "data"    # Lcom/ultragol/app/network/StreamingApi$ServerData;

    .line 120
    move-object/from16 v7, p1

    move-object/from16 v8, p3

    sget v0, Lcom/ultragol/app/R$id;->serverContainer:I

    invoke-virtual {v7, v0}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v0

    move-object v9, v0

    check-cast v9, Landroid/widget/LinearLayout;

    .line 121
    .local v9, "cnt":Landroid/widget/LinearLayout;
    sget v0, Lcom/ultragol/app/R$id;->langTabs:I

    invoke-virtual {v7, v0}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v10

    .line 122
    .local v10, "tabs":Landroid/view/View;
    if-nez v9, :cond_0

    return-void

    .line 123
    :cond_0
    invoke-virtual {v9}, Landroid/widget/LinearLayout;->removeAllViews()V

    .line 124
    iget-object v0, v8, Lcom/ultragol/app/network/StreamingApi$ServerData;->latino:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->isEmpty()Z

    move-result v0

    const/4 v1, 0x1

    xor-int/2addr v0, v1

    move v11, v0

    .local v11, "hL":Z
    iget-object v0, v8, Lcom/ultragol/app/network/StreamingApi$ServerData;->espanol:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->isEmpty()Z

    move-result v0

    xor-int/2addr v0, v1

    move v12, v0

    .local v12, "hE":Z
    iget-object v0, v8, Lcom/ultragol/app/network/StreamingApi$ServerData;->subtitulado:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->isEmpty()Z

    move-result v0

    xor-int/2addr v0, v1

    move v13, v0

    .line 125
    .local v13, "hS":Z
    const/4 v0, 0x0

    if-eqz v11, :cond_1

    const/4 v2, 0x1

    goto :goto_0

    :cond_1
    const/4 v2, 0x0

    :goto_0
    if-eqz v12, :cond_2

    const/4 v3, 0x1

    goto :goto_1

    :cond_2
    const/4 v3, 0x0

    :goto_1
    add-int/2addr v2, v3

    if-eqz v13, :cond_3

    const/4 v3, 0x1

    goto :goto_2

    :cond_3
    const/4 v3, 0x0

    :goto_2
    add-int v14, v2, v3

    .line 126
    .local v14, "n":I
    if-le v14, v1, :cond_4

    if-eqz v10, :cond_4

    .line 127
    invoke-virtual {v10, v0}, Landroid/view/View;->setVisibility(I)V

    .line 128
    move-object v0, p0

    move-object/from16 v1, p1

    move-object/from16 v2, p2

    move-object/from16 v3, p3

    move v4, v11

    move v5, v12

    move v6, v13

    invoke-static/range {v0 .. v6}, Lcom/ultragol/app/ServerSelectDialog;->setupTabs(Landroid/content/Context;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;Lcom/ultragol/app/network/StreamingApi$ServerData;ZZZ)V

    goto :goto_5

    .line 130
    :cond_4
    if-eqz v10, :cond_5

    const/16 v0, 0x8

    invoke-virtual {v10, v0}, Landroid/view/View;->setVisibility(I)V

    .line 131
    :cond_5
    if-eqz v11, :cond_6

    iget-object v0, v8, Lcom/ultragol/app/network/StreamingApi$ServerData;->latino:Ljava/util/List;

    :goto_3
    move-object v4, v0

    goto :goto_4

    :cond_6
    if-eqz v12, :cond_7

    iget-object v0, v8, Lcom/ultragol/app/network/StreamingApi$ServerData;->espanol:Ljava/util/List;

    goto :goto_3

    :cond_7
    iget-object v0, v8, Lcom/ultragol/app/network/StreamingApi$ServerData;->subtitulado:Ljava/util/List;

    goto :goto_3

    :goto_4
    const/4 v5, 0x0

    move-object v0, p0

    move-object v1, v9

    move-object/from16 v2, p1

    move-object/from16 v3, p2

    invoke-static/range {v0 .. v5}, Lcom/ultragol/app/ServerSelectDialog;->addRows(Landroid/content/Context;Landroid/widget/LinearLayout;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;Ljava/util/List;I)V

    .line 133
    :goto_5
    return-void
.end method

.method private static varargs setActive(Landroid/widget/TextView;[Landroid/widget/TextView;)V
    .locals 6
    .param p0, "active"    # Landroid/widget/TextView;
    .param p1, "all"    # [Landroid/widget/TextView;

    .line 152
    array-length v0, p1

    const/4 v1, 0x0

    const/4 v2, 0x0

    :goto_0
    if-ge v2, v0, :cond_4

    aget-object v3, p1, v2

    .local v3, "t":Landroid/widget/TextView;
    if-nez v3, :cond_0

    goto :goto_4

    .line 153
    :cond_0
    if-ne v3, p0, :cond_1

    const/4 v4, 0x1

    goto :goto_1

    :cond_1
    const/4 v4, 0x0

    .line 154
    .local v4, "a":Z
    :goto_1
    if-eqz v4, :cond_2

    sget v5, Lcom/ultragol/app/R$drawable;->tab_active:I

    goto :goto_2

    :cond_2
    sget v5, Lcom/ultragol/app/R$drawable;->tab_inactive:I

    :goto_2
    invoke-virtual {v3, v5}, Landroid/widget/TextView;->setBackgroundResource(I)V

    .line 155
    if-eqz v4, :cond_3

    const/4 v5, -0x1

    goto :goto_3

    :cond_3
    const v5, -0x7f7f70

    :goto_3
    invoke-virtual {v3, v5}, Landroid/widget/TextView;->setTextColor(I)V

    .line 152
    .end local v3    # "t":Landroid/widget/TextView;
    .end local v4    # "a":Z
    :goto_4
    add-int/lit8 v2, v2, 0x1

    goto :goto_0

    .line 157
    :cond_4
    return-void
.end method

.method private static setupTabs(Landroid/content/Context;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;Lcom/ultragol/app/network/StreamingApi$ServerData;ZZZ)V
    .locals 16
    .param p0, "ctx"    # Landroid/content/Context;
    .param p1, "dialog"    # Landroid/app/Dialog;
    .param p2, "item"    # Lcom/ultragol/app/models/ContentItem;
    .param p3, "data"    # Lcom/ultragol/app/network/StreamingApi$ServerData;
    .param p4, "hL"    # Z
    .param p5, "hE"    # Z
    .param p6, "hS"    # Z

    .line 137
    move-object/from16 v9, p1

    move-object/from16 v10, p3

    sget v0, Lcom/ultragol/app/R$id;->tabLatino:I

    invoke-virtual {v9, v0}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v0

    move-object v11, v0

    check-cast v11, Landroid/widget/TextView;

    .line 138
    .local v11, "tL":Landroid/widget/TextView;
    sget v0, Lcom/ultragol/app/R$id;->tabEspanol:I

    invoke-virtual {v9, v0}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v0

    move-object v12, v0

    check-cast v12, Landroid/widget/TextView;

    .line 139
    .local v12, "tE":Landroid/widget/TextView;
    sget v0, Lcom/ultragol/app/R$id;->tabSubtitulado:I

    invoke-virtual {v9, v0}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v0

    move-object v13, v0

    check-cast v13, Landroid/widget/TextView;

    .line 140
    .local v13, "tS":Landroid/widget/TextView;
    sget v0, Lcom/ultragol/app/R$id;->serverContainer:I

    invoke-virtual {v9, v0}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v0

    move-object v14, v0

    check-cast v14, Landroid/widget/LinearLayout;

    .line 141
    .local v14, "cnt":Landroid/widget/LinearLayout;
    const/16 v0, 0x8

    const/4 v1, 0x0

    if-eqz v11, :cond_1

    if-eqz p4, :cond_0

    const/4 v2, 0x0

    goto :goto_0

    :cond_0
    const/16 v2, 0x8

    :goto_0
    invoke-virtual {v11, v2}, Landroid/widget/TextView;->setVisibility(I)V

    .line 142
    :cond_1
    if-eqz v12, :cond_3

    if-eqz p5, :cond_2

    const/4 v2, 0x0

    goto :goto_1

    :cond_2
    const/16 v2, 0x8

    :goto_1
    invoke-virtual {v12, v2}, Landroid/widget/TextView;->setVisibility(I)V

    .line 143
    :cond_3
    if-eqz v13, :cond_5

    if-eqz p6, :cond_4

    const/4 v0, 0x0

    :cond_4
    invoke-virtual {v13, v0}, Landroid/widget/TextView;->setVisibility(I)V

    .line 144
    :cond_5
    if-eqz p4, :cond_6

    move-object v0, v11

    goto :goto_2

    :cond_6
    if-eqz p5, :cond_7

    move-object v0, v12

    goto :goto_2

    :cond_7
    move-object v0, v13

    :goto_2
    const/4 v2, 0x3

    new-array v2, v2, [Landroid/widget/TextView;

    aput-object v11, v2, v1

    const/4 v1, 0x1

    aput-object v12, v2, v1

    const/4 v1, 0x2

    aput-object v13, v2, v1

    invoke-static {v0, v2}, Lcom/ultragol/app/ServerSelectDialog;->setActive(Landroid/widget/TextView;[Landroid/widget/TextView;)V

    .line 145
    if-eqz p4, :cond_8

    iget-object v0, v10, Lcom/ultragol/app/network/StreamingApi$ServerData;->latino:Ljava/util/List;

    :goto_3
    move-object v4, v0

    goto :goto_4

    :cond_8
    if-eqz p5, :cond_9

    iget-object v0, v10, Lcom/ultragol/app/network/StreamingApi$ServerData;->espanol:Ljava/util/List;

    goto :goto_3

    :cond_9
    iget-object v0, v10, Lcom/ultragol/app/network/StreamingApi$ServerData;->subtitulado:Ljava/util/List;

    goto :goto_3

    :goto_4
    const/4 v5, 0x0

    move-object/from16 v0, p0

    move-object v1, v14

    move-object/from16 v2, p1

    move-object/from16 v3, p2

    invoke-static/range {v0 .. v5}, Lcom/ultragol/app/ServerSelectDialog;->addRows(Landroid/content/Context;Landroid/widget/LinearLayout;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;Ljava/util/List;I)V

    .line 146
    if-eqz v11, :cond_a

    new-instance v15, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda2;

    move-object v0, v15

    move-object v1, v11

    move-object v2, v12

    move-object v3, v13

    move-object v4, v14

    move-object/from16 v5, p0

    move-object/from16 v6, p1

    move-object/from16 v7, p2

    move-object/from16 v8, p3

    invoke-direct/range {v0 .. v8}, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda2;-><init>(Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/LinearLayout;Landroid/content/Context;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;Lcom/ultragol/app/network/StreamingApi$ServerData;)V

    invoke-virtual {v11, v15}, Landroid/widget/TextView;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 147
    :cond_a
    if-eqz v12, :cond_b

    new-instance v15, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda3;

    move-object v0, v15

    move-object v1, v12

    move-object v2, v11

    move-object v3, v13

    move-object v4, v14

    move-object/from16 v5, p0

    move-object/from16 v6, p1

    move-object/from16 v7, p2

    move-object/from16 v8, p3

    invoke-direct/range {v0 .. v8}, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda3;-><init>(Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/LinearLayout;Landroid/content/Context;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;Lcom/ultragol/app/network/StreamingApi$ServerData;)V

    invoke-virtual {v12, v15}, Landroid/widget/TextView;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 148
    :cond_b
    if-eqz v13, :cond_c

    new-instance v15, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda4;

    move-object v0, v15

    move-object v1, v13

    move-object v2, v11

    move-object v3, v12

    move-object v4, v14

    move-object/from16 v5, p0

    move-object/from16 v6, p1

    move-object/from16 v7, p2

    move-object/from16 v8, p3

    invoke-direct/range {v0 .. v8}, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda4;-><init>(Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/LinearLayout;Landroid/content/Context;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;Lcom/ultragol/app/network/StreamingApi$ServerData;)V

    invoke-virtual {v13, v15}, Landroid/widget/TextView;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 149
    :cond_c
    return-void
.end method

.method public static show(Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;)V
    .locals 25
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "item"    # Lcom/ultragol/app/models/ContentItem;

    .line 22
    move-object/from16 v6, p0

    new-instance v0, Landroid/app/Dialog;

    sget v1, Lcom/ultragol/app/R$style;->FullScreenServerDialog:I

    invoke-direct {v0, v6, v1}, Landroid/app/Dialog;-><init>(Landroid/content/Context;I)V

    move-object v7, v0

    .line 23
    .local v7, "dialog":Landroid/app/Dialog;
    const/4 v0, 0x1

    invoke-virtual {v7, v0}, Landroid/app/Dialog;->requestWindowFeature(I)Z

    .line 24
    sget v1, Lcom/ultragol/app/R$layout;->dialog_server_select:I

    invoke-virtual {v7, v1}, Landroid/app/Dialog;->setContentView(I)V

    .line 26
    invoke-virtual {v7}, Landroid/app/Dialog;->getWindow()Landroid/view/Window;

    move-result-object v8

    .line 27
    .local v8, "win":Landroid/view/Window;
    if-eqz v8, :cond_0

    .line 28
    const/4 v1, -0x1

    invoke-virtual {v8, v1, v1}, Landroid/view/Window;->setLayout(II)V

    .line 30
    const v1, 0x106000d

    invoke-virtual {v8, v1}, Landroid/view/Window;->setBackgroundDrawableResource(I)V

    .line 34
    :cond_0
    sget v1, Lcom/ultragol/app/R$id;->dialogDismiss:I

    invoke-virtual {v7, v1}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v9

    .line 35
    .local v9, "dismiss":Landroid/view/View;
    if-eqz v9, :cond_1

    new-instance v1, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda0;

    invoke-direct {v1, v7}, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda0;-><init>(Landroid/app/Dialog;)V

    invoke-virtual {v9, v1}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 38
    :cond_1
    sget v1, Lcom/ultragol/app/R$id;->serverDialogTitle:I

    invoke-virtual {v7, v1}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v1

    move-object v10, v1

    check-cast v10, Landroid/widget/TextView;

    .line 39
    .local v10, "tvTitle":Landroid/widget/TextView;
    if-eqz v10, :cond_2

    invoke-virtual/range {p1 .. p1}, Lcom/ultragol/app/models/ContentItem;->getTitle()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v10, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 41
    :cond_2
    sget v1, Lcom/ultragol/app/R$id;->dialogBadge:I

    invoke-virtual {v7, v1}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v1

    move-object v11, v1

    check-cast v11, Landroid/widget/TextView;

    .line 42
    .local v11, "tvBadge":Landroid/widget/TextView;
    if-eqz v11, :cond_3

    .line 43
    invoke-virtual/range {p1 .. p1}, Lcom/ultragol/app/models/ContentItem;->getContentType()I

    move-result v1

    packed-switch v1, :pswitch_data_0

    .line 47
    const-string v1, "FILM"

    invoke-virtual {v11, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    goto :goto_0

    .line 46
    :pswitch_0
    const-string v1, "DORAMA"

    invoke-virtual {v11, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    goto :goto_0

    .line 45
    :pswitch_1
    const-string v1, "ANIME"

    invoke-virtual {v11, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    goto :goto_0

    .line 44
    :pswitch_2
    const-string v1, "SERIE"

    invoke-virtual {v11, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 51
    :cond_3
    :goto_0
    sget v1, Lcom/ultragol/app/R$id;->dialogPoster:I

    invoke-virtual {v7, v1}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v1

    move-object v12, v1

    check-cast v12, Landroid/widget/ImageView;

    .line 52
    .local v12, "poster":Landroid/widget/ImageView;
    if-eqz v12, :cond_4

    invoke-virtual/range {p1 .. p1}, Lcom/ultragol/app/models/ContentItem;->getPosterUrl()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/String;->isEmpty()Z

    move-result v1

    if-nez v1, :cond_4

    .line 53
    invoke-static/range {p0 .. p0}, Lcom/bumptech/glide/Glide;->with(Landroid/content/Context;)Lcom/bumptech/glide/RequestManager;

    move-result-object v1

    invoke-virtual/range {p1 .. p1}, Lcom/ultragol/app/models/ContentItem;->getPosterUrl()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/bumptech/glide/RequestManager;->load(Ljava/lang/String;)Lcom/bumptech/glide/RequestBuilder;

    move-result-object v1

    .line 54
    invoke-static {}, Lcom/bumptech/glide/load/resource/drawable/DrawableTransitionOptions;->withCrossFade()Lcom/bumptech/glide/load/resource/drawable/DrawableTransitionOptions;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/bumptech/glide/RequestBuilder;->transition(Lcom/bumptech/glide/TransitionOptions;)Lcom/bumptech/glide/RequestBuilder;

    move-result-object v1

    .line 55
    invoke-virtual {v1}, Lcom/bumptech/glide/RequestBuilder;->centerCrop()Lcom/bumptech/glide/request/BaseRequestOptions;

    move-result-object v1

    check-cast v1, Lcom/bumptech/glide/RequestBuilder;

    invoke-virtual {v1, v12}, Lcom/bumptech/glide/RequestBuilder;->into(Landroid/widget/ImageView;)Lcom/bumptech/glide/request/target/ViewTarget;

    .line 59
    :cond_4
    invoke-virtual/range {p1 .. p1}, Lcom/ultragol/app/models/ContentItem;->getContentType()I

    move-result v1

    if-eqz v1, :cond_5

    const/4 v1, 0x1

    goto :goto_1

    :cond_5
    const/4 v1, 0x0

    :goto_1
    move v14, v1

    .line 60
    .local v14, "isTV":Z
    sget v1, Lcom/ultragol/app/R$id;->episodeSection:I

    invoke-virtual {v7, v1}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v15

    .line 61
    .local v15, "episodeSection":Landroid/view/View;
    if-eqz v15, :cond_7

    if-eqz v14, :cond_6

    const/4 v1, 0x0

    goto :goto_2

    :cond_6
    const/16 v1, 0x8

    :goto_2
    invoke-virtual {v15, v1}, Landroid/view/View;->setVisibility(I)V

    .line 63
    :cond_7
    filled-new-array {v0}, [I

    move-result-object v1

    move-object v5, v1

    .line 64
    .local v5, "season":[I
    filled-new-array {v0}, [I

    move-result-object v0

    move-object v4, v0

    .line 66
    .local v4, "episode":[I
    if-eqz v14, :cond_d

    .line 67
    sget v0, Lcom/ultragol/app/R$id;->tvSeason:I

    invoke-virtual {v7, v0}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v0

    move-object v3, v0

    check-cast v3, Landroid/widget/TextView;

    .line 68
    .local v3, "tvS":Landroid/widget/TextView;
    sget v0, Lcom/ultragol/app/R$id;->tvEpisode:I

    invoke-virtual {v7, v0}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v0

    move-object v2, v0

    check-cast v2, Landroid/widget/TextView;

    .line 69
    .local v2, "tvE":Landroid/widget/TextView;
    sget v0, Lcom/ultragol/app/R$id;->btnSeasonMinus:I

    invoke-virtual {v7, v0}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v1

    .line 70
    .local v1, "bSM":Landroid/view/View;
    sget v0, Lcom/ultragol/app/R$id;->btnSeasonPlus:I

    invoke-virtual {v7, v0}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v0

    .line 71
    .local v0, "bSP":Landroid/view/View;
    sget v13, Lcom/ultragol/app/R$id;->btnEpMinus:I

    invoke-virtual {v7, v13}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v13

    .line 72
    .local v13, "bEM":Landroid/view/View;
    move-object/from16 v16, v8

    .end local v8    # "win":Landroid/view/Window;
    .local v16, "win":Landroid/view/Window;
    sget v8, Lcom/ultragol/app/R$id;->btnEpPlus:I

    invoke-virtual {v7, v8}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v8

    .line 73
    .local v8, "bEP":Landroid/view/View;
    move-object/from16 v17, v9

    .end local v9    # "dismiss":Landroid/view/View;
    .local v17, "dismiss":Landroid/view/View;
    sget v9, Lcom/ultragol/app/R$id;->btnLoadEpisode:I

    invoke-virtual {v7, v9}, Landroid/app/Dialog;->findViewById(I)Landroid/view/View;

    move-result-object v9

    .line 74
    .local v9, "bLoad":Landroid/view/View;
    if-eqz v1, :cond_8

    move-object/from16 v18, v10

    .end local v10    # "tvTitle":Landroid/widget/TextView;
    .local v18, "tvTitle":Landroid/widget/TextView;
    new-instance v10, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda5;

    invoke-direct {v10, v5, v3}, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda5;-><init>([ILandroid/widget/TextView;)V

    invoke-virtual {v1, v10}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    goto :goto_3

    .end local v18    # "tvTitle":Landroid/widget/TextView;
    .restart local v10    # "tvTitle":Landroid/widget/TextView;
    :cond_8
    move-object/from16 v18, v10

    .line 75
    .end local v10    # "tvTitle":Landroid/widget/TextView;
    .restart local v18    # "tvTitle":Landroid/widget/TextView;
    :goto_3
    if-eqz v0, :cond_9

    new-instance v10, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda6;

    invoke-direct {v10, v5, v3}, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda6;-><init>([ILandroid/widget/TextView;)V

    invoke-virtual {v0, v10}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 76
    :cond_9
    if-eqz v13, :cond_a

    new-instance v10, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda7;

    invoke-direct {v10, v4, v2}, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda7;-><init>([ILandroid/widget/TextView;)V

    invoke-virtual {v13, v10}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 77
    :cond_a
    if-eqz v8, :cond_b

    new-instance v10, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda8;

    invoke-direct {v10, v4, v2}, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda8;-><init>([ILandroid/widget/TextView;)V

    invoke-virtual {v8, v10}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 78
    :cond_b
    if-eqz v9, :cond_c

    new-instance v10, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda9;

    move-object/from16 v19, v0

    .end local v0    # "bSP":Landroid/view/View;
    .local v19, "bSP":Landroid/view/View;
    move-object v0, v10

    move-object/from16 v20, v1

    .end local v1    # "bSM":Landroid/view/View;
    .local v20, "bSM":Landroid/view/View;
    move-object/from16 v1, p0

    move-object/from16 v21, v2

    .end local v2    # "tvE":Landroid/widget/TextView;
    .local v21, "tvE":Landroid/widget/TextView;
    move-object v2, v7

    move-object/from16 v22, v3

    .end local v3    # "tvS":Landroid/widget/TextView;
    .local v22, "tvS":Landroid/widget/TextView;
    move-object/from16 v3, p1

    move-object/from16 v23, v4

    .end local v4    # "episode":[I
    .local v23, "episode":[I
    move-object v4, v5

    move-object/from16 v24, v5

    .end local v5    # "season":[I
    .local v24, "season":[I
    move-object/from16 v5, v23

    invoke-direct/range {v0 .. v5}, Lcom/ultragol/app/ServerSelectDialog$$ExternalSyntheticLambda9;-><init>(Landroid/content/Context;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;[I[I)V

    invoke-virtual {v9, v10}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    goto :goto_4

    .end local v19    # "bSP":Landroid/view/View;
    .end local v20    # "bSM":Landroid/view/View;
    .end local v21    # "tvE":Landroid/widget/TextView;
    .end local v22    # "tvS":Landroid/widget/TextView;
    .end local v23    # "episode":[I
    .end local v24    # "season":[I
    .restart local v0    # "bSP":Landroid/view/View;
    .restart local v1    # "bSM":Landroid/view/View;
    .restart local v2    # "tvE":Landroid/widget/TextView;
    .restart local v3    # "tvS":Landroid/widget/TextView;
    .restart local v4    # "episode":[I
    .restart local v5    # "season":[I
    :cond_c
    move-object/from16 v19, v0

    move-object/from16 v20, v1

    move-object/from16 v21, v2

    move-object/from16 v22, v3

    move-object/from16 v23, v4

    move-object/from16 v24, v5

    .end local v0    # "bSP":Landroid/view/View;
    .end local v1    # "bSM":Landroid/view/View;
    .end local v2    # "tvE":Landroid/widget/TextView;
    .end local v3    # "tvS":Landroid/widget/TextView;
    .end local v4    # "episode":[I
    .end local v5    # "season":[I
    .restart local v19    # "bSP":Landroid/view/View;
    .restart local v20    # "bSM":Landroid/view/View;
    .restart local v21    # "tvE":Landroid/widget/TextView;
    .restart local v22    # "tvS":Landroid/widget/TextView;
    .restart local v23    # "episode":[I
    .restart local v24    # "season":[I
    goto :goto_4

    .line 66
    .end local v13    # "bEM":Landroid/view/View;
    .end local v16    # "win":Landroid/view/Window;
    .end local v17    # "dismiss":Landroid/view/View;
    .end local v18    # "tvTitle":Landroid/widget/TextView;
    .end local v19    # "bSP":Landroid/view/View;
    .end local v20    # "bSM":Landroid/view/View;
    .end local v21    # "tvE":Landroid/widget/TextView;
    .end local v22    # "tvS":Landroid/widget/TextView;
    .end local v23    # "episode":[I
    .end local v24    # "season":[I
    .restart local v4    # "episode":[I
    .restart local v5    # "season":[I
    .local v8, "win":Landroid/view/Window;
    .local v9, "dismiss":Landroid/view/View;
    .restart local v10    # "tvTitle":Landroid/widget/TextView;
    :cond_d
    move-object/from16 v23, v4

    move-object/from16 v24, v5

    move-object/from16 v16, v8

    move-object/from16 v17, v9

    move-object/from16 v18, v10

    .line 81
    .end local v4    # "episode":[I
    .end local v5    # "season":[I
    .end local v8    # "win":Landroid/view/Window;
    .end local v9    # "dismiss":Landroid/view/View;
    .end local v10    # "tvTitle":Landroid/widget/TextView;
    .restart local v16    # "win":Landroid/view/Window;
    .restart local v17    # "dismiss":Landroid/view/View;
    .restart local v18    # "tvTitle":Landroid/widget/TextView;
    .restart local v23    # "episode":[I
    .restart local v24    # "season":[I
    :goto_4
    const/4 v0, 0x0

    aget v1, v24, v0

    aget v0, v23, v0

    move-object/from16 v2, p1

    invoke-static {v6, v7, v2, v1, v0}, Lcom/ultragol/app/ServerSelectDialog;->loadServers(Landroid/content/Context;Landroid/app/Dialog;Lcom/ultragol/app/models/ContentItem;II)V

    .line 82
    invoke-virtual {v7}, Landroid/app/Dialog;->show()V

    .line 83
    return-void

    nop

    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_2
        :pswitch_1
        :pswitch_0
    .end packed-switch
.end method
