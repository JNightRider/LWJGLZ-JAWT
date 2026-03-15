/*
 * Copyright LWJGLZ. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.awt;

import org.lwjgl.system.Platform;

/**
 *
 * @author wil
 */
public class AWT {
    
    public static final long UINT_MAX = 4294967295L;
    
    public static boolean isWayland() {
        switch (Platform.get()) {
            case FREEBSD, LINUX -> {
                // The following matches the test GLFW does to enable the Wayland backend.
                if ("wayland".equals(System.getenv("XDG_SESSION_TYPE")) && System.getenv("WAYLAND_DISPLAY") != null) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean BOOL(Integer number) {
        if (number == null) {
            return false;
        }
        return number != 0;
    }
    
    public static boolean BOOL(Long number) {
        if (number == null) {
            return false;
        }
        return number != 0;
    }
}
