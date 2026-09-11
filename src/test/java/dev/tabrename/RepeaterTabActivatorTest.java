package dev.tabrename;

import burp.api.montoya.logging.Logging;
import org.junit.jupiter.api.Test;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class RepeaterTabActivatorTest {
    @Test
    void selectsTheNewTabEvenWhenAnotherTabHasTheSameTitle() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JPanel suite = new JPanel();
            JTabbedPane tools = new JTabbedPane();
            JPanel repeaterRoot = new JPanel();
            JTabbedPane repeaterTabs = new JTabbedPane();
            JPanel existingTab = new JPanel();

            repeaterTabs.addTab("GET /users", existingTab);
            repeaterRoot.add(repeaterTabs);
            tools.addTab("Proxy", new JPanel());
            tools.addTab("Repeater", repeaterRoot);
            suite.add(tools);

            RepeaterTabActivator activator =
                    new RepeaterTabActivator(suite, mock(Logging.class));
            RepeaterTabActivator.Snapshot snapshot = activator.snapshot("GET /users");

            JPanel newTab = new JPanel();
            repeaterTabs.addTab("GET /users", newTab);
            repeaterTabs.setSelectedIndex(0);

            assertTrue(activator.activateNewTabNow(snapshot));
            assertEquals(1, repeaterTabs.getSelectedIndex());
        });
    }
}
