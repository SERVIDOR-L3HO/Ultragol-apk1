package com.ultragol.app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ultragol.app.models.ContentItem;

public class ServerSelectDialog {

    private static final String[][] SERVERS = {
        {"UnlimPlay",  "Servidor Principal"},
        {"VidSrc",     "Servidor 2"},
        {"2Embed",     "Servidor 3"}
    };

    public static void show(Context context, ContentItem item) {
        BottomSheetDialog sheet = new BottomSheetDialog(context, R.style.GlassBottomSheetTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_server_select, null);
        sheet.setContentView(view);

        ((TextView) view.findViewById(R.id.serverDialogTitle)).setText(item.getTitle());

        boolean isTV = item.getContentType() != ContentItem.TYPE_MOVIE;
        View episodeSection = view.findViewById(R.id.episodeSection);
        episodeSection.setVisibility(isTV ? View.VISIBLE : View.GONE);

        final int[] season  = {1};
        final int[] episode = {1};

        if (isTV) {
            TextView tvSeason  = view.findViewById(R.id.tvSeason);
            TextView tvEpisode = view.findViewById(R.id.tvEpisode);

            view.findViewById(R.id.btnSeasonMinus).setOnClickListener(v -> {
                if (season[0] > 1) { season[0]--; tvSeason.setText(String.valueOf(season[0])); }
            });
            view.findViewById(R.id.btnSeasonPlus).setOnClickListener(v -> {
                if (season[0] < 20) { season[0]++; tvSeason.setText(String.valueOf(season[0])); }
            });
            view.findViewById(R.id.btnEpMinus).setOnClickListener(v -> {
                if (episode[0] > 1) { episode[0]--; tvEpisode.setText(String.valueOf(episode[0])); }
            });
            view.findViewById(R.id.btnEpPlus).setOnClickListener(v -> {
                if (episode[0] < 50) { episode[0]++; tvEpisode.setText(String.valueOf(episode[0])); }
            });
        }

        int[] btnIds = {R.id.btnServer1, R.id.btnServer2, R.id.btnServer3};
        for (int i = 0; i < btnIds.length; i++) {
            final int idx = i;
            view.findViewById(btnIds[i]).setOnClickListener(v -> {
                sheet.dismiss();
                launch(context, item, idx, season[0], episode[0]);
            });
        }

        sheet.show();
    }

    private static void launch(Context ctx, ContentItem item, int serverIdx, int s, int e) {
        String url = buildUrl(item, serverIdx, s, e);
        Intent intent = new Intent(ctx, PlayerActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", item.getTitle());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    public static String buildUrl(ContentItem item, int serverIdx, int s, int e) {
        int id      = item.getTmdbId();
        boolean mov = item.getContentType() == ContentItem.TYPE_MOVIE;
        String p    = "?sub=es&lang=es&audio=es&muted=0&autoplay=1";
        switch (serverIdx) {
            case 0:
                return mov
                    ? "https://unlimplay.com/play/embed/movie/" + id + p
                    : "https://unlimplay.com/play/embed/tv/"    + id + "/" + s + "/" + e + p;
            case 1:
                return mov
                    ? "https://vidsrc.to/embed/movie/" + id
                    : "https://vidsrc.to/embed/tv/"    + id + "/" + s + "-" + e;
            case 2:
                return mov
                    ? "https://www.2embed.cc/embed/"   + id
                    : "https://www.2embed.cc/embedtv/" + id + "&s=" + s + "&e=" + e;
            default:
                return item.getStreamUrl();
        }
    }
}
