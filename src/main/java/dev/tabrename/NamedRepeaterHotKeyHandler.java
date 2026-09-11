package dev.tabrename;

import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.repeater.Repeater;
import burp.api.montoya.ui.hotkey.HotKeyEvent;
import burp.api.montoya.ui.hotkey.HotKeyHandler;

import java.util.List;

final class NamedRepeaterHotKeyHandler implements HotKeyHandler {
    private final Repeater repeater;
    private final Logging logging;
    private final RepeaterTabActivator tabActivator;

    NamedRepeaterHotKeyHandler(
            Repeater repeater,
            Logging logging,
            RepeaterTabActivator tabActivator) {
        this.repeater = repeater;
        this.logging = logging;
        this.tabActivator = tabActivator;
    }

    @Override
    public void handle(HotKeyEvent event) {
        for (HttpRequest request : requestsFrom(event)) {
            sendToRepeater(request);
        }
    }

    private List<HttpRequest> requestsFrom(HotKeyEvent event) {
        List<HttpRequestResponse> selected = event.selectedRequestResponses();
        if (!selected.isEmpty()) {
            return selected.stream().map(HttpRequestResponse::request).toList();
        }

        return event.messageEditorRequestResponse()
                .map(editor -> List.of(editor.requestResponse().request()))
                .orElseGet(List::of);
    }

    private void sendToRepeater(HttpRequest request) {
        try {
            String title = RepeaterTabTitle.from(request);
            RepeaterTabActivator.Snapshot snapshot = tabActivator.snapshot(title);
            repeater.sendToRepeater(request, title);
            tabActivator.activateNewTab(snapshot);
            logging.logToOutput("Opened Repeater tab: " + title);
        } catch (RuntimeException exception) {
            logging.logToError("Could not send request to a named Repeater tab", exception);
        }
    }
}
