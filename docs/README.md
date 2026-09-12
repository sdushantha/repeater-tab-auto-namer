# Repeater Tab Auto-Namer
Burp Suite extension that auto-renames Repeater tabs to `{METHOD} {PATH}`
<p align="center">
    <img src="images/preview.png">
</p>

## Install

1. Execute `./gradlew clean test jar` from project's root Requires Java 17 or newer.
2. Open **Extensions > Installed** in Burp Suite.
3. Click **Add**.
4. Choose **Java** as the extension type.
5. Select `build/libs/repeater-tab-auto-namer.jar`.

> [!IMPORTANT]
> Remember to go to **Settings > User interface > Hotkeys** and remove the existing Ctrl+R assignment.

## Use

The extension registers a Burp command named:

```text
Send to Repeater with method/path tab name
```

Its shortcut is `Ctrl+R`. Before loading the extension for the first time:

1. Open **Settings > User interface > Hotkeys**.
2. Remove or change `Ctrl+R` from Burp's built-in **Send to Repeater** command.
3. Load or reload the extension.

Now `Ctrl+R` sends the current request—or every selected request in a supported table—to Repeater through the extension. Each request opens in a tab named from its HTTP method and path. The newly created tab is made active inside Repeater, but Burp does not switch away from the tool you are currently viewing.
