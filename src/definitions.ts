export interface LedColor {
  red: number;   // 0-255
  green: number; // 0-255
  blue: number;  // 0-255
}

export interface LedOptions {
  color: LedColor;
  onMs?: number;  // milliseconds the LED is on during blink (default: 1000)
  offMs?: number; // milliseconds the LED is off during blink (default: 0)
}

export interface LedInfo {
  available: boolean;
  lightsCount?: number;
  lightTypes?: string[];
}

export interface NotificationLightPlugin {
  /**
   * Check if LED lights are available on this device
   */
  checkAvailability(): Promise<LedInfo>;

  /**
   * Turn on the notification LED with specified color and pattern
   */
  turnOn(options: LedOptions): Promise<void>;

  /**
   * Turn off the notification LED
   */
  turnOff(): Promise<void>;

  /**
   * Echo test method (for development)
   */
  echo(options: { value: string }): Promise<{ value: string }>;
}
