package dev.duma.capacitor.notificationlight;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import com.getcapacitor.Logger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationLight {

    private static final String TAG = "NotificationLight";
    private Context context;
    private NotificationManager notificationManager;
    private android.hardware.lights.LightsManager.LightsSession lightsSession;

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
     *
     * On Android 12+ (API 31+): Uses LightsManager API for direct LED control
     * On older versions: Uses notification channels with LED settings
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
        int color = parseColor(lightColor);

        // Android 12+ (API 31+): Use LightsManager API for direct LED control
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            boolean ledControlled = controlLedWithLightsManager(
                color,
                lightOnMs,
                lightOffMs
            );
            if (ledControlled) {
                Logger.info(
                    TAG,
                    "LED controlled directly via LightsManager API with color: " +
                    lightColor
                );
            } else {
                Logger.warn(
                    TAG,
                    "LightsManager LED control failed, falling back to notification method"
                );
            }
        }

        // Create notification (required for Android 8+ and for Android 12+ fallback)
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
            channel.setLightColor(color);

            // Disable sound/vibration
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
            builder.setLights(color, lightOnMs, lightOffMs);
        }

        notificationManager.notify(notificationId, builder.build());
        Logger.info(TAG, "Notification shown with ID: " + notificationId);
    }

    /**
     * Control LED directly using LightsManager API (Android 12+)
     * @return true if LED was successfully controlled, false otherwise
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
    private boolean controlLedWithLightsManager(
        int color,
        int lightOnMs,
        int lightOffMs
    ) {
        try {
            // Close any existing session
            if (lightsSession != null) {
                lightsSession.close();
                lightsSession = null;
            }

            android.hardware.lights.LightsManager lightsManager = context.getSystemService(
                android.hardware.lights.LightsManager.class
            );

            if (lightsManager == null) {
                Logger.warn(TAG, "LightsManager not available");
                return false;
            }

            // Find notification LED
            List<android.hardware.lights.Light> lights = lightsManager.getLights();
            android.hardware.lights.Light notificationLight = null;

            for (android.hardware.lights.Light light : lights) {
                if (light.getType() == 3) { // LIGHT_TYPE_NOTIFICATION
                    notificationLight = light;
                    Logger.info(
                        TAG,
                        "Found notification LED: " +
                        light.getName() +
                        " (ID: " +
                        light.getId() +
                        ")"
                    );
                    break;
                }
            }

            if (notificationLight == null) {
                Logger.warn(TAG, "No notification LED found");
                return false;
            }

            // Create light state
            android.hardware.lights.LightState lightState = new android.hardware.lights.LightState.Builder()
                .setColor(color)
                .build();

            // Open session and control the LED
            lightsSession = lightsManager.openSession();

            // Build the lights request using addLight() method
            android.hardware.lights.LightsRequest.Builder requestBuilder = new android.hardware.lights.LightsRequest.Builder();
            requestBuilder.addLight(notificationLight, lightState);

            lightsSession.requestLights(requestBuilder.build());

            Logger.info(
                TAG,
                "LED controlled successfully with color: " +
                String.format("#%06X", (0xFFFFFF & color))
            );
            return true;
        } catch (Exception e) {
            Logger.error(TAG, "Error controlling LED with LightsManager", e);
            return false;
        }
    }

    /**
     * Cancels a notification by its ID and turns off LED
     */
    public void cancelNotification(int notificationId) {
        notificationManager.cancel(notificationId);

        // Turn off LED if using LightsManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && lightsSession != null) {
            try {
                lightsSession.close();
                lightsSession = null;
                Logger.info(TAG, "LED turned off via LightsManager");
            } catch (Exception e) {
                Logger.error(TAG, "Error turning off LED", e);
            }
        }

        Logger.info(TAG, "Cancelled notification with ID: " + notificationId);
    }

    /**
     * Clears all notifications and turns off LED
     */
    public void clearAllNotifications() {
        notificationManager.cancelAll();

        // Turn off LED if using LightsManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && lightsSession != null) {
            try {
                lightsSession.close();
                lightsSession = null;
                Logger.info(TAG, "LED turned off via LightsManager");
            } catch (Exception e) {
                Logger.error(TAG, "Error turning off LED", e);
            }
        }

        Logger.info(TAG, "Cleared all notifications");
    }

    /**
     * Check if the device supports notification LED
     *
     * On Android 12+ (API 31+): Uses LightsManager API to detect actual LED hardware
     * On older versions: Uses heuristic based on device model and Android version
     *
     * @return true if LED is supported, false otherwise
     */
    public boolean isLedSupported() {
        int sdkVersion = Build.VERSION.SDK_INT;

        // Android 12+ (API 31+): Use LightsManager API for accurate detection
        if (sdkVersion >= Build.VERSION_CODES.S) {
            return checkLedSupportWithLightsManager();
        }

        // Older versions: Use heuristic approach
        return checkLedSupportHeuristic();
    }

    /**
     * Check LED support using LightsManager API (Android 12+)
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
    private boolean checkLedSupportWithLightsManager() {
        try {
            android.hardware.lights.LightsManager lightsManager = context.getSystemService(
                android.hardware.lights.LightsManager.class
            );

            if (lightsManager == null) {
                Logger.warn(TAG, "LightsManager not available");
                return checkLedSupportHeuristic();
            }

            List<android.hardware.lights.Light> lights = lightsManager.getLights();
            Logger.info(TAG, "Found " + lights.size() + " lights on device");

            // Check if any notification LED is available
            for (android.hardware.lights.Light light : lights) {
                int lightType = light.getType();
                Logger.info(
                    TAG,
                    "Light ID: " +
                    light.getId() +
                    ", Type: " +
                    lightType +
                    ", Name: " +
                    light.getName()
                );

                // Light type 3 is LIGHT_TYPE_NOTIFICATION
                if (lightType == 3) {
                    Logger.info(TAG, "Notification LED found!");
                    return true;
                }
            }

            Logger.info(TAG, "No notification LED found in lights list");
            return false;
        } catch (Exception e) {
            Logger.error(TAG, "Error checking LED with LightsManager", e);
            return checkLedSupportHeuristic();
        }
    }

    /**
     * Check LED support using heuristic (for pre-Android 12 devices)
     */
    private boolean checkLedSupportHeuristic() {
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        String model = Build.MODEL.toLowerCase();
        int sdkVersion = Build.VERSION.SDK_INT;

        // Google Pixel devices after Pixel 3 don't have notification LEDs
        if (manufacturer.contains("google")) {
            if (
                model.contains("pixel 4") ||
                model.contains("pixel 5") ||
                model.contains("pixel 6") ||
                model.contains("pixel 7") ||
                model.contains("pixel 8") ||
                model.contains("pixel 9")
            ) {
                Logger.info(TAG, "Device is newer Pixel without LED (heuristic)");
                return false;
            }
        }

        // Samsung flagship devices after S9 often don't have LEDs
        if (manufacturer.contains("samsung")) {
            if (
                model.contains("s10") ||
                model.contains("s20") ||
                model.contains("s21") ||
                model.contains("s22") ||
                model.contains("s23") ||
                model.contains("s24")
            ) {
                Logger.info(
                    TAG,
                    "Device is newer Samsung flagship, likely no LED (heuristic)"
                );
                return false;
            }
        }

        // Most devices before Android 9 (API 28) had LEDs
        if (sdkVersion < Build.VERSION_CODES.P) {
            Logger.info(TAG, "Older Android version, likely has LED (heuristic)");
            return true;
        }

        // Default to "might have LED" for unknown older devices
        Logger.info(
            TAG,
            "LED support uncertain for device: " +
            manufacturer +
            " " +
            model +
            " (heuristic)"
        );
        return true;
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
