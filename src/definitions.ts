export interface NotificationLightPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;

  /**
   * Check if notification permissions are granted (Android 13+ only)
   * @returns Promise with permission status
   */
  checkPermissions(): Promise<PermissionStatus>;

  /**
   * Request notification permissions (Android 13+ only)
   * @returns Promise with permission status
   */
  requestPermissions(): Promise<PermissionStatus>;

  /**
   * Creates a notification channel with LED light configuration and shows a notification
   * @param options Configuration for the notification channel and LED
   */
  showNotificationWithLight(options: NotificationLightOptions): Promise<void>;

  /**
   * Cancels/removes a notification by its ID
   * @param options The notification ID to cancel
   */
  cancelNotification(options: { notificationId: number }): Promise<void>;

  /**
   * Clears all notifications created by this plugin
   */
  clearAllNotifications(): Promise<void>;
}

export interface PermissionStatus {
  /**
   * Permission state: 'granted', 'denied', or 'prompt'
   */
  display: 'granted' | 'denied' | 'prompt';
}

export interface NotificationLightOptions {
  /**
   * Unique identifier for the notification channel
   */
  channelId: string;

  /**
   * Display name for the notification channel
   */
  channelName: string;

  /**
   * Notification ID (for canceling later)
   */
  notificationId: number;

  /**
   * Title of the notification
   */
  title: string;

  /**
   * Body text of the notification
   */
  body: string;

  /**
   * LED light color in hex format (e.g., "#FF0000" for red, "#00FF00" for green, "#0000FF" for blue)
   */
  lightColor: string;

  /**
   * LED on duration in milliseconds (default: 1000)
   */
  lightOnMs?: number;

  /**
   * LED off duration in milliseconds (default: 3000)
   */
  lightOffMs?: number;
}
