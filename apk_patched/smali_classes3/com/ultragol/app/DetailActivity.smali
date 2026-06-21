.class public Lcom/ultragol/app/DetailActivity;
.super Landroidx/appcompat/app/AppCompatActivity;
.source "DetailActivity.java"


# instance fields
.field private item:Lcom/ultragol/app/models/ContentItem;


# direct methods
.method public constructor <init>()V
    .locals 0

    .line 20
    invoke-direct {p0}, Landroidx/appcompat/app/AppCompatActivity;-><init>()V

    return-void
.end method

.method private bindViews()V
    .locals 26

    .line 37
    move-object/from16 v0, p0

    sget v1, Lcom/ultragol/app/R$id;->detailBackdrop:I

    invoke-virtual {v0, v1}, Lcom/ultragol/app/DetailActivity;->findViewById(I)Landroid/view/View;

    move-result-object v1

    check-cast v1, Landroid/widget/ImageView;

    .line 38
    .local v1, "backdrop":Landroid/widget/ImageView;
    sget v2, Lcom/ultragol/app/R$id;->detailPoster:I

    invoke-virtual {v0, v2}, Lcom/ultragol/app/DetailActivity;->findViewById(I)Landroid/view/View;

    move-result-object v2

    check-cast v2, Landroid/widget/ImageView;

    .line 39
    .local v2, "poster":Landroid/widget/ImageView;
    sget v3, Lcom/ultragol/app/R$id;->detailTypeBadge:I

    invoke-virtual {v0, v3}, Lcom/ultragol/app/DetailActivity;->findViewById(I)Landroid/view/View;

    move-result-object v3

    check-cast v3, Landroid/widget/TextView;

    .line 40
    .local v3, "typeBadge":Landroid/widget/TextView;
    sget v4, Lcom/ultragol/app/R$id;->detailBadge:I

    invoke-virtual {v0, v4}, Lcom/ultragol/app/DetailActivity;->findViewById(I)Landroid/view/View;

    move-result-object v4

    check-cast v4, Landroid/widget/TextView;

    .line 41
    .local v4, "ratingBadge":Landroid/widget/TextView;
    sget v5, Lcom/ultragol/app/R$id;->detailTitle:I

    invoke-virtual {v0, v5}, Lcom/ultragol/app/DetailActivity;->findViewById(I)Landroid/view/View;

    move-result-object v5

    check-cast v5, Landroid/widget/TextView;

    .line 42
    .local v5, "title":Landroid/widget/TextView;
    sget v6, Lcom/ultragol/app/R$id;->detailMeta:I

    invoke-virtual {v0, v6}, Lcom/ultragol/app/DetailActivity;->findViewById(I)Landroid/view/View;

    move-result-object v6

    check-cast v6, Landroid/widget/TextView;

    .line 43
    .local v6, "meta":Landroid/widget/TextView;
    sget v7, Lcom/ultragol/app/R$id;->detailRating:I

    invoke-virtual {v0, v7}, Lcom/ultragol/app/DetailActivity;->findViewById(I)Landroid/view/View;

    move-result-object v7

    check-cast v7, Landroid/widget/TextView;

    .line 44
    .local v7, "rating":Landroid/widget/TextView;
    sget v8, Lcom/ultragol/app/R$id;->detailOverview:I

    invoke-virtual {v0, v8}, Lcom/ultragol/app/DetailActivity;->findViewById(I)Landroid/view/View;

    move-result-object v8

    check-cast v8, Landroid/widget/TextView;

    .line 45
    .local v8, "overview":Landroid/widget/TextView;
    sget v9, Lcom/ultragol/app/R$id;->genreChips:I

    invoke-virtual {v0, v9}, Lcom/ultragol/app/DetailActivity;->findViewById(I)Landroid/view/View;

    move-result-object v9

    check-cast v9, Landroid/widget/LinearLayout;

    .line 46
    .local v9, "genreChips":Landroid/widget/LinearLayout;
    sget v10, Lcom/ultragol/app/R$id;->btnPlay:I

    invoke-virtual {v0, v10}, Lcom/ultragol/app/DetailActivity;->findViewById(I)Landroid/view/View;

    move-result-object v10

    .line 47
    .local v10, "btnPlay":Landroid/view/View;
    sget v11, Lcom/ultragol/app/R$id;->btnDetailBack:I

    invoke-virtual {v0, v11}, Lcom/ultragol/app/DetailActivity;->findViewById(I)Landroid/view/View;

    move-result-object v11

    .line 48
    .local v11, "btnBack":Landroid/view/View;
    sget v12, Lcom/ultragol/app/R$id;->btnFavorite:I

    invoke-virtual {v0, v12}, Lcom/ultragol/app/DetailActivity;->findViewById(I)Landroid/view/View;

    move-result-object v12

    check-cast v12, Landroid/widget/LinearLayout;

    .line 49
    .local v12, "btnFavorite":Landroid/widget/LinearLayout;
    sget v13, Lcom/ultragol/app/R$id;->btnMyList:I

    invoke-virtual {v0, v13}, Lcom/ultragol/app/DetailActivity;->findViewById(I)Landroid/view/View;

    move-result-object v13

    check-cast v13, Landroid/widget/LinearLayout;

    .line 52
    .local v13, "btnMyList":Landroid/widget/LinearLayout;
    if-eqz v5, :cond_0

    iget-object v14, v0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-virtual {v14}, Lcom/ultragol/app/models/ContentItem;->getTitle()Ljava/lang/String;

    move-result-object v14

    invoke-virtual {v5, v14}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 55
    :cond_0
    const/4 v14, 0x0

    if-eqz v3, :cond_1

    .line 58
    iget-object v15, v0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-virtual {v15}, Lcom/ultragol/app/models/ContentItem;->getContentType()I

    move-result v15

    packed-switch v15, :pswitch_data_0

    .line 70
    const-string v15, "PEL\u00cdCULA"

    .local v15, "label":Ljava/lang/String;
    const-string v16, "#CC1111"

    invoke-static/range {v16 .. v16}, Landroid/graphics/Color;->parseColor(Ljava/lang/String;)I

    move-result v16

    move/from16 v17, v16

    .local v16, "badgeColor":I
    goto :goto_0

    .line 68
    .end local v15    # "label":Ljava/lang/String;
    .end local v16    # "badgeColor":I
    :pswitch_0
    const-string v15, "TV"

    .restart local v15    # "label":Ljava/lang/String;
    const-string v16, "#2E7D32"

    invoke-static/range {v16 .. v16}, Landroid/graphics/Color;->parseColor(Ljava/lang/String;)I

    move-result v16

    move/from16 v17, v16

    .restart local v16    # "badgeColor":I
    goto :goto_0

    .line 66
    .end local v15    # "label":Ljava/lang/String;
    .end local v16    # "badgeColor":I
    :pswitch_1
    const-string v15, "EN VIVO"

    .restart local v15    # "label":Ljava/lang/String;
    const-string v16, "#C62828"

    invoke-static/range {v16 .. v16}, Landroid/graphics/Color;->parseColor(Ljava/lang/String;)I

    move-result v16

    move/from16 v17, v16

    .restart local v16    # "badgeColor":I
    goto :goto_0

    .line 64
    .end local v15    # "label":Ljava/lang/String;
    .end local v16    # "badgeColor":I
    :pswitch_2
    const-string v15, "DORAMA"

    .restart local v15    # "label":Ljava/lang/String;
    const-string v16, "#00695C"

    invoke-static/range {v16 .. v16}, Landroid/graphics/Color;->parseColor(Ljava/lang/String;)I

    move-result v16

    move/from16 v17, v16

    .restart local v16    # "badgeColor":I
    goto :goto_0

    .line 62
    .end local v15    # "label":Ljava/lang/String;
    .end local v16    # "badgeColor":I
    :pswitch_3
    const-string v15, "ANIME"

    .restart local v15    # "label":Ljava/lang/String;
    const-string v16, "#AD1457"

    invoke-static/range {v16 .. v16}, Landroid/graphics/Color;->parseColor(Ljava/lang/String;)I

    move-result v16

    move/from16 v17, v16

    .restart local v16    # "badgeColor":I
    goto :goto_0

    .line 60
    .end local v15    # "label":Ljava/lang/String;
    .end local v16    # "badgeColor":I
    :pswitch_4
    const-string v15, "SERIE"

    .restart local v15    # "label":Ljava/lang/String;
    const-string v16, "#00838F"

    invoke-static/range {v16 .. v16}, Landroid/graphics/Color;->parseColor(Ljava/lang/String;)I

    move-result v16

    move/from16 v17, v16

    .line 72
    .local v17, "badgeColor":I
    :goto_0
    invoke-virtual {v3, v15}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 73
    new-instance v16, Landroid/graphics/drawable/GradientDrawable;

    invoke-direct/range {v16 .. v16}, Landroid/graphics/drawable/GradientDrawable;-><init>()V

    move-object/from16 v18, v16

    .line 74
    .local v18, "bg":Landroid/graphics/drawable/GradientDrawable;
    move-object/from16 v16, v5

    move-object/from16 v5, v18

    .end local v18    # "bg":Landroid/graphics/drawable/GradientDrawable;
    .local v5, "bg":Landroid/graphics/drawable/GradientDrawable;
    .local v16, "title":Landroid/widget/TextView;
    invoke-virtual {v5, v14}, Landroid/graphics/drawable/GradientDrawable;->setShape(I)V

    .line 75
    move/from16 v14, v17

    .end local v17    # "badgeColor":I
    .local v14, "badgeColor":I
    invoke-virtual {v5, v14}, Landroid/graphics/drawable/GradientDrawable;->setColor(I)V

    .line 76
    nop

    .end local v14    # "badgeColor":I
    .restart local v17    # "badgeColor":I
    const/4 v14, 0x5

    invoke-direct {v0, v14}, Lcom/ultragol/app/DetailActivity;->dp(I)I

    move-result v14

    int-to-float v14, v14

    invoke-virtual {v5, v14}, Landroid/graphics/drawable/GradientDrawable;->setCornerRadius(F)V

    .line 77
    invoke-virtual {v3, v5}, Landroid/widget/TextView;->setBackground(Landroid/graphics/drawable/Drawable;)V

    goto :goto_1

    .line 55
    .end local v15    # "label":Ljava/lang/String;
    .end local v16    # "title":Landroid/widget/TextView;
    .end local v17    # "badgeColor":I
    .local v5, "title":Landroid/widget/TextView;
    :cond_1
    move-object/from16 v16, v5

    .line 81
    .end local v5    # "title":Landroid/widget/TextView;
    .restart local v16    # "title":Landroid/widget/TextView;
    :goto_1
    if-eqz v4, :cond_2

    iget-object v5, v0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-virtual {v5}, Lcom/ultragol/app/models/ContentItem;->getBadge()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {v4, v5}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 84
    :cond_2
    if-eqz v6, :cond_6

    .line 85
    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    .line 86
    .local v5, "sb":Ljava/lang/StringBuilder;
    iget-object v14, v0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-virtual {v14}, Lcom/ultragol/app/models/ContentItem;->getYear()Ljava/lang/String;

    move-result-object v14

    invoke-virtual {v14}, Ljava/lang/String;->isEmpty()Z

    move-result v14

    if-nez v14, :cond_3

    iget-object v14, v0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-virtual {v14}, Lcom/ultragol/app/models/ContentItem;->getYear()Ljava/lang/String;

    move-result-object v14

    invoke-virtual {v5, v14}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 87
    :cond_3
    iget-object v14, v0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-virtual {v14}, Lcom/ultragol/app/models/ContentItem;->getRating()Ljava/lang/String;

    move-result-object v14

    invoke-virtual {v14}, Ljava/lang/String;->isEmpty()Z

    move-result v14

    if-nez v14, :cond_5

    .line 88
    invoke-virtual {v5}, Ljava/lang/StringBuilder;->length()I

    move-result v14

    if-lez v14, :cond_4

    const-string v14, "   \u00b7   "

    invoke-virtual {v5, v14}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 89
    :cond_4
    iget-object v14, v0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-virtual {v14}, Lcom/ultragol/app/models/ContentItem;->getRating()Ljava/lang/String;

    move-result-object v14

    invoke-virtual {v5, v14}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 91
    :cond_5
    const-string v14, "   \u00b7   ES"

    invoke-virtual {v5, v14}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 92
    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v14

    invoke-virtual {v6, v14}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 96
    .end local v5    # "sb":Ljava/lang/StringBuilder;
    :cond_6
    if-eqz v7, :cond_8

    .line 97
    iget-object v5, v0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-virtual {v5}, Lcom/ultragol/app/models/ContentItem;->getRating()Ljava/lang/String;

    move-result-object v5

    .line 98
    .local v5, "r":Ljava/lang/String;
    invoke-virtual {v5}, Ljava/lang/String;->isEmpty()Z

    move-result v14

    if-eqz v14, :cond_7

    const-string v14, "\u2014"

    goto :goto_2

    :cond_7
    move-object v14, v5

    :goto_2
    invoke-virtual {v7, v14}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 102
    .end local v5    # "r":Ljava/lang/String;
    :cond_8
    if-eqz v9, :cond_b

    iget-object v5, v0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-virtual {v5}, Lcom/ultragol/app/models/ContentItem;->getGenre()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {v5}, Ljava/lang/String;->isEmpty()Z

    move-result v5

    if-nez v5, :cond_b

    .line 103
    iget-object v5, v0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-virtual {v5}, Lcom/ultragol/app/models/ContentItem;->getGenre()Ljava/lang/String;

    move-result-object v5

    const-string v14, "[,/\u2022]"

    invoke-virtual {v5, v14}, Ljava/lang/String;->split(Ljava/lang/String;)[Ljava/lang/String;

    move-result-object v5

    .line 104
    .local v5, "genres":[Ljava/lang/String;
    array-length v14, v5

    const/4 v15, 0x0

    :goto_3
    if-ge v15, v14, :cond_a

    aget-object v17, v5, v15

    .line 105
    .local v17, "g":Ljava/lang/String;
    move-object/from16 v19, v3

    .end local v3    # "typeBadge":Landroid/widget/TextView;
    .local v19, "typeBadge":Landroid/widget/TextView;
    invoke-virtual/range {v17 .. v17}, Ljava/lang/String;->trim()Ljava/lang/String;

    move-result-object v3

    .line 106
    .local v3, "lbl":Ljava/lang/String;
    invoke-virtual {v3}, Ljava/lang/String;->isEmpty()Z

    move-result v20

    if-eqz v20, :cond_9

    move-object/from16 v20, v4

    move-object/from16 v22, v5

    move-object/from16 v23, v6

    goto :goto_4

    .line 107
    :cond_9
    move-object/from16 v20, v4

    .end local v4    # "ratingBadge":Landroid/widget/TextView;
    .local v20, "ratingBadge":Landroid/widget/TextView;
    new-instance v4, Landroid/widget/TextView;

    invoke-direct {v4, v0}, Landroid/widget/TextView;-><init>(Landroid/content/Context;)V

    .line 108
    .local v4, "chip":Landroid/widget/TextView;
    invoke-virtual {v4, v3}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 109
    move-object/from16 v21, v3

    .end local v3    # "lbl":Ljava/lang/String;
    .local v21, "lbl":Ljava/lang/String;
    const v3, -0x33000001    # -1.3421772E8f

    invoke-virtual {v4, v3}, Landroid/widget/TextView;->setTextColor(I)V

    .line 110
    const/4 v3, 0x2

    move-object/from16 v22, v5

    .end local v5    # "genres":[Ljava/lang/String;
    .local v22, "genres":[Ljava/lang/String;
    const/high16 v5, 0x41400000    # 12.0f

    invoke-virtual {v4, v3, v5}, Landroid/widget/TextView;->setTextSize(IF)V

    .line 111
    new-instance v3, Landroid/graphics/drawable/GradientDrawable;

    invoke-direct {v3}, Landroid/graphics/drawable/GradientDrawable;-><init>()V

    .line 112
    .local v3, "chipBg":Landroid/graphics/drawable/GradientDrawable;
    const/4 v5, 0x0

    invoke-virtual {v3, v5}, Landroid/graphics/drawable/GradientDrawable;->setShape(I)V

    .line 113
    invoke-virtual {v3, v5}, Landroid/graphics/drawable/GradientDrawable;->setColor(I)V

    .line 114
    const/4 v5, 0x1

    invoke-direct {v0, v5}, Lcom/ultragol/app/DetailActivity;->dp(I)I

    move-result v5

    move-object/from16 v23, v6

    .end local v6    # "meta":Landroid/widget/TextView;
    .local v23, "meta":Landroid/widget/TextView;
    const v6, 0x55ffffff    # 3.518437E13f

    invoke-virtual {v3, v5, v6}, Landroid/graphics/drawable/GradientDrawable;->setStroke(II)V

    .line 115
    const/16 v5, 0x14

    invoke-direct {v0, v5}, Lcom/ultragol/app/DetailActivity;->dp(I)I

    move-result v5

    int-to-float v5, v5

    invoke-virtual {v3, v5}, Landroid/graphics/drawable/GradientDrawable;->setCornerRadius(F)V

    .line 116
    invoke-virtual {v4, v3}, Landroid/widget/TextView;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 117
    const/16 v5, 0xe

    invoke-direct {v0, v5}, Lcom/ultragol/app/DetailActivity;->dp(I)I

    move-result v5

    .local v5, "padH":I
    const/4 v6, 0x6

    invoke-direct {v0, v6}, Lcom/ultragol/app/DetailActivity;->dp(I)I

    move-result v6

    .line 118
    .local v6, "padV":I
    invoke-virtual {v4, v5, v6, v5, v6}, Landroid/widget/TextView;->setPadding(IIII)V

    .line 119
    move-object/from16 v24, v3

    .end local v3    # "chipBg":Landroid/graphics/drawable/GradientDrawable;
    .local v24, "chipBg":Landroid/graphics/drawable/GradientDrawable;
    new-instance v3, Landroid/widget/LinearLayout$LayoutParams;

    move/from16 v25, v5

    .end local v5    # "padH":I
    .local v25, "padH":I
    const/4 v5, -0x2

    invoke-direct {v3, v5, v5}, Landroid/widget/LinearLayout$LayoutParams;-><init>(II)V

    .line 121
    .local v3, "lp":Landroid/widget/LinearLayout$LayoutParams;
    const/16 v5, 0x8

    invoke-direct {v0, v5}, Lcom/ultragol/app/DetailActivity;->dp(I)I

    move-result v5

    invoke-virtual {v3, v5}, Landroid/widget/LinearLayout$LayoutParams;->setMarginEnd(I)V

    .line 122
    invoke-virtual {v4, v3}, Landroid/widget/TextView;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    .line 123
    invoke-virtual {v9, v4}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 104
    .end local v3    # "lp":Landroid/widget/LinearLayout$LayoutParams;
    .end local v4    # "chip":Landroid/widget/TextView;
    .end local v6    # "padV":I
    .end local v17    # "g":Ljava/lang/String;
    .end local v21    # "lbl":Ljava/lang/String;
    .end local v24    # "chipBg":Landroid/graphics/drawable/GradientDrawable;
    .end local v25    # "padH":I
    :goto_4
    add-int/lit8 v15, v15, 0x1

    move-object/from16 v3, v19

    move-object/from16 v4, v20

    move-object/from16 v5, v22

    move-object/from16 v6, v23

    goto/16 :goto_3

    .end local v19    # "typeBadge":Landroid/widget/TextView;
    .end local v20    # "ratingBadge":Landroid/widget/TextView;
    .end local v22    # "genres":[Ljava/lang/String;
    .end local v23    # "meta":Landroid/widget/TextView;
    .local v3, "typeBadge":Landroid/widget/TextView;
    .local v4, "ratingBadge":Landroid/widget/TextView;
    .local v5, "genres":[Ljava/lang/String;
    .local v6, "meta":Landroid/widget/TextView;
    :cond_a
    move-object/from16 v19, v3

    move-object/from16 v20, v4

    move-object/from16 v22, v5

    move-object/from16 v23, v6

    .end local v3    # "typeBadge":Landroid/widget/TextView;
    .end local v4    # "ratingBadge":Landroid/widget/TextView;
    .end local v5    # "genres":[Ljava/lang/String;
    .end local v6    # "meta":Landroid/widget/TextView;
    .restart local v19    # "typeBadge":Landroid/widget/TextView;
    .restart local v20    # "ratingBadge":Landroid/widget/TextView;
    .restart local v22    # "genres":[Ljava/lang/String;
    .restart local v23    # "meta":Landroid/widget/TextView;
    goto :goto_5

    .line 102
    .end local v19    # "typeBadge":Landroid/widget/TextView;
    .end local v20    # "ratingBadge":Landroid/widget/TextView;
    .end local v22    # "genres":[Ljava/lang/String;
    .end local v23    # "meta":Landroid/widget/TextView;
    .restart local v3    # "typeBadge":Landroid/widget/TextView;
    .restart local v4    # "ratingBadge":Landroid/widget/TextView;
    .restart local v6    # "meta":Landroid/widget/TextView;
    :cond_b
    move-object/from16 v19, v3

    move-object/from16 v20, v4

    move-object/from16 v23, v6

    .line 128
    .end local v3    # "typeBadge":Landroid/widget/TextView;
    .end local v4    # "ratingBadge":Landroid/widget/TextView;
    .end local v6    # "meta":Landroid/widget/TextView;
    .restart local v19    # "typeBadge":Landroid/widget/TextView;
    .restart local v20    # "ratingBadge":Landroid/widget/TextView;
    .restart local v23    # "meta":Landroid/widget/TextView;
    :goto_5
    if-eqz v8, :cond_c

    iget-object v3, v0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-virtual {v3}, Lcom/ultragol/app/models/ContentItem;->getOverview()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v8, v3}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 131
    :cond_c
    if-eqz v1, :cond_d

    iget-object v3, v0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-virtual {v3}, Lcom/ultragol/app/models/ContentItem;->getBackdropUrl()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/String;->isEmpty()Z

    move-result v3

    if-nez v3, :cond_d

    .line 132
    invoke-static/range {p0 .. p0}, Lcom/bumptech/glide/Glide;->with(Landroidx/fragment/app/FragmentActivity;)Lcom/bumptech/glide/RequestManager;

    move-result-object v3

    iget-object v4, v0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-virtual {v4}, Lcom/ultragol/app/models/ContentItem;->getBackdropUrl()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v3, v4}, Lcom/bumptech/glide/RequestManager;->load(Ljava/lang/String;)Lcom/bumptech/glide/RequestBuilder;

    move-result-object v3

    .line 133
    invoke-static {}, Lcom/bumptech/glide/load/resource/drawable/DrawableTransitionOptions;->withCrossFade()Lcom/bumptech/glide/load/resource/drawable/DrawableTransitionOptions;

    move-result-object v4

    invoke-virtual {v3, v4}, Lcom/bumptech/glide/RequestBuilder;->transition(Lcom/bumptech/glide/TransitionOptions;)Lcom/bumptech/glide/RequestBuilder;

    move-result-object v3

    invoke-virtual {v3, v1}, Lcom/bumptech/glide/RequestBuilder;->into(Landroid/widget/ImageView;)Lcom/bumptech/glide/request/target/ViewTarget;

    .line 135
    :cond_d
    if-eqz v2, :cond_e

    iget-object v3, v0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-virtual {v3}, Lcom/ultragol/app/models/ContentItem;->getPosterUrl()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/String;->isEmpty()Z

    move-result v3

    if-nez v3, :cond_e

    .line 136
    invoke-static/range {p0 .. p0}, Lcom/bumptech/glide/Glide;->with(Landroidx/fragment/app/FragmentActivity;)Lcom/bumptech/glide/RequestManager;

    move-result-object v3

    iget-object v4, v0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-virtual {v4}, Lcom/ultragol/app/models/ContentItem;->getPosterUrl()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v3, v4}, Lcom/bumptech/glide/RequestManager;->load(Ljava/lang/String;)Lcom/bumptech/glide/RequestBuilder;

    move-result-object v3

    .line 137
    invoke-static {}, Lcom/bumptech/glide/load/resource/drawable/DrawableTransitionOptions;->withCrossFade()Lcom/bumptech/glide/load/resource/drawable/DrawableTransitionOptions;

    move-result-object v4

    invoke-virtual {v3, v4}, Lcom/bumptech/glide/RequestBuilder;->transition(Lcom/bumptech/glide/TransitionOptions;)Lcom/bumptech/glide/RequestBuilder;

    move-result-object v3

    invoke-virtual {v3, v2}, Lcom/bumptech/glide/RequestBuilder;->into(Landroid/widget/ImageView;)Lcom/bumptech/glide/request/target/ViewTarget;

    .line 141
    :cond_e
    if-eqz v11, :cond_f

    new-instance v3, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda2;

    invoke-direct {v3, v0}, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda2;-><init>(Lcom/ultragol/app/DetailActivity;)V

    invoke-virtual {v11, v3}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 145
    :cond_f
    if-eqz v10, :cond_10

    new-instance v3, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda3;

    invoke-direct {v3, v0}, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda3;-><init>(Lcom/ultragol/app/DetailActivity;)V

    invoke-virtual {v10, v3}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 149
    :cond_10
    if-eqz v12, :cond_11

    .line 150
    invoke-direct {v0, v12}, Lcom/ultragol/app/DetailActivity;->updateFavoriteBtn(Landroid/widget/LinearLayout;)V

    .line 151
    new-instance v3, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda4;

    invoke-direct {v3, v0, v12}, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda4;-><init>(Lcom/ultragol/app/DetailActivity;Landroid/widget/LinearLayout;)V

    invoke-virtual {v12, v3}, Landroid/widget/LinearLayout;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 162
    :cond_11
    if-eqz v13, :cond_12

    .line 163
    invoke-direct {v0, v13}, Lcom/ultragol/app/DetailActivity;->updateMyListBtn(Landroid/widget/LinearLayout;)V

    .line 164
    new-instance v3, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda5;

    invoke-direct {v3, v0, v13}, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda5;-><init>(Lcom/ultragol/app/DetailActivity;Landroid/widget/LinearLayout;)V

    invoke-virtual {v13, v3}, Landroid/widget/LinearLayout;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 173
    :cond_12
    return-void

    nop

    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_4
        :pswitch_3
        :pswitch_2
        :pswitch_1
        :pswitch_0
    .end packed-switch
.end method

.method private dp(I)I
    .locals 3
    .param p1, "value"    # I

    .line 237
    int-to-float v0, p1

    .line 238
    invoke-virtual {p0}, Lcom/ultragol/app/DetailActivity;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    invoke-virtual {v1}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    move-result-object v1

    .line 237
    const/4 v2, 0x1

    invoke-static {v2, v0, v1}, Landroid/util/TypedValue;->applyDimension(IFLandroid/util/DisplayMetrics;)F

    move-result v0

    float-to-int v0, v0

    return v0
.end method

.method private loadRelated()V
    .locals 7

    .line 200
    sget v0, Lcom/ultragol/app/R$id;->rowRelated:I

    invoke-virtual {p0, v0}, Lcom/ultragol/app/DetailActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    .line 201
    .local v0, "rowRelated":Landroid/view/View;
    if-nez v0, :cond_0

    return-void

    .line 203
    :cond_0
    sget v1, Lcom/ultragol/app/R$id;->rowTitle:I

    invoke-virtual {v0, v1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v1

    check-cast v1, Landroid/widget/TextView;

    .line 204
    .local v1, "rowTitle":Landroid/widget/TextView;
    sget v2, Lcom/ultragol/app/R$id;->rowRv:I

    invoke-virtual {v0, v2}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object v2

    check-cast v2, Landroidx/recyclerview/widget/RecyclerView;

    .line 205
    .local v2, "rv":Landroidx/recyclerview/widget/RecyclerView;
    if-eqz v2, :cond_1

    new-instance v3, Landroidx/recyclerview/widget/LinearLayoutManager;

    const/4 v4, 0x0

    invoke-direct {v3, p0, v4, v4}, Landroidx/recyclerview/widget/LinearLayoutManager;-><init>(Landroid/content/Context;IZ)V

    invoke-virtual {v2, v3}, Landroidx/recyclerview/widget/RecyclerView;->setLayoutManager(Landroidx/recyclerview/widget/RecyclerView$LayoutManager;)V

    .line 208
    :cond_1
    iget-object v3, p0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-virtual {v3}, Lcom/ultragol/app/models/ContentItem;->getContentType()I

    move-result v3

    if-nez v3, :cond_2

    .line 209
    const-string v3, "Pel\u00edculas Relacionadas"

    goto :goto_0

    .line 210
    :cond_2
    iget-object v3, p0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-virtual {v3}, Lcom/ultragol/app/models/ContentItem;->getContentType()I

    move-result v3

    const/4 v4, 0x2

    if-ne v3, v4, :cond_3

    .line 211
    const-string v3, "Animes Relacionados"

    goto :goto_0

    .line 212
    :cond_3
    const-string v3, "Series Relacionadas"

    :goto_0
    nop

    .line 213
    .local v3, "sectionTitle":Ljava/lang/String;
    if-eqz v1, :cond_4

    invoke-virtual {v1, v3}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 215
    :cond_4
    new-instance v4, Landroid/os/Handler;

    invoke-static {}, Landroid/os/Looper;->getMainLooper()Landroid/os/Looper;

    move-result-object v5

    invoke-direct {v4, v5}, Landroid/os/Handler;-><init>(Landroid/os/Looper;)V

    .line 216
    .local v4, "h":Landroid/os/Handler;
    invoke-static {}, Ljava/util/concurrent/Executors;->newSingleThreadExecutor()Ljava/util/concurrent/ExecutorService;

    move-result-object v5

    .line 217
    .local v5, "pool":Ljava/util/concurrent/ExecutorService;
    new-instance v6, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda1;

    invoke-direct {v6, p0, v4, v2}, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda1;-><init>(Lcom/ultragol/app/DetailActivity;Landroid/os/Handler;Landroidx/recyclerview/widget/RecyclerView;)V

    invoke-interface {v5, v6}, Ljava/util/concurrent/ExecutorService;->execute(Ljava/lang/Runnable;)V

    .line 233
    invoke-interface {v5}, Ljava/util/concurrent/ExecutorService;->shutdown()V

    .line 234
    return-void
.end method

.method private updateFavoriteBtn(Landroid/widget/LinearLayout;)V
    .locals 6
    .param p1, "btn"    # Landroid/widget/LinearLayout;

    .line 176
    iget-object v0, p0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-static {p0, v0}, Lcom/ultragol/app/FavoritesManager;->isFav(Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;)Z

    move-result v0

    .line 177
    .local v0, "isFav":Z
    invoke-virtual {p1}, Landroid/widget/LinearLayout;->getChildCount()I

    move-result v1

    const/4 v2, 0x2

    if-lt v1, v2, :cond_4

    .line 178
    const/4 v1, 0x0

    invoke-virtual {p1, v1}, Landroid/widget/LinearLayout;->getChildAt(I)Landroid/view/View;

    move-result-object v1

    check-cast v1, Landroid/widget/TextView;

    .line 179
    .local v1, "icon":Landroid/widget/TextView;
    const/4 v2, 0x1

    invoke-virtual {p1, v2}, Landroid/widget/LinearLayout;->getChildAt(I)Landroid/view/View;

    move-result-object v2

    check-cast v2, Landroid/widget/TextView;

    .line 180
    .local v2, "label":Landroid/widget/TextView;
    if-eqz v0, :cond_0

    const-string v3, "\u2665  "

    goto :goto_0

    :cond_0
    const-string v3, "\u2661  "

    :goto_0
    invoke-virtual {v1, v3}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 181
    const-string v3, "#FF5252"

    const/4 v4, -0x1

    if-eqz v0, :cond_1

    invoke-static {v3}, Landroid/graphics/Color;->parseColor(Ljava/lang/String;)I

    move-result v5

    goto :goto_1

    :cond_1
    const/4 v5, -0x1

    :goto_1
    invoke-virtual {v1, v5}, Landroid/widget/TextView;->setTextColor(I)V

    .line 182
    if-eqz v0, :cond_2

    const-string v5, "Favorito \u2713"

    goto :goto_2

    :cond_2
    const-string v5, "Favorito"

    :goto_2
    invoke-virtual {v2, v5}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 183
    if-eqz v0, :cond_3

    invoke-static {v3}, Landroid/graphics/Color;->parseColor(Ljava/lang/String;)I

    move-result v4

    :cond_3
    invoke-virtual {v2, v4}, Landroid/widget/TextView;->setTextColor(I)V

    .line 185
    .end local v1    # "icon":Landroid/widget/TextView;
    .end local v2    # "label":Landroid/widget/TextView;
    :cond_4
    return-void
.end method

.method private updateMyListBtn(Landroid/widget/LinearLayout;)V
    .locals 6
    .param p1, "btn"    # Landroid/widget/LinearLayout;

    .line 188
    iget-object v0, p0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-static {p0, v0}, Lcom/ultragol/app/MyListManager;->isInList(Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;)Z

    move-result v0

    .line 189
    .local v0, "inList":Z
    invoke-virtual {p1}, Landroid/widget/LinearLayout;->getChildCount()I

    move-result v1

    const/4 v2, 0x2

    if-lt v1, v2, :cond_4

    .line 190
    const/4 v1, 0x0

    invoke-virtual {p1, v1}, Landroid/widget/LinearLayout;->getChildAt(I)Landroid/view/View;

    move-result-object v1

    check-cast v1, Landroid/widget/TextView;

    .line 191
    .local v1, "icon":Landroid/widget/TextView;
    const/4 v2, 0x1

    invoke-virtual {p1, v2}, Landroid/widget/LinearLayout;->getChildAt(I)Landroid/view/View;

    move-result-object v2

    check-cast v2, Landroid/widget/TextView;

    .line 192
    .local v2, "label":Landroid/widget/TextView;
    if-eqz v0, :cond_0

    const-string v3, "\u229e  "

    goto :goto_0

    :cond_0
    const-string v3, "\u229f  "

    :goto_0
    invoke-virtual {v1, v3}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 193
    const-string v3, "#4FC3F7"

    const/4 v4, -0x1

    if-eqz v0, :cond_1

    invoke-static {v3}, Landroid/graphics/Color;->parseColor(Ljava/lang/String;)I

    move-result v5

    goto :goto_1

    :cond_1
    const/4 v5, -0x1

    :goto_1
    invoke-virtual {v1, v5}, Landroid/widget/TextView;->setTextColor(I)V

    .line 194
    if-eqz v0, :cond_2

    const-string v5, "Mi Lista \u2713"

    goto :goto_2

    :cond_2
    const-string v5, "Mi Lista"

    :goto_2
    invoke-virtual {v2, v5}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    .line 195
    if-eqz v0, :cond_3

    invoke-static {v3}, Landroid/graphics/Color;->parseColor(Ljava/lang/String;)I

    move-result v4

    :cond_3
    invoke-virtual {v2, v4}, Landroid/widget/TextView;->setTextColor(I)V

    .line 197
    .end local v1    # "icon":Landroid/widget/TextView;
    .end local v2    # "label":Landroid/widget/TextView;
    :cond_4
    return-void
.end method


# virtual methods
.method synthetic lambda$bindViews$0$com-ultragol-app-DetailActivity(Landroid/view/View;)V
    .locals 2
    .param p1, "v"    # Landroid/view/View;

    .line 142
    invoke-virtual {p0}, Lcom/ultragol/app/DetailActivity;->finish()V

    .line 143
    const/4 v0, 0x0

    const v1, 0x10a0001

    invoke-virtual {p0, v0, v1}, Lcom/ultragol/app/DetailActivity;->overridePendingTransition(II)V

    .line 144
    return-void
.end method

.method synthetic lambda$bindViews$1$com-ultragol-app-DetailActivity(Landroid/view/View;)V
    .locals 1
    .param p1, "v"    # Landroid/view/View;

    .line 146
    iget-object v0, p0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-static {p0, v0}, Lcom/ultragol/app/ServerSelectDialog;->show(Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;)V

    return-void
.end method

.method synthetic lambda$bindViews$2$com-ultragol-app-DetailActivity(Landroid/widget/LinearLayout;Landroid/view/View;)V
    .locals 3
    .param p1, "btnFavorite"    # Landroid/widget/LinearLayout;
    .param p2, "v"    # Landroid/view/View;

    .line 152
    iget-object v0, p0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-static {p0, v0}, Lcom/ultragol/app/FavoritesManager;->toggle(Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;)V

    .line 153
    invoke-direct {p0, p1}, Lcom/ultragol/app/DetailActivity;->updateFavoriteBtn(Landroid/widget/LinearLayout;)V

    .line 154
    iget-object v0, p0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-static {p0, v0}, Lcom/ultragol/app/FavoritesManager;->isFav(Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;)Z

    move-result v0

    .line 155
    .local v0, "isFav":Z
    nop

    .line 156
    if-eqz v0, :cond_0

    const-string v1, "Agregado a Favoritos"

    goto :goto_0

    :cond_0
    const-string v1, "Eliminado de Favoritos"

    .line 155
    :goto_0
    const/4 v2, 0x0

    invoke-static {p0, v1, v2}, Landroid/widget/Toast;->makeText(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;

    move-result-object v1

    .line 157
    invoke-virtual {v1}, Landroid/widget/Toast;->show()V

    .line 158
    return-void
.end method

.method synthetic lambda$bindViews$3$com-ultragol-app-DetailActivity(Landroid/widget/LinearLayout;Landroid/view/View;)V
    .locals 3
    .param p1, "btnMyList"    # Landroid/widget/LinearLayout;
    .param p2, "v"    # Landroid/view/View;

    .line 165
    iget-object v0, p0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-static {p0, v0}, Lcom/ultragol/app/MyListManager;->toggle(Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;)V

    .line 166
    invoke-direct {p0, p1}, Lcom/ultragol/app/DetailActivity;->updateMyListBtn(Landroid/widget/LinearLayout;)V

    .line 167
    iget-object v0, p0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-static {p0, v0}, Lcom/ultragol/app/MyListManager;->isInList(Landroid/content/Context;Lcom/ultragol/app/models/ContentItem;)Z

    move-result v0

    .line 168
    .local v0, "inList":Z
    nop

    .line 169
    if-eqz v0, :cond_0

    const-string v1, "Agregado a Mi Lista"

    goto :goto_0

    :cond_0
    const-string v1, "Eliminado de Mi Lista"

    .line 168
    :goto_0
    const/4 v2, 0x0

    invoke-static {p0, v1, v2}, Landroid/widget/Toast;->makeText(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;

    move-result-object v1

    .line 170
    invoke-virtual {v1}, Landroid/widget/Toast;->show()V

    .line 171
    return-void
.end method

.method synthetic lambda$loadRelated$4$com-ultragol-app-DetailActivity(Landroidx/recyclerview/widget/RecyclerView;Ljava/util/List;)V
    .locals 1
    .param p1, "rv"    # Landroidx/recyclerview/widget/RecyclerView;
    .param p2, "related"    # Ljava/util/List;

    .line 227
    invoke-virtual {p0}, Lcom/ultragol/app/DetailActivity;->isFinishing()Z

    move-result v0

    if-nez v0, :cond_0

    if-eqz p1, :cond_0

    .line 228
    new-instance v0, Lcom/ultragol/app/adapters/ContentRowAdapter;

    invoke-direct {v0, p0, p2}, Lcom/ultragol/app/adapters/ContentRowAdapter;-><init>(Landroid/content/Context;Ljava/util/List;)V

    invoke-virtual {p1, v0}, Landroidx/recyclerview/widget/RecyclerView;->setAdapter(Landroidx/recyclerview/widget/RecyclerView$Adapter;)V

    .line 230
    :cond_0
    return-void
.end method

.method synthetic lambda$loadRelated$5$com-ultragol-app-DetailActivity(Landroid/os/Handler;Landroidx/recyclerview/widget/RecyclerView;)V
    .locals 2
    .param p1, "h"    # Landroid/os/Handler;
    .param p2, "rv"    # Landroidx/recyclerview/widget/RecyclerView;

    .line 220
    :try_start_0
    iget-object v0, p0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    invoke-virtual {v0}, Lcom/ultragol/app/models/ContentItem;->getContentType()I

    move-result v0

    packed-switch v0, :pswitch_data_0

    .line 224
    invoke-static {}, Lcom/ultragol/app/network/TmdbApi;->fetchMovies()Ljava/util/List;

    move-result-object v0

    goto :goto_0

    .line 223
    :pswitch_0
    invoke-static {}, Lcom/ultragol/app/network/TmdbApi;->fetchDoramas()Ljava/util/List;

    move-result-object v0

    .local v0, "related":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    goto :goto_0

    .line 221
    .end local v0    # "related":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    :pswitch_1
    invoke-static {}, Lcom/ultragol/app/network/TmdbApi;->fetchAnime()Ljava/util/List;

    move-result-object v0

    .restart local v0    # "related":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    goto :goto_0

    .line 222
    .end local v0    # "related":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    :pswitch_2
    invoke-static {}, Lcom/ultragol/app/network/TmdbApi;->fetchSeries()Ljava/util/List;

    move-result-object v0

    .restart local v0    # "related":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    nop

    .line 226
    :goto_0
    new-instance v1, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda0;

    invoke-direct {v1, p0, p2, v0}, Lcom/ultragol/app/DetailActivity$$ExternalSyntheticLambda0;-><init>(Lcom/ultragol/app/DetailActivity;Landroidx/recyclerview/widget/RecyclerView;Ljava/util/List;)V

    invoke-virtual {p1, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 231
    nop

    .end local v0    # "related":Ljava/util/List;, "Ljava/util/List<Lcom/ultragol/app/models/ContentItem;>;"
    goto :goto_1

    :catch_0
    move-exception v0

    .line 232
    :goto_1
    return-void

    nop

    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_2
        :pswitch_1
        :pswitch_0
    .end packed-switch
.end method

.method protected onCreate(Landroid/os/Bundle;)V
    .locals 2
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    .line 26
    invoke-super {p0, p1}, Landroidx/appcompat/app/AppCompatActivity;->onCreate(Landroid/os/Bundle;)V

    .line 27
    sget v0, Lcom/ultragol/app/R$layout;->activity_detail:I

    invoke-virtual {p0, v0}, Lcom/ultragol/app/DetailActivity;->setContentView(I)V

    .line 29
    invoke-virtual {p0}, Lcom/ultragol/app/DetailActivity;->getIntent()Landroid/content/Intent;

    move-result-object v0

    const-string v1, "item"

    invoke-virtual {v0, v1}, Landroid/content/Intent;->getSerializableExtra(Ljava/lang/String;)Ljava/io/Serializable;

    move-result-object v0

    check-cast v0, Lcom/ultragol/app/models/ContentItem;

    iput-object v0, p0, Lcom/ultragol/app/DetailActivity;->item:Lcom/ultragol/app/models/ContentItem;

    .line 30
    if-nez v0, :cond_0

    invoke-virtual {p0}, Lcom/ultragol/app/DetailActivity;->finish()V

    return-void

    .line 32
    :cond_0
    invoke-direct {p0}, Lcom/ultragol/app/DetailActivity;->bindViews()V

    .line 33
    invoke-direct {p0}, Lcom/ultragol/app/DetailActivity;->loadRelated()V

    .line 34
    return-void
.end method
