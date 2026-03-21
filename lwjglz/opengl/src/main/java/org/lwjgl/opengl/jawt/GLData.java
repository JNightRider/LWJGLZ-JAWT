/*
 * Copyright LWJGLZ. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.jawt;

import org.lwjgl.opengl.awt.*;

/**
 *
 * @author wil
 */
public final class GLData {

    private final GLFBConfig fbconfig        = new GLFBConfig();
    private final GLPlatformConfig ctxconfig = new GLPlatformConfig();

    public GLData() { }
    
    GLFBConfig getFBConfig() {
        return fbconfig;
    }

    GLPlatformConfig getPlatformConfig() {
        return ctxconfig;
    }
}
