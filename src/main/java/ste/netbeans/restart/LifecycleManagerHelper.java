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

import org.openide.LifecycleManager;
import org.openide.util.Exceptions;

/**
 * Utility class that encapsulates the restart logic for the NetBeans IDE.
 *
 * <p>NetBeans exposes restart capability through
 * {@link LifecycleManager#markForRestart()} followed by
 * {@link LifecycleManager#exit()}.  If the platform's lifecycle manager does
 * not support restart (i.e. {@code markForRestart()} throws
 * {@link UnsupportedOperationException}), we fall back to a plain
 * {@code exit()}.</p>
 */
public final class LifecycleManagerHelper {

    /** Utility class – do not instantiate. */
    private LifecycleManagerHelper() {}

    /**
     * Restarts the IDE.
     *
     * <p>Calls {@link LifecycleManager#markForRestart()} on the default
     * lifecycle manager, then {@link LifecycleManager#exit()}.  If the
     * manager does not support restart, falls back to a normal exit so the
     * user is never left with a broken state.</p>
     */
    public static void restartIDE() {
        LifecycleManager lm = LifecycleManager.getDefault();
        try {
            lm.markForRestart();
        } catch (UnsupportedOperationException ex) {
            // Platform does not support restart; log and fall back to exit.
            Exceptions.printStackTrace(ex);
        }
        lm.exit();
    }
}
