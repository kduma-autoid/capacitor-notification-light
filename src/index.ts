import { registerPlugin } from '@capacitor/core';

import type { NotificationLightPlugin } from './definitions';

const NotificationLight = registerPlugin<NotificationLightPlugin>('NotificationLight', {
  web: () => import('./web').then((m) => new m.NotificationLightWeb()),
});

export * from './definitions';
export { NotificationLight };
