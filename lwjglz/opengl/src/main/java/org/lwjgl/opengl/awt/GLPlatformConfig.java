/*
 * Copyright LWJGLZ. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.awt;

import static org.lwjgl.opengl.awt.AWTGL.*;

/**
 *
 * @author wil
 */
class GLPlatformConfig {
    int      client      = AWT_OPENGL_API;
    int      source      = AWT_NATIVE_CONTEXT_API;
    int      major       = 1;
    int      minor       = 0;
    boolean  forward;
    boolean  debug;
    boolean  noerror;
    int      profile;
    int      robustness;
    int      release;
    long     share;
}
