/*
 * Copyright LWJGLZ. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.awt;

/**
 *
 * @author wil
 */
public class GLPlatformConfig {
    
    public static final int
            JAWT_NO_API         = 0,
            JAWT_OPENGL_ES_API  = 1,
            JAWT_OPENGL_API     = 2;
    
    public static final int
            JAWT_NO_ROBUSTNESS          = 0,
            JAWT_NO_RESET_NOTIFICATION  = 1,
            JAWT_LOSE_CONTEXT_ON_RESET  = 2;
    
    public static final int
            JAWT_OPENGL_ANY_PROFILE     = 0,
            JAWT_OPENGL_CORE_PROFILE    = 1,
            JAWT_OPENGL_COMPAT_PROFILE  = 2;
    
    public static final int
            JAWT_ANY_RELEASE_BEHAVIOR   = 0,
            JAWT_RELEASE_BEHAVIOR_FLUSH = 1,
            JAWT_RELEASE_BEHAVIOR_NONE  = 2;
    
    public int      client      = JAWT_OPENGL_API;
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
