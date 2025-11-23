package dev.duma.capacitor.notificationlight;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "NotificationLight")
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
    public void checkAvailability(PluginCall call) {
        try {
            JSObject result = implementation.checkAvailability();
            call.resolve(result);
        } catch (Exception e) {
            call.reject("Failed to check LED availability: " + e.getMessage(), e);
        }
    }

    @PluginMethod
    public void turnOn(PluginCall call) {
        try {
            JSObject colorObj = call.getObject("color");
            if (colorObj == null) {
                call.reject("Color parameter is required");
                return;
            }

            int red = colorObj.getInteger("red", 255);
            int green = colorObj.getInteger("green", 255);
            int blue = colorObj.getInteger("blue", 255);
            int onMs = call.getInt("onMs", 1000);
            int offMs = call.getInt("offMs", 0);

            // Validate color values
            if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
                call.reject("Color values must be between 0 and 255");
                return;
            }

            implementation.turnOn(red, green, blue, onMs, offMs);
            call.resolve();
        } catch (Exception e) {
            call.reject("Failed to turn on LED: " + e.getMessage(), e);
        }
    }

    @PluginMethod
    public void turnOff(PluginCall call) {
        try {
            implementation.turnOff();
            call.resolve();
        } catch (Exception e) {
            call.reject("Failed to turn off LED: " + e.getMessage(), e);
        }
    }

    @Override
    protected void handleOnDestroy() {
        implementation.cleanup();
        super.handleOnDestroy();
    }
}
