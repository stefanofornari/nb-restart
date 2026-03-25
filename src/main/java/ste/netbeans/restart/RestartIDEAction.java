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

import java.awt.event.ActionEvent;
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

import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

/**
 * Action that restarts the NetBeans IDE.
 *
 * <p>Registered under <b>File   Restart IDE</b> (after a separator at the end
 * of the menu) and bound to the <b>Ctrl+Alt+Backspace</b> keyboard shortcut.</p>
 *
 * <p>The restart is delegated to
 * {@link ste.netbeans.restart.LifecycleManagerHelper#restartIDE()}, which
 * locates the platform's {@code LifecycleManager} via the global Lookup and
 * calls its {@code markForRestart()} / {@code exit()} pair.</p>
 */
@ActionID(
    category = "File",
    id = "ste.netbeans.restart.RestartIDEAction"
)
@ActionRegistration(
    displayName = "#CTL_RestartIDEAction",
    iconBase = "ste/netbeans/restart/restart-16x16.png",
    surviveFocusChange = true
)
@ActionReferences({
    //  File menu
    // Put Restart just after Exit (position 2200)
    @ActionReference(
        path = "Menu/File",
        position = 3000
    ),
    //  Global keyboard shortcut
    @ActionReference(
        path = "Shortcuts",
        name = "CS-BACK_SPACE"          // Ctrl+Shift+Backspace
    )
})
@NbBundle.Messages("CTL_RestartIDEAction=&Restart...")
public final class RestartIDEAction implements ActionListener {

    /**
     * Invoked when the user triggers the action (menu click or key shortcut).
     *
     * @param e the action event (not used)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            RestartCountdownDialog dialog = new RestartCountdownDialog(() -> {
                LifecycleManagerHelper.restartIDE();
            });
            dialog.setVisible(true);
        });
    }
}
