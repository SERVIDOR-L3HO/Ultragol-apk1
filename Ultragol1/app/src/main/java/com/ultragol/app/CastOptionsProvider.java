package com.ultragol.app;

import android.content.Context;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.framework.CastOptions;
import com.google.android.gms.cast.framework.OptionsProvider;
import com.google.android.gms.cast.framework.SessionProvider;
import com.google.android.gms.cast.framework.media.CastMediaOptions;
import com.google.android.gms.cast.framework.media.MediaIntentReceiver;
import com.google.android.gms.cast.framework.media.NotificationOptions;
import java.util.Arrays;
import java.util.List;

/**
 * Provides Google Cast SDK configuration.
 * Referenced from AndroidManifest meta-data.
 */
public class CastOptionsProvider implements OptionsProvider {

    @Override
    public CastOptions getCastOptions(Context ctx) {
        NotificationOptions notifOptions = new NotificationOptions.Builder()
            .setActions(
                Arrays.asList(
                    MediaIntentReceiver.ACTION_TOGGLE_PLAYBACK,
                    MediaIntentReceiver.ACTION_STOP_CASTING),
                new int[]{0, 1})
            .build();

        CastMediaOptions mediaOptions = new CastMediaOptions.Builder()
            .setNotificationOptions(notifOptions)
            .setExpandedControllerActivityClassName(MediaActivity.class.getName())
            .build();

        return new CastOptions.Builder()
            .setReceiverApplicationId(
                CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID)
            .setCastMediaOptions(mediaOptions)
            .build();
    }

    @Override
    public List<SessionProvider> getAdditionalSessionProviders(Context ctx) {
        return null;
    }
}
