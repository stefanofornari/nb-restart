/*
 * Copyright 2025 Stefano Fornari
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ste.netbeans.restart;

import com.github.caciocavallosilano.cacio.ctc.junit.CacioTest;
import static org.assertj.core.api.BDDAssertions.then;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.Robot;
import org.assertj.swing.fixture.DialogFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

@CacioTest
public class RestartCountdownDialogTest {

    private Robot robot;
    private DialogFixture dialog;
    private AtomicBoolean restartTriggered;

    @BeforeEach
    public void beforeEach() {
        robot = BasicRobot.robotWithNewAwtHierarchy();
        restartTriggered = new AtomicBoolean(false);
    }

    @AfterEach
    public void afterEach() {
        if (dialog != null) {
            dialog.cleanUp();
        }
        robot.cleanUp();
    }

    private RestartCountdownDialog createDialog() {
        // Pass a callback which sets restartTriggered to true when restart is called
        return new RestartCountdownDialog(() -> restartTriggered.set(true), 3, false);
    }

    @Test
    public void dialog_shows_message_and_buttons() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            RestartCountdownDialog dlg = createDialog();
            dlg.setVisible(true);
            dialog = new DialogFixture(robot, dlg);
        });

        dialog.requireVisible();
        dialog.label("messageLabel").requireVisible();
        dialog.button("cancelButton").requireVisible();
        dialog.button("restartNowButton").requireVisible();

        String labelText = dialog.label("messageLabel").text();
        then(labelText).contains("NetBeans will restart in a few seconds");
    }

    @Test
    public void cancel_button_closes_dialog_and_no_restart() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            RestartCountdownDialog dlg = createDialog();
            dlg.setVisible(true);
            dialog = new DialogFixture(robot, dlg);
        });

        dialog.button("cancelButton").click();

        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        then(dialog.target().isVisible()).isFalse();
        then(restartTriggered.get()).isFalse();
    }

    @Test
    public void restart_now_button_triggers_restart_immediately() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            RestartCountdownDialog dlg = createDialog();
            dlg.setVisible(true);
            dialog = new DialogFixture(robot, dlg);
        });

        dialog.button("restartNowButton").click();

        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        then(restartTriggered.get()).isTrue();
        then(dialog.target().isVisible()).isFalse();
    }

    @Test
    public void countdown_ticks_down_and_restarts() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            RestartCountdownDialog dlg = new RestartCountdownDialog(() -> {
                restartTriggered.set(true);
            }, 1, false); // use 1 second countdown and non-modal for test speed
            dlg.setVisible(true);
            dialog = new DialogFixture(robot, dlg);
        });

        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        then(restartTriggered.get()).isTrue();
        then(dialog.target().isVisible()).isFalse();
    }

    @Test
    public void dialog_ui_elements() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            RestartCountdownDialog dlg = createDialog();
            dlg.setVisible(true);
            dialog = new DialogFixture(robot, dlg);
        });

        dialog.requireVisible();

        // Cast target to JDialog to access getContentPane()
        JDialog jDialog = (JDialog) dialog.target();
        Container contentPane = jDialog.getContentPane();

        // Check icon label presence
        JPanel topPanel = (JPanel) contentPane.getComponent(0);
        JPanel iconPanel = (JPanel) topPanel.getComponent(0);
        JLabel iconLabel = (JLabel) iconPanel.getComponent(0);
        then(iconLabel).isNotNull();
        Icon icon = iconLabel.getIcon();
        then(icon).isNotNull();

        // Assuming icon is a warning icon by type
        String iconClass = icon.getClass().getSimpleName();
        then(iconClass.toLowerCase()).contains("icon");

        // Buttons order: first Restart Now, then Cancel
        JPanel buttonPanel = (JPanel) contentPane.getComponent(2);
        Component firstButton = buttonPanel.getComponent(0);
        Component secondButton = buttonPanel.getComponent(1);
        then(firstButton).isSameAs(dialog.button("restartNowButton").target());
        then(secondButton).isSameAs(dialog.button("cancelButton").target());

        // Restart Now button color check
        JButton restartNowButton = dialog.button("restartNowButton").target();
        Color bg = restartNowButton.getBackground();
        Color fg = restartNowButton.getForeground();
        then(bg).isEqualTo(Color.RED);
        then(fg).isEqualTo(Color.WHITE);

        // Check countdownLabel presence and large font size, centered
        JLabel countdownLabel = (JLabel) contentPane.getComponent(1);
        then(countdownLabel.getName()).isEqualTo("countdownLabel");
        then(countdownLabel.getFont().getSize() > 20).isTrue();
        then(countdownLabel.getHorizontalAlignment()).isEqualTo(SwingConstants.CENTER);

        String initialCountdownText = countdownLabel.getText();
        then(initialCountdownText).contains("3");
    }

    @Test
    public void esc_key_closes_dialog_like_cancel() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            RestartCountdownDialog dlg = createDialog();
            dlg.setVisible(true);
            dialog = new DialogFixture(robot, dlg);
        });

        dialog.requireVisible();

        // Press ESC key on the dialog fixture
        dialog.pressAndReleaseKeys(java.awt.event.KeyEvent.VK_ESCAPE);

        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        then(dialog.target().isVisible()).isFalse();
        then(restartTriggered.get()).isFalse();
    }

}
