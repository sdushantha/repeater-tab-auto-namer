package dev.tabrename;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.hotkey.HotKey;

/** Burp Suite entry point. */
public final class TabRenameExtension implements BurpExtension {
    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("Repeater Tab Auto-Namer");
        RepeaterTabActivator tabActivator = new RepeaterTabActivator(
                api.userInterface().swingUtils().suiteFrame(),
                api.logging());
        api.extension().registerUnloadingHandler(tabActivator::stop);

        HotKey hotKey = HotKey.hotKey(
                "Send to Repeater with method/path tab name",
                "Ctrl+R");
        api.userInterface().registerHotKeyHandler(
                hotKey,
                new NamedRepeaterHotKeyHandler(api.repeater(), api.logging(), tabActivator));
        api.logging().logToOutput(
                "Repeater Tab Auto-Namer loaded. Ctrl+R sends requests to named Repeater tabs.");
    }
}
