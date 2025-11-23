# Testing Setup for LED Control Plugin

This plugin uses the `CONTROL_DEVICE_LIGHTS` permission, which is a **signature|privileged** system permission. Normal apps cannot request this permission through standard means.

## Prerequisites

- Android 12 (API level 31) or higher
- Device with LED lights (or emulator)
- Root access OR Android Emulator with writable system

## Testing Options

### Option 1: Android Emulator (Recommended for Development)

1. **Start emulator with writable system partition:**
   ```bash
   emulator -avd Your_AVD_Name -writable-system
   ```

2. **Install app as system app:**
   ```bash
   # Build your app first
   cd example-app
   npx cap sync android
   cd android
   ./gradlew assembleDebug

   # Install to system partition
   adb root
   adb remount
   adb push app/build/outputs/apk/debug/app-debug.apk /system/priv-app/NotificationLightDemo/NotificationLightDemo.apk
   adb reboot
   ```

3. **After reboot, the app will have system privileges and can use the LED control**

### Option 2: Rooted Physical Device

If you have a rooted Android device:

```bash
# Build your APK
cd example-app/android
./gradlew assembleDebug

# Install to system partition
adb root
adb remount
adb push app/build/outputs/apk/debug/app-debug.apk /system/priv-app/NotificationLightDemo/NotificationLightDemo.apk
adb reboot
```

### Option 3: Custom ROM Development

For production use, the app needs to be:
- Signed with the platform certificate, OR
- Whitelisted in the ROM's permission configuration, OR
- Installed as a privileged system app

## Verifying Installation

After installation as a system app, verify:

```bash
# Check if app is in system partition
adb shell ls -la /system/priv-app/NotificationLightDemo/

# Check app's granted permissions
adb shell dumpsys package com.example.plugin | grep CONTROL_DEVICE_LIGHTS
```

You should see the permission granted without the "requested but not granted" message.

## Testing the Plugin

1. Launch the example app
2. Click "Check LED Availability"
3. If successful, you should see:
   - LED Available: true
   - Number of Lights: X
   - Light Types: [NOTIFICATION, ATTENTION, etc.]

4. Try the color presets or custom colors to control the LED

## Troubleshooting

### "Permission denied" error
- Ensure the app is installed in `/system/priv-app/` not `/data/app/`
- Verify emulator was started with `-writable-system` flag
- Check SELinux isn't blocking (try `adb shell setenforce 0` temporarily)

### "No LED lights available"
- Not all devices have LED lights (especially newer phones)
- Try the emulator or an older Android device with notification LED

### Build errors
- Ensure you have Android 12 (API 31) or higher SDK installed
- Check that gradle dependencies are properly synchronized

## Example Commands Reference

```bash
# Quick setup script for emulator
emulator -avd Pixel_6_API_31 -writable-system &
sleep 30  # Wait for emulator to boot
adb root
adb remount
cd example-app/android
./gradlew assembleDebug
adb push app/build/outputs/apk/debug/app-debug.apk /system/priv-app/NotificationLightDemo/NotificationLightDemo.apk
adb reboot
```

## Production Deployment

For production apps:
1. Work with device manufacturer to get platform signing
2. Include app as part of custom ROM
3. Add to privileged permission whitelist in ROM configuration
