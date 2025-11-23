# @kduma-autoid/capacitor-notification-light

Control the notification LED on various Android devices using Notification Channels.

## Features

- 🔴 Control LED color (Red, Green, Blue, and more)
- ⏱️ Customize LED blink patterns (on/off duration)
- 📱 Full Android Notification Channel support
- 🎯 Individual notification control
- 🧹 Clear individual or all notifications

## Install

```bash
npm install @kduma-autoid/capacitor-notification-light
npx cap sync
```

## Usage

```typescript
import { NotificationLight } from '@kduma-autoid/capacitor-notification-light';

// Show a notification with a red LED light
await NotificationLight.showNotificationWithLight({
  channelId: 'important-alerts',
  channelName: 'Important Alerts',
  notificationId: 1,
  title: 'Important Alert',
  body: 'This is an important notification',
  lightColor: '#FF0000',  // Red
  lightOnMs: 1000,        // LED on for 1 second
  lightOffMs: 3000        // LED off for 3 seconds
});

// Show a notification with a blue LED light
await NotificationLight.showNotificationWithLight({
  channelId: 'info-channel',
  channelName: 'Information',
  notificationId: 2,
  title: 'Information',
  body: 'This is informational',
  lightColor: '#0000FF',  // Blue
  lightOnMs: 500,
  lightOffMs: 2000
});

// Cancel a specific notification
await NotificationLight.cancelNotification({
  notificationId: 1
});

// Clear all notifications
await NotificationLight.clearAllNotifications();
```

## Example App

The plugin includes a complete example app demonstrating all features. To run it:

```bash
cd example-app
npm install
npx cap sync
npx cap open android
```

The example app provides:
- Quick buttons for Red, Green, and Blue LED notifications
- Custom notification form with color picker
- Notification management (cancel last, clear all)
- Real-time output log

## How It Works

This plugin uses Android Notification Channels (API level 26+) to control the LED:

```java
NotificationChannel channel = new NotificationChannel(
    channelId,
    channelName,
    NotificationManager.IMPORTANCE_DEFAULT
);
channel.enableLights(true);
channel.setLightColor(Color.BLUE);
notificationManager.createNotificationChannel(channel);
```

For devices below Android O (API level 26), the plugin falls back to the legacy notification LED API.

## Important Notes

- **Hardware Support**: LED functionality depends on device hardware. Many modern devices (especially flagship phones) have removed the notification LED.
- **Android Version**: Notification Channels are supported on Android 8.0 (API level 26) and above. Legacy LED API is used for older versions.
- **Channel Configuration**: Once a notification channel is created, its LED settings are cached. To change LED settings, you must use a different channel ID or clear the app data.
- **Permissions**: The plugin requires notification permissions on Android 13+ (automatically handled by Capacitor).

## Supported LED Colors

You can use any hex color, but device hardware may limit the actual colors displayed. Common colors:

- Red: `#FF0000`
- Green: `#00FF00`
- Blue: `#0000FF`
- Yellow: `#FFFF00`
- Magenta: `#FF00FF`
- Cyan: `#00FFFF`
- White: `#FFFFFF`

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`showNotificationWithLight(...)`](#shownotificationwithlight)
* [`cancelNotification(...)`](#cancelnotification)
* [`clearAllNotifications()`](#clearallnotifications)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------


### showNotificationWithLight(...)

```typescript
showNotificationWithLight(options: NotificationLightOptions) => Promise<void>
```

Creates a notification channel with LED light configuration and shows a notification

| Param         | Type                                                                          | Description                                        |
| ------------- | ----------------------------------------------------------------------------- | -------------------------------------------------- |
| **`options`** | <code><a href="#notificationlightoptions">NotificationLightOptions</a></code> | Configuration for the notification channel and LED |

--------------------


### cancelNotification(...)

```typescript
cancelNotification(options: { notificationId: number; }) => Promise<void>
```

Cancels/removes a notification by its ID

| Param         | Type                                     | Description                   |
| ------------- | ---------------------------------------- | ----------------------------- |
| **`options`** | <code>{ notificationId: number; }</code> | The notification ID to cancel |

--------------------


### clearAllNotifications()

```typescript
clearAllNotifications() => Promise<void>
```

Clears all notifications created by this plugin

--------------------


### Interfaces


#### NotificationLightOptions

| Prop                 | Type                | Description                                                                                      |
| -------------------- | ------------------- | ------------------------------------------------------------------------------------------------ |
| **`channelId`**      | <code>string</code> | Unique identifier for the notification channel                                                   |
| **`channelName`**    | <code>string</code> | Display name for the notification channel                                                        |
| **`notificationId`** | <code>number</code> | Notification ID (for canceling later)                                                            |
| **`title`**          | <code>string</code> | Title of the notification                                                                        |
| **`body`**           | <code>string</code> | Body text of the notification                                                                    |
| **`lightColor`**     | <code>string</code> | LED light color in hex format (e.g., "#FF0000" for red, "#00FF00" for green, "#0000FF" for blue) |
| **`lightOnMs`**      | <code>number</code> | LED on duration in milliseconds (default: 1000)                                                  |
| **`lightOffMs`**     | <code>number</code> | LED off duration in milliseconds (default: 3000)                                                 |

</docgen-api>
