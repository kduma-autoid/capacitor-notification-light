import { WebPlugin } from '@capacitor/core';

import type { NotificationLightPlugin, LedOptions, LedInfo } from './definitions';

export class NotificationLightWeb extends WebPlugin implements NotificationLightPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  async checkAvailability(): Promise<LedInfo> {
    console.log('LED control is not available on web platform');
    return {
      available: false
    };
  }

  async turnOn(options: LedOptions): Promise<void> {
    console.log('LED turnOn called on web (not supported):', options);
    throw new Error('LED control is only available on Android platform');
  }

  async turnOff(): Promise<void> {
    console.log('LED turnOff called on web (not supported)');
    throw new Error('LED control is only available on Android platform');
  }
}
