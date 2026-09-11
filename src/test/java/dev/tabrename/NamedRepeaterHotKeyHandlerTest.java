package dev.tabrename;

import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.repeater.Repeater;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import burp.api.montoya.ui.hotkey.HotKeyEvent;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NamedRepeaterHotKeyHandlerTest {
    @Test
    void sendsSelectedRequestToRepeaterWithGeneratedTitle() {
        Repeater repeater = mock(Repeater.class);
        Logging logging = mock(Logging.class);
        RepeaterTabActivator tabActivator = mock(RepeaterTabActivator.class);
        HotKeyEvent event = mock(HotKeyEvent.class);
        HttpRequestResponse requestResponse = mock(HttpRequestResponse.class);
        HttpRequest request = mock(HttpRequest.class);

        when(event.selectedRequestResponses()).thenReturn(List.of(requestResponse));
        when(requestResponse.request()).thenReturn(request);
        when(request.method()).thenReturn("PUT");
        when(request.pathWithoutQuery()).thenReturn("/api/profile");
        RepeaterTabActivator.Snapshot snapshot = mock(RepeaterTabActivator.Snapshot.class);
        when(tabActivator.snapshot("PUT /api/profile")).thenReturn(snapshot);

        NamedRepeaterHotKeyHandler handler =
                new NamedRepeaterHotKeyHandler(repeater, logging, tabActivator);
        handler.handle(event);

        verify(repeater).sendToRepeater(request, "PUT /api/profile");
        verify(tabActivator).activateNewTab(snapshot);
        verify(logging).logToOutput("Opened Repeater tab: PUT /api/profile");
    }

    @Test
    void sendsRequestFromActiveMessageEditorWhenThereIsNoTableSelection() {
        Repeater repeater = mock(Repeater.class);
        Logging logging = mock(Logging.class);
        RepeaterTabActivator tabActivator = mock(RepeaterTabActivator.class);
        HotKeyEvent event = mock(HotKeyEvent.class);
        MessageEditorHttpRequestResponse editor = mock(MessageEditorHttpRequestResponse.class);
        HttpRequestResponse requestResponse = mock(HttpRequestResponse.class);
        HttpRequest request = mock(HttpRequest.class);

        when(event.selectedRequestResponses()).thenReturn(List.of());
        when(event.messageEditorRequestResponse()).thenReturn(Optional.of(editor));
        when(editor.requestResponse()).thenReturn(requestResponse);
        when(requestResponse.request()).thenReturn(request);
        when(request.method()).thenReturn("GET");
        when(request.pathWithoutQuery()).thenReturn("/orders/42");

        new NamedRepeaterHotKeyHandler(repeater, logging, tabActivator).handle(event);

        verify(repeater).sendToRepeater(request, "GET /orders/42");
    }
}
