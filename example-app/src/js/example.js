import { NotificationLight } from '@kduma-autoid/capacitor-notification-light';

// Store preset color
let presetColor = { red: 255, green: 0, blue: 0 };

// Check LED availability
window.checkLedAvailability = async () => {
  const output = document.getElementById('statusOutput');
  try {
    output.textContent = 'Checking LED availability...';
    const result = await NotificationLight.checkAvailability();

    let statusText = `LED Available: ${result.available}\n`;
    if (result.available) {
      statusText += `Number of Lights: ${result.lightsCount}\n`;
      statusText += `Light Types: ${result.lightTypes?.join(', ') || 'N/A'}\n`;
      statusText += '\nUsing LightsManager API (Android 12+)';
    } else {
      if (result.error) {
        statusText += `\n⚠️ ${result.error}\n`;
        statusText += '\nTo grant permission (requires root/system app):\n';
        statusText += '1. Install as system app in /system/priv-app/, OR\n';
        statusText += '2. Sign app with platform certificate, OR\n';
        statusText += '3. Use emulator with -writable-system flag';
      } else {
        statusText += '\nNote: This plugin requires Android 12 (API 31) or higher.\n';
        statusText += 'Not all devices have notification LEDs.';
      }
    }

    output.textContent = statusText;
  } catch (error) {
    output.textContent = `Error: ${error.message}`;
    console.error('LED availability check failed:', error);
  }
};

// Set preset color from color picker
window.setPresetColor = (red, green, blue) => {
  presetColor = { red, green, blue };
  document.getElementById('redValue').value = red;
  document.getElementById('greenValue').value = green;
  document.getElementById('blueValue').value = blue;
  console.log(`Preset color set to RGB(${red}, ${green}, ${blue})`);
};

// Turn on LED with preset color
window.turnOnPreset = async () => {
  try {
    await NotificationLight.turnOn({
      color: presetColor,
      onMs: 1000,
      offMs: 0
    });
    console.log('LED turned on with preset color:', presetColor);
  } catch (error) {
    alert(`Failed to turn on LED: ${error.message}`);
    console.error('Turn on LED failed:', error);
  }
};

// Turn on LED with custom values
window.turnOnCustom = async () => {
  try {
    const red = parseInt(document.getElementById('redValue').value) || 0;
    const green = parseInt(document.getElementById('greenValue').value) || 0;
    const blue = parseInt(document.getElementById('blueValue').value) || 0;
    const onMs = parseInt(document.getElementById('onMs').value) || 1000;
    const offMs = parseInt(document.getElementById('offMs').value) || 0;

    await NotificationLight.turnOn({
      color: { red, green, blue },
      onMs,
      offMs
    });

    console.log(`LED turned on - RGB(${red}, ${green}, ${blue}), On: ${onMs}ms, Off: ${offMs}ms`);
  } catch (error) {
    alert(`Failed to turn on LED: ${error.message}`);
    console.error('Turn on LED failed:', error);
  }
};

// Turn off LED
window.turnOffLed = async () => {
  try {
    await NotificationLight.turnOff();
    console.log('LED turned off');
  } catch (error) {
    alert(`Failed to turn off LED: ${error.message}`);
    console.error('Turn off LED failed:', error);
  }
};

// Run a color sequence
window.runColorSequence = async () => {
  const colors = [
    { red: 255, green: 0, blue: 0, name: 'Red' },
    { red: 0, green: 255, blue: 0, name: 'Green' },
    { red: 0, green: 0, blue: 255, name: 'Blue' },
    { red: 255, green: 255, blue: 0, name: 'Yellow' },
    { red: 255, green: 0, blue: 255, name: 'Magenta' }
  ];

  const output = document.getElementById('statusOutput');

  try {
    for (let i = 0; i < colors.length; i++) {
      const color = colors[i];
      output.textContent = `Running sequence: ${color.name} (${i + 1}/${colors.length})`;

      await NotificationLight.turnOn({
        color: { red: color.red, green: color.green, blue: color.blue },
        onMs: 1000,
        offMs: 0
      });

      console.log(`Sequence step ${i + 1}: ${color.name}`);

      // Wait 2 seconds before next color
      await new Promise(resolve => setTimeout(resolve, 2000));
    }

    // Turn off at the end
    await NotificationLight.turnOff();
    output.textContent = 'Sequence completed!';
    console.log('Color sequence completed');
  } catch (error) {
    output.textContent = `Sequence error: ${error.message}`;
    console.error('Color sequence failed:', error);
  }
};

// Echo test (keeping for backward compatibility)
window.testEcho = async () => {
  const inputValue = document.getElementById("echoInput")?.value || "Test";
  try {
    const result = await NotificationLight.echo({ value: inputValue });
    console.log('Echo result:', result);
  } catch (error) {
    console.error('Echo failed:', error);
  }
};

// Auto-check availability on load
window.addEventListener('load', () => {
  console.log('Notification Light Demo App Loaded');
  checkLedAvailability();
});
