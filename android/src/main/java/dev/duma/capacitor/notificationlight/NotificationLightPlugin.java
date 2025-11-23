package dev.duma.capacitor.notificationlight;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

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
    public void checkPermissions(PluginCall call) {
        JSObject permissionsResult = new JSObject();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ requires runtime permission
            String permissionState = getPermissionState("notifications");
            permissionsResult.put("display", permissionState);
        } else {
            // Before Android 13, notification permissions are granted by default
            permissionsResult.put("display", "granted");
        }

        call.resolve(permissionsResult);
    }

    @PluginMethod
    public void requestPermissions(PluginCall call) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ requires runtime permission request
            requestPermissionForAlias("notifications", call, "permissionsCallback");
        } else {
            // Before Android 13, permissions are granted by default
            JSObject result = new JSObject();
            result.put("display", "granted");
            call.resolve(result);
        }
    }

    @PermissionCallback
    private void permissionsCallback(PluginCall call) {
        JSObject permissionsResult = new JSObject();
        String permissionState = getPermissionState("notifications");
        permissionsResult.put("display", permissionState);
        call.resolve(permissionsResult);
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

    @PluginMethod
    public void isLedSupported(PluginCall call) {
        try {
            boolean supported = implementation.isLedSupported();
            JSObject ret = new JSObject();
            ret.put("supported", supported);
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("Failed to check LED support", e);
        }
    }
}
