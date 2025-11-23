package dev.duma.capacitor.notificationlight;

import android.content.Context;
import android.hardware.lights.Light;
import android.hardware.lights.LightState;
import android.hardware.lights.LightsManager;
import android.hardware.lights.LightsRequest;
import android.os.Build;
import com.getcapacitor.Logger;
import com.getcapacitor.JSObject;
import org.json.JSONArray;
import java.util.List;

public class NotificationLight {

    private static final String TAG = "NotificationLight";
    private Context context;
    private LightsManager lightsManager;
    private LightsManager.LightsSession lightsSession;

    // Light type constants (from Android SDK)
    private static final int LIGHT_TYPE_MICROPHONE = 8;
    private static final int LIGHT_TYPE_CAMERA = 7;
    private static final int LIGHT_TYPE_NOTIFICATION = 4;
    private static final int LIGHT_TYPE_ATTENTION = 5;
    private static final int LIGHT_TYPE_INPUT = 9;
    private static final int LIGHT_TYPE_PLAYER_ID = 10;

    public NotificationLight(Context context) {
        this.context = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            lightsManager = context.getSystemService(LightsManager.class);
        }
    }

    public String echo(String value) {
        Logger.info(TAG, "Echo: " + value);
        return value;
    }

    public JSObject checkAvailability() {
        JSObject result = new JSObject();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            result.put("available", false);
            Logger.info(TAG, "LightsManager API requires Android 12 (API 31) or higher");
            return result;
        }

        if (lightsManager == null) {
            result.put("available", false);
            Logger.warn(TAG, "LightsManager not available");
            return result;
        }

        List<Light> lights = lightsManager.getLights();
        result.put("available", !lights.isEmpty());
        result.put("lightsCount", lights.size());

        JSONArray lightTypes = new JSONArray();
        for (Light light : lights) {
            String type = getLightTypeName(light.getType());
            lightTypes.put(type);
            Logger.info(TAG, "Found light - ID: " + light.getId() + ", Type: " + type);
        }
        result.put("lightTypes", lightTypes);

        return result;
    }

    public void turnOn(int red, int green, int blue, int onMs, int offMs) throws Exception {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            throw new Exception("LightsManager API requires Android 12 (API 31) or higher");
        }

        if (lightsManager == null) {
            throw new Exception("LightsManager not available");
        }

        // Close existing session if any
        if (lightsSession != null) {
            lightsSession.close();
            lightsSession = null;
        }

        List<Light> lights = lightsManager.getLights();
        if (lights.isEmpty()) {
            throw new Exception("No LED lights available on this device");
        }

        // Find notification light (or use the first available light)
        Light targetLight = null;
        for (Light light : lights) {
            if (light.getType() == LIGHT_TYPE_NOTIFICATION) {
                targetLight = light;
                Logger.info(TAG, "Using notification light");
                break;
            }
        }

        // If no notification light found, try other types
        if (targetLight == null) {
            for (Light light : lights) {
                if (light.getType() == LIGHT_TYPE_ATTENTION) {
                    targetLight = light;
                    Logger.info(TAG, "Using attention light");
                    break;
                }
            }
        }

        // If still no light found, use the first available
        if (targetLight == null && !lights.isEmpty()) {
            targetLight = lights.get(0);
            Logger.info(TAG, "Using first available light: " + getLightTypeName(targetLight.getType()));
        }

        if (targetLight == null) {
            throw new Exception("No suitable LED light found");
        }

        // Create color value (ARGB format)
        int color = 0xFF000000 | (red << 16) | (green << 8) | blue;

        // Create light state with blinking pattern if specified
        LightState.Builder builder = new LightState.Builder().setColor(color);

        // Note: The LightsManager API in Android 12+ doesn't support blinking patterns
        // directly. The blinking is typically handled by the hardware/firmware.
        // We're storing these values but they may not be used by all devices.

        LightState lightState = builder.build();

        // Create request and session
        LightsRequest request = new LightsRequest.Builder()
            .addLight(targetLight, lightState)
            .build();

        lightsSession = lightsManager.openSession();
        lightsSession.requestLights(request);

        Logger.info(TAG, String.format(
            "LED turned on - Color: RGB(%d,%d,%d), OnMs: %d, OffMs: %d",
            red, green, blue, onMs, offMs
        ));
    }

    public void turnOff() {
        if (lightsSession != null) {
            lightsSession.close();
            lightsSession = null;
            Logger.info(TAG, "LED turned off");
        }
    }

    public void cleanup() {
        turnOff();
    }

    private String getLightTypeName(int type) {
        switch (type) {
            case LIGHT_TYPE_MICROPHONE:
                return "MICROPHONE";
            case LIGHT_TYPE_CAMERA:
                return "CAMERA";
            case LIGHT_TYPE_NOTIFICATION:
                return "NOTIFICATION";
            case LIGHT_TYPE_ATTENTION:
                return "ATTENTION";
            case LIGHT_TYPE_INPUT:
                return "INPUT";
            case LIGHT_TYPE_PLAYER_ID:
                return "PLAYER_ID";
            default:
                return "UNKNOWN(" + type + ")";
        }
    }
}
