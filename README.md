# Repeater Tab Auto-Namer

A small Burp Suite extension that sends HTTP requests to Repeater with tab names in this format:

```text
{HTTP_VERB} {PATH}
```

Examples: `GET /api/users`, `POST /login`, and `DELETE /items/42`.

Query strings are deliberately excluded so that tabs remain readable. The original request is not modified.

## Build

Requires Java 17 or newer.

```shell
./gradlew clean test jar
```

The loadable extension is written to `build/libs/repeater-tab-auto-namer.jar`.

## Install

1. Open **Extensions > Installed** in Burp Suite.
2. Click **Add**.
3. Choose **Java** as the extension type.
4. Select `build/libs/repeater-tab-auto-namer.jar`.

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
