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
public class GLPlatformConfig {
    
    public int      client      = AWT_OPENGL_API;
    public int      source      = AWT_NATIVE_CONTEXT_API;
    public int      major       = 1;
    public int      minor       = 0;
    public boolean  forward;
    public boolean  debug;
    public boolean  noerror;
    public int      profile;
    public int      robustness;
    public int      release;
    public long     share;
}
