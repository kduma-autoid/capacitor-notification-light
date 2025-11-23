import { WebPlugin } from '@capacitor/core';

import type { NotificationLightPlugin, NotificationLightOptions, PermissionStatus } from './definitions';

export class NotificationLightWeb extends WebPlugin implements NotificationLightPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  async checkPermissions(): Promise<PermissionStatus> {
    throw this.unimplemented('Notification permissions are only available on Android devices');
  }

  async requestPermissions(): Promise<PermissionStatus> {
    throw this.unimplemented('Notification permissions are only available on Android devices');
  }

  async showNotificationWithLight(_options: NotificationLightOptions): Promise<void> {
    throw this.unimplemented('LED notification control is only available on Android devices');
  }

  async cancelNotification(_options: { notificationId: number }): Promise<void> {
    throw this.unimplemented('Notification cancellation is only available on Android devices');
  }

  async clearAllNotifications(): Promise<void> {
    throw this.unimplemented('Notification clearing is only available on Android devices');
  }
}
