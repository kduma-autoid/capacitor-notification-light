export interface NotificationLightPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
