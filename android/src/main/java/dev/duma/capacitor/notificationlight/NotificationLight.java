package dev.duma.capacitor.notificationlight;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.getcapacitor.Logger;

public class NotificationLight {

    private static final String TAG = "NotificationLight";
    private Context context;
    private NotificationManager notificationManager;

    public NotificationLight(Context context) {
        this.context = context;
        this.notificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public String echo(String value) {
        Logger.info("Echo", value);
        return value;
    }

    /**
     * Creates a notification channel with LED configuration and shows a notification
     */
    public void showNotificationWithLight(
        String channelId,
        String channelName,
        int notificationId,
        String title,
        String body,
        String lightColor,
        int lightOnMs,
        int lightOffMs
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create notification channel with LED settings
            // IMPORTANT: LED requires at least IMPORTANCE_HIGH to work
            NotificationChannel channel = new NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            );

            // Enable LED lights on the channel
            channel.enableLights(true);

            // Set LED color (parse hex color string)
            int color = parseColor(lightColor);
            channel.setLightColor(color);

            // Enable sound (some devices require this for LED to work)
            channel.enableVibration(false);
            channel.setSound(null, null);

            // Create the channel
            notificationManager.createNotificationChannel(channel);

            Logger.info(
                TAG,
                "Created notification channel: " +
                channelId +
                " with LED color: " +
                lightColor
            );
        }

        // Build and show the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
            context,
            channelId
        )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Required for LED to work
            .setAutoCancel(true)
            .setDefaults(0); // Disable default sound/vibration

        // For devices below Android O, set LED on the notification itself
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            int color = parseColor(lightColor);
            builder.setLights(color, lightOnMs, lightOffMs);
        }

        notificationManager.notify(notificationId, builder.build());
        Logger.info(TAG, "Notification shown with ID: " + notificationId);
    }

    /**
     * Cancels a notification by its ID
     */
    public void cancelNotification(int notificationId) {
        notificationManager.cancel(notificationId);
        Logger.info(TAG, "Cancelled notification with ID: " + notificationId);
    }

    /**
     * Clears all notifications
     */
    public void clearAllNotifications() {
        notificationManager.cancelAll();
        Logger.info(TAG, "Cleared all notifications");
    }

    /**
     * Parses a hex color string (e.g., "#FF0000") to an integer color value
     */
    private int parseColor(String colorString) {
        try {
            return Color.parseColor(colorString);
        } catch (IllegalArgumentException e) {
            Logger.error(
                TAG,
                "Invalid color format: " + colorString + ", using default blue",
                e
            );
            return Color.BLUE; // Default color
        }
    }
}
