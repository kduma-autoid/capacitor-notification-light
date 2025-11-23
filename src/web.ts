import { WebPlugin } from '@capacitor/core';

import type { NotificationLightPlugin } from './definitions';

export class NotificationLightWeb extends WebPlugin implements NotificationLightPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
