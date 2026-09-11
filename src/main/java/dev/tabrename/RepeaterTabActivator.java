package dev.tabrename;

import burp.api.montoya.logging.Logging;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/** Selects a newly created named Repeater tab without activating the Repeater tool itself. */
final class RepeaterTabActivator {
    private static final int RETRY_DELAY_MILLIS = 50;
    private static final int MAX_RETRIES = 40;

    private final Component suiteFrame;
    private final Logging logging;
    private final List<Timer> timers = new ArrayList<>();

    RepeaterTabActivator(Component suiteFrame, Logging logging) {
        this.suiteFrame = suiteFrame;
        this.logging = logging;
    }

    Snapshot snapshot(String title) {
        return onEventDispatchThread(() -> new Snapshot(title, matchingTabComponents(title)));
    }

    void activateNewTab(Snapshot snapshot) {
        SwingUtilities.invokeLater(() -> startRetryTimer(snapshot));
    }

    void stop() {
        SwingUtilities.invokeLater(() -> {
            for (Timer timer : List.copyOf(timers)) {
                timer.stop();
            }
            timers.clear();
        });
    }

    boolean activateNewTabNow(Snapshot snapshot) {
        Component repeaterRoot = findRepeaterRoot(suiteFrame);
        return repeaterRoot != null
                && selectNewMatchingTab(repeaterRoot, snapshot.title(), snapshot.existingTabs());
    }

    private void startRetryTimer(Snapshot snapshot) {
        int[] retries = {0};
        Timer timer = new Timer(RETRY_DELAY_MILLIS, null);
        timer.addActionListener(ignored -> {
            retries[0]++;
            if (activateNewTabNow(snapshot)) {
                timer.stop();
                timers.remove(timer);
            } else if (retries[0] >= MAX_RETRIES) {
                timer.stop();
                timers.remove(timer);
                logging.logToOutput("Opened the named Repeater tab, but could not make it active: "
                        + snapshot.title());
            }
        });
        timer.setInitialDelay(0);
        timers.add(timer);
        timer.start();
    }

    private Set<Component> matchingTabComponents(String title) {
        Set<Component> matches = Collections.newSetFromMap(new IdentityHashMap<>());
        Component repeaterRoot = findRepeaterRoot(suiteFrame);
        if (repeaterRoot != null) {
            collectMatchingTabComponents(repeaterRoot, title, matches);
        }
        return matches;
    }

    private static void collectMatchingTabComponents(
            Component component,
            String title,
            Set<Component> matches) {
        if (component instanceof JTabbedPane tabbedPane) {
            for (int index = 0; index < tabbedPane.getTabCount(); index++) {
                if (title.equals(tabbedPane.getTitleAt(index))) {
                    matches.add(tabbedPane.getComponentAt(index));
                }
            }
        }

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                collectMatchingTabComponents(child, title, matches);
            }
        }
    }

    private static boolean selectNewMatchingTab(
            Component component,
            String title,
            Set<Component> existingTabs) {
        if (component instanceof JTabbedPane tabbedPane) {
            for (int index = tabbedPane.getTabCount() - 1; index >= 0; index--) {
                Component tab = tabbedPane.getComponentAt(index);
                if (title.equals(tabbedPane.getTitleAt(index)) && !existingTabs.contains(tab)) {
                    tabbedPane.setSelectedIndex(index);
                    return true;
                }
            }

            for (int index = tabbedPane.getTabCount() - 1; index >= 0; index--) {
                if (selectNewMatchingTab(tabbedPane.getComponentAt(index), title, existingTabs)) {
                    tabbedPane.setSelectedIndex(index);
                    return true;
                }
            }
            return false;
        }

        if (component instanceof Container container) {
            Component[] children = container.getComponents();
            for (int index = children.length - 1; index >= 0; index--) {
                if (selectNewMatchingTab(children[index], title, existingTabs)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Component findRepeaterRoot(Component component) {
        if (component instanceof JTabbedPane tabbedPane) {
            for (int index = 0; index < tabbedPane.getTabCount(); index++) {
                if ("Repeater".equalsIgnoreCase(tabbedPane.getTitleAt(index).trim())) {
                    return tabbedPane.getComponentAt(index);
                }
            }
        }

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                Component result = findRepeaterRoot(child);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static <T> T onEventDispatchThread(java.util.function.Supplier<T> supplier) {
        if (SwingUtilities.isEventDispatchThread()) {
            return supplier.get();
        }

        AtomicReference<T> result = new AtomicReference<>();
        try {
            SwingUtilities.invokeAndWait(() -> result.set(supplier.get()));
            return result.get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while inspecting Burp's UI", exception);
        } catch (InvocationTargetException exception) {
            throw new IllegalStateException("Could not inspect Burp's UI", exception.getCause());
        }
    }

    record Snapshot(String title, Set<Component> existingTabs) {
    }
}
