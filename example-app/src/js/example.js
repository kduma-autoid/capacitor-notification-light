import { NotificationLight } from '@kduma-autoid/capacitor-notification-light';

let lastNotificationId = 0;
let permissionsGranted = false;

// Helper function to log output
function logOutput(message) {
    const output = document.getElementById('output');
    const timestamp = new Date().toLocaleTimeString();
    output.textContent = `[${timestamp}] ${message}\n` + output.textContent;
}

// Request notification permissions on app load
async function requestNotificationPermissions() {
    try {
        const result = await NotificationLight.requestPermissions();
        if (result.display === 'granted') {
            permissionsGranted = true;
            logOutput('✅ Notification permissions granted');
        } else {
            logOutput('❌ Notification permissions denied');
        }
    } catch (error) {
        logOutput(`Permission request error: ${error.message}`);
        // On older Android versions, permissions are granted by default
        permissionsGranted = true;
    }
}

// Check permissions on page load
async function checkPermissions() {
    try {
        const result = await NotificationLight.checkPermissions();
        if (result.display === 'granted') {
            permissionsGranted = true;
            logOutput('✅ Notification permissions already granted');
        } else {
            logOutput('⚠️ Requesting notification permissions...');
            await requestNotificationPermissions();
        }
    } catch (error) {
        logOutput(`Permission check error: ${error.message}`);
        // On older Android versions, permissions are granted by default
        permissionsGranted = true;
    }
}

// Check permissions when page loads
window.addEventListener('DOMContentLoaded', () => {
    checkPermissions();
});

// Manual permission request function
window.requestPermissions = async () => {
    await requestNotificationPermissions();
}

// Original echo test
window.testEcho = async () => {
    try {
        const inputValue = document.getElementById("echoInput").value;
        const result = await NotificationLight.echo({ value: inputValue });
        logOutput(`Echo result: ${result.value}`);
    } catch (error) {
        logOutput(`Echo error: ${error.message}`);
    }
}

// Helper to check permissions before showing notification
async function ensurePermissions() {
    if (!permissionsGranted) {
        logOutput('⚠️ Requesting permissions first...');
        await requestNotificationPermissions();
        if (!permissionsGranted) {
            logOutput('❌ Cannot show notification: permissions denied');
            return false;
        }
    }
    return true;
}

// Show notification with red LED
window.showRedNotification = async () => {
    if (!(await ensurePermissions())) return;

    try {
        lastNotificationId++;
        await NotificationLight.showNotificationWithLight({
            channelId: 'red-led-channel',
            channelName: 'Red LED Notifications',
            notificationId: lastNotificationId,
            title: 'Red LED Notification',
            body: 'This notification has a red LED light',
            lightColor: '#FF0000',
            lightOnMs: 1000,
            lightOffMs: 3000
        });
        logOutput(`Red LED notification shown (ID: ${lastNotificationId})`);
    } catch (error) {
        logOutput(`Error: ${error.message}`);
    }
}

// Show notification with green LED
window.showGreenNotification = async () => {
    if (!(await ensurePermissions())) return;

    try {
        lastNotificationId++;
        await NotificationLight.showNotificationWithLight({
            channelId: 'green-led-channel',
            channelName: 'Green LED Notifications',
            notificationId: lastNotificationId,
            title: 'Green LED Notification',
            body: 'This notification has a green LED light',
            lightColor: '#00FF00',
            lightOnMs: 500,
            lightOffMs: 2000
        });
        logOutput(`Green LED notification shown (ID: ${lastNotificationId})`);
    } catch (error) {
        logOutput(`Error: ${error.message}`);
    }
}

// Show notification with blue LED
window.showBlueNotification = async () => {
    if (!(await ensurePermissions())) return;

    try {
        lastNotificationId++;
        await NotificationLight.showNotificationWithLight({
            channelId: 'blue-led-channel',
            channelName: 'Blue LED Notifications',
            notificationId: lastNotificationId,
            title: 'Blue LED Notification',
            body: 'This notification has a blue LED light',
            lightColor: '#0000FF',
            lightOnMs: 2000,
            lightOffMs: 1000
        });
        logOutput(`Blue LED notification shown (ID: ${lastNotificationId})`);
    } catch (error) {
        logOutput(`Error: ${error.message}`);
    }
}

// Show notification with custom LED color from form
window.showCustomNotification = async () => {
    if (!(await ensurePermissions())) return;

    try {
        const title = document.getElementById('notificationTitle').value;
        const body = document.getElementById('notificationBody').value;
        const color = document.getElementById('ledColor').value;

        lastNotificationId++;
        await NotificationLight.showNotificationWithLight({
            channelId: 'custom-led-channel',
            channelName: 'Custom LED Notifications',
            notificationId: lastNotificationId,
            title: title,
            body: body,
            lightColor: color,
            lightOnMs: 1000,
            lightOffMs: 3000
        });
        logOutput(`Custom notification shown with color ${color} (ID: ${lastNotificationId})`);
    } catch (error) {
        logOutput(`Error: ${error.message}`);
    }
}

// Cancel the last notification
window.cancelNotification = async () => {
    try {
        if (lastNotificationId === 0) {
            logOutput('No notifications to cancel');
            return;
        }
        await NotificationLight.cancelNotification({
            notificationId: lastNotificationId
        });
        logOutput(`Cancelled notification ID: ${lastNotificationId}`);
    } catch (error) {
        logOutput(`Error: ${error.message}`);
    }
}

// Clear all notifications
window.clearAll = async () => {
    try {
        await NotificationLight.clearAllNotifications();
        logOutput('All notifications cleared');
        lastNotificationId = 0;
    } catch (error) {
        logOutput(`Error: ${error.message}`);
    }
}
