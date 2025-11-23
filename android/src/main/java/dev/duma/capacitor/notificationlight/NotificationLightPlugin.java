package dev.duma.capacitor.notificationlight;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;

@CapacitorPlugin(
    name = "NotificationLight",
    permissions = { @Permission(alias = "notifications", strings = { "android.permission.POST_NOTIFICATIONS" }) }
)
public class NotificationLightPlugin extends Plugin {

    private NotificationLight implementation;

    @Override
    public void load() {
        implementation = new NotificationLight(getContext());
    }

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    @PluginMethod
    public void showNotificationWithLight(PluginCall call) {
        String channelId = call.getString("channelId");
        String channelName = call.getString("channelName");
        Integer notificationId = call.getInt("notificationId");
        String title = call.getString("title");
        String body = call.getString("body");
        String lightColor = call.getString("lightColor");
        Integer lightOnMs = call.getInt("lightOnMs", 1000);
        Integer lightOffMs = call.getInt("lightOffMs", 3000);

        if (channelId == null || channelName == null || notificationId == null || title == null || body == null || lightColor == null) {
            call.reject("Missing required parameters");
            return;
        }

        try {
            implementation.showNotificationWithLight(
                channelId,
                channelName,
                notificationId,
                title,
                body,
                lightColor,
                lightOnMs,
                lightOffMs
            );
            call.resolve();
        } catch (Exception e) {
            call.reject("Failed to show notification", e);
        }
    }

    @PluginMethod
    public void cancelNotification(PluginCall call) {
        Integer notificationId = call.getInt("notificationId");

        if (notificationId == null) {
            call.reject("Missing notificationId parameter");
            return;
        }

        try {
            implementation.cancelNotification(notificationId);
            call.resolve();
        } catch (Exception e) {
            call.reject("Failed to cancel notification", e);
        }
    }

    @PluginMethod
    public void clearAllNotifications(PluginCall call) {
        try {
            implementation.clearAllNotifications();
            call.resolve();
        } catch (Exception e) {
            call.reject("Failed to clear notifications", e);
        }
    }
}
