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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog that shows a restart countdown with Cancel and Restart Now buttons.
 * UI improvements include warning icon, swapped buttons, red background for "Restart Now",
 * large countdown below message, and center alignment.
 */
public class RestartCountdownDialog extends JDialog {

    private static final int DEFAULT_COUNTDOWN_SECONDS = 30;

    private final JLabel messageLabel;
    private final JLabel countdownLabel;
    private final JButton cancelButton;
    private final JButton restartNowButton;
    private final Timer countdownTimer;
    private int secondsLeft;
    private final Runnable restartCallback;

    /**
     * Constructor with default 3 seconds countdown and modality.
     *
     * @param restartCallback called when restart should be triggered
     */
    public RestartCountdownDialog(Runnable restartCallback) {
        this(restartCallback, DEFAULT_COUNTDOWN_SECONDS, true);
    }

    /**
     * Constructor with configurable countdown duration.
     *
     * @param restartCallback called when restart should be triggered
     * @param countdownSeconds initial countdown seconds
     */
    public RestartCountdownDialog(Runnable restartCallback, int countdownSeconds) {
        this(restartCallback, countdownSeconds, true);
    }

    /**
     * Constructor with configurable countdown duration and modality.
     *
     * @param restartCallback called when restart should be triggered
     * @param countdownSeconds initial countdown seconds
     * @param modal whether the dialog is modal
     */
    public RestartCountdownDialog(Runnable restartCallback, int countdownSeconds, boolean modal) {
        super((Frame) null, "Restart IDE", modal);
        this.restartCallback = restartCallback;
        this.secondsLeft = countdownSeconds;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        // Create a top panel to hold icon and message vertically centered
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        // Warning icon label using JOptionPane warning icon
        Icon warningIcon = UIManager.getIcon("OptionPane.warningIcon");
        JLabel iconLabel = new JLabel(warningIcon);
        iconLabel.setVerticalAlignment(SwingConstants.TOP);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // Wrap icon in panel to keep left alignment nicely
        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.add(iconLabel, BorderLayout.NORTH);

        // Message label centered
        messageLabel = new JLabel("NetBeans will restart in a few seconds:", SwingConstants.CENTER);
        messageLabel.setName("messageLabel");
        messageLabel.setFont(messageLabel.getFont().deriveFont(Font.PLAIN, 14f));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        topPanel.add(iconPanel, BorderLayout.WEST);
        topPanel.add(messageLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Countdown label large font, centered below message
        countdownLabel = new JLabel(String.valueOf(secondsLeft), SwingConstants.CENTER);
        countdownLabel.setName("countdownLabel");
        countdownLabel.setFont(countdownLabel.getFont().deriveFont(Font.BOLD, 36f));
        countdownLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(countdownLabel, BorderLayout.CENTER);

        // Buttons panel: Restart Now, then Cancel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        restartNowButton = new JButton("Restart Now");
        restartNowButton.setName("restartNowButton");
        restartNowButton.setForeground(Color.WHITE);
        restartNowButton.setBackground(Color.RED);
        restartNowButton.setOpaque(true);
        restartNowButton.setBorderPainted(false);
        restartNowButton.addActionListener(e -> triggerRestart());
        buttonPanel.add(restartNowButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setName("cancelButton");
        cancelButton.addActionListener(e -> cancelRestart());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        updateMessageLabel();

        countdownTimer = new Timer(1000, new CountdownTickListener());
        countdownTimer.setInitialDelay(1000);
        countdownTimer.start();

        // Update the UI to current Look and Feel
        SwingUtilities.updateComponentTreeUI(this);

        // Add ESC key binding to close the dialog like pressing Cancel
        String cancelName = "cancel";
        KeyStroke escKeyStroke = KeyStroke.getKeyStroke("ESCAPE");
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(escKeyStroke, cancelName);
        getRootPane().getActionMap().put(cancelName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelRestart();
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    private void updateMessageLabel() {
        countdownLabel.setText(String.valueOf(secondsLeft));
    }

    private void cancelRestart() {
        countdownTimer.stop();
        dispose();
    }

    private void triggerRestart() {
        countdownTimer.stop();
        dispose();
        if (restartCallback != null) {
            restartCallback.run();
        }
    }

    private class CountdownTickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            secondsLeft--;
            if (secondsLeft > 0) {
                updateMessageLabel();
            } else {
                triggerRestart();
            }
        }
    }
}
