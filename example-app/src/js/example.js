import { NotificationLight } from '@kduma-autoid/capacitor-notification-light';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    NotificationLight.echo({ value: inputValue })
}
